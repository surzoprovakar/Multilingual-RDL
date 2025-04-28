package main

import (
	"fmt"
	set "set/SyncMsg"
	"sort"
	"strconv"
	"strings"
	"sync"

	"google.golang.org/protobuf/proto"
)

var mu sync.Mutex

type Set struct {
	id      int
	values  map[int]struct{}
	updates []string
}

func NewSet(id int) *Set {
	return &Set{id: id, values: make(map[int]struct{})}
}

func (s *Set) Id() int {
	return s.id
}

func (s *Set) Values() map[int]struct{} {
	return s.values
}

func (s *Set) Contain(value int) bool {
	_, c := s.values[value]
	return c
}

func (s *Set) SortedValues() []int {
	keys := make([]int, 0, len(s.values))
	for key := range s.values {
		keys = append(keys, key)
	}

	sort.Ints(keys)

	return keys
}

func (s *Set) Add(value int) {
	mu.Lock()
	if !s.Contain(value) {
		s.values[value] = struct{}{}
		cur_update := "Add:" + strconv.Itoa(value)
		s.updates = append(s.updates, cur_update)
	}
	mu.Unlock()
}

func (s *Set) Remove(value int) {
	mu.Lock()
	if s.Contain(value) {
		delete(s.values, value)
		cur_update := "Remove:" + strconv.Itoa(value)
		s.updates = append(s.updates, cur_update)
	}
	mu.Unlock()
}

func (s *Set) SetRemoteVal(rid int, opt_name string, value int) {
	if opt_name == "Add" {
		s.Add(value)
	} else if opt_name == "Remove" {
		s.Remove(value)
	}
}

func (s *Set) Union(o *Set) {
	mu.Lock()
	m := make(map[int]bool)

	for item := range s.values {
		m[item] = true
	}

	for item := range o.values {
		if _, ok := m[item]; !ok {
			s.values[item] = struct{}{}
		}
	}
	mu.Unlock()
}

func (s *Set) Merge(rid int, r_updates []string) {
	fmt.Println("Starting to merge req from replica_", rid)

	if len(r_updates) > 0 {
		for i := 0; i < len(r_updates); i++ {
			reqs := strings.Split(r_updates[i], ":")
			r_opt := reqs[0]
			r_value, _ := strconv.Atoi(reqs[1])
			s.SetRemoteVal(rid, r_opt, r_value)
		}
	}

	fmt.Print("Merged: ")
	s.Print()
}

func (s *Set) Print() {
	fmt.Print("Set:", s.id, " ")
	fmt.Println(s.SortedValues())
}

// func (s *Set) ToMarshal() []byte {
// 	data := append([]string{strconv.Itoa(s.Id())}, s.updates...)
// 	jsonData, err := json.Marshal(data)
// 	if err != nil {
// 		fmt.Println("Error while marshaling updates")
// 		return nil
// 	}
// 	s.updates = []string{}
// 	return jsonData
// }

func (s *Set) ToMarshal() []byte {

	msg := &set.SyncMsg{
		Id:      int32(s.Id()),
		Updates: []string{},
	}

	msg.Updates = append(msg.Updates, s.updates...)
	s.updates = []string{}

	serialized, err := proto.Marshal(msg)
	if err != nil {
		return nil
	}
	return serialized
}

// func FromMarshalData(bytes []byte) (int, []string) {
// 	var remote_updates []string
// 	err := json.Unmarshal(bytes, &remote_updates)

// 	if err != nil {
// 		fmt.Println("Error while unmarshaling ", err)
// 		return -1, nil
// 	}

// 	rid, _ := strconv.Atoi(remote_updates[0])
// 	if len(remote_updates) == 1 {
// 		return rid, nil
// 	}
// 	r_updates := remote_updates[1:]
// 	return rid, r_updates
// }

func FromMarshalData(bytes []byte) (int, []string) {
	var msg set.SyncMsg
	err := proto.Unmarshal(bytes, &msg)
	if err != nil {
		fmt.Println("unmarshalling error")
		return -1, nil
	}

	// fmt.Println("id: ", msg.GetId())
	// fmt.Println("Updates: ", msg.GetUpdates())

	return int(msg.GetId()), msg.GetUpdates()
}
