package main

import (
	"fmt"
	"testing"
)

func TestCounter(t *testing.T) {

	counter1, counter2 := NewCounter(1), NewCounter(2)

	counter1.Inc()
	counter1.Inc()

	counter2.Inc()
	counter2.Inc()
	counter2.Inc()

	counter1.Dec()
	counter2.Dec()

	fmt.Println(counter1.Print())
	fmt.Println(counter2.Print())

	b1 := counter1.ToMarshal()
	// fmt.Println("b1: ", b1)
	b2 := counter2.ToMarshal()

	rid1, updates1 := FromMarshalData(b1)
	rid2, updates2 := FromMarshalData(b2)

	counter3, counter4 := NewCounter(3), NewCounter(4)

	counter3.Merge(rid1, updates1)
	counter4.Merge(rid2, updates2)

	fmt.Println(counter3.Print())
	fmt.Println(counter4.Print())

	if counter1.Value() != counter3.Value() {
		t.Errorf("counter1 and counter3 values are not the same")
	}

	if counter2.Value() != counter4.Value() {
		t.Errorf("counter2 and counter4 values are not the same")
	}

}
