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
import javax.swing.table.DefaultTableModel;
import controller.AnggotaController;
import model.Anggota;
import util.LoggerUtil;


public class AnggotaPanel extends JPanel {

    private final AnggotaController controller;
    private final Runnable onDataChanged;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtId, txtNama, txtAlamat, txtTelp, txtEmail;
    private JTextField txtSearch;

    public AnggotaPanel(AnggotaController controller, Runnable onDataChanged) {
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
        JPanel panel = new JPanel(new GridLayout(2, 5, 8, 8));
        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Anggota"));

        txtId = new JTextField();
        txtNama = new JTextField();
        txtAlamat = new JTextField();
        txtTelp = new JTextField();
        txtEmail = new JTextField();

        panel.add(new JLabel("ID Anggota:"));
        panel.add(new JLabel("Nama:"));
        panel.add(new JLabel("Alamat:"));
        panel.add(new JLabel("No. Telp:"));
        panel.add(new JLabel("Email:"));

        panel.add(txtId);
        panel.add(txtNama);
        panel.add(txtAlamat);
        panel.add(txtTelp);
        panel.add(txtEmail);

        return panel;
    }

    private JScrollPane buildTablePanel() {
        String[] columns = {"ID Anggota", "Nama", "Alamat", "No. Telp", "Email", "Tgl Daftar"};
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

        panel.add(new JLabel("Cari (nama/ID):"));
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
        txtId.setText(tableModel.getValueAt(row, 0).toString());
        txtId.setEditable(false);
        txtNama.setText(tableModel.getValueAt(row, 1).toString());
        txtAlamat.setText(tableModel.getValueAt(row, 2).toString());
        txtTelp.setText(tableModel.getValueAt(row, 3).toString());
        txtEmail.setText(tableModel.getValueAt(row, 4).toString());
    }

    private void clearForm() {
        txtId.setText("");
        txtId.setEditable(true);
        txtNama.setText("");
        txtAlamat.setText("");
        txtTelp.setText("");
        txtEmail.setText("");
        table.clearSelection();
    }

    private void tambah() {
        try {
            Anggota a = new Anggota(txtId.getText().trim(), txtNama.getText().trim(),
                    txtAlamat.getText().trim(), txtTelp.getText().trim(), txtEmail.getText().trim());
            controller.tambahAnggota(a);
            JOptionPane.showMessageDialog(this, "Anggota berhasil ditambahkan!");
            clearForm();
            refreshTable();
            onDataChanged.run();
        } catch (RuntimeException | SQLException ex) {
            tampilkanError(ex.getMessage());
        }
    }

    private void update() {
        if (txtId.getText().trim().isEmpty()) {
            tampilkanError("Pilih data anggota terlebih dahulu dari tabel!");
            return;
        }
        try {
            Anggota a = new Anggota(txtId.getText().trim(), txtNama.getText().trim(),
                    txtAlamat.getText().trim(), txtTelp.getText().trim(), txtEmail.getText().trim());
            controller.updateAnggota(a);
            JOptionPane.showMessageDialog(this, "Anggota berhasil diupdate!");
            clearForm();
            refreshTable();
            onDataChanged.run();
        } catch (RuntimeException | SQLException ex) {
            tampilkanError(ex.getMessage());
        }
    }

    private void hapus() {
        if (txtId.getText().trim().isEmpty()) {
            tampilkanError("Pilih data anggota terlebih dahulu dari tabel!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus anggota '" + txtId.getText() + "'?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            controller.hapusAnggota(txtId.getText().trim());
            JOptionPane.showMessageDialog(this, "Anggota berhasil dihapus!");
            clearForm();
            refreshTable();
            onDataChanged.run();
        } catch (SQLException ex) {
            tampilkanError("Gagal menghapus anggota (mungkin masih ada transaksi terkait): " + ex.getMessage());
        }
    }

    private void cari() {
        try {
            List<Anggota> hasil = controller.cariAnggota(txtSearch.getText());
            tampilkanKeTabel(hasil);
        } catch (SQLException ex) {
            tampilkanError(ex.getMessage());
        }
    }

    private void exportCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("data_anggota.csv"));
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                controller.exportToCSV(chooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Data anggota berhasil diexport ke CSV!");
            } catch (IOException ex) {
                LoggerUtil.logError("Gagal export CSV anggota", ex);
                tampilkanError("Gagal export: " + ex.getMessage());
            }
        }
    }

    public void refreshTable() {
        controller.refreshCache();
        tampilkanKeTabel(controller.getAllAnggota());
    }

    private void tampilkanKeTabel(List<Anggota> list) {
        tableModel.setRowCount(0);
        for (Anggota a : list) {
            tableModel.addRow(new Object[]{
                    a.getIdAnggota(), a.getNama(), a.getAlamat(),
                    a.getNoTelp(), a.getEmail(), a.getTglDaftar()
            });
        }
    }

    private void tampilkanError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
