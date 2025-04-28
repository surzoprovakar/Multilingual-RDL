import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

class ReplicaState {
    Map<Integer, Integer> versionMapState;
    int lastVersion;

    public ReplicaState() {
        this.versionMapState = new HashMap<>();
        this.lastVersion = 0;
        this.versionMapState.put(0, 0);
        this.lastVersion++;
    }
}

public class JavaLogger {
    private static LamportClock lc = null;
    private static Map<Integer, ReplicaState> replicaStates = new HashMap<>();

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
        replicaStates.put(rId, new ReplicaState());
    }

    private static void persist(int rId, String msg) {
        String logFile = "Replica_" + rId + ".log";
        if (lc == null) {
            lc = new LamportClock();
        }

        lc.increment();
        LamportClock.Timestamp timestamp = lc.getTimestamp();
        String physicalTimeStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(timestamp.physicalTime);

        String logEntry = String.format("%s, Lamport Time: %d, Physical Time: %s%n", msg, timestamp.lamportTime,
                physicalTimeStr);

        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(logEntry);
        } catch (IOException e) {
            System.err.println("Failed to log: " + e.getMessage());
        }
        if (msg.contains("Updated Value is")) {
            ReplicaState state = replicaStates.get(rId);
            int value = extractVersionValue(msg);
            state.versionMapState.put(state.lastVersion, value);
            state.lastVersion++;
        }
    }

    private static void execute(byte[] bytes) {
        try {
            LogMsg logMsg = LogMsg.parseFrom(bytes);
            int id = logMsg.getId();
            String logMessage = logMsg.getLogs();

            if (logMessage.contains("Undo")) {
                String[] tasks = logMessage.split("_");
                String task = tasks[1];
                System.out.println("Undo request came from application");

                String action = undo(task);
                LogMsg counterAction = LogMsg.newBuilder()
                        .setId(id)
                        .setLogs(action)
                        .build();

                byte[] serialized = counterAction.toByteArray();

                byte[] messageWithTerminator = new byte[serialized.length + 1];
                System.arraycopy(serialized, 0, messageWithTerminator, 0, serialized.length);
                messageWithTerminator[serialized.length] = 0x00;

                String replicaAddr;
                if (id == 1) {
                    replicaAddr = "localhost:8081";
                } else if (id == 2) {
                    replicaAddr = "localhost:8082";
                } else {
                    replicaAddr = "localhost:8083";
                }

                sendBackToReplica(replicaAddr, messageWithTerminator);
            } else if (logMessage.contains("Rev")) {
                String[] tasks = logMessage.split("_");
                String rev = tasks[1];
                System.out.println("Rollback request came from application: " + rev);
                String action = reversibility(id, rev);
                System.out.println("Counter action is: " + action);
                LogMsg counterAction = LogMsg.newBuilder()
                        .setId(id)
                        .setLogs(action)
                        .build();

                byte[] serialized = counterAction.toByteArray();

                byte[] messageWithTerminator = new byte[serialized.length + 1];
                System.arraycopy(serialized, 0, messageWithTerminator, 0, serialized.length);
                messageWithTerminator[serialized.length] = 0x00;

                String replicaAddr;
                if (id == 1) {
                    replicaAddr = "localhost:8081";
                } else if (id == 2) {
                    replicaAddr = "localhost:8082";
                } else {
                    replicaAddr = "localhost:8083";
                }

                sendBackToReplica(replicaAddr, messageWithTerminator);
            }
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

    public static void sendBackToReplica(String replicaAddr, byte[] message) {
        String[] parts = replicaAddr.split(":");
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);

        try (Socket socket = new Socket(host, port)) {
            socket.getOutputStream().write(message);
        } catch (IOException e) {
            System.err.println("Failed to connect to replica: " + e.getMessage());
        }
    }

    public static String undo(String task) {
        return task.equals("Inc") ? "Dec" : "Inc";
    }

    public static int extractVersionValue(String s) {
        Pattern pattern = Pattern.compile("Updated Value is (\\d+)");
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    public static String reversibility(int id, String version) {
        ReplicaState state = replicaStates.get(id);
        int revVersion = Integer.parseInt(version);
        int revVal = state.versionMapState.getOrDefault(revVersion, 0);
        int curVal = state.versionMapState.getOrDefault(state.lastVersion - 1, 0);
        String action = "";

        if (curVal == revVal) {
            System.out.println("Rolled back version is the same");
        } else if (curVal > revVal) {
            int diff = curVal - revVal;
            action = "Rev_" + diff + "_Dec";
        } else {
            int diff = revVal - curVal;
            action = "Rev_" + diff + "_Inc";
        }

        return action;
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
