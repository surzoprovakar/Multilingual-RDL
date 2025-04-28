package main

import (
	"fmt"
	"io/ioutil"

	"github.com/dop251/goja"
)

func main() {
	// Load the JavaScript file
	jsFile, err := ioutil.ReadFile("API.js")
	if err != nil {
		panic(err)
	}

	// Create a new Goja VM
	vm := goja.New()

	// Run the JavaScript code
	_, err = vm.RunString(string(jsFile))
	if err != nil {
		panic(err)
	}

	NUM_Iterations := 1000
	// Invoke JavaScript functions from Go
	// Counter
	for i := 0; i < NUM_Iterations; i++ {
		vm.RunString("inc_1();")
		vm.RunString("inc_2();")
		vm.RunString("inc_3();")
		vm.RunString("dec_1();")
		vm.RunString("dec_2();")
		vm.RunString("dec_3();")
	}

	fmt.Println("Updated Counter Values:")

	value1, err1 := vm.RunString("getCounterValue_1();")
	if err1 != nil {
		panic(err1)
	}
	fmt.Println("Counter 1 value from Go:", value1)

	value2, err2 := vm.RunString("getCounterValue_2();")
	if err2 != nil {
		panic(err2)
	}
	fmt.Println("Counter 2 value from Go:", value2)

	value3, err3 := vm.RunString("getCounterValue_3();")
	if err3 != nil {
		panic(err3)
	}
	fmt.Println("Counter 3 value from Go:", value3)

	// Set
	for i := 0; i < NUM_Iterations; i++ {
		vm.RunString("add_1_set(1);")
		vm.RunString("add_2_set(2);")
		vm.RunString("add_3_set(3);")
		vm.RunString("remove_1_set(2);")
		vm.RunString("remove_2_set(3);")
		vm.RunString("remove_3_set(1);")
	}

	fmt.Println("Updated Set Values:")
	set1, s_err1 := vm.RunString("getSetValues_1();")
	if s_err1 != nil {
		panic(s_err1)
	}
	fmt.Println("Set 1 value from Go:", set1)

	set2, s_err2 := vm.RunString("getSetValues_2();")
	if s_err2 != nil {
		panic(s_err2)
	}
	fmt.Println("Set 2 value from Go:", set2)

	set3, s_err3 := vm.RunString("getSetValues_3();")
	if s_err3 != nil {
		panic(s_err3)
	}
	fmt.Println("Set 3 value from Go:", set3)

	// Map
	for i := 0; i < NUM_Iterations; i++ {
		vm.RunString("add_1_map();")
		vm.RunString("add_2_map();")
		vm.RunString("add_3_map();")
		vm.RunString("remove_1_map();")
		vm.RunString("remove_2_map();")
		vm.RunString("remove_3_map();")
	}
	fmt.Println("Updated Map Values:")
	map1, m_err1 := vm.RunString("getMapValues_1();")
	if m_err1 != nil {
		panic(m_err1)
	}
	fmt.Println("Map 1 value from Go:", map1)

	map2, m_err2 := vm.RunString("getMapValues_2();")
	if m_err2 != nil {
		panic(m_err2)
	}
	fmt.Println("Map 2 value from Go:", map2)

	map3, m_err3 := vm.RunString("getMapValues_3();")
	if m_err3 != nil {
		panic(m_err3)
	}
	fmt.Println("Map 3 value from Go:", map3)
}
