const fs = require('fs');
const crypto = require('crypto');

// Define the polyfill for globalThis.crypto
globalThis.crypto = {
    getRandomValues: (array) => crypto.randomFillSync(array)
};

const go = require('./wasm_exec.js'); // Path to wasm_exec.js

async function loadWasm() {
    const goInstance = new Go(); // Create a new Go instance
    const wasmBuffer = fs.readFileSync('go-api.wasm'); // Read the compiled Wasm file
    const result = await WebAssembly.instantiate(wasmBuffer, goInstance.importObject);
    goInstance.run(result.instance); // Run the Wasm module
}

async function createCounters() {
    await new Promise(resolve => {
        loadWasm().then(resolve);
    });

    // Create counters
    NewPNCounter("counter1", "node1");
    NewPNCounter("counter2", "node2");
    NewPNCounter("counter3", "node3");
}

async function runCounters() {
    // Increment and decrement counters
    var NUM_ITERATIONS = 10000
    for (let i = 0; i < NUM_ITERATIONS; i++) { // Replace 10 with NUM_ITERATIONS if needed
        Increment("counter1");
        Increment("counter2");
        Increment("counter3");
        Decrement("counter1");
        Decrement("counter2");
        Decrement("counter3");
    }

    // Get counter values
    const counter1Value = Value("counter1");
    const counter2Value = Value("counter2");
    const counter3Value = Value("counter3");

    console.log("Updated Counter Values:");
    console.log("Counter 1:", counter1Value);
    console.log("Counter 2:", counter2Value);
    console.log("Counter 3:", counter3Value);
}

async function createPNSets() {
    await new Promise(resolve => {
        loadWasm().then(resolve);
    });

    // Create PNSet instances
    NewPNSet("pnset1");
    NewPNSet("pnset2");
    NewPNSet("pnset3");
}

async function modifyPNSets() {
    var NUM_ITERATIONS = 10000
    for (let i = 0; i < NUM_ITERATIONS; i++) { // Replace 10 with NUM_ITERATIONS if needed
        Add("pnset1", 1);
        Add("pnset2", 2);
        Add("pnset3", 3);
        Remove("pnset1", 2);
        Remove("pnset1", 3);
        Remove("pnset1", 1);
    }
}

async function printPNSetSizes() {
    const size1 = Size("pnset1");
    const size2 = Size("pnset2");
    const size3 = Size("pnset3");

    console.log("Updated PNSet Sizes:");
    console.log("PNSet 1 size:", size1);
    console.log("PNSet 2 size:", size2);
    console.log("PNSet 3 size:", size3);
}

async function createMaps() {
    await new Promise(resolve => {
        loadWasm().then(resolve);
    });

    // Creating maps
    NewMap(1);
    NewMap(2);
    NewMap(3);
}

async function runMaps() {
    var NUM_ITERATIONS = 10000
    for (let i = 0; i < NUM_ITERATIONS; i++) {
        AddMap(1, "a", 1);
        AddMap(2, "b", 2);
        AddMap(3, "c", 3);
        DeleteMap(1, "c");
        DeleteMap(2, "a");
        DeleteMap(3, "b");
    }

    console.log("Updated Map Values:");
    console.log("Map 1:", Values(1));
    console.log("Map 2:", Values(2));
    console.log("Map 3:", Values(3));
}

async function main() {
    await createCounters();
    await runCounters();
    await createPNSets();
    await modifyPNSets();
    await printPNSetSizes();
    await createMaps()
    await runMaps()
}

main().catch(console.error);
