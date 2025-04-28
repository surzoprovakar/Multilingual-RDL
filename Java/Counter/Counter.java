import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.protobuf.InvalidProtocolBufferException;

import counter.Msg.SyncMsg;

public class Counter {
    private int id;
    private int value;
    private List<String> updates;
    private final Lock lock = new ReentrantLock();
    private final PluginManager pm = new PluginManager();

    public Counter(int id) {
        this.id = id;
        this.value = 0;
        this.updates = new ArrayList<>();
        pm.notify(id, "create");
    }

    public void SetVal(int newVal, String optName) {
        this.value = newVal;
        this.updates.add(optName);
    }

    public void SetRemoteVal(int rid, String optName) {
        if ("Inc".equals(optName)) {
            this.value += 1;
        } else if ("Dec".equals(optName)) {
            this.value -= 1;
        }
    }

    public void Inc() {
        lock.lock();
        try {
            int newVal = this.value + 1;
            SetVal(newVal, "Inc");
        } finally {
            lock.unlock();
        }

        // Logging
        String logMsg = "Local Inc, Updated Value is " + this.value;
        pm.notify(this.id, logMsg);
    }

    public void Dec() {
        lock.lock();
        try {
            int newVal = this.value - 1;
            SetVal(newVal, "Dec");
        } finally {
            lock.unlock();
        }

        // Logging
        String logMsg = "Local Dec, Updated Value is " + this.value;
        pm.notify(this.id, logMsg);
    }

    public int GetId() {
        return id;
    }

    public int GetValue() {
        return value;
    }

    public void Merge(int rid, List<String> rUpdates) {
        lock.lock();
        try {
            System.out.println("Starting to merge req from replica_" + rid);
            if (!rUpdates.isEmpty()) {
                for (String update : rUpdates) {
                    SetRemoteVal(rid, update);
                }
            }
            System.out.println("Merged " + Print());
        } finally {
            lock.unlock();
        }

        // Logging
        String logMsg = "Synchronizing with Replica " + rid + ", Updated Value is " + this.value;
        pm.notify(this.id, logMsg);
    }

    public String Print() {
        return String.format("Counter_%d:%d", GetId(), GetValue());
    }

    public byte[] ToMarshal() {
        SyncMsg msg = SyncMsg.newBuilder().setId(this.id).addAllUpdates(this.updates).build();
        this.updates.clear();

        // Logging
        String logMsg = "Broadcasting current state";
        pm.notify(this.id, logMsg);

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