package model;

/**
 * Abstract class dasar untuk semua entity (Buku, Anggota, Peminjaman).
 * Menerapkan konsep OOP: Abstraction & Inheritance.
 */
public abstract class BaseEntity {

    /**
     * Mengembalikan ID unik dari entity.
     */
    public abstract String getId();

    /**
     * Mengembalikan informasi ringkas entity untuk ditampilkan (misal di JTable / log).
     */
    public abstract String getDisplayInfo();
}
