
public class ConsoleLogger implements Runnable {

    private NumberConsumer consumer;
    private long lastTotal      = 0;
    private long lastDuplicates = 0;

    public ConsoleLogger(NumberConsumer consumer) {
        this.consumer = consumer;
    }

    public void run() {
        SynchronizedCounter counts = consumer.counts.getCopy();

        System.out.println("received "+(counts.total - lastTotal)+" numbers, "+(counts.duplicates - lastDuplicates)+" duplicates");

        lastDuplicates = counts.duplicates;
        lastTotal      = counts.total;
    }

}
