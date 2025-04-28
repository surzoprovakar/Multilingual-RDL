import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import logger.Logger.LogMsg;

import com.google.protobuf.InvalidProtocolBufferException;

public class PluginManager {

    public void notify(int id, String msg) {
        LogMsg logMessage = LogMsg.newBuilder()
                .setId(id)
                .setLogs(msg)
                .build();

        byte[] serialized = logMessage.toByteArray();

        byte[] messageWithTerminator = new byte[serialized.length + 1];
        System.arraycopy(serialized, 0, messageWithTerminator, 0, serialized.length);
        messageWithTerminator[serialized.length] = 0x00;

        propagateToLogger(messageWithTerminator);
    }

    private void propagateToLogger(byte[] message) {
        try (Socket client = new Socket("localhost", 8080);
                OutputStream out = client.getOutputStream()) {
            out.write(message);
            out.flush();
            // System.out.println("Message sent to logger");

        } catch (IOException e) {
            System.out.println("Failed to connect or send message to logger: " + e.getMessage());
        }
    }
}
