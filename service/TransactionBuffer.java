
package service;

public class TransactionBuffer {

    private final int[] buffer;
    private int count = 0;
    private int in = 0;
    private int out = 0;

    public TransactionBuffer(int capacity) {
        buffer = new int[capacity];
    }

    public synchronized void put(int transaction)
            throws InterruptedException {

        while (count == buffer.length) {
            wait();
        }

        buffer[in] = transaction;
        in = (in + 1) % buffer.length;
        count++;

        System.out.println("Produced transaction: " + transaction);

        notify();
    }

    public synchronized int take()
            throws InterruptedException {

        while (count == 0) {
            wait();
        }

        int transaction = buffer[out];
        out = (out + 1) % buffer.length;
        count--;

        notify();

        return transaction;
    }
}
