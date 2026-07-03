package controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import dao.PeminjamanDAO;
import exception.BukuTidakTersediaException;
import exception.StokHabisException;
import model.Peminjaman;
import util.LoggerUtil;

/**
 * Controller untuk modul Transaksi Peminjaman.
 * Mengatur alur pinjam & kembali, termasuk validasi stok dan pencatatan log.
 */
public class PeminjamanController {

    private final PeminjamanDAO peminjamanDAO = new PeminjamanDAO();
    private final BukuController bukuController;

    public PeminjamanController(BukuController bukuController) {
        this.bukuController = bukuController;
    }

    /**
     * Proses peminjaman buku oleh anggota.
     * Alur: validasi stok buku (lewat BukuController) -> kurangi stok -> catat transaksi.
     */
    public void pinjamBuku(String kodeBuku, String idAnggota)
            throws BukuTidakTersediaException, StokHabisException, SQLException {

        // Validasi & kurangi stok terlebih dahulu (bisa throw BukuTidakTersediaException / StokHabisException)
        bukuController.prosesPeminjamanStok(kodeBuku);

        Peminjaman p = new Peminjaman(kodeBuku, idAnggota, LocalDate.now());
        peminjamanDAO.insert(p);

        LoggerUtil.log("PEMINJAMAN: Buku=" + kodeBuku + ", Anggota=" + idAnggota + " berhasil diproses.");
    }

    /**
     * Proses pengembalian buku.
     */
    public void kembalikanBuku(int idPinjam, String kodeBuku) throws SQLException {
        peminjamanDAO.updatePengembalian(idPinjam, LocalDate.now());
        bukuController.prosesPengembalianStok(kodeBuku);
        LoggerUtil.log("PENGEMBALIAN: Transaksi #" + idPinjam + " berhasil diproses.");
    }

    public List<Peminjaman> getTransaksiAktif() throws SQLException {
        return peminjamanDAO.findAktif();
    }

    public List<Peminjaman> getRiwayat() throws SQLException {
        return peminjamanDAO.findAllRiwayat();
    }

    public int getTotalDipinjam() throws SQLException {
        return peminjamanDAO.countTotalDipinjam();
    }
}
