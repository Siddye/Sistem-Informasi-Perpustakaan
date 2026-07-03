package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.Anggota;
import model.Buku;

/**
 * Utility class untuk export/import data ke/dari file CSV,
 * serta menulis log aktivitas ke file teks.
 */
public class FileExporter {

    private FileExporter() {
    }

    /**
     * Export daftar buku ke file CSV.
     */
    public static void exportBukuToCSV(List<Buku> list, String filePath) throws IOException {
        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write("kode_buku,judul,pengarang,penerbit,tahun_terbit,stok\n");
            for (Buku b : list) {
                fw.write(b.toCSV());
                fw.write("\n");
            }
        }
        LoggerUtil.log("Export data buku ke CSV berhasil: " + filePath + " (" + list.size() + " data)");
    }

    /**
     * Export daftar anggota ke file CSV.
     */
    public static void exportAnggotaToCSV(List<Anggota> list, String filePath) throws IOException {
        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write("id_anggota,nama,alamat,no_telp,email,tgl_daftar\n");
            for (Anggota a : list) {
                fw.write(a.toCSV());
                fw.write("\n");
            }
        }
        LoggerUtil.log("Export data anggota ke CSV berhasil: " + filePath + " (" + list.size() + " data)");
    }

    /**
     * Import data buku dari file CSV (format: kode_buku,judul,pengarang,penerbit,tahun_terbit,stok).
     * Baris pertama (header) dilewati.
     */
    public static List<Buku> importBukuFromCSV(String filePath) throws IOException {
        List<Buku> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 6) continue;
                Buku b = new Buku(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim(),
                        Integer.parseInt(parts[4].trim()),
                        Integer.parseInt(parts[5].trim())
                );
                result.add(b);
            }
        }
        LoggerUtil.log("Import data buku dari CSV berhasil: " + filePath + " (" + result.size() + " data)");
        return result;
    }

    /**
     * Generate cetak struk peminjaman (Bonus Challenge) ke file teks.
     */
    public static void cetakStrukPeminjaman(String filePath, String namaAnggota, String judulBuku,
                                             String tglPinjam, long denda) throws IOException {
        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write("=========================================\n");
            fw.write("     STRUK PEMINJAMAN PERPUSTAKAAN\n");
            fw.write("=========================================\n");
            fw.write("Anggota   : " + namaAnggota + "\n");
            fw.write("Buku      : " + judulBuku + "\n");
            fw.write("Tgl Pinjam: " + tglPinjam + "\n");
            fw.write("Denda     : Rp " + denda + "\n");
            fw.write("=========================================\n");
            fw.write("Terima kasih telah menggunakan layanan kami.\n");
        }
        LoggerUtil.log("Struk peminjaman dicetak: " + filePath);
    }
}
