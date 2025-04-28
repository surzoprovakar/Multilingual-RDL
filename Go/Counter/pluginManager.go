package main

import (
	logger "counter/Plugin"
	"fmt"
	"net"

	"google.golang.org/protobuf/proto"
)

type PluginManager struct {
}

func NewPluginManager() *PluginManager {
	return &PluginManager{}
}

func propagateToLogger(message []byte) {
	conn, err := net.Dial("tcp", "localhost:8080")
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

func (pm *PluginManager) notify(id int, msg string) {
	log := &logger.LogMsg{
		Id:   int32(id),
		Logs: string(msg),
	}
	serialized, err := proto.Marshal(log)
	if err != nil {
		fmt.Errorf("error in marshalling, ", err)
	}

	serialized = append(serialized, 0x00)
	propagateToLogger(serialized)
}
