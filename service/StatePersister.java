
package src.test.java.service;

import model.Account;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StatePersister {

    public static void save(Account[] accounts, Path file)
            throws IOException {

        // Create the parent folder if it does not exist
        Path parent = file.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        // Save accounts to the file
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(file.toFile()))) {

            out.writeObject(accounts);
        }
    }

    public static Account[] load(Path file)
            throws IOException, ClassNotFoundException {

        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(file.toFile()))) {

            return (Account[]) in.readObject();
        }
    }
}
