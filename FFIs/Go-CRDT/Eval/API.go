package main

/*
// this import is for JS only, comment in the other times.
// import "syscall/js"

// this import is for Java Only. */
import "C"
import (
	"sync"
	"unsafe"
)

var mu sync.Mutex

// Counter
type GCounter struct {
	name   string
	node   string
	values map[string]int
}

func NewGCounter(name string, node string) *GCounter {
	counter := new(GCounter)
	counter.name = name
	counter.node = node
	counter.values = make(map[string]int)
	counter.values[node] = 0
	return counter
}

func (counter *GCounter) Value() int {
	sum := 0
	for _, val := range counter.values {
		sum += val
	}
	return sum
}

func (counter *GCounter) Merge(that *GCounter) {
	if counter.name != that.name {
		return
	}
	for key, thatValue := range that.values {
		if counterValue, exists := counter.values[key]; exists {
			if counterValue < thatValue {
				counter.values[key] = thatValue
			}
		} else {
			counter.values[key] = thatValue
		}
	}
}

type PNCounter struct {
	name                 string
	node                 string
	positives, negatives *GCounter
}

func NewPNCounter(name string, node string) *PNCounter {
	counter := new(PNCounter)
	counter.name = name
	counter.node = node
	counter.positives = NewGCounter(name+"_positives", node)
	counter.negatives = NewGCounter(name+"_negatives", node)
	return counter
}

func (counter *PNCounter) Value() int {
	return counter.positives.Value() - counter.negatives.Value()
}

func (counter *PNCounter) Increment() {
	counter.positives.values[counter.node]++
}

func (counter *PNCounter) Decrement() {
	counter.negatives.values[counter.node]++
}

func (counter *PNCounter) Merge(that *PNCounter) {
	if counter.name != that.name {
		return
	}
	counter.positives.Merge(that.positives)
	counter.negatives.Merge(that.negatives)
}

// Set
// type PNSet[T comparable] struct {
// 	name string
// 	set  map[T]*PNCounter
// }

//	func NewPNSet[T comparable](name string) *PNSet[T] {
//		pnset := new(PNSet[T])
//		pnset.name = name
//		pnset.set = make(map[T]*PNCounter)
//		return pnset
//	}
type PNSet struct {
	name string
	set  map[int]*PNCounter
}

func NewPNSet(name string) *PNSet {
	pnset := new(PNSet)
	pnset.name = name
	pnset.set = make(map[int]*PNCounter)
	return pnset
}

func (pnset *PNSet) Add(value int) {
	if counter, exists := pnset.set[value]; exists {
		counter.Increment()
	} else {
		newCounter := NewPNCounter(pnset.name, pnset.name)
		newCounter.Increment()
		pnset.set[value] = newCounter
	}
}

func (pnset *PNSet) Remove(value int) {
	if counter, exists := pnset.set[value]; exists {
		counter.Decrement()
	}
}

func (pnset *PNSet) Lookup(value int) bool {
	if counter, exists := pnset.set[value]; exists {
		return counter.Value() > 0
	}
	return false
}

func (pnset *PNSet) Size() int {
	size := 0
	for _, counter := range pnset.set {
		if counter.Value() > 0 {
			size++
		}
	}
	return size
}

func (pnset *PNSet) Merge(that *PNSet) {
	if pnset.name != that.name {
		return
	}
	for value, counter := range that.set {
		if _, exists := pnset.set[value]; !exists {
			pnset.set[value] = counter
		} else {
			pnset.set[value].Merge(counter)
		}
	}
}

// Map Demo
type Map struct {
	id     int
	values map[string]int
}

func NewMap(id int) *Map {
	return &Map{id: id, values: make(map[string]int)}
}

func (m *Map) Values() map[string]int {
	return m.values
}

func (m *Map) Add(key string, val int) {
	if _, ok := m.values[key]; !ok {
		m.values[key] = val
	}
}

func (m *Map) Delete(key string) {
	if _, ok := m.values[key]; ok {
		delete(m.values, key)
	}
}

// Data collection
func main() {
	/*
		// For Go Usage
		NUM_ITERATIONS := 1000

		// Counter
		counter1 := NewPNCounter("counter1", "")
		counter2 := NewPNCounter("counter2", "")
		counter3 := NewPNCounter("counter3", "")

		for i := 0; i < NUM_ITERATIONS; i++ {
			counter1.Increment()
			counter2.Increment()
			counter3.Increment()
			counter1.Decrement()
			counter2.Decrement()
			counter3.Decrement()
		}
		fmt.Println("Updated Counter Values:")
		fmt.Println("Counter 1:", counter1.Value())
		fmt.Println("Counter 2:", counter2.Value())
		fmt.Println("Counter 3:", counter3.Value())

		// Set
		pnset1 := NewPNSet[int]("pnset")
		pnset2 := NewPNSet[int]("pnset")
		pnset3 := NewPNSet[int]("pnset")
		for i := 0; i < NUM_ITERATIONS; i++ {
			pnset1.Add(1)
			pnset2.Add(2)
			pnset3.Add(3)
			pnset1.Remove(2)
			pnset1.Remove(3)
			pnset1.Remove(1)
		}
		fmt.Println("Updated Set Values:")
		fmt.Println("Set 1:", pnset1.set)
		fmt.Println("Set 2:", pnset2.set)
		fmt.Println("Set 3:", pnset3.set)

		// Map
		map1 := NewMap(1)
		map2 := NewMap(2)
		map3 := NewMap(3)
		for i := 0; i < NUM_ITERATIONS; i++ {
			map1.Add("a", 1)
			map2.Add("b", 2)
			map3.Add("c", 3)
			map1.Delete("c")
			map2.Delete("a")
			map3.Delete("b")
		}

		fmt.Println("Updated Map Values:")
		fmt.Println("Map 1:", map1.Values())
		fmt.Println("Set 2:", map2.Values())
		fmt.Println("Set 3:", map3.Values())
	*/

	/*
		// For JS Usage
		c := make(chan struct{}, 0)
		// Counter
		js.Global().Set("NewPNCounter", js.FuncOf(NewPNCounterWrapper))
		js.Global().Set("Increment", js.FuncOf(IncrementWrapper))
		js.Global().Set("Decrement", js.FuncOf(DecrementWrapper))
		js.Global().Set("Value", js.FuncOf(ValueWrapper))
		// Set
		js.Global().Set("NewPNSet", js.FuncOf(NewPNSetWrapper))
		js.Global().Set("Add", js.FuncOf(AddWrapper))
		js.Global().Set("Remove", js.FuncOf(RemoveWrapper))
		js.Global().Set("Lookup", js.FuncOf(LookupWrapper))
		js.Global().Set("Size", js.FuncOf(SizeWrapper))
		js.Global().Set("Merge", js.FuncOf(MergeWrapper))
		// Map
		js.Global().Set("NewMap", js.FuncOf(NewMapWrapper))
		js.Global().Set("AddMap", js.FuncOf(AddMapWrapper))
		js.Global().Set("DeleteMap", js.FuncOf(DeleteMapWrapper))
		js.Global().Set("Values", js.FuncOf(ValuesWrapper))
		<-c
	*/
}

/*
// Counter Wrapper for JS
var counters = make(map[string]*PNCounter)

func NewPNCounterWrapper(this js.Value, p []js.Value) interface{} {
	name := p[0].String()
	node := p[1].String()
	counter := NewPNCounter(name, node)
	counters[name] = counter
	return js.ValueOf(nil)
}

func IncrementWrapper(this js.Value, p []js.Value) interface{} {
	name := p[0].String()
	if counter, exists := counters[name]; exists {
		counter.Increment()
	}
	return js.ValueOf(nil)
}

func DecrementWrapper(this js.Value, p []js.Value) interface{} {
	name := p[0].String()
	if counter, exists := counters[name]; exists {
		counter.Decrement()
	}
	return js.ValueOf(nil)
}

func ValueWrapper(this js.Value, p []js.Value) interface{} {
	name := p[0].String()
	if counter, exists := counters[name]; exists {
		return js.ValueOf(counter.Value())
	}
	return js.Undefined()
}

// Set Wrapper for JS
var pnSets = make(map[string]*PNSet[int])

func NewPNSetWrapper(this js.Value, p []js.Value) interface{} {
	name := p[0].String()
	pnSet := NewPNSet[int](name)
	pnSets[name] = pnSet
	return js.ValueOf(nil)
}

func AddWrapper(this js.Value, p []js.Value) interface{} {
	name := p[0].String()
	value := p[1].Int()
	if pnSet, exists := pnSets[name]; exists {
		pnSet.Add(value)
	}
	return js.ValueOf(nil)
}

func RemoveWrapper(this js.Value, p []js.Value) interface{} {
	name := p[0].String()
	value := p[1].Int()
	if pnSet, exists := pnSets[name]; exists {
		pnSet.Remove(value)
	}
	return js.ValueOf(nil)
}

func LookupWrapper(this js.Value, p []js.Value) interface{} {
	name := p[0].String()
	value := p[1].Int()
	if pnSet, exists := pnSets[name]; exists {
		return js.ValueOf(pnSet.Lookup(value))
	}
	return js.Undefined()
}

func SizeWrapper(this js.Value, p []js.Value) interface{} {
	name := p[0].String()
	if pnSet, exists := pnSets[name]; exists {
		return js.ValueOf(pnSet.Size())
	}
	return js.Undefined()
}

func MergeWrapper(this js.Value, p []js.Value) interface{} {
	name1 := p[0].String()
	name2 := p[1].String()
	if pnSet1, exists1 := pnSets[name1]; exists1 {
		if pnSet2, exists2 := pnSets[name2]; exists2 {
			pnSet1.Merge(pnSet2)
		}
	}
	return js.ValueOf(nil)
}

// Map Wrapper for JS
var maps = make(map[int]*Map)

func NewMapWrapper(this js.Value, p []js.Value) interface{} {
	id := p[0].Int()
	m := NewMap(id)
	maps[id] = m
	return js.ValueOf(nil)
}

func AddMapWrapper(this js.Value, p []js.Value) interface{} {
	id := p[0].Int()
	key := p[1].String()
	val := p[2].Int()
	if m, exists := maps[id]; exists {
		m.Add(key, val)
	}
	return js.ValueOf(nil)
}

func DeleteMapWrapper(this js.Value, p []js.Value) interface{} {
	id := p[0].Int()
	key := p[1].String()
	if m, exists := maps[id]; exists {
		m.Delete(key)
	}
	return js.ValueOf(nil)
}

func ValuesWrapper(this js.Value, p []js.Value) interface{} {
	id := p[0].Int()
	if m, exists := maps[id]; exists {
		jsObj := js.ValueOf(map[string]interface{}{})
		for key, val := range m.Values() {
			jsObj.Set(key, val)
		}
		return jsObj
	}
	return js.Undefined()
}
*/
// Counter Wrapper for Java

// Exported functions to be called from Java

//export NewPNCounter_C
func NewPNCounter_C(name *C.char, node *C.char) uintptr {
	counter := NewPNCounter(C.GoString(name), C.GoString(node))
	return uintptr(unsafe.Pointer(counter))
}

//export Increment_C
func Increment_C(ptr uintptr) {
	counter := (*PNCounter)(unsafe.Pointer(ptr))
	counter.positives.values[counter.positives.node]++
}

//export Decrement_C
func Decrement_C(ptr uintptr) {
	counter := (*PNCounter)(unsafe.Pointer(ptr))
	counter.negatives.values[counter.negatives.node]++
}

//export Value_C
func Value_C(ptr uintptr) int {
	counter := (*PNCounter)(unsafe.Pointer(ptr))
	return counter.positives.values[counter.positives.node] - counter.negatives.values[counter.negatives.node]
}

// Set Wrapper for Java

var (
	pnSets    = make(map[uintptr]*PNSet)
	pnSetLock sync.Mutex
)

//export NewPNSet_C
func NewPNSet_C(name *C.char) uintptr {
	pnSetLock.Lock()
	defer pnSetLock.Unlock()

	pnset := &PNSet{name: C.GoString(name), set: make(map[int]*PNCounter)}
	ptr := uintptr(unsafe.Pointer(pnset))
	pnSets[ptr] = pnset
	return ptr
}

// Exported function to add a value to the PNSet
//
//export AddToPNSet
func AddToPNSet(ptr uintptr, value C.int) {
	pnSetLock.Lock()
	defer pnSetLock.Unlock()

	pnset := pnSets[ptr]
	if counter, exists := pnset.set[int(value)]; exists {
		counter.Increment()
	} else {
		newCounter := NewPNCounter(pnset.name, pnset.name)
		newCounter.Increment()
		pnset.set[int(value)] = newCounter
	}
}

// Exported function to remove a value from the PNSet
//
//export RemoveFromPNSet
func RemoveFromPNSet(ptr uintptr, value C.int) {
	pnSetLock.Lock()
	defer pnSetLock.Unlock()

	pnset := pnSets[ptr]
	if counter, exists := pnset.set[int(value)]; exists {
		counter.Decrement()
	}
}

// Exported function to look up a value in the PNSet
//
//export LookupPNSet
func LookupPNSet(ptr uintptr, value C.int) C.int {
	pnSetLock.Lock()
	defer pnSetLock.Unlock()

	pnset := pnSets[ptr]
	if counter, exists := pnset.set[int(value)]; exists {
		if counter.Value() > 0 {
			return 1
		}
	}
	return 0
}

// Exported function to get the size of the PNSet
//
//export SizeOfPNSet
func SizeOfPNSet(ptr uintptr) C.int {
	pnSetLock.Lock()
	defer pnSetLock.Unlock()

	pnset := pnSets[ptr]
	size := 0
	for _, counter := range pnset.set {
		if counter.Value() > 0 {
			size++
		}
	}
	return C.int(size)
}

// Map Wrapper for Java

var (
	maps    = make(map[uintptr]*Map)
	mapLock sync.Mutex
)

//export NewMap_C
func NewMap_C(id C.int) uintptr {
	mapLock.Lock()
	defer mapLock.Unlock()

	m := &Map{id: int(id), values: make(map[string]int)}
	ptr := uintptr(unsafe.Pointer(m))
	maps[ptr] = m
	return ptr
}

//export AddToMap
func AddToMap(ptr uintptr, key *C.char, val C.int) {
	mapLock.Lock()
	defer mapLock.Unlock()

	m := maps[ptr]
	m.values[C.GoString(key)] = int(val)
}

//export DeleteFromMap
func DeleteFromMap(ptr uintptr, key *C.char) {
	mapLock.Lock()
	defer mapLock.Unlock()

	m := maps[ptr]
	delete(m.values, C.GoString(key))
}

//export GetValues
func GetValues(ptr uintptr) *C.char {
	mapLock.Lock()
	defer mapLock.Unlock()

	m := maps[ptr]
	// Convert the map to a string representation
	result := ""
	for k, v := range m.values {
		result += k + ":" + string(v) + ","
	}
	return C.CString(result)
}
