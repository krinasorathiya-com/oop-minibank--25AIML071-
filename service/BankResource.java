package service;

public class BankResource implements AutoCloseable {

    @Override
    public void close() {
        System.out.println("Resource closed.");
    }
}