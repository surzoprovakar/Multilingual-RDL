import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.protobuf.InvalidProtocolBufferException;

import set.Msg.SyncMsg;

public class SetCRDT {
    private int id;
    private Set<Integer> value;
    private List<String> updates;
    private final Lock lock = new ReentrantLock();

    public SetCRDT(int id) {
        this.id = id;
        this.value = new HashSet<Integer>();
        this.updates = new ArrayList<>();
    }

    public void Add(int val) {
        lock.lock();
        try {
            if (!this.value.contains(val)) {
                this.value.add(val);
                this.updates.add("Add:" + val);
            }
        } finally {
            lock.unlock();
        }
    }

    public void Remove(int val) {
        lock.lock();
        try {
            if (this.value.contains(val)) {
                this.value.remove(val);
                this.updates.add("Remove:" + val);
            }
        } finally {
            lock.unlock();
        }
    }

    public void SetRemoteVal(int rid, String optName, int val) {
        if ("Add".equals(optName)) {
            this.Add(val);
        } else if ("Remove".equals(optName)) {
            this.Remove(val);
        }
    }

    public int GetId() {
        return id;
    }

    public List<Integer> GetValues() {
        List<Integer> vals = new ArrayList<>(this.value);
        Collections.sort(vals);
        return vals;
    }

    public void Merge(int rid, List<String> rUpdates) {
        lock.lock();
        try {
            System.out.println("Starting to merge req from replica_" + rid);
            if (!rUpdates.isEmpty()) {
                for (String update : rUpdates) {
                    String[] reqs = update.split(":");
                    String opt = reqs[0];
                    int val = Integer.parseInt(reqs[1]);
                    SetRemoteVal(rid, opt, val);
                }
            }
            System.out.print("Merged-> ");
            Print();
        } finally {
            lock.unlock();
        }
    }

    public void Print() {
        System.out.print("Set_" + GetId() + ": ");
        System.out.println(GetValues());
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