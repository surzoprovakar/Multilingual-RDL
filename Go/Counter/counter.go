package main

import (
	counter "counter/SyncMsg"
	"fmt"
	"strconv"
	"strings"
	"sync"

	"google.golang.org/protobuf/proto"
)

var mu sync.Mutex
var pm *PluginManager

type CounterIface interface {
	Inc()
	Dec()
	Value() int
}

type Counter struct {
	id      int
	value   int
	updates []string
}

func NewCounter(id int) *Counter {
	pm.notify(id, "create")
	return &Counter{id: id, value: 0}
}

func (c *Counter) SetVal(new_val int, opt_name string) {
	c.value = new_val
	c.updates = append(c.updates, opt_name)
}

func (c *Counter) SetRemoteVal(rid int, opt_name string) {
	if opt_name == "Inc" {
		c.value += 1
	} else if opt_name == "Dec" {
		c.value -= 1
	}

}

func (c *Counter) Inc() {
	mu.Lock()
	new_val := c.value + 1
	c.SetVal(new_val, "Inc")
	mu.Unlock()

	// logging
	logMsg := fmt.Sprintf("Local Inc, Updated Value is %d", c.Value())
	pm.notify(counterReplica.Id(), logMsg)
}

func (c *Counter) Dec() {
	mu.Lock()
	new_val := c.value - 1
	c.SetVal(new_val, "Dec")
	mu.Unlock()

	// logging
	logMsg := fmt.Sprintf("Local Dec, Updated Value is %d", c.Value())
	pm.notify(counterReplica.Id(), logMsg)
}

func (c *Counter) Id() int {
	return c.id
}

func (c *Counter) Value() int {
	return c.value
}

func (c *Counter) Merge(rid int, r_updates []string) {
	// mu.Lock()
	// res := fmt.Sprintf("%s%d:%d", "Counter_", rid, rval)
	if c.id != rid {
		fmt.Println("Starting to merge req from replica_", rid)
		if len(r_updates) > 0 {
			for i := 0; i < len(r_updates); i++ {
				c.SetRemoteVal(rid, r_updates[i])
			}
		}
		fmt.Println("Merged " + c.Print())
		// mu.Unlock()

		// logging
		logMsg := fmt.Sprintf("Synchronizing with Replica %d, Updated Value is %d", rid, c.Value())
		pm.notify(c.Id(), logMsg)
	} else {
		fmt.Println("Modifying current state due to plugin activity")
		if len(r_updates) > 0 {
			for i := 0; i < len(r_updates); i++ {
				fmt.Println("plug in action is: ", r_updates[i])
				if strings.Contains(r_updates[i], "Rev") {
					tasks := strings.Split(r_updates[i], "_")
					times, _ := strconv.Atoi(tasks[1])
					opt := tasks[2]
					for j := 0; j < times; j++ {
						c.SetRemoteVal(rid, opt)
						c.updates = append(c.updates, opt)
					}

				} else {
					c.updates = append(c.updates, r_updates[i])
					c.SetRemoteVal(rid, r_updates[i])
				}
			}
		}
		// logging
		logMsg := fmt.Sprintf("Plugin action, Updated Value is %d", c.Value())
		pm.notify(c.Id(), logMsg)
	}
}

func (c *Counter) Print() string {
	res := fmt.Sprintf("%s%d:%d", "Counter_", c.Id(), c.Value())
	return res
}

// func (c *Counter) ToMarshal() []byte {
// 	data := append([]string{strconv.Itoa(c.Id())}, c.updates...)
// 	jsonData, err := json.Marshal(data)
// 	if err != nil {
// 		fmt.Println("Error while marshaling updates")
// 		return nil
// 	}
// 	c.updates = []string{}
// 	return jsonData
// }

func (c *Counter) ToMarshal() []byte {

	msg := &counter.SyncMsg{
		Id:      int32(c.Id()),
		Updates: []string{},
	}

	msg.Updates = append(msg.Updates, c.updates...)
	c.updates = []string{}

	serialized, err := proto.Marshal(msg)
	if err != nil {
		return nil
	}

	// logging
	logMsg := fmt.Sprintf("BroadCasting current state")
	pm.notify(c.Id(), logMsg)

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
	var msg counter.SyncMsg
	err := proto.Unmarshal(bytes, &msg)
	if err != nil {
		fmt.Println("unmarshalling error")
		return -1, nil
	}

	// fmt.Println("id: ", msg.GetId())
	// fmt.Println("Updates: ", msg.GetUpdates())

	return int(msg.GetId()), msg.GetUpdates()
}

func (c *Counter) Call_Plugin(id int, msg string) {
	// to enable notifying Plugin Manager from Application code
	pm.notify(id, msg)
}
