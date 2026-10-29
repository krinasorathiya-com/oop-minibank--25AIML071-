package service;

import model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BankServiceTest {

    private BankService bankService;
    private Repository<Account> repository;

    @BeforeEach
    void setUp() {
        repository = new Repository<>();
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
}