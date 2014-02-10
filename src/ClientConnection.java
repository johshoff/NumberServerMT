import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class ClientConnection implements Runnable {

    private Socket         socket;
    private BufferedReader inputStream;
    private NumberConsumer consumer;

    public ClientConnection(Socket socket, NumberConsumer consumer) throws IOException {
        this.consumer = consumer;
        this.socket   = socket;

        inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void run() {
        try {
            while (true)
            {
                String line = inputStream.readLine();

                if (line == null)
                    break;

                if (line.equals("terminate"))
                {
                    consumer.interrupt();
                    break;
                }

                if (line.length() != 9) // illegal input
                    break;

                int number = Integer.parseInt(line);

                if (number <= 0) // illegal input
                    break;

                consumer.newNumber(number);
            }
        } catch (IOException e) {
            // probably caused by the main thread calling end()
        } catch (NumberFormatException e) {
            // illegal input
        } catch (InterruptedException e) {
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // failing the close is acceptable
            }
        }
    }

    /*
     * Make the thread stop running
     */
    public void end() {
        try {
            socket.close();
        } catch (IOException e) {
            // acceptable, for the same reason as in ListenerThread.end()
        }
        Thread.currentThread().interrupt();
    }
}
