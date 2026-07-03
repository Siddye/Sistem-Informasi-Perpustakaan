package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Anggota;
import util.DBConnection;
import util.LoggerUtil;

/**
 * Data Access Object untuk tabel 'anggota'.
 */
public class AnggotaDAO {

    public void insert(Anggota anggota) throws SQLException {
        String sql = "INSERT INTO anggota (id_anggota, nama, alamat, no_telp, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, anggota.getIdAnggota());
            ps.setString(2, anggota.getNama());
            ps.setString(3, anggota.getAlamat());
            ps.setString(4, anggota.getNoTelp());
            ps.setString(5, anggota.getEmail());
            ps.executeUpdate();
            LoggerUtil.log("Tambah anggota baru: " + anggota.getIdAnggota() + " - " + anggota.getNama());
        }
    }

    public void update(Anggota anggota) throws SQLException {
        String sql = "UPDATE anggota SET nama=?, alamat=?, no_telp=?, email=? WHERE id_anggota=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, anggota.getNama());
            ps.setString(2, anggota.getAlamat());
            ps.setString(3, anggota.getNoTelp());
            ps.setString(4, anggota.getEmail());
            ps.setString(5, anggota.getIdAnggota());
            ps.executeUpdate();
            LoggerUtil.log("Update anggota: " + anggota.getIdAnggota());
        }
    }

    public void delete(String idAnggota) throws SQLException {
        String sql = "DELETE FROM anggota WHERE id_anggota=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idAnggota);
            ps.executeUpdate();
            LoggerUtil.log("Hapus anggota: " + idAnggota);
        }
    }

    public Anggota findById(String idAnggota) throws SQLException {
        String sql = "SELECT * FROM anggota WHERE id_anggota=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idAnggota);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<Anggota> findAll() throws SQLException {
        List<Anggota> list = new ArrayList<>();
        String sql = "SELECT * FROM anggota ORDER BY id_anggota";
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
     * Pencarian anggota berdasarkan nama atau ID (fitur search).
     */
    public List<Anggota> search(String keyword) throws SQLException {
        List<Anggota> list = new ArrayList<>();
        String sql = "SELECT * FROM anggota WHERE nama LIKE ? OR id_anggota LIKE ? ORDER BY nama";
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
     * Cek keunikan ID anggota (validasi wajib sesuai panduan).
     */
    public boolean isIdExist(String idAnggota) throws SQLException {
        return findById(idAnggota) != null;
    }

    private Anggota mapResultSet(ResultSet rs) throws SQLException {
        Anggota a = new Anggota();
        a.setIdAnggota(rs.getString("id_anggota"));
        a.setNama(rs.getString("nama"));
        a.setAlamat(rs.getString("alamat"));
        a.setNoTelp(rs.getString("no_telp"));
        a.setEmail(rs.getString("email"));
        java.sql.Date tgl = rs.getDate("tgl_daftar");
        if (tgl != null) {
            a.setTglDaftar(LocalDate.parse(tgl.toString()));
        }
        return a;
    }
}
