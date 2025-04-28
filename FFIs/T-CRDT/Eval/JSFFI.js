
var API = JavaImporter(Packages.API);

function run() {
    var NUM_ITERATIONS = 1000

    // Counter
    var counter1 = new API.API.Counter(1);
    var counter2 = new API.API.Counter(2);
    var counter3 = new API.API.Counter(3);

    for (var i = 0; i < NUM_ITERATIONS; i++) {
        counter1.inc();
        counter2.inc();
        counter3.inc();
        counter1.dec();
        counter2.dec();
        counter3.dec();
    }

    java.lang.System.out.println("Updated Counter Values:")
    counter1.printValue();
    counter2.printValue();
    counter3.printValue();

    // Set
    var set1 = new API.API.SetCRDT(1);
    var set2 = new API.API.SetCRDT(2);
    var set3 = new API.API.SetCRDT(3);

    for (var i = 0; i < NUM_ITERATIONS; i++) {
        set1.add("a");
        set2.add("b");
        set3.add("c");
        set1.remove("a");
        set2.remove("c");
        set3.remove("b");
    }
    java.lang.System.out.println("Updated Set Values:");
    set1.printValue();
    set2.printValue();
    set3.printValue();

    // Map
    var map1 = new API.API.MapCRDT(1);
    var map2 = new API.API.MapCRDT(2);
    var map3 = new API.API.MapCRDT(3);

    for (var i = 0; i < NUM_ITERATIONS; i++) {
        map1.addKey("key1", "value1");
        map2.addKey("key2", "value2");
        map3.addKey("key3", "value3");
        map1.removeKey("key2");
        map2.removeKey("key3");
        map3.removeKey("key1");
    }

    java.lang.System.out.println("Updated Map Values:");
    map1.printValue();
    map2.printValue();
    map3.printValue();
}


run()
