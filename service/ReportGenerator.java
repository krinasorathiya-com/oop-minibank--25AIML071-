
package src.test.java.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;

public class ReportGenerator {

    public static void generate(Path logDirectory, Path reportFile)
            throws IOException {

        long totalDeposits = 0;
        long totalWithdrawals = 0;

        StringBuilder report = new StringBuilder();

        report.append("===== TRANSACTION REPORT =====\n");

        if (Files.exists(logDirectory)
                && Files.isDirectory(logDirectory)) {

            try (DirectoryStream<Path> stream =
                    Files.newDirectoryStream(logDirectory)) {

                for (Path file : stream) {

                    if (!Files.isRegularFile(file)
                            || !file.getFileName().toString()
                                    .toLowerCase().endsWith(".log")) {
                        continue;
                    }

                    report.append("\nFile: ")
                          .append(file.getFileName())
                          .append("\n");

                    BasicFileAttributes attributes =
                            Files.readAttributes(
                                    file,
                                    BasicFileAttributes.class);

                    report.append("Size: ")
                          .append(attributes.size())
                          .append(" bytes\n");

                    report.append("Last Modified: ")
                          .append(Instant.ofEpochMilli(
                                  attributes.lastModifiedTime()
                                      .toMillis()))
                          .append("\n");

                    try (BufferedReader reader =
                            Files.newBufferedReader(file)) {

                        String line;

                        while ((line = reader.readLine()) != null) {

                            String[] parts = line.trim().split("\\s+");

                            if (parts.length < 3) {
                                continue;
                            }

                            try {
                                long amount =
                                        Long.parseLong(parts[2]);

                                if (parts[0].equalsIgnoreCase("DEPOSIT")) {
                                    totalDeposits += amount;
                                } else if (
                                        parts[0].equalsIgnoreCase("WITHDRAW")) {
                                    totalWithdrawals += amount;
                                }
                            } catch (NumberFormatException e) {
                                // Ignore invalid transaction amounts.
                            }
                        }
                    }
                }
            }
        }

        report.append("\nTotal Deposits: ")
              .append(totalDeposits)
              .append("\n");

        report.append("Total Withdrawals: ")
              .append(totalWithdrawals)
              .append("\n");

        report.append("Net Transaction Amount: ")
              .append(totalDeposits - totalWithdrawals)
              .append("\n");

        report.append("==============================\n");

        Path parent = reportFile.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                reportFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {

            writer.write(report.toString());
        }

        System.out.println(report);
    }
}
