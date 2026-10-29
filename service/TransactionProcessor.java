
package service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import model.Account;

public class TransactionProcessor {

    private final ExecutorService executor;
    private final ConcurrentHashMap<String, Account> accounts =
        new ConcurrentHashMap<>();

    public TransactionProcessor() {
        executor = Executors.newFixedThreadPool(4);
    }

    public void submit(Runnable task) {
        executor.execute(task);
    }

    public void stop() {
        executor.shutdown();

        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                System.out.println(
                        "Tasks are taking too long. Waiting for completion."
                );
                executor.shutdownNow();

                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.out.println("Some tasks did not terminate.");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            System.out.println("Processor was interrupted.");
        }
    }
}
