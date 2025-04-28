package main

import (
	example "example/proto"
	"fmt"
	"log"

	"google.golang.org/protobuf/proto"
)

// MarshalExampleMessage serializes the ExampleMessage into a byte slice.
func MarshalExampleMessage(msg *example.ExampleMessage) ([]byte, error) {
	serialized, err := proto.Marshal(msg)
	if err != nil {
		return nil, fmt.Errorf("failed to marshal message: %w", err)
	}
	return serialized, nil
}

// UnmarshalExampleMessage deserializes the byte slice into an ExampleMessage.
func UnmarshalExampleMessage(data []byte) (*example.ExampleMessage, error) {
	var msg example.ExampleMessage
	err := proto.Unmarshal(data, &msg)
	if err != nil {
		return nil, fmt.Errorf("failed to unmarshal message: %w", err)
	}
	return &msg, nil
}

func main() {
	// Create an instance of ExampleMessage and set its fields
	msg := &example.ExampleMessage{
		Text:   "Hello, Protocol Buffers!",
		Number: 42,
		Items:  []string{},
	}

	newItems := []string{"item1", "item2", "item3"} // Example items
	for _, item := range newItems {
		msg.Items = append(msg.Items, item) // Append each item
	}

	// Serialize the message to a byte slice
	// serialized, err := proto.Marshal(msg)
	serialized, err := MarshalExampleMessage(msg)
	if err != nil {
		log.Fatalf("Failed to marshal message: %v", err)
	}

	fmt.Println("Serialized message:", serialized)

	// Deserialize the byte slice back into an ExampleMessage
	// var deserialized example.ExampleMessage
	// err = proto.Unmarshal(serialized, &deserialized)
	deserialized, err := UnmarshalExampleMessage(serialized)
	if err != nil {
		log.Fatalf("Failed to unmarshal message: %v", err)
	}

	// Print the deserialized message
	fmt.Println("Deserialized message:", deserialized)
	fmt.Printf("Text: %s, Number: %d\n", deserialized.GetText(), deserialized.GetNumber())
	fmt.Println("Items: ", deserialized.Items)
}
