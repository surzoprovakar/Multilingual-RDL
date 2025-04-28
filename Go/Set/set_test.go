package main

import (
	"reflect"
	"testing"
)

func TestSet(t *testing.T) {

	set1, set2 := NewSet(1), NewSet(2)

	set1.Add(1)
	set1.Add(3)
	set1.Add(5)

	set2.Add(2)
	set2.Add(4)
	set2.Add(6)

	set1.Remove(5)
	set2.Remove(8)

	set1.Print()
	set2.Print()

	b1 := set1.ToMarshal()
	// fmt.Println("b1: ", b1)
	b2 := set2.ToMarshal()

	rid1, updates1 := FromMarshalData(b1)
	rid2, updates2 := FromMarshalData(b2)

	set3, set4 := NewSet(3), NewSet(4)

	set3.Merge(rid1, updates1)
	set4.Merge(rid2, updates2)

	set3.Print()
	set4.Print()

	if !reflect.DeepEqual(set1.SortedValues(), set3.SortedValues()) {
		t.Errorf("set1 and set3 values are not the same")
	}

	if !reflect.DeepEqual(set2.SortedValues(), set4.SortedValues()) {
		t.Errorf("set2 and set4 values are not the same")
	}

}
