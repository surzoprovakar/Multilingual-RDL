package crdt

import (
	"slices"
	"testing"
)

func TestGSetInitialization(t *testing.T) {
	gset := NewGSet[int]("gset1")
	if gset.Size() != 0 {
		t.Fatalf("gset should initialize to a size of 0")
	}
}

func TestGSetAdd(t *testing.T) {
	gset := NewGSet[int]("gset1")
	gset.Add(5)
	if gset.Size() != 1 {
		t.Fatalf("gset size should be 1")
	}
}

func TestGSetLookupTrue(t *testing.T) {
	gset := NewGSet[int]("gset1")
	gset.Add(5)
	if !gset.Lookup(5) {
		t.Fatalf("gset lookup should have been true")
	}
}

func TestGSetLookupFalse(t *testing.T) {
	gset := NewGSet[int]("gset1")
	gset.Add(5)
	if gset.Lookup(10) {
		t.Fatalf("gset lookup should have been false")
	}
}

func TestGSetgetSet(t *testing.T) {
	gset := NewGSet[int]("gset1")
	gset.Add(5)
	gset.Add(15)
	gset.Add(26)
	set := gset.getSet()
	expectedSet := []int{5, 15, 26}
	for _, e := range set {
		if !slices.Contains(expectedSet, e) {
			t.Fatalf("all elements in gset: %x should have been existed in: %x", set, expectedSet)
		}
	}
}

func TestGSetMerge(t *testing.T) {
	gset1 := NewGSet[int]("gset1")
	gset1.Add(5)
	gset2 := NewGSet[int]("gset1")
	gset2.Add(10)

	if gset1.Lookup(10) {
		t.Fatalf("gset lookup for 10 should have been false before merge")
	}

	gset1.Merge(gset2)

	if gset1.Size() != 2 {
		t.Fatalf("gset lookup for 10 should have been true after merge")
	}
	if !gset1.Lookup(10) {
		t.Fatalf("gset lookup for 10 should have been true after merge")
	}
}

func TestGSetMergeIdempotent(t *testing.T) {
	gset1 := NewGSet[int]("gset1")
	gset1.Add(5)
	gset2 := NewGSet[int]("gset1")
	gset2.Add(10)

	if gset1.Lookup(10) {
		t.Fatalf("gset lookup for 10 should have been false before merge")
	}

	gset1.Merge(gset2)
	gset1.Merge(gset2)
	gset1.Merge(gset2)

	if gset1.Size() != 2 {
		t.Fatalf("gset size should stay 2 after subsequent merges with no changes")
	}
	if !gset1.Lookup(10) {
		t.Fatalf("gset lookup for 10 should have been true after merge")
	}
}

func TestGSetMergeUnmatchedName(t *testing.T) {
	gset1 := NewGSet[int]("gset1")
	gset1.Add(5)
	gset2 := NewGSet[int]("gset2")
	gset2.Add(10)

	if gset1.Lookup(10) {
		t.Fatalf("gset lookup for 10 should have been false before merge")
	}

	gset1.Merge(gset2) // Should be no-op due to mismatched name

	if gset1.Size() != 1 {
		t.Fatalf("gset Size should have been 1 after merge")
	}
	if gset1.Lookup(10) {
		t.Fatalf("gset lookup for 10 should have been false after merge")
	}
}
