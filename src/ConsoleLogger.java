
public class ConsoleLogger implements Runnable {

    static final int REPORT_INTERVAL_MS = 10*1000;

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
