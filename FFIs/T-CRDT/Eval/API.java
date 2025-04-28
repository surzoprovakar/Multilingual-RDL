import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class API {
    // Counter
    public static class Counter {
        private int id;
        private int value;

        public Counter(int id) {
            this.id = id;
            this.value = 0;
        }

        public void inc() {
            value++;
        }

        public void dec() {
            value--;
        }

        public void printValue() {
            System.out.println("Counter_" + id + "==> Value: " + value);
        }

        // JNI methods for Go
        public static native long createCounter(int id);
        public static native void incrementCounter(long ptr);
        public static native void decrementCounter(long ptr);
        public static native void printCounter(long ptr);
    }

    // Set
    public static class SetCRDT {
        private int id;
        private Set<String> set;

        public SetCRDT(int id) {
            this.id = id;
            this.set = new HashSet<>();
        }

        public void add(String element) {
            set.add(element);
        }

        public void remove(String element) {
            set.remove(element);
        }

        public void printValue() {
            System.out.println("Set_" + id + "==> Elements: " + set);
        }

        // JNI methods for Go
        public static native long createSet(int id);
        public static native void addElement(long ptr, String element);
        public static native void removeElement(long ptr, String element);
        public static native void printSet(long ptr);
    }

    // Map CRDT
    public static class MapCRDT {
        private int id;
        private Map<String, String> map;

        public MapCRDT(int id) {
            this.id = id;
            this.map = new HashMap<>();
        }

        public void addKey(String key, String value) {
            map.put(key, value);
        }

        public void removeKey(String key) {
            map.remove(key);
        }

        public void printValue() {
            System.out.println("Map_" + id + "==> Entries: " + map);
        }

        // JNI methods for Go
        public static native long createMap(int id);
        public static native void addEntry(long ptr, String key, String value);
        public static native void removeEntry(long ptr, String key);
        public static native void printMap(long ptr);
    }

    public static void main(String[] args) {
        int NUM_ITERATIONS = 1000;

        // Counter
        Counter counter1 = new Counter(1);
        Counter counter2 = new Counter(2);
        Counter counter3 = new Counter(3);

        for (int i = 0; i < NUM_ITERATIONS; i++) {
            counter1.inc();
            counter2.inc();
            counter3.inc();
            counter1.dec();
            counter2.dec();
            counter3.dec();
        }

        System.out.println("Updated Counter Values:");
        counter1.printValue();
        counter2.printValue();
        counter3.printValue();

        // Set
        SetCRDT set1 = new SetCRDT(1);
        SetCRDT set2 = new SetCRDT(2);
        SetCRDT set3 = new SetCRDT(3);

        for (int i = 0; i < NUM_ITERATIONS; i++) {
            set1.add("a");
            set2.add("b");
            set3.add("c");
            set1.remove("a");
            set2.remove("c");
            set3.remove("b");
        }
        System.out.println("Updated Set Values:");
        set1.printValue();
        set2.printValue();
        set3.printValue();

        // Map
        MapCRDT map1 = new MapCRDT(1);
        MapCRDT map2 = new MapCRDT(2);
        MapCRDT map3 = new MapCRDT(3);

        for (int i = 0; i < NUM_ITERATIONS; i++) {
            map1.addKey("key1", "value1");
            map2.addKey("key2", "value2");
            map3.addKey("key3", "value3");
            map1.removeKey("key2");
            map2.removeKey("key3");
            map3.removeKey("key1");
        }

        System.out.println("Updated Map Values:");
        map1.printValue();
        map2.printValue();
        map3.printValue();
    }

}
