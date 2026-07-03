package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import controller.BukuController;
import model.Buku;
import util.LoggerUtil;

/**
 * Panel Manajemen Buku - CRUD lengkap, pencarian, dan export ke CSV.
 */
public class BukuPanel extends JPanel {

    private final BukuController controller;
    private final Runnable onDataChanged;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtKode, txtJudul, txtPengarang, txtPenerbit, txtTahun, txtStok;
    private JTextField txtSearch;

    public BukuPanel(BukuController controller, Runnable onDataChanged) {
        this.controller = controller;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout(10, 10));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 6, 8, 8));
        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Buku"));

        txtKode = new JTextField();
        txtJudul = new JTextField();
        txtPengarang = new JTextField();
        txtPenerbit = new JTextField();
        txtTahun = new JTextField();
        txtStok = new JTextField();

        panel.add(new JLabel("Kode Buku:"));
        panel.add(new JLabel("Judul:"));
        panel.add(new JLabel("Pengarang:"));
        panel.add(new JLabel("Penerbit:"));
        panel.add(new JLabel("Tahun Terbit:"));
        panel.add(new JLabel("Stok:"));

        panel.add(txtKode);
        panel.add(txtJudul);
        panel.add(txtPengarang);
        panel.add(txtPenerbit);
        panel.add(txtTahun);
        panel.add(txtStok);

        return panel;
    }

    private JScrollPane buildTablePanel() {
        String[] columns = {"Kode", "Judul", "Pengarang", "Penerbit", "Tahun", "Stok"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                isiFormDariTabel();
            }
        });
        return new JScrollPane(table);
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        txtSearch = new JTextField(15);
        JButton btnSearch = new JButton("Cari");
        JButton btnTambah = new JButton("Tambah");
        JButton btnUpdate = new JButton("Update");
        JButton btnHapus = new JButton("Hapus");
        JButton btnClear = new JButton("Clear");
        JButton btnExport = new JButton("Export CSV");

        panel.add(new JLabel("Cari (judul/pengarang):"));
        panel.add(txtSearch);
        panel.add(btnSearch);
        panel.add(btnTambah);
        panel.add(btnUpdate);
        panel.add(btnHapus);
        panel.add(btnClear);
        panel.add(btnExport);

        btnSearch.addActionListener(e -> cari());
        btnTambah.addActionListener(e -> tambah());
        btnUpdate.addActionListener(e -> update());
        btnHapus.addActionListener(e -> hapus());
        btnClear.addActionListener(e -> clearForm());
        btnExport.addActionListener(e -> exportCSV());

        return panel;
    }

    private void isiFormDariTabel() {
        int row = table.getSelectedRow();
        txtKode.setText(tableModel.getValueAt(row, 0).toString());
        txtKode.setEditable(false);
        txtJudul.setText(tableModel.getValueAt(row, 1).toString());
        txtPengarang.setText(tableModel.getValueAt(row, 2).toString());
        txtPenerbit.setText(tableModel.getValueAt(row, 3).toString());
        txtTahun.setText(tableModel.getValueAt(row, 4).toString());
        txtStok.setText(tableModel.getValueAt(row, 5).toString());
    }

    private void clearForm() {
        txtKode.setText("");
        txtKode.setEditable(true);
        txtJudul.setText("");
        txtPengarang.setText("");
        txtPenerbit.setText("");
        txtTahun.setText("");
        txtStok.setText("");
        table.clearSelection();
    }

    private void tambah() {
        try {
            Buku buku = ambilDataForm();
            controller.tambahBuku(buku);
            JOptionPane.showMessageDialog(this, "Buku berhasil ditambahkan!");
            clearForm();
            refreshTable();
            onDataChanged.run();
        } catch (NumberFormatException nfe) {
            tampilkanError("Tahun terbit dan stok harus berupa angka!");
        } catch (RuntimeException | SQLException ex) {
            tampilkanError(ex.getMessage());
        }
    }

    private void update() {
        if (txtKode.getText().trim().isEmpty()) {
            tampilkanError("Pilih data buku terlebih dahulu dari tabel!");
            return;
        }
        try {
            Buku buku = ambilDataForm();
            controller.updateBuku(buku);
            JOptionPane.showMessageDialog(this, "Buku berhasil diupdate!");
            clearForm();
            refreshTable();
            onDataChanged.run();
        } catch (NumberFormatException nfe) {
            tampilkanError("Tahun terbit dan stok harus berupa angka!");
        } catch (RuntimeException | SQLException ex) {
            tampilkanError(ex.getMessage());
        }
    }

    private void hapus() {
        if (txtKode.getText().trim().isEmpty()) {
            tampilkanError("Pilih data buku terlebih dahulu dari tabel!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus buku '" + txtKode.getText() + "'?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            controller.hapusBuku(txtKode.getText().trim());
            JOptionPane.showMessageDialog(this, "Buku berhasil dihapus!");
            clearForm();
            refreshTable();
            onDataChanged.run();
        } catch (SQLException ex) {
            tampilkanError("Gagal menghapus buku (mungkin masih ada transaksi terkait): " + ex.getMessage());
        }
    }

    private void cari() {
        try {
            List<Buku> hasil = controller.cariBuku(txtSearch.getText());
            tampilkanKeTabel(hasil);
        } catch (SQLException ex) {
            tampilkanError(ex.getMessage());
        }
    }

    private void exportCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("data_buku.csv"));
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                controller.exportToCSV(chooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Data buku berhasil diexport ke CSV!");
            } catch (IOException ex) {
                LoggerUtil.logError("Gagal export CSV buku", ex);
                tampilkanError("Gagal export: " + ex.getMessage());
            }
        }
    }

    private Buku ambilDataForm() {
        String kode = txtKode.getText().trim();
        String judul = txtJudul.getText().trim();
        String pengarang = txtPengarang.getText().trim();
        String penerbit = txtPenerbit.getText().trim();
        int tahun = Integer.parseInt(txtTahun.getText().trim());
        int stok = Integer.parseInt(txtStok.getText().trim());
        return new Buku(kode, judul, pengarang, penerbit, tahun, stok);
    }

    public void refreshTable() {
        controller.refreshCache();
        tampilkanKeTabel(controller.getAllBuku());
    }

    private void tampilkanKeTabel(List<Buku> list) {
        tableModel.setRowCount(0);
        for (Buku b : list) {
            tableModel.addRow(new Object[]{
                    b.getKodeBuku(), b.getJudul(), b.getPengarang(),
                    b.getPenerbit(), b.getTahunTerbit(), b.getStok()
            });
        }
    }

    private void tampilkanError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
