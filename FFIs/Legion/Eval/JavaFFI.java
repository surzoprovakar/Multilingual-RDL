import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class JavaFFI {

    public static void main(String[] args) {
        // Create a ScriptEngineManager and get the JavaScript engine
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("nashorn");

        try {
            // Load and execute the JavaScript file
            engine.eval(new java.io.FileReader("API.js"));

            int NUM_Iterations = 1000;

            // Counter
            for (int i = 0; i < NUM_Iterations; i++) {
                engine.eval("inc_1();");
                engine.eval("inc_2();");
                engine.eval("inc_3();");
                engine.eval("dec_1();");
                engine.eval("dec_2();");
                engine.eval("dec_3();");
            }
            System.out.println("Updated Counter Values:");
            Object counterValue1 = engine.eval("getCounterValue_1();");
            System.out.println("Counter 1 value from Java: " + counterValue1);
            Object counterValue2 = engine.eval("getCounterValue_2();");
            System.out.println("Counter 2 value from Java: " + counterValue2);
            Object counterValue3 = engine.eval("getCounterValue_3();");
            System.out.println("Counter 3 value from Java: " + counterValue3);

            // Set
            for (int i = 0; i < NUM_Iterations; i++) {
                engine.eval("add_1_set(1);");
                engine.eval("add_2_set(2);");
                engine.eval("add_3_set(3);");
                engine.eval("remove_1_set(2);");
                engine.eval("remove_2_set(3);");
                engine.eval("remove_3_set(1);");
            }

            System.out.println("Updated Set Values:");
            Object set1 = engine.eval("getSetValues_1();");
            if (set1 instanceof ScriptObjectMirror) {
                ScriptObjectMirror mirror = (ScriptObjectMirror) set1;
                if (mirror.isArray()) {
                    System.out.print("Set 1 values from Java: ");
                    for (int i = 0; i < mirror.size(); i++) {
                        System.out.print(mirror.getSlot(i) + " ");
                    }
                    System.out.println();
                } else {
                    System.out.println("Set 1 Result is not an array.");
                }
            } else {
                System.out.println("Unexpected result type for Set 1: " + set1.getClass());
            }

            Object set2 = engine.eval("getSetValues_2();");
            if (set2 instanceof ScriptObjectMirror) {
                ScriptObjectMirror mirror = (ScriptObjectMirror) set2;
                if (mirror.isArray()) {
                    System.out.print("Set 2 values from Java: ");
                    for (int i = 0; i < mirror.size(); i++) {
                        System.out.print(mirror.getSlot(i) + " ");
                    }
                    System.out.println();
                } else {
                    System.out.println("Set 2 Result is not an array.");
                }
            } else {
                System.out.println("Unexpected result type for Set 2: " + set2.getClass());
            }

            Object set3 = engine.eval("getSetValues_3();");
            if (set3 instanceof ScriptObjectMirror) {
                ScriptObjectMirror mirror = (ScriptObjectMirror) set3;
                if (mirror.isArray()) {
                    System.out.print("Set 3 values from Java: ");
                    for (int i = 0; i < mirror.size(); i++) {
                        System.out.print(mirror.getSlot(i) + " ");
                    }
                    System.out.println();
                } else {
                    System.out.println("Set 3 Result is not an array.");
                }
            } else {
                System.out.println("Unexpected result type for Set 3: " + set3.getClass());
            }

            // Map
            for (int i = 0; i < NUM_Iterations; i++) {
                engine.eval("add_1_map();");
                engine.eval("add_2_map();");
                engine.eval("add_3_map();");
                engine.eval("remove_1_map();");
                engine.eval("remove_2_map();");
                engine.eval("remove_3_map();");
            }
            System.out.println("Updated Set Values:");

            Object map1 = engine.eval("getMapValues_1();");
            if (map1 instanceof ScriptObjectMirror) {
                ScriptObjectMirror mirror = (ScriptObjectMirror) map1;
                if (mirror.isArray()) {
                    System.out.println("Map 1 values from Java:");
                    for (int i = 0; i < mirror.size(); i++) {
                        ScriptObjectMirror entry = (ScriptObjectMirror) mirror.getSlot(i);
                        if (entry.isArray() && entry.size() == 2) {
                            Object key = entry.getSlot(0);
                            Object value = entry.getSlot(1);
                            System.out.println(key + ": " + value);
                        }
                    }
                } else {
                    System.out.println("Map 1 Result is not an array.");
                }
            } else {
                System.out.println("Unexpected result type for Map 1: " + map1.getClass());
            }

            Object map2 = engine.eval("getMapValues_2();");
            if (map2 instanceof ScriptObjectMirror) {
                ScriptObjectMirror mirror = (ScriptObjectMirror) map2;
                if (mirror.isArray()) {
                    System.out.println("Map 2 values from Java:");
                    for (int i = 0; i < mirror.size(); i++) {
                        ScriptObjectMirror entry = (ScriptObjectMirror) mirror.getSlot(i);
                        if (entry.isArray() && entry.size() == 2) {
                            Object key = entry.getSlot(0);
                            Object value = entry.getSlot(1);
                            System.out.println(key + ": " + value);
                        }
                    }
                } else {
                    System.out.println("Map 2 Result is not an array.");
                }
            } else {
                System.out.println("Unexpected result type for Map 2: " + map2.getClass());
            }

            Object map3 = engine.eval("getMapValues_3();");
            if (map3 instanceof ScriptObjectMirror) {
                ScriptObjectMirror mirror = (ScriptObjectMirror) map1;
                if (mirror.isArray()) {
                    System.out.println("Map 3 values from Java:");
                    for (int i = 0; i < mirror.size(); i++) {
                        ScriptObjectMirror entry = (ScriptObjectMirror) mirror.getSlot(i);
                        if (entry.isArray() && entry.size() == 2) {
                            Object key = entry.getSlot(0);
                            Object value = entry.getSlot(1);
                            System.out.println(key + ": " + value);
                        }
                    }
                } else {
                    System.out.println("Map 3 Result is not an array.");
                }
            } else {
                System.out.println("Unexpected result type for Map 3: " + map3.getClass());
            }

        } catch (ScriptException | java.io.FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
