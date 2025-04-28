// Import the generated code
const { ExampleMessage } = require('./proto/example_pb.js')

// Function to serialize a message
function serializeExampleMessage() {
    // Create a new message
    let message = new ExampleMessage()

    // Set the fields
    message.setText("Hello, Protocol Buffers!")
    message.setNumber(42)
    message.setItemsList(["item1", "item2", "item3"])

    // Serialize to binary
    let bytes = message.serializeBinary()
    console.log("Serialized bytes:", bytes)

    return bytes
}

// Function to deserialize a message
function deserializeExampleMessage(bytes) {
    // Deserialize the binary data into a new message object
    let receivedMessage = ExampleMessage.deserializeBinary(bytes)

    // Access the fields
    console.log("Text:", receivedMessage.getText())
    console.log("Number:", receivedMessage.getNumber())
    console.log("Items:", receivedMessage.getItemsList())
}

// Main function to demonstrate serialization and deserialization
function main() {
    // Serialize the message
    let bytes = serializeExampleMessage()

    // Deserialize the message
    deserializeExampleMessage(bytes)
}

// Run the main function
main()
