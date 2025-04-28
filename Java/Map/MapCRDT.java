import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.protobuf.InvalidProtocolBufferException;

import map.Msg.SyncMsg;

public class MapCRDT {
    private int id;
    private Map<String, Integer> value;
    private List<String> updates;
    private final Lock lock = new ReentrantLock();

    public MapCRDT(int id) {
        this.id = id;
        this.value = new HashMap<String, Integer>();
        this.updates = new ArrayList<>();
    }

    public void Add(String key, int val) {
        lock.lock();
        try {
            if (!this.value.containsKey(key)) {
                this.value.put(key, val);
                this.updates.add("Add:" + key + ":" + val);
            }
        } finally {
            lock.unlock();
        }
    }

    public void Delete(String key) {
        lock.lock();
        try {
            if (this.value.containsKey(key)) {
                this.value.remove(key);
                this.updates.add("Delete:" + key);
            }
        } finally {
            lock.unlock();
        }
    }

    public void Update(String key, int val) {
        lock.lock();
        try {
            if (this.value.containsKey(key)) {
                this.value.put(key, val);
                this.updates.add("Update:" + key + ":" + val);
            }
        } finally {
            lock.unlock();
        }
    }

    public void SetRemoteVal(int rid, String optName, String key, int val) {
        if ("Add".equals(optName)) {
            this.Add(key, val);
        } else if ("Delete".equals(optName)) {
            this.Delete(key);
        } else if ("Update".equals(optName)) {
            this.Update(key, val);
        }
    }

    public int GetId() {
        return id;
    }

    public Map<String, Integer> GetValues() {
        return this.value;
    }

    public void Merge(int rid, List<String> rUpdates) {
        lock.lock();
        try {
            System.out.println("Starting to merge req from replica_" + rid);
            if (!rUpdates.isEmpty()) {
                for (String update : rUpdates) {
                    String[] reqs = update.split(":");
                    String opt = reqs[0];
                    String key = reqs[1];
                    if (reqs.length > 2) {
                        int val = Integer.parseInt(reqs[2]);
                        SetRemoteVal(rid, opt, key, val);
                    } else {
                        SetRemoteVal(rid, opt, key, Integer.MAX_VALUE);
                    }
                }
            }
            System.out.print("Merged-> ");
            Print();
        } finally {
            lock.unlock();
        }
    }

    public void Print() {
        System.out.print("Map_" + GetId() + ": ");
        List<String> keys = new ArrayList<>(this.value.keySet());
        Collections.sort(keys);
        System.out.print("{ ");
        for (int i = 0; i < keys.size(); i++) {
            System.out.print(keys.get(i) + " => " + this.value.get(keys.get(i)));
            if (i < keys.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println(" }");
    }

    public byte[] ToMarshal() {
        SyncMsg msg = SyncMsg.newBuilder().setId(this.id).addAllUpdates(this.updates).build();
        this.updates.clear();

        return msg.toByteArray();
    }

    public static Pair<Integer, List<String>> FromMarshalData(byte[] bytes) {
        try {
            SyncMsg msg = SyncMsg.parseFrom(bytes);
            int rid = msg.getId();
            List<String> rUpdates = new ArrayList<>();
            for (int i = 0; i < msg.getUpdatesCount(); i++) {
                rUpdates.add(msg.getUpdates(i));
            }
            return new Pair<>(rid, rUpdates);

        } catch (InvalidProtocolBufferException e) {
            System.out.println("Unmarshalling error");
            return new Pair<>(-1, null);
        }
    }
}

class Pair<F, S> {
    public final F first;
    public final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }
}