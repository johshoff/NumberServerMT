
public class ConsoleLogger extends Thread {

    static final int REPORT_INTERVAL_MS = 10*1000;

    private NumberConsumer consumer;

    public ConsoleLogger(NumberConsumer consumer) {
        this.consumer = consumer;
    }

    public void run() {
        long lastTotal      = 0;
        long lastDuplicates = 0;

        try {
            while (true) {
                Thread.sleep(REPORT_INTERVAL_MS);
                SynchronizedCounter counts = consumer.counts.getCopy();

                System.out.println("received "+(counts.total - lastTotal)+" numbers, "+(counts.duplicates - lastDuplicates)+" duplicates");

                lastDuplicates = counts.duplicates;
                lastTotal      = counts.total;
            }
        }
        catch (InterruptedException e) {
        }
    }

}
