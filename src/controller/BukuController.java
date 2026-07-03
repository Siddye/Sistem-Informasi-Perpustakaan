package controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dao.BukuDAO;
import exception.BukuTidakTersediaException;
import exception.DataTidakValidException;
import model.Buku;
import util.FileExporter;
import util.LoggerUtil;

/**
 * Controller untuk modul Buku.
 * Menjembatani View (GUI) dengan DAO, sekaligus mengelola caching
 * menggunakan Collections (ArrayList & HashMap) sesuai requirement panduan.
 */
public class BukuController {

    private final BukuDAO bukuDAO = new BukuDAO();

    // Collections untuk caching data buku (mempercepat pencarian di memori)
    private List<Buku> bukuList = new ArrayList<>();
    private Map<String, Buku> bukuMap = new HashMap<>();

    public BukuController() {
        refreshCache();
    }

    /**
     * Muat ulang data dari database ke dalam cache (List & Map).
     */
    public void refreshCache() {
        try {
            bukuList = bukuDAO.findAll();
            bukuMap = new HashMap<>();
            for (Buku b : bukuList) {
                bukuMap.put(b.getKodeBuku(), b);
            }
        } catch (SQLException e) {
            LoggerUtil.logError("Gagal memuat data buku", e);
        }
    }

    public List<Buku> getAllBuku() {
        return bukuList;
    }

    public Buku getByKode(String kode) {
        return bukuMap.get(kode);
    }

    public void tambahBuku(Buku buku) throws SQLException {
        validasiBuku(buku);
        if (bukuMap.containsKey(buku.getKodeBuku())) {
            throw new DataTidakValidException("Kode buku '" + buku.getKodeBuku() + "' sudah terdaftar!");
        }
        bukuDAO.insert(buku);
        refreshCache();
    }

    public void updateBuku(Buku buku) throws SQLException {
        validasiBuku(buku);
        bukuDAO.update(buku);
        refreshCache();
    }

    public void hapusBuku(String kode) throws SQLException {
        bukuDAO.delete(kode);
        refreshCache();
    }

    public List<Buku> cariBuku(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBuku();
        }
        return bukuDAO.search(keyword.trim());
    }

    public void exportToCSV(String filePath) throws java.io.IOException {
        FileExporter.exportBukuToCSV(bukuList, filePath);
    }

    /**
     * Proses peminjaman: validasi buku ada & stok cukup, lalu kurangi stok.
     * Melempar BukuTidakTersediaException (checked) jika buku tidak ditemukan,
     * dan StokHabisException (unchecked, dari model.Buku) jika stok tidak cukup.
     */
    public void prosesPeminjamanStok(String kodeBuku) throws BukuTidakTersediaException, SQLException {
        Buku buku = bukuMap.get(kodeBuku);
        if (buku == null) {
            throw new BukuTidakTersediaException("Buku dengan kode '" + kodeBuku + "' tidak ditemukan!");
        }
        buku.kurangiStok(1); // bisa melempar StokHabisException jika stok = 0
        bukuDAO.updateStok(kodeBuku, buku.getStok());
        refreshCache();
    }

    /**
     * Proses pengembalian: tambah stok kembali.
     */
    public void prosesPengembalianStok(String kodeBuku) throws SQLException {
        Buku buku = bukuMap.get(kodeBuku);
        if (buku != null) {
            buku.tambahStok(1);
            bukuDAO.updateStok(kodeBuku, buku.getStok());
            refreshCache();
        }
    }

    private void validasiBuku(Buku buku) {
        if (buku.getKodeBuku() == null || buku.getKodeBuku().trim().isEmpty()) {
            throw new DataTidakValidException("Kode buku tidak boleh kosong!");
        }
        if (buku.getJudul() == null || buku.getJudul().trim().isEmpty()) {
            throw new DataTidakValidException("Judul buku tidak boleh kosong!");
        }
        if (buku.getStok() < 0) {
            throw new DataTidakValidException("Stok tidak boleh negatif!");
        }
    }

    public int getTotalBuku() {
        return bukuList.size();
    }
}
