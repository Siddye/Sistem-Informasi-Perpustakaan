package exception;

/**
 * Unchecked exception - dilempar ketika data input tidak valid
 * (contoh: format email salah, ID duplikat, field kosong).
 */
public class DataTidakValidException extends RuntimeException {
    public DataTidakValidException(String message) {
        super(message);
    }
}
