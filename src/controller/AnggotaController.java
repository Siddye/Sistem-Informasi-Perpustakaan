package controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dao.AnggotaDAO;
import exception.DataTidakValidException;
import model.Anggota;
import util.FileExporter;
import util.LoggerUtil;

/**
 * Controller untuk modul Anggota.
 */
public class AnggotaController {

    private final AnggotaDAO anggotaDAO = new AnggotaDAO();

    private List<Anggota> anggotaList = new ArrayList<>();
    private Map<String, Anggota> anggotaMap = new HashMap<>();

    public AnggotaController() {
        refreshCache();
    }

    public void refreshCache() {
        try {
            anggotaList = anggotaDAO.findAll();
            anggotaMap = new HashMap<>();
            for (Anggota a : anggotaList) {
                anggotaMap.put(a.getIdAnggota(), a);
            }
        } catch (SQLException e) {
            LoggerUtil.logError("Gagal memuat data anggota", e);
        }
    }

    public List<Anggota> getAllAnggota() {
        return anggotaList;
    }

    public Anggota getById(String id) {
        return anggotaMap.get(id);
    }

    public void tambahAnggota(Anggota anggota) throws SQLException {
        validasiAnggota(anggota);
        if (anggotaMap.containsKey(anggota.getIdAnggota())) {
            throw new DataTidakValidException("ID anggota '" + anggota.getIdAnggota() + "' sudah terdaftar!");
        }
        anggotaDAO.insert(anggota);
        refreshCache();
    }

    public void updateAnggota(Anggota anggota) throws SQLException {
        validasiAnggota(anggota);
        anggotaDAO.update(anggota);
        refreshCache();
    }

    public void hapusAnggota(String id) throws SQLException {
        anggotaDAO.delete(id);
        refreshCache();
    }

    public List<Anggota> cariAnggota(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllAnggota();
        }
        return anggotaDAO.search(keyword.trim());
    }

    public void exportToCSV(String filePath) throws java.io.IOException {
        FileExporter.exportAnggotaToCSV(anggotaList, filePath);
    }

    private void validasiAnggota(Anggota anggota) {
        if (anggota.getIdAnggota() == null || anggota.getIdAnggota().trim().isEmpty()) {
            throw new DataTidakValidException("ID anggota tidak boleh kosong!");
        }
        if (anggota.getNama() == null || anggota.getNama().trim().isEmpty()) {
            throw new DataTidakValidException("Nama anggota tidak boleh kosong!");
        }
        // Validasi email sudah dilakukan otomatis di setter model.Anggota
    }

    public int getTotalAnggota() {
        return anggotaList.size();
    }
}
