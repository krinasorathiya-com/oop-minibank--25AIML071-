
package service;

import model.Account;
import exception.InsufficientFundsException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankServiceTest {

    private BankService bankService;

    @BeforeEach
    void setUp() {
        Repository<Account> repository = new Repository<>();
        bankService = new BankService(repository);
    }

    @Test
    void depositShouldIncreaseBalance() throws Exception {
        Account account = bankService.openAccount(
                "Test Customer", "SAVINGS", 5000
        );

        bankService.deposit(account.getAccountNumber(), 500);

        assertEquals(5500, account.getBalance());
    }

    @Test
    void withdrawalOverAvailableBalanceShouldThrow() throws Exception {
        Account account = bankService.openAccount(
                "Test Customer", "SAVINGS", 5000
        );

        boolean exceptionThrown = false;
        try {
            bankService.withdraw(account.getAccountNumber(), 100000);
        } catch (InsufficientFundsException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown, "Expected insufficient funds exception");
    }

    @Test
    void concurrentDepositsShouldKeepBalanceCorrect() throws Exception {
        Account account = bankService.openAccount(
                "Concurrent Customer", "SAVINGS", 1000
        );

        int threadCount = 10;
        int depositsPerThread = 100;
        long amountPerDeposit = 10;

        ExecutorService executor =
                Executors.newFixedThreadPool(threadCount);

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        try {
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        start.await();

                        for (int j = 0; j < depositsPerThread; j++) {
                            bankService.deposit(
                                    account.getAccountNumber(),
                                    amountPerDeposit
                            );
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        done.countDown();
                    }
                });
            }

            start.countDown();

            assertTrue(
                    done.await(10, TimeUnit.SECONDS),
                    "Deposits did not finish in time"
            );

            assertEquals(11000, account.getBalance());

        } finally {
            executor.shutdownNow();
        }
    }
}
