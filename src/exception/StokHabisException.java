package exception;

/**
 * Unchecked exception - dilempar ketika stok buku habis (stok = 0)
 * saat proses peminjaman.
 */
public class StokHabisException extends RuntimeException {
    public StokHabisException(String message) {
        super(message);
    }
}
