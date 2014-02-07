import java.io.IOException;

public class NumberServerMT {
    public static void main(String[] args) throws IOException, InterruptedException {
        NumberConsumer consumer = new NumberConsumer("numbers.log");
        consumer.start();

        ConsoleLogger consoleLogger = new ConsoleLogger(consumer);
        consoleLogger.start();

        ListenerThread listener = new ListenerThread(4000, consumer);
        listener.start();

        // run until someone interrupts the consumer thread
        consumer.join();

        consoleLogger.interrupt();
        listener.end();
    }
}
