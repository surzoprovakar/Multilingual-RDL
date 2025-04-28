import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class JavaFFI {

    public interface GoApiLibrary extends Library {
        GoApiLibrary INSTANCE = (GoApiLibrary) Native.load("goApi", GoApiLibrary.class);

        // Counter
        Pointer NewPNCounter_C(String name, String node);

        void Increment_C(Pointer counter);

        void Decrement_C(Pointer counter);

        int Value_C(Pointer counter);

        // Set
        long NewPNSet_C(String name);

        void AddToPNSet(long pnSetPtr, int value);

        void RemoveFromPNSet(long pnSetPtr, int value);

        int LookupPNSet(long pnSetPtr, int value);

        int SizeOfPNSet(long pnSetPtr);

        // Map
        long NewMap_C(int id);

        void AddToMap(long mapPtr, String key, int value);

        void DeleteFromMap(long mapPtr, String key);

        String GetValues(long mapPtr);

    }

    public static void main(String[] args) {
        GoApiLibrary lib = GoApiLibrary.INSTANCE;
        int NUM_ITERATIONS = 1000;

        // Counter

        Pointer counter1 = lib.NewPNCounter_C("counter1", "");
        Pointer counter2 = lib.NewPNCounter_C("counter2", "");
        Pointer counter3 = lib.NewPNCounter_C("counter3", "");

        for (int i = 0; i < NUM_ITERATIONS; i++) {
            lib.Increment_C(counter1);
            lib.Increment_C(counter2);
            lib.Increment_C(counter3);
            lib.Decrement_C(counter1);
            lib.Decrement_C(counter2);
            lib.Decrement_C(counter3);
        }

        System.out.println("Updated Counter Values:");
        System.out.println("Counter 1: " + lib.Value_C(counter1));
        System.out.println("Counter 2: " + lib.Value_C(counter2));
        System.out.println("Counter 3: " + lib.Value_C(counter3));

        // Set

        long pnSetPtr1 = lib.INSTANCE.NewPNSet_C("pnset1");
        long pnSetPtr2 = lib.INSTANCE.NewPNSet_C("pnset2");
        long pnSetPtr3 = lib.INSTANCE.NewPNSet_C("pnset3");

        for (int i = 0; i < NUM_ITERATIONS; i++) {
            lib.INSTANCE.AddToPNSet(pnSetPtr1, 2);
            lib.INSTANCE.AddToPNSet(pnSetPtr2, 1);
            lib.INSTANCE.AddToPNSet(pnSetPtr3, 3);
            lib.INSTANCE.RemoveFromPNSet(pnSetPtr1, 3);
            lib.INSTANCE.RemoveFromPNSet(pnSetPtr2, 1);
            lib.INSTANCE.RemoveFromPNSet(pnSetPtr3, 2);
        }

        System.out.println("Updated Set Size:");
        System.out.println("Set 1 Size: " + lib.INSTANCE.SizeOfPNSet(pnSetPtr1));
        System.out.println("Set 2 Size: " + lib.INSTANCE.SizeOfPNSet(pnSetPtr2));
        System.out.println("Set 3 Size: " + lib.INSTANCE.SizeOfPNSet(pnSetPtr3));

        // Map

        long mapPtr1 = lib.INSTANCE.NewMap_C(1);
        long mapPtr2 = lib.INSTANCE.NewMap_C(2);
        long mapPtr3 = lib.INSTANCE.NewMap_C(3);

        for (int i = 0; i < NUM_ITERATIONS; i++) {
            lib.INSTANCE.AddToMap(mapPtr1, "foo", 42);
            lib.INSTANCE.AddToMap(mapPtr2, "bar", 7);
            lib.INSTANCE.AddToMap(mapPtr3, "bar2", 5);
        }

        System.out.println("Updated Map Values:");
        System.out.println("Map 1: " + lib.INSTANCE.GetValues(mapPtr1));
        System.out.println("Map 2: " + lib.INSTANCE.GetValues(mapPtr2));
        System.out.println("Map 3: " + lib.INSTANCE.GetValues(mapPtr3));
    }
}
