package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Peminjaman;
import util.DBConnection;
import util.LoggerUtil;

/**
 * Data Access Object untuk tabel 'peminjaman'.
 */
public class PeminjamanDAO {

    public int insert(Peminjaman p) throws SQLException {
        String sql = "INSERT INTO peminjaman (kode_buku, id_anggota, tgl_pinjam, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getKodeBuku());
            ps.setString(2, p.getIdAnggota());
            ps.setDate(3, Date.valueOf(p.getTglPinjam()));
            ps.setString(4, p.getStatus().name());
            ps.executeUpdate();

            int generatedId = -1;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    generatedId = keys.getInt(1);
                }
            }
            LoggerUtil.log("Transaksi peminjaman baru: Buku=" + p.getKodeBuku() + ", Anggota=" + p.getIdAnggota());
            return generatedId;
        }
    }

    public void updatePengembalian(int idPinjam, java.time.LocalDate tglKembali) throws SQLException {
        String sql = "UPDATE peminjaman SET tgl_kembali=?, status='KEMBALI' WHERE id_pinjam=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(tglKembali));
            ps.setInt(2, idPinjam);
            ps.executeUpdate();
            LoggerUtil.log("Pengembalian buku untuk transaksi #" + idPinjam);
        }
    }

    /**
     * Ambil semua transaksi yang masih berstatus DIPINJAM, di-JOIN dengan buku & anggota
     * agar bisa langsung ditampilkan di ComboBox/JTable pengembalian.
     */
    public List<Peminjaman> findAktif() throws SQLException {
        List<Peminjaman> list = new ArrayList<>();
        String sql = "SELECT p.*, b.judul, a.nama FROM peminjaman p " +
                "JOIN buku b ON p.kode_buku = b.kode_buku " +
                "JOIN anggota a ON p.id_anggota = a.id_anggota " +
                "WHERE p.status = 'DIPINJAM' ORDER BY p.tgl_pinjam";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetWithJoin(rs));
            }
        }
        return list;
    }

    /**
     * Riwayat semua transaksi peminjaman (fitur Riwayat Transaksi).
     */
    public List<Peminjaman> findAllRiwayat() throws SQLException {
        List<Peminjaman> list = new ArrayList<>();
        String sql = "SELECT p.*, b.judul, a.nama FROM peminjaman p " +
                "JOIN buku b ON p.kode_buku = b.kode_buku " +
                "JOIN anggota a ON p.id_anggota = a.id_anggota " +
                "ORDER BY p.tgl_pinjam DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetWithJoin(rs));
            }
        }
        return list;
    }

    public int countTotalDipinjam() throws SQLException {
        String sql = "SELECT COUNT(*) FROM peminjaman WHERE status='DIPINJAM'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private Peminjaman mapResultSetWithJoin(ResultSet rs) throws SQLException {
        Peminjaman p = new Peminjaman();
        p.setIdPinjam(rs.getInt("id_pinjam"));
        p.setKodeBuku(rs.getString("kode_buku"));
        p.setIdAnggota(rs.getString("id_anggota"));
        p.setTglPinjam(rs.getDate("tgl_pinjam").toLocalDate());
        Date tglKembali = rs.getDate("tgl_kembali");
        if (tglKembali != null) {
            p.setTglKembali(tglKembali.toLocalDate());
        }
        p.setStatus(Peminjaman.Status.valueOf(rs.getString("status")));
        p.setJudulBuku(rs.getString("judul"));
        p.setNamaAnggota(rs.getString("nama"));
        return p;
    }
}
