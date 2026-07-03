package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Buku;
import util.DBConnection;
import util.LoggerUtil;

/**
 * Data Access Object untuk tabel 'buku'.
 * Semua query menggunakan PreparedStatement (mencegah SQL Injection)
 * dan try-with-resources (auto-close koneksi/statement).
 */
public class BukuDAO {

    public void insert(Buku buku) throws SQLException {
        String sql = "INSERT INTO buku (kode_buku, judul, pengarang, penerbit, tahun_terbit, stok) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, buku.getKodeBuku());
            ps.setString(2, buku.getJudul());
            ps.setString(3, buku.getPengarang());
            ps.setString(4, buku.getPenerbit());
            ps.setInt(5, buku.getTahunTerbit());
            ps.setInt(6, buku.getStok());
            ps.executeUpdate();
            LoggerUtil.log("Tambah buku baru: " + buku.getKodeBuku() + " - " + buku.getJudul());
        }
    }

    public void update(Buku buku) throws SQLException {
        String sql = "UPDATE buku SET judul=?, pengarang=?, penerbit=?, tahun_terbit=?, stok=? WHERE kode_buku=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, buku.getJudul());
            ps.setString(2, buku.getPengarang());
            ps.setString(3, buku.getPenerbit());
            ps.setInt(4, buku.getTahunTerbit());
            ps.setInt(5, buku.getStok());
            ps.setString(6, buku.getKodeBuku());
            ps.executeUpdate();
            LoggerUtil.log("Update buku: " + buku.getKodeBuku());
        }
    }

    public void delete(String kodeBuku) throws SQLException {
        String sql = "DELETE FROM buku WHERE kode_buku=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kodeBuku);
            ps.executeUpdate();
            LoggerUtil.log("Hapus buku: " + kodeBuku);
        }
    }

    public Buku findByKode(String kodeBuku) throws SQLException {
        String sql = "SELECT * FROM buku WHERE kode_buku=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kodeBuku);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<Buku> findAll() throws SQLException {
        List<Buku> list = new ArrayList<>();
        String sql = "SELECT * FROM buku ORDER BY kode_buku";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    /**
     * Pencarian buku berdasarkan judul atau pengarang (fitur search).
     */
    public List<Buku> search(String keyword) throws SQLException {
        List<Buku> list = new ArrayList<>();
        String sql = "SELECT * FROM buku WHERE judul LIKE ? OR pengarang LIKE ? ORDER BY judul";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        }
        return list;
    }

    /**
     * Update stok buku (dipakai saat proses pinjam/kembali).
     */
    public void updateStok(String kodeBuku, int stokBaru) throws SQLException {
        String sql = "UPDATE buku SET stok=? WHERE kode_buku=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stokBaru);
            ps.setString(2, kodeBuku);
            ps.executeUpdate();
        }
    }

    private Buku mapResultSet(ResultSet rs) throws SQLException {
        return new Buku(
                rs.getString("kode_buku"),
                rs.getString("judul"),
                rs.getString("pengarang"),
                rs.getString("penerbit"),
                rs.getInt("tahun_terbit"),
                rs.getInt("stok")
        );
    }
}
