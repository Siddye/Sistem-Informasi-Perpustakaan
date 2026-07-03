package exception;

/**
 * Checked exception - dilempar ketika buku yang dicari tidak ditemukan
 * atau tidak tersedia untuk dipinjam.
 */
public class BukuTidakTersediaException extends Exception {
    public BukuTidakTersediaException(String message) {
        super(message);
    }
}
