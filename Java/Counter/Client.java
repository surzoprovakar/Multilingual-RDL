import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {

    public static List<Socket> EstablishConnections(List<String> addresses) {
        List<Socket> sockets = new ArrayList<>();

        for (String address : addresses) {
            try {
                System.out.println("Establishing connection to " + address);
                String[] parts = address.split(":");
                String host = parts[0];
                int port = Integer.parseInt(parts[1]);
                Socket socket = new Socket(host, port);
                sockets.add(socket);
            } catch (IOException e) {
                System.out.println("Error connecting: " + e.getMessage());
                System.exit(1);
            }
        }
        return sockets;
    }

    public static void Broadcast(List<Socket> sockets, byte[] content) {
        byte[] contentWithDelimiter = new byte[content.length + 1];
        System.arraycopy(content, 0, contentWithDelimiter, 0, content.length);
        contentWithDelimiter[content.length] = 0x00; // Append custom delimiter

        for (Socket socket : sockets) {
            try {
                OutputStream out = socket.getOutputStream();
                out.write(contentWithDelimiter);
                out.flush();
            } catch (IOException e) {
                System.out.println("Error writing to socket: " + e.getMessage());
                System.exit(1);
            }
        }
    }

    // public static void main(String[] args) {
    //     String[] addresses = {"localhost:8080", "localhost:8081"}; // Replace with actual addresses
    //     List<Socket> sockets = EstablishConnections(addresses);
    //     byte[] content = "Hello, World!".getBytes(); // Replace with actual content
    //     Broadcast(sockets, content);
    // }
}
