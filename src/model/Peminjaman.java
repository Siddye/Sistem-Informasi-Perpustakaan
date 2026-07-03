package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Model Peminjaman - merepresentasikan transaksi peminjaman buku.
 */
public class Peminjaman extends BaseEntity {

    public enum Status {
        DIPINJAM, KEMBALI
    }

    private int idPinjam;
    private String kodeBuku;
    private String idAnggota;
    private LocalDate tglPinjam;
    private LocalDate tglKembali;
    private Status status;

    // Field tambahan untuk tampilan (hasil JOIN), tidak disimpan langsung ke kolom peminjaman
    private String judulBuku;
    private String namaAnggota;

    public Peminjaman() {
    }

    public Peminjaman(String kodeBuku, String idAnggota, LocalDate tglPinjam) {
        this.kodeBuku = kodeBuku;
        this.idAnggota = idAnggota;
        this.tglPinjam = tglPinjam;
        this.status = Status.DIPINJAM;
    }

    // ===== Getter & Setter =====
    public int getIdPinjam() {
        return idPinjam;
    }

    public void setIdPinjam(int idPinjam) {
        this.idPinjam = idPinjam;
    }

    public String getKodeBuku() {
        return kodeBuku;
    }

    public void setKodeBuku(String kodeBuku) {
        this.kodeBuku = kodeBuku;
    }

    public String getIdAnggota() {
        return idAnggota;
    }

    public void setIdAnggota(String idAnggota) {
        this.idAnggota = idAnggota;
    }

    public LocalDate getTglPinjam() {
        return tglPinjam;
    }

    public void setTglPinjam(LocalDate tglPinjam) {
        this.tglPinjam = tglPinjam;
    }

    public LocalDate getTglKembali() {
        return tglKembali;
    }

    public void setTglKembali(LocalDate tglKembali) {
        this.tglKembali = tglKembali;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getJudulBuku() {
        return judulBuku;
    }

    public void setJudulBuku(String judulBuku) {
        this.judulBuku = judulBuku;
    }

    public String getNamaAnggota() {
        return namaAnggota;
    }

    public void setNamaAnggota(String namaAnggota) {
        this.namaAnggota = namaAnggota;
    }

    /**
     * Hitung denda keterlambatan (Bonus Challenge): Rp 1.000/hari jika > 7 hari.
     * Dihitung dari tglPinjam sampai hari ini (jika masih dipinjam) atau sampai tglKembali.
     */
    public long hitungDenda() {
        LocalDate acuan = (status == Status.KEMBALI && tglKembali != null) ? tglKembali : LocalDate.now();
        long lamaHari = ChronoUnit.DAYS.between(tglPinjam, acuan);
        if (lamaHari > 7) {
            return (lamaHari - 7) * 1000L;
        }
        return 0L;
    }

    // ===== BaseEntity =====
    @Override
    public String getId() {
        return String.valueOf(idPinjam);
    }

    @Override
    public String getDisplayInfo() {
        return String.format("Pinjam #%d | Buku: %s | Anggota: %s | Tgl Pinjam: %s | Status: %s",
                idPinjam, kodeBuku, idAnggota, tglPinjam, status);
    }

    @Override
    public String toString() {
        return getDisplayInfo();
    }
}
