package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import controller.AnggotaController;
import controller.BukuController;
import controller.PeminjamanController;
import exception.BukuTidakTersediaException;
import exception.StokHabisException;
import model.Anggota;
import model.Buku;
import model.Peminjaman;
import util.FileExporter;
import util.LoggerUtil;

/**
 * Panel Transaksi Peminjaman - menangani proses pinjam, kembali, dan riwayat transaksi.
 */
public class PeminjamanPanel extends JPanel {

    private final PeminjamanController peminjamanController;
    private final BukuController bukuController;
    private final AnggotaController anggotaController;
    private final Runnable onDataChanged;

    // Tab Pinjam
    private JComboBox<Buku> comboBuku;
    private JComboBox<Anggota> comboAnggota;

    // Tab Kembali
    private JTable tableAktif;
    private DefaultTableModel modelAktif;

    // Tab Riwayat
    private JTable tableRiwayat;
    private DefaultTableModel modelRiwayat;

    public PeminjamanPanel(PeminjamanController peminjamanController, BukuController bukuController,
                            AnggotaController anggotaController, Runnable onDataChanged) {
        this.peminjamanController = peminjamanController;
        this.bukuController = bukuController;
        this.anggotaController = anggotaController;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout());

        JTabbedPane innerTabs = new JTabbedPane();
        innerTabs.addTab("Pinjam Buku", buildPinjamPanel());
        innerTabs.addTab("Kembalikan Buku", buildKembaliPanel());
        innerTabs.addTab("Riwayat Transaksi", buildRiwayatPanel());

        add(innerTabs, BorderLayout.CENTER);

        refreshData();
    }

    // ================== TAB PINJAM ==================
    private JPanel buildPinjamPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        comboBuku = new JComboBox<>();
        comboAnggota = new JComboBox<>();

        form.add(new JLabel("Pilih Buku:"));
        form.add(comboBuku);
        form.add(new JLabel("Pilih Anggota:"));
        form.add(comboAnggota);

        JButton btnPinjam = new JButton("Proses Peminjaman");
        btnPinjam.addActionListener(e -> prosesPinjam());

        panel.add(form, BorderLayout.NORTH);
        panel.add(btnPinjam, BorderLayout.SOUTH);

        return panel;
    }

    private void prosesPinjam() {
        Buku buku = (Buku) comboBuku.getSelectedItem();
        Anggota anggota = (Anggota) comboAnggota.getSelectedItem();

        if (buku == null || anggota == null) {
            tampilkanError("Data buku atau anggota tidak tersedia. Pastikan data sudah diinput.");
            return;
        }
        if (buku.getStok() <= 0) {
            tampilkanError("Stok buku '" + buku.getJudul() + "' habis, tidak bisa dipinjam!");
            return;
        }

        try {
            peminjamanController.pinjamBuku(buku.getKodeBuku(), anggota.getIdAnggota());
            JOptionPane.showMessageDialog(this,
                    "Peminjaman berhasil!\nBuku: " + buku.getJudul() + "\nAnggota: " + anggota.getNama());
            refreshData();
            onDataChanged.run();
        } catch (BukuTidakTersediaException ex) {
            tampilkanError(ex.getMessage());
        } catch (StokHabisException ex) {
            tampilkanError(ex.getMessage());
        } catch (SQLException ex) {
            LoggerUtil.logError("Gagal memproses peminjaman", ex);
            tampilkanError("Terjadi kesalahan database: " + ex.getMessage());
        }
    }

    // ================== TAB KEMBALI ==================
    private JPanel buildKembaliPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID Pinjam", "Buku", "Anggota", "Tgl Pinjam", "Estimasi Denda"};
        modelAktif = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableAktif = new JTable(modelAktif);

        JButton btnKembali = new JButton("Proses Pengembalian");
        JButton btnCetakStruk = new JButton("Cetak Struk");

        btnKembali.addActionListener(e -> prosesKembali());
        btnCetakStruk.addActionListener(e -> cetakStruk());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(btnKembali);
        btnPanel.add(btnCetakStruk);

        panel.add(new JScrollPane(tableAktif), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void prosesKembali() {
        int row = tableAktif.getSelectedRow();
        if (row == -1) {
            tampilkanError("Pilih transaksi yang ingin dikembalikan!");
            return;
        }
        int idPinjam = (int) modelAktif.getValueAt(row, 0);
        String judulBuku = modelAktif.getValueAt(row, 1).toString();

        try {
            List<Peminjaman> aktif = peminjamanController.getTransaksiAktif();
            Peminjaman target = aktif.stream().filter(p -> p.getIdPinjam() == idPinjam).findFirst().orElse(null);
            if (target == null) {
                tampilkanError("Transaksi tidak ditemukan, silakan refresh.");
                return;
            }
            peminjamanController.kembalikanBuku(idPinjam, target.getKodeBuku());
            JOptionPane.showMessageDialog(this, "Buku '" + judulBuku + "' berhasil dikembalikan!");
            refreshData();
            onDataChanged.run();
        } catch (SQLException ex) {
            LoggerUtil.logError("Gagal memproses pengembalian", ex);
            tampilkanError("Terjadi kesalahan database: " + ex.getMessage());
        }
    }

    private void cetakStruk() {
        int row = tableAktif.getSelectedRow();
        if (row == -1) {
            tampilkanError("Pilih transaksi terlebih dahulu untuk mencetak struk!");
            return;
        }
        String judulBuku = modelAktif.getValueAt(row, 1).toString();
        String namaAnggota = modelAktif.getValueAt(row, 2).toString();
        String tglPinjam = modelAktif.getValueAt(row, 3).toString();
        long denda = (long) modelAktif.getValueAt(row, 4);

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("struk_peminjaman.txt"));
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                FileExporter.cetakStrukPeminjaman(chooser.getSelectedFile().getAbsolutePath(),
                        namaAnggota, judulBuku, tglPinjam, denda);
                JOptionPane.showMessageDialog(this, "Struk berhasil dicetak!");
            } catch (IOException ex) {
                tampilkanError("Gagal mencetak struk: " + ex.getMessage());
            }
        }
    }

    // ================== TAB RIWAYAT ==================
    private JPanel buildRiwayatPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID Pinjam", "Buku", "Anggota", "Tgl Pinjam", "Tgl Kembali", "Status"};
        modelRiwayat = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableRiwayat = new JTable(modelRiwayat);

        JButton btnRefresh = new JButton("Refresh Riwayat");
        btnRefresh.addActionListener(e -> loadRiwayat());

        panel.add(new JScrollPane(tableRiwayat), BorderLayout.CENTER);
        panel.add(btnRefresh, BorderLayout.SOUTH);

        return panel;
    }

    // ================== REFRESH & LOAD ==================
    public void refreshData() {
        loadComboBuku();
        loadComboAnggota();
        loadTransaksiAktif();
        loadRiwayat();
    }

    private void loadComboBuku() {
        bukuController.refreshCache();
        comboBuku.removeAllItems();
        for (Buku b : bukuController.getAllBuku()) {
            if (b.getStok() > 0) {
                comboBuku.addItem(b);
            }
        }
    }

    private void loadComboAnggota() {
        anggotaController.refreshCache();
        comboAnggota.removeAllItems();
        for (Anggota a : anggotaController.getAllAnggota()) {
            comboAnggota.addItem(a);
        }
    }

    private void loadTransaksiAktif() {
        try {
            List<Peminjaman> aktif = peminjamanController.getTransaksiAktif();
            modelAktif.setRowCount(0);
            for (Peminjaman p : aktif) {
                modelAktif.addRow(new Object[]{
                        p.getIdPinjam(), p.getJudulBuku(), p.getNamaAnggota(),
                        p.getTglPinjam(), p.hitungDenda()
                });
            }
        } catch (SQLException ex) {
            LoggerUtil.logError("Gagal memuat transaksi aktif", ex);
        }
    }

    private void loadRiwayat() {
        try {
            List<Peminjaman> riwayat = peminjamanController.getRiwayat();
            modelRiwayat.setRowCount(0);
            for (Peminjaman p : riwayat) {
                modelRiwayat.addRow(new Object[]{
                        p.getIdPinjam(), p.getJudulBuku(), p.getNamaAnggota(),
                        p.getTglPinjam(), p.getTglKembali() == null ? "-" : p.getTglKembali(),
                        p.getStatus()
                });
            }
        } catch (SQLException ex) {
            LoggerUtil.logError("Gagal memuat riwayat transaksi", ex);
        }
    }

    private void tampilkanError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
