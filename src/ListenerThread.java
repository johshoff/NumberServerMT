import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ListenerThread extends Thread {
    static final int MAX_CLIENTS = 5;

    public List<ClientConnection> clients;

    private NumberConsumer consumer;
    private ServerSocket   server;

    public ListenerThread(int port, NumberConsumer consumer) throws IOException
    {
        this.consumer = consumer;
        clients = Collections.synchronizedList(new ArrayList<ClientConnection>());
        server = new ServerSocket(port);
    }

    public void run() {
        try {
            while (true) {
                Socket client = server.accept();

                if (clients.size() >= MAX_CLIENTS)
                {
                    client.close();
                    continue;
                }

                ClientConnection clientThread = new ClientConnection(client, consumer, this);
                clients.add(clientThread);
                clientThread.start();
            }
        } catch (SocketException e) {
            // probably caused by the main thread calling end()
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // take the consumer down with us, so the program will end
            // (during normal operation, the consumer is already closing)
            consumer.interrupt();

            for (ClientConnection client : clients)
                client.end();
        }
    }

    /*
     * Close the listening socket, which causes the thread to end
     */
    public void end()  {
        try {
            server.close();
        } catch (IOException e) {
            // If this happens, it will happen in the accept call as
            // well [citation needed]. The end result will still be
            // closing the thread.
        }
    }
}
