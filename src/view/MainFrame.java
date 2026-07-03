package view;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import controller.AnggotaController;
import controller.BukuController;
import controller.PeminjamanController;

/**
 * Frame utama aplikasi. Menampung semua panel modul dalam JTabbedPane.
 */
public class MainFrame extends JFrame {

    private final BukuController bukuController = new BukuController();
    private final AnggotaController anggotaController = new AnggotaController();
    private final PeminjamanController peminjamanController = new PeminjamanController(bukuController);

    private DashboardPanel dashboardPanel;
    private BukuPanel bukuPanel;
    private AnggotaPanel anggotaPanel;
    private PeminjamanPanel peminjamanPanel;

    public MainFrame() {
        setTitle("Sistem Informasi Perpustakaan - Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        dashboardPanel = new DashboardPanel(bukuController, anggotaController, peminjamanController);
        bukuPanel = new BukuPanel(bukuController, this::refreshAll);
        anggotaPanel = new AnggotaPanel(anggotaController, this::refreshAll);
        peminjamanPanel = new PeminjamanPanel(peminjamanController, bukuController, anggotaController, this::refreshAll);

        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("Buku", bukuPanel);
        tabbedPane.addTab("Anggota", anggotaPanel);
        tabbedPane.addTab("Peminjaman", peminjamanPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Refresh semua panel setelah ada perubahan data (dipanggil via callback).
     */
    private void refreshAll() {
        dashboardPanel.refreshData();
        bukuPanel.refreshTable();
        anggotaPanel.refreshTable();
        peminjamanPanel.refreshData();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
