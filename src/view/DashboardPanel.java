package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import controller.AnggotaController;
import controller.BukuController;
import controller.PeminjamanController;
import util.LoggerUtil;

/**
 * Panel Dashboard - menampilkan statistik ringkas perpustakaan.
 */
public class DashboardPanel extends JPanel {

    private final BukuController bukuController;
    private final AnggotaController anggotaController;
    private final PeminjamanController peminjamanController;

    private JLabel lblTotalBuku;
    private JLabel lblTotalAnggota;
    private JLabel lblBukuDipinjam;

    public DashboardPanel(BukuController bukuController, AnggotaController anggotaController,
                           PeminjamanController peminjamanController) {
        this.bukuController = bukuController;
        this.anggotaController = anggotaController;
        this.peminjamanController = peminjamanController;

        setLayout(new GridLayout(1, 3, 15, 15));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        lblTotalBuku = buildCard("Total Buku", "0", new Color(52, 152, 219));
        lblTotalAnggota = buildCard("Total Anggota", "0", new Color(46, 204, 113));
        lblBukuDipinjam = buildCard("Buku Sedang Dipinjam", "0", new Color(231, 76, 60));

        refreshData();
    }

    private JLabel buildCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new java.awt.BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setForeground(Color.WHITE);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 40));

        card.add(lblTitle, java.awt.BorderLayout.NORTH);
        card.add(lblValue, java.awt.BorderLayout.CENTER);

        add(card);
        return lblValue;
    }

    public void refreshData() {
        bukuController.refreshCache();
        anggotaController.refreshCache();

        lblTotalBuku.setText(String.valueOf(bukuController.getTotalBuku()));
        lblTotalAnggota.setText(String.valueOf(anggotaController.getTotalAnggota()));
        try {
            lblBukuDipinjam.setText(String.valueOf(peminjamanController.getTotalDipinjam()));
        } catch (SQLException e) {
            LoggerUtil.logError("Gagal memuat statistik dashboard", e);
            lblBukuDipinjam.setText("-");
        }
    }
}
