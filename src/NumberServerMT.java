import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

public class NumberServerMT {
    public static void main(String[] args) throws IOException, InterruptedException {
        NumberConsumer consumer = new NumberConsumer("numbers.log");
        consumer.start();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new ConsoleLogger(consumer), 1, 10, SECONDS);

        ListenerThread listener = new ListenerThread(4000, consumer);
        listener.start();

        // run until someone interrupts the consumer thread
        consumer.join();

        scheduler.shutdown();
        listener.end();
    }
}
