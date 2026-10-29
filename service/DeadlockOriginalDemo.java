
package src.test.java.service;

public class DeadlockOriginalDemo {

    private static final Object lockA = new Object();
    private static final Object lockB = new Object();

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            synchronized (lockA) {
                System.out.println("Thread 1 locked A");

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                System.out.println("Thread 1 waiting for B");

                synchronized (lockB) {
                    System.out.println("Thread 1 locked B");
                }
            }
        }, "Thread-1");

        Thread t2 = new Thread(() -> {
            synchronized (lockB) {
                System.out.println("Thread 2 locked B");

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                System.out.println("Thread 2 waiting for A");

                synchronized (lockA) {
                    System.out.println("Thread 2 locked A");
                }
            }
        }, "Thread-2");

        t1.start();
        t2.start();
    }
}
