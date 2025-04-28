package main

import (
	"bufio"
	logger "counter/Plugin"
	"fmt"
	"net"
	"os"
	"regexp"
	"strconv"
	"strings"
	"time"

	"google.golang.org/protobuf/proto"
)

type LamportClock struct {
	lClock int
}

func NewLamportClock() *LamportClock {
	return &LamportClock{lClock: 0}
}

func (lc *LamportClock) Increment() {
	lc.lClock++
}

func (lc *LamportClock) GetTimestamp() (int, time.Time) {
	return lc.lClock, time.Now()
}

var lc *LamportClock

type ReplicaState struct {
	VersionMapState map[int]int
	LastVersion     int
}

var replicaStates = make(map[int]*ReplicaState)

func create_log(rId int) {
	logFile := fmt.Sprintf("Replica_%d.log", rId)

	if _, err := os.Stat(logFile); err == nil {
		fmt.Println("Log File Already Exists")
	}

	file, err := os.Create(logFile)
	if err != nil {
		fmt.Errorf("failed to create file: %w", err)
	}

	defer file.Close()

	lc = NewLamportClock()

	replicaStates[rId] = &ReplicaState{
		VersionMapState: make(map[int]int),
		LastVersion:     0,
	}
	// init version
	replicaStates[rId].VersionMapState[0] = 0
	replicaStates[rId].LastVersion++
}

func persist(rId int, msg string) {
	logFile := fmt.Sprintf("Replica_%d.log", rId)

	// append mode
	file, err := os.OpenFile(logFile, os.O_APPEND|os.O_WRONLY|os.O_CREATE, 0644)
	if err != nil {
		fmt.Errorf("failed to open file: %w", err)
		return
	}
	defer file.Close()

	lc.Increment()
	lamportTime, physicalTime := lc.GetTimestamp()

	logEntry := fmt.Sprintf("%s, Lamport Time: %d, Physical Time: %s\n",
		msg, lamportTime, physicalTime.Format(time.RFC3339))
	if _, err := file.WriteString(logEntry); err != nil {
		fmt.Errorf("failed to log: %w", err)
	}

	if strings.Contains(msg, "Updated Value is") {
		state, _ := replicaStates[rId]
		state.VersionMapState[state.LastVersion] = extractVersionValue(msg)
		state.LastVersion++
	}
}

func execute(bytes []byte) {

	var log logger.LogMsg
	err := proto.Unmarshal(bytes, &log)

	if err != nil {
		fmt.Errorf("error in unmarshalling, ", err)
	}

	id, logMsg := int(log.GetId()), log.GetLogs()

	if strings.Contains(logMsg, "Undo") {
		tasks := strings.Split(logMsg, "_")
		task := tasks[1]
		// fmt.Println("Undo taks is ", task)
		fmt.Println("Undo request came from application")
		action := undo(task)
		// fmt.Println("counter action is ", action)
		replica_addr := ""

		counter_action := &logger.LogMsg{
			Id:   int32(id),
			Logs: string(action),
		}
		serialized, err := proto.Marshal(counter_action)
		if err != nil {
			fmt.Errorf("error in marshalling, ", err)
		}

		serialized = append(serialized, 0x00)
		if id == 1 {
			replica_addr = "localhost:8081"
		} else if id == 2 {
			replica_addr = "localhost:8082"
		} else {
			replica_addr = "localhost:8083"
		}
		sendBacktoReplica(replica_addr, serialized)
	} else if strings.Contains(logMsg, "Rev") {
		tasks := strings.Split(logMsg, "_")
		rev := tasks[1]
		// fmt.Println("Undo taks is ", task)
		fmt.Println("Rollback request came from application: ", rev)
		action := reversibility(id, rev)
		// fmt.Println(replicaStates[id])
		fmt.Println("counter action is ", action)
		replica_addr := ""

		counter_action := &logger.LogMsg{
			Id:   int32(id),
			Logs: string(action),
		}
		serialized, err := proto.Marshal(counter_action)
		if err != nil {
			fmt.Errorf("error in marshalling, ", err)
		}

		serialized = append(serialized, 0x00)
		if id == 1 {
			replica_addr = "localhost:8081"
		} else if id == 2 {
			replica_addr = "localhost:8082"
		} else {
			replica_addr = "localhost:8083"
		}
		sendBacktoReplica(replica_addr, serialized)

	} else if logMsg == "create" {
		create_log(id)
	} else {
		persist(id, logMsg)
	}
}

func main() {
	listener, err := net.Listen("tcp", "localhost:8080")
	if err != nil {
		fmt.Printf("Failed to listen on port 8080: %v\n", err)
		os.Exit(1)
	}
	defer listener.Close()

	fmt.Println("Logger server started on localhost:8080")

	for {
		conn, err := listener.Accept()
		if err != nil {
			fmt.Printf("Failed to accept connection: %v\n", err)
			continue
		}

		go keepConnection(conn)
	}
}

func keepConnection(conn net.Conn) {
	reader := bufio.NewReader(conn)
	// reqs, err := reader.ReadBytes('\n')
	var message []byte
	for {
		b, err := reader.ReadByte()
		if err != nil {
			// fmt.Println("Client left.")
			conn.Close()
			return
		}

		// Check for delimiter
		if b == 0x00 {
			break
		}

		message = append(message, b)
	}
	execute(message)
	keepConnection(conn)
}

func sendBacktoReplica(replica_addr string, message []byte) {
	conn, err := net.Dial("tcp", replica_addr)
	if err != nil {
		fmt.Printf("Failed to connect to logger: %v\n", err)
		return
	}
	defer conn.Close()

	_, err = conn.Write(message)
	if err != nil {
		fmt.Printf("Failed to send message: %v\n", err)
	}
}

func undo(task string) string {
	if task == "Inc" {
		return "Dec"
	}
	return "Inc"
}

func reversibility(id int, version string) string {
	state, _ := replicaStates[id]
	rev_version, _ := strconv.Atoi(version)
	rev_val := state.VersionMapState[rev_version]

	cur_val := state.VersionMapState[state.LastVersion-1]
	action := ""
	if cur_val == rev_val {
		fmt.Println("Rolledback version is the same")
	} else if cur_val > rev_val {
		diff := cur_val - rev_val
		action = fmt.Sprintf("Rev_%d_Dec", diff)
	} else {
		diff := rev_val - cur_val
		action = fmt.Sprintf("Rev_%d_Inc", diff)
	}
	return action
}

func extractVersionValue(s string) int {
	substring := "Updated Value is"

	// Regular expression to find digits following "Updated Value is"
	re := regexp.MustCompile(substring + ` (\d+)`)
	match := re.FindStringSubmatch(s)
	if len(match) > 1 {
		// Convert the matched number to an integer
		var value int
		fmt.Sscanf(match[1], "%d", &value)
		return value
	}

	// error value
	return 0
}
