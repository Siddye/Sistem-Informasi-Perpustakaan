-- ============================================================
-- Database Setup: Sistem Informasi Perpustakaan
-- ============================================================

CREATE DATABASE IF NOT EXISTS perpustakaan_db;
USE perpustakaan_db;

-- Tabel Buku
DROP TABLE IF EXISTS peminjaman;
DROP TABLE IF EXISTS buku;
DROP TABLE IF EXISTS anggota;

CREATE TABLE buku (
    kode_buku VARCHAR(10) PRIMARY KEY,
    judul VARCHAR(200) NOT NULL,
    pengarang VARCHAR(100) NOT NULL,
    penerbit VARCHAR(100),
    tahun_terbit INT,
    stok INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabel Anggota
CREATE TABLE anggota (
    id_anggota VARCHAR(10) PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    alamat TEXT,
    no_telp VARCHAR(15),
    email VARCHAR(50),
    tgl_daftar DATE DEFAULT (CURRENT_DATE)
);

-- Tabel Peminjaman
CREATE TABLE peminjaman (
    id_pinjam INT AUTO_INCREMENT PRIMARY KEY,
    kode_buku VARCHAR(10) NOT NULL,
    id_anggota VARCHAR(10) NOT NULL,
    tgl_pinjam DATE NOT NULL,
    tgl_kembali DATE,
    status ENUM('DIPINJAM', 'KEMBALI') DEFAULT 'DIPINJAM',
    FOREIGN KEY (kode_buku) REFERENCES buku(kode_buku) ON UPDATE CASCADE,
    FOREIGN KEY (id_anggota) REFERENCES anggota(id_anggota) ON UPDATE CASCADE
);

-- Insert data contoh
INSERT INTO buku (kode_buku, judul, pengarang, penerbit, tahun_terbit, stok) VALUES
('B001', 'Pemrograman Java Dasar', 'Abdul Kadir', 'Andi Offset', 2020, 5),
('B002', 'Algoritma dan Struktur Data', 'Rinaldi Munir', 'Informatika', 2019, 3),
('B003', 'Basis Data Lanjut', 'Fathansyah', 'Informatika', 2018, 4),
('B004', 'Jaringan Komputer', 'Andrew Tanenbaum', 'Pearson', 2021, 2);

INSERT INTO anggota (id_anggota, nama, alamat, no_telp, email) VALUES
('A001', 'Raffi Pratama', 'Jl. Merdeka No. 1, Lampung', '081234567890', 'raffi@mail.com'),
('A002', 'Siti Aminah', 'Jl. Sudirman No. 5, Lampung', '081298765432', 'siti@mail.com');
