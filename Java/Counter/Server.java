import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class Server {

    private static List<String> hosts;
    private static Counter counterReplica;
    private static List<Socket> conns;
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static void DoActions(List<String> actions) throws InterruptedException, IOException {
        Thread.sleep(5000); // sleep for 5 secs
        System.out.println("Starting to do_actions");
        for (String action : actions) {
            switch (action) {
                case "Inc":
                    counterReplica.Inc();
                    System.out.println(counterReplica.Print());
                    break;
                case "Dec":
                    counterReplica.Dec();
                    System.out.println(counterReplica.Print());
                    break;
                case "Broadcast":
                    System.out.println("processing Broadcast");
                    if (conns == null) { // establish connections on first broadcast
                        conns = Client.EstablishConnections(hosts);
                    }
                    System.out.println("About to broadcast Counter" + counterReplica.Print());
                    Client.Broadcast(conns, counterReplica.ToMarshal());
                    break;
                default: // assume it is delay
                    int number = Integer.parseInt(action);
                    Thread.sleep(number * 1000);
                    break;
            }
        }
    }

    public static void HandleConnection(Socket socket) {
        executor.execute(() -> {
            try {
                InputStream input = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                byte b;
                ByteArrayOutputStream messageBuffer = new ByteArrayOutputStream();

                while ((bytesRead = input.read(buffer)) != -1) {
                    for (int i = 0; i < bytesRead; i++) {
                        b = buffer[i];
                        if (b == 0x00) { // delimiter
                            byte[] message = messageBuffer.toByteArray();
                            Pair<Integer, List<String>> req = Counter.FromMarshalData(message);
                            counterReplica.Merge(req.first, req.second);
                            messageBuffer.reset(); // reset buffer for next message
                        } else {
                            messageBuffer.write(b);
                        }
                    }
                }

            } catch (IOException e) {
                System.out.println("Client left.");
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 4) {
            System.out.println("Usage: counter_id ip_address crdt_socket_server Replicas'_Addresses.txt Actions.txt");
            System.exit(1);
        }

        int id = Integer.parseInt(args[0]);
        counterReplica = new Counter(id);
        String ipAddress = args[1];
        hosts = FileReader.ReadFile(args[2]);
        List<String> actions = FileReader.ReadFile(args[3]);

        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(ipAddress.split(":")[1]));
        System.out.println("Starting tcp server on " + ipAddress);

        executor.execute(() -> {
            try {
                DoActions(actions);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        });

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Client connected: " + socket.getRemoteSocketAddress());
            HandleConnection(socket);
        }
    }
}
