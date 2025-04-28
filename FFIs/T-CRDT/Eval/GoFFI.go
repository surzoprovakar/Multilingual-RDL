package main

/*
#cgo CFLAGS: -I/usr/lib/jvm/java-11-openjdk-amd64/include/ -I/usr/lib/jvm/java-11-openjdk-amd64/include/linux
#cgo LDFLAGS: -L/usr/lib/jvm/java-11-openjdk-amd64/lib -ljvm
#include <jni.h>
#include <stdlib.h>
#include "API_Counter.h"
#include "API_MapCRDT.h"
#include "API_SetCRDT.h"
*/
import "C"
import (
	"fmt"
	"unsafe"
)

func main() {
	var jvm *C.JavaVM
	var jniEnv *C.JNIEnv
	var jclassCounter C.jclass

	// Initialize JVM
	var args C.JavaVMOption
	args.optionString = nil
	args.extraInfo = nil
	options := []C.JavaVMOption{args}

	var jvmArgs C.JavaVMInitArgs
	jvmArgs.version = C.JNI_VERSION_1_6
	jvmArgs.nOptions = C.int(len(options))
	jvmArgs.options = &options[0]
	jvmArgs.ignoreUnrecognized = C.JNI_TRUE

	res := C.JNI_CreateJavaVM(&jvm, unsafe.Pointer(&jniEnv), &jvmArgs)
	if res != C.JNI_OK {
		fmt.Println("Failed to create JVM")
		return
	}
	defer C.JVM_DestroyJavaVM(jvm)

	// Obtain the class reference for API_Counter
	className := C.CString("API$Counter") // Use correct class name
	defer C.free(unsafe.Pointer(className))
	cls := C.JNI_FindClass(jniEnv, className)
	if cls == nil {
		fmt.Println("Failed to find class")
		return
	}
	jclassCounter = cls

	// Create Counter instances
	counter1 := C.Java_API_00024Counter_createCounter(jniEnv, jclassCounter, C.int(1))
	counter2 := C.Java_API_00024Counter_createCounter(jniEnv, jclassCounter, C.int(2))
	counter3 := C.Java_API_00024Counter_createCounter(jniEnv, jclassCounter, C.int(3))

	// Increment and decrement operations
	numIterations := 1000
	for i := 0; i < numIterations; i++ {
		C.Java_API_00024Counter_incrementCounter(counter1)
		C.Java_API_00024Counter_incrementCounter(counter2)
		C.Java_API_00024Counter_incrementCounter(counter3)
		C.Java_API_00024Counter_decrementCounter(counter1)
		C.Java_API_00024Counter_decrementCounter(counter2)
		C.Java_API_00024Counter_decrementCounter(counter3)
	}

	// Print Counter values
	fmt.Println("Updated Counter Values:")
	C.Java_API_00024Counter_printCounter(counter1)
	C.Java_API_00024Counter_printCounter(counter2)
	C.Java_API_00024Counter_printCounter(counter3)

	// Create SetCRDT instances
	set1 := C.Java_API_00024SetCRDT_createSet(C.int(1))
	set2 := C.Java_API_00024SetCRDT_createSet(C.int(2))
	set3 := C.Java_API_00024SetCRDT_createSet(C.int(3))

	// Add and remove elements
	for i := 0; i < numIterations; i++ {
		C.Java_API_00024SetCRDT_addElement(set1, C.CString("a"))
		C.Java_API_00024SetCRDT_addElement(set2, C.CString("b"))
		C.Java_API_00024SetCRDT_addElement(set3, C.CString("c"))
		C.Java_API_00024SetCRDT_removeElement(set1, C.CString("a"))
		C.Java_API_00024SetCRDT_removeElement(set2, C.CString("c"))
		C.Java_API_00024SetCRDT_removeElement(set3, C.CString("b"))
	}

	// Print Set values
	fmt.Println("Updated Set Values:")
	C.Java_API_00024SetCRDT_printSet(set1)
	C.Java_API_00024SetCRDT_printSet(set2)
	C.Java_API_00024SetCRDT_printSet(set3)

	// Create MapCRDT instances
	map1 := C.Java_API_00024MapCRDT_createMap(C.int(1))
	map2 := C.Java_API_00024MapCRDT_createMap(C.int(2))
	map3 := C.Java_API_00024MapCRDT_createMap(C.int(3))

	// Add and remove entries
	for i := 0; i < numIterations; i++ {
		C.Java_API_00024MapCRDT_addEntry(map1, C.CString("key1"), C.CString("value1"))
		C.Java_API_00024MapCRDT_addEntry(map2, C.CString("key2"), C.CString("value2"))
		C.Java_API_00024MapCRDT_addEntry(map3, C.CString("key3"), C.CString("value3"))
		C.Java_API_00024MapCRDT_removeEntry(map1, C.CString("key2"))
		C.Java_API_00024MapCRDT_removeEntry(map2, C.CString("key3"))
		C.Java_API_00024MapCRDT_removeEntry(map3, C.CString("key1"))
	}

	// Print Map values
	fmt.Println("Updated Map Values:")
	C.Java_API_00024MapCRDT_printMap(map1)
	C.Java_API_00024MapCRDT_printMap(map2)
	C.Java_API_00024MapCRDT_printMap(map3)

	// Destroy JVM
	// C.JVM_DestroyJavaVM(jvm)

}
