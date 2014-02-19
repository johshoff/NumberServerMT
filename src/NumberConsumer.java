import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Queue;

/**
 * Get numbers from ClientConnections, check for uniqueness, and write to disk.
 */
public class NumberConsumer extends Thread {

    public SynchronizedCounter counts = new SynchronizedCounter(0, 0);

    private Queue<Integer> unhandledNumbers = new ArrayDeque<Integer>(65536);
    private PrintWriter    writer;
    private BitSet         valuesSeen;

    public NumberConsumer(String filename) throws IOException {
        writer     = new PrintWriter(filename);
        valuesSeen = new BitSet();

        // make sure we have enough memory right off the bat
        try {
            valuesSeen.set  (999999999);
            valuesSeen.clear(999999999);
        } catch (OutOfMemoryError e) {
            System.out.println("Please run with -Xmx256m to have a heap big enough for a billion bits.\n\n");
            throw e;
        }
    }

    /**
     * @param number to be processed later
     */
    public synchronized void newNumber(int number) throws InterruptedException {
        // the queue shouldn't get very big with only five clients,
        // but just in case, I've added an upper limit.
        while (unhandledNumbers.size() > 200000) {
            System.out.println("Queue full");
            wait();
        }

        unhandledNumbers.offer(number);
        notifyAll();
    }

    public void run() {
        ArrayList<Integer> buffer = new ArrayList<Integer>(1024);

        try {
            while (true) {
                // put all unhandled numbers in a local buffer, so the
                // other threads can keep on processing.
                synchronized (this) {
                    while (unhandledNumbers.size() == 0) {
                        wait();
                    }

                    while (unhandledNumbers.size() > 0) {
                        buffer.add(unhandledNumbers.remove());
                    }

                    notifyAll();
                }

                int duplicates = 0;
                for (int number : buffer)
                {
                    if (valuesSeen.get(number)) {
                        duplicates++;
                    } else {
                        writer.println(number);
                        valuesSeen.set(number);
                    }
                }
                //writer.flush();

                counts.increment(buffer.size(), duplicates);

                buffer.clear();
            }
        } catch (InterruptedException e) {}
        finally {
            writer.close();
        }
    }
}
