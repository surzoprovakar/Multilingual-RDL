package main

import (
	"bufio"
	"fmt"
	"net"
	"os"
	"time"

	logger "counter/Plugin"

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
}

func execute(bytes []byte) {

	var log logger.LogMsg
	err := proto.Unmarshal(bytes, &log)

	if err != nil {
		fmt.Errorf("error in unmarshalling, ", err)
	}

	id, logMsg := int(log.GetId()), log.GetLogs()

	if logMsg == "create" {
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
