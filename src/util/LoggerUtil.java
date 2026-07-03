package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class untuk mencatat setiap aktivitas/transaksi ke file log.txt
 * lengkap dengan timestamp (sesuai fitur Dashboard & Logging).
 */
public class LoggerUtil {

    private static final String LOG_FILE = "log.txt";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private LoggerUtil() {
    }

    public static synchronized void log(String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String line = "[" + timestamp + "] " + message;

        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(line);
        } catch (IOException e) {
            System.err.println("Gagal menulis log: " + e.getMessage());
        }

        // Tampilkan juga di console untuk debugging
        System.out.println(line);
    }

    public static void logError(String message, Exception e) {
        log("ERROR - " + message + " | Detail: " + e.getMessage());
    }
}
