
public class SynchronizedCounter {
    public long total;
    public long duplicates;

    public SynchronizedCounter(long total, long duplicates) {
        this.total      = total;
        this.duplicates = duplicates;
    }

    public synchronized void increment(int inc_total, int inc_duplicates) {
        total      += inc_total;
        duplicates += inc_duplicates;
    }

    public synchronized SynchronizedCounter getCopy() {
        return new SynchronizedCounter(total, duplicates);
    }
}
