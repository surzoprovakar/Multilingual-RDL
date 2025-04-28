// Initialize API and data structures
var counter_1, counter_2, counter_3;
var map_1, map_2, map_3;
var set_1, set_2, set_3;


// Counter objects initialization
counter_1 = initCounter();
counter_2 = initCounter();
counter_3 = initCounter();

// Set objects initialization
set_1 = initSet();
set_2 = initSet();
set_3 = initSet();

// Map objects initialization
map_1 = initMap();
map_2 = initMap();
map_3 = initMap();


// Functions to increment and decrement counters
var inc_1 = function () {
    counter_1.increment(1);
};
var dec_1 = function () {
    counter_1.decrement(1);
};
var inc_2 = function () {
    counter_2.increment(1);
};
var dec_2 = function () {
    counter_2.decrement(1);
};
var inc_3 = function () {
    counter_3.increment(1);
};
var dec_3 = function () {
    counter_3.decrement(1);
};

// Functions to add and remove items from sets
var add_1_set = function (value) {
    set_1.add(value);
};
var add_2_set = function (value) {
    set_2.add(value);
};
var add_3_set = function (value) {
    set_3.add(value);
};
var remove_1_set = function (value) {
    set_1.remove(value);
};
var remove_2_set = function (value) {
    set_2.remove(value);
};
var remove_3_set = function (value) {
    set_3.remove(value);
};

// Functions to get current values of Counter, Set, and Map
var getCounterValue_1 = function () {
    return counter_1.getValue();
};
var getCounterValue_2 = function () {
    return counter_2.getValue();
};
var getCounterValue_3 = function () {
    return counter_3.getValue();
};

// var getSetValues_1 = function () {
//     return Array.from(set_1.values());
// };
// var getSetValues_2 = function () {
//     return Array.from(set_2.values());
// };
// var getSetValues_3 = function () {
//     return Array.from(set_3.values());
// };

// var getMapValues_1 = function () {
//     return Array.from(map_1.entries());
// };
// var getMapValues_2 = function () {
//     return Array.from(map_2.entries());
// };
// var getMapValues_3 = function () {
//     return Array.from(map_3.entries());
// };

// Workaround for Java: Will be added to additioanl LOC required 
var getSetValues_1 = function () {
    var valuesArray = [];
    var values = set_1.values();
    for (var i = 0; i < values.length; i++) {
        valuesArray.push(values[i]);
    }
    return valuesArray;
};
var getSetValues_2 = function () {
    var valuesArray = [];
    var values = set_2.values();
    for (var i = 0; i < values.length; i++) {
        valuesArray.push(values[i]);
    }
    return valuesArray;
};
var getSetValues_3 = function () {
    var valuesArray = [];
    var values = set_3.values();
    for (var i = 0; i < values.length; i++) {
        valuesArray.push(values[i]);
    }
    return valuesArray;
};
var getMapValues_1 = function () {
    return map_1.entries();
};
var getMapValues_2 = function () {
    return map_2.entries();
};
var getMapValues_3 = function () {
    return map_3.entries();
};

// Helper function to get key-value pairs from input
var getKeyValue = function (kv, vv) {
    return {
        k: kv,
        v: vv
    };
};

// Functions to add and remove key-value pairs from maps
var add_1_map = function () {
    var kv = getKeyValue('a', 1);
    map_1.set(kv.k, kv.v);
};
var add_2_map = function () {
    var kv = getKeyValue('b', 2);
    map_2.set(kv.k, kv.v);
};
var add_3_map = function () {
    var kv = getKeyValue('c', 3);
    map_3.set(kv.k, kv.v);
};
var remove_1_map = function () {
    var kv = getKeyValue('a', 1);
    map_1.delete(kv.k);
};
var remove_2_map = function () {
    var kv = getKeyValue('x', 2);
    map_2.delete(kv.k);
};
var remove_3_map = function () {
    var kv = getKeyValue('c', 3);
    map_3.delete(kv.k);
};


function initCounter() {
    var count = 0;
    return {
        increment: function (value) {
            count += value;
            // console.log("Counter incremented to:", count);
        },
        decrement: function (value) {
            count -= value;
            // console.log("Counter decremented to:", count);
        },
        getValue: function () {
            // console.log("Current counter value:", count);
            return count;
        }
    };
}

// function initSet() {
//     var set = new Set();
//     return {
//         add: function (value) {
//             set.add(value);
//             // console.log("Added to set:", value);
//         },
//         remove: function (value) {
//             set.delete(value);
//             // console.log("Removed from set:", value);
//         },
//         values: function () {
//             // console.log("Current set values:", Array.from(set));
//             return set;
//         }
//     };
// }

// Workaround for Java: Will be added to additioanl LOC required 
function initSet() {
    var set = [];
    return {
        add: function (value) {
            if (set.indexOf(value) === -1) { // Avoid duplicates
                set.push(value);
            }
        },
        remove: function (value) {
            var index = set.indexOf(value);
            if (index !== -1) {
                set.splice(index, 1);
            }
        },
        values: function () {
            return set;
        }
    };
}

// function initMap() {
//     var map = new Map();
//     return {
//         set: function (key, value) {
//             map.set(key, value);
//             // console.log("Added to map:", key, value);
//         },
//         delete: function (key) {
//             map.delete(key);
//             // console.log("Removed from map:", key);
//         },
//         entries: function () {
//             // console.log("Current map entries:", Array.from(map.entries()));
//             return map;
//         }
//     };
// }

// Workaround for Java: Will be added to additioanl LOC required 
function initMap() {
    var map = {};
    var keys = []; // To maintain the order of keys

    return {
        set: function (key, value) {
            if (!map.hasOwnProperty(key)) {
                keys.push(key); // Add new key if it doesn't exist
            }
            map[key] = value;
        },
        delete: function (key) {
            if (map.hasOwnProperty(key)) {
                delete map[key];
                var index = keys.indexOf(key);
                if (index !== -1) {
                    keys.splice(index, 1);
                }
            }
        },
        entries: function () {
            var entriesArray = [];
            for (var i = 0; i < keys.length; i++) {
                var key = keys[i];
                entriesArray.push([key, map[key]]);
            }
            return entriesArray;
        }
    };
}


/*
// Dummy code to demonstrate usage
console.log("Initial Counter Values:");
console.log("Counter 1:", getCounterValue_1());
console.log("Counter 2:", getCounterValue_2());
console.log("Counter 3:", getCounterValue_3());

var NUM_Iterations = 1000;

for (i = 0; i < NUM_Iterations; i++) {
    inc_1();
    inc_2();
    inc_3();
    dec_1();
    dec_2();
    dec_3();
}

console.log("Updated Counter Values:");
console.log("Counter 1:", getCounterValue_1());
console.log("Counter 2:", getCounterValue_2());
console.log("Counter 3:", getCounterValue_3());

for (i = 0; i < NUM_Iterations; i++) {
    add_1_set(1);
    add_2_set(2);
    add_3_set(3);
    remove_1_set(1);
    remove_2_set(2);
    remove_3_set(3);
}

console.log("Updated Set Values:");
console.log("Set 1 Values:", getSetValues_1());
console.log("Set 2 Values:", getSetValues_2());
console.log("Set 3 Values:", getSetValues_3());

for (i = 0; i < NUM_Iterations; i++) {
    add_1_map();
    add_2_map();
    add_3_map();
    remove_1_map();
    remove_2_map();
    remove_3_map();
}
console.log("Updated Map Values:");
console.log("Map 1 Entries:", getMapValues_1());
console.log("Map 2 Entries:", getMapValues_2());
console.log("Map 3 Entries:", getMapValues_3());
*/
