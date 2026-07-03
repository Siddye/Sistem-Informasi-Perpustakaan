package model;

import java.io.FileWriter;
import java.io.IOException;
import exception.StokHabisException;

/**
 * Model Buku - merepresentasikan data buku di perpustakaan.
 */
public class Buku extends BaseEntity implements Exportable {

    private String kodeBuku;
    private String judul;
    private String pengarang;
    private String penerbit;
    private int tahunTerbit;
    private int stok;

    public Buku() {
    }

    public Buku(String kodeBuku, String judul, String pengarang, String penerbit, int tahunTerbit, int stok) {
        this.kodeBuku = kodeBuku;
        this.judul = judul;
        this.pengarang = pengarang;
        this.penerbit = penerbit;
        this.tahunTerbit = tahunTerbit;
        setStok(stok);
    }

    // ===== Getter & Setter =====
    public String getKodeBuku() {
        return kodeBuku;
    }

    public void setKodeBuku(String kodeBuku) {
        this.kodeBuku = kodeBuku;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getPengarang() {
        return pengarang;
    }

    public void setPengarang(String pengarang) {
        this.pengarang = pengarang;
    }

    public String getPenerbit() {
        return penerbit;
    }

    public void setPenerbit(String penerbit) {
        this.penerbit = penerbit;
    }

    public int getTahunTerbit() {
        return tahunTerbit;
    }

    public void setTahunTerbit(int tahunTerbit) {
        this.tahunTerbit = tahunTerbit;
    }

    public int getStok() {
        return stok;
    }

    /**
     * Validasi: stok tidak boleh negatif (sesuai requirement panduan).
     */
    public void setStok(int stok) {
        if (stok < 0) {
            throw new IllegalArgumentException("Stok tidak boleh negatif!");
        }
        this.stok = stok;
    }

    public void tambahStok(int jumlah) {
        this.stok += jumlah;
    }

    public void kurangiStok(int jumlah) {
        if (this.stok - jumlah < 0) {
            throw new StokHabisException("Stok buku '" + judul + "' tidak mencukupi! Sisa stok: " + this.stok);
        }
        this.stok -= jumlah;
    }

    // ===== BaseEntity =====
    @Override
    public String getId() {
        return kodeBuku;
    }

    @Override
    public String getDisplayInfo() {
        return String.format("[%s] %s - %s (%s, %d) | Stok: %d",
                kodeBuku, judul, pengarang, penerbit, tahunTerbit, stok);
    }

    // ===== Exportable =====
    @Override
    public String toCSV() {
        return String.join(",",
                safe(kodeBuku), safe(judul), safe(pengarang), safe(penerbit),
                String.valueOf(tahunTerbit), String.valueOf(stok));
    }

    @Override
    public void exportToFile(String filePath) throws IOException {
        try (FileWriter fw = new FileWriter(filePath, true)) {
            fw.write(toCSV());
            fw.write(System.lineSeparator());
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.replace(",", " ");
    }

    @Override
    public String toString() {
        return getDisplayInfo();
    }
}
