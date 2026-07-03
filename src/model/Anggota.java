package model;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Pattern;
import exception.DataTidakValidException;

/**
 * Model Anggota - merepresentasikan data anggota perpustakaan.
 */
public class Anggota extends BaseEntity implements Exportable {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private String idAnggota;
    private String nama;
    private String alamat;
    private String noTelp;
    private String email;
    private LocalDate tglDaftar;

    public Anggota() {
    }

    public Anggota(String idAnggota, String nama, String alamat, String noTelp, String email) {
        this.idAnggota = idAnggota;
        this.nama = nama;
        this.alamat = alamat;
        this.noTelp = noTelp;
        setEmail(email);
        this.tglDaftar = LocalDate.now();
    }

    // ===== Getter & Setter =====
    public String getIdAnggota() {
        return idAnggota;
    }

    public void setIdAnggota(String idAnggota) {
        this.idAnggota = idAnggota;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public String getEmail() {
        return email;
    }

    /**
     * Validasi format email sesuai requirement panduan.
     */
    public void setEmail(String email) {
        if (email != null && !email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new DataTidakValidException("Format email tidak valid: " + email);
        }
        this.email = email;
    }

    public LocalDate getTglDaftar() {
        return tglDaftar;
    }

    public void setTglDaftar(LocalDate tglDaftar) {
        this.tglDaftar = tglDaftar;
    }

    // ===== BaseEntity =====
    @Override
    public String getId() {
        return idAnggota;
    }

    @Override
    public String getDisplayInfo() {
        return String.format("[%s] %s | Telp: %s | Email: %s", idAnggota, nama, noTelp, email);
    }

    // ===== Exportable =====
    @Override
    public String toCSV() {
        return String.join(",",
                safe(idAnggota), safe(nama), safe(alamat), safe(noTelp), safe(email),
                tglDaftar != null ? tglDaftar.toString() : "");
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
