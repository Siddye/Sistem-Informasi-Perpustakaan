package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DB_NAME = "perpustakaan_db";
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME
            + "?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // ganti sesuai password MySQL Anda

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver tidak ditemukan. Pastikan mysql-connector-j ada di classpath.");
            e.printStackTrace();
        }
    }

    private DBConnection() {
        // Prevent instantiation
    }

    /**
     * Mengembalikan koneksi baru ke database.
     * Gunakan try-with-resources saat memanggil method ini di DAO.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Untuk testing koneksi lewat console (lihat Langkah 1: Setup).
     */
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Koneksi database BERHASIL: " + conn.getCatalog());
        } catch (SQLException e) {
            System.out.println("Koneksi database GAGAL: " + e.getMessage());
        }
    }
}
