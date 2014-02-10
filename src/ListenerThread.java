import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ListenerThread extends Thread {
    static final int MAX_CLIENTS = 5;

    private NumberConsumer  consumer;
    private ServerSocket    server;
    private ExecutorService threadPool;

    public ListenerThread(int port, NumberConsumer consumer) throws IOException
    {
        this.consumer = consumer;
        server = new ServerSocket(port);
        threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
    }

    public void run() {
        try {
            while (true) {
                Socket client = server.accept();
                threadPool.execute(new ClientConnection(client, consumer));
            }
        } catch (SocketException e) {
            // for the purposes of this exercise, this is caused by the
            // main thread calling end()
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // take the consumer down with us, so the program will end
            // (during normal operation, the consumer is already closing)
            consumer.interrupt();

            threadPool.shutdownNow();
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
