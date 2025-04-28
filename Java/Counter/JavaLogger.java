import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.protobuf.InvalidProtocolBufferException;
import logger.Logger.LogMsg;

class LamportClock {
    private int lClock;

    public LamportClock() {
        this.lClock = 0;
    }

    public void increment() {
        lClock++;
    }

    public Timestamp getTimestamp() {
        return new Timestamp(lClock, new Date());
    }

    public static class Timestamp {
        public final int lamportTime;
        public final Date physicalTime;

        public Timestamp(int lamportTime, Date physicalTime) {
            this.lamportTime = lamportTime;
            this.physicalTime = physicalTime;
        }
    }
}

public class JavaLogger {
    private static LamportClock lc = null;

    private static void createLog(int rId) {
        String logFile = "Replica_" + rId + ".log";
        Path logPath = Paths.get(logFile);

        if (Files.exists(logPath)) {
            System.out.println("Log File Already Exists");
        } else {
            try {
                Files.createFile(logPath);
            } catch (IOException e) {
                System.err.println("Failed to create log file: " + e.getMessage());
            }
        }

        lc = new LamportClock();
    }

    private static void persist(int rId, String msg) {
        String logFile = "Replica_" + rId + ".log";
        if (lc == null) {
            lc = new LamportClock();
        }

        lc.increment();
        LamportClock.Timestamp timestamp = lc.getTimestamp();
        String physicalTimeStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(timestamp.physicalTime);

        String logEntry = String.format("%s, Lamport Time: %d, Physical Time: %s%n", msg, timestamp.lamportTime, physicalTimeStr);

        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(logEntry);
        } catch (IOException e) {
            System.err.println("Failed to log: " + e.getMessage());
        }
    }

    private static void execute(byte[] bytes) {
        try {
            LogMsg logMsg = LogMsg.parseFrom(bytes);
            int id = logMsg.getId();
            String logMessage = logMsg.getLogs();

            if ("create".equals(logMessage)) {
                createLog(id);
            } else {
                persist(id, logMessage);
            }
        } catch (InvalidProtocolBufferException e) {
            System.err.println("Error in unmarshalling: " + e.getMessage());
        }
    }

    private static void keepConnection(Socket conn) {
        try (InputStream in = conn.getInputStream()) {
            ByteArrayOutputStream message = new ByteArrayOutputStream();

            int byteRead;
            while ((byteRead = in.read()) != -1) {
                if (byteRead == 0x00) {
                    execute(message.toByteArray());
                    message.reset();
                } else {
                    message.write(byteRead);
                }
            }
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (IOException e) {
                System.err.println("Failed to close connection: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Logger server started on localhost:8080");

            while (true) {
                try {
                    Socket conn = serverSocket.accept();
                    new Thread(() -> keepConnection(conn)).start();
                } catch (IOException e) {
                    System.err.println("Failed to accept connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            System.exit(1);
        }
    }
}
