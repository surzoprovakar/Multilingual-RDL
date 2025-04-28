import example.Example.ExampleMessage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class javaproto {
    public static void main(String[] args) {
        try {
            // Create a new ExampleMessage
            ExampleMessage message = ExampleMessage.newBuilder()
                    .setText("Hello, Protocol Buffers!")
                    .setNumber(123)
                    .addItems("Item 1")
                    .addItems("Item 2")
                    .addItems("Item 3")
                    .build();

            // Serialize the message to a file
            try (FileOutputStream output = new FileOutputStream("example_message.bin")) {
                message.writeTo(output);
            }

            // Deserialize the message from the file
            ExampleMessage deserializedMessage;
            try (FileInputStream input = new FileInputStream("example_message.bin")) {
                deserializedMessage = ExampleMessage.parseFrom(input);
            }

            // Print the deserialized message
            System.out.println("Deserialized message: " + deserializedMessage);

            System.out.println("---------------");
            System.out.println("Text:" + deserializedMessage.getText());
            System.out.println("Number:" + deserializedMessage.getNumber());
            System.out.println("Items:" + deserializedMessage.getItemsList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}