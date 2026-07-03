# Sistem Informasi Perpustakaan (Library Management System)

Proyek Akhir - Java Swing + JDBC + MVC

## Struktur Proyek

```
LibraryManagementSystem/
├── database_setup.sql        
├── src/
│   ├── model/
│   │   ├── BaseEntity.java
│   │   ├── Exportable.java
│   │   ├── Buku.java
│   │   ├── Anggota.java
│   │   └── Peminjaman.java
│   ├── view/
│   │   ├── LoginFrame.java
│   │   ├── MainFrame.java
│   │   ├── DashboardPanel.java
│   │   ├── BukuPanel.java
│   │   ├── AnggotaPanel.java
│   │   └── PeminjamanPanel.java
│   ├── controller/
│   │   ├── BukuController.java
│   │   ├── AnggotaController.java
│   │   └── PeminjamanController.java
│   ├── dao/
│   │   ├── BukuDAO.java
│   │   ├── AnggotaDAO.java
│   │   └── PeminjamanDAO.java
│   ├── exception/
│   │   ├── BukuTidakTersediaException.java
│   │   ├── StokHabisException.java
│   │   └── DataTidakValidException.java
│   └── util/
│       ├── DBConnection.java
│       ├── FileExporter.java
│       └── LoggerUtil.java
└── lib/   <- taruh mysql-connector-j-x.x.x.jar di sini
```

## Cara Menjalankan

### 1. Setup Database
Buka phpMyAdmin atau MySQL client, lalu jalankan file `database_setup.sql`.
Ini akan membuat database `perpustakaan_db` beserta tabel dan data contoh.

### 2. Download MySQL Connector/J
Download `mysql-connector-j` (driver JDBC MySQL) dari:
https://dev.mysql.com/downloads/connector/j/

Taruh file `.jar` nya di folder `lib/`.

### 3. Konfigurasi Koneksi
Buka `src/util/DBConnection.java`, sesuaikan `USER` dan `PASSWORD` dengan
konfigurasi MySQL lokal kamu (default XAMPP: user=`root`, password kosong).

### 4. Compile & Jalankan (via terminal)

```bash
# compile (dari root folder project)
javac -cp "lib/*" -d bin $(find src -name "*.java")

# jalankan
java -cp "bin:lib/*" view.MainFrame
```

Kalau pakai IDE (NetBeans/IntelliJ/Eclipse):
1. Buat project baru, import folder `src` sebagai source root.
2. Tambahkan `mysql-connector-j.jar` ke Project Libraries / Classpath.
3. Set `view.MainFrame` sebagai Main Class.
4. Run.

### 5. Login
Aplikasi akan menampilkan form login dulu (Bonus Challenge).
- Username: `admin`
- Password: `admin123`

## Fitur yang Sudah Diimplementasikan

-  CRUD Buku (tambah/edit/hapus/lihat), pencarian judul/pengarang, export CSV, validasi stok
-  CRUD Anggota, pencarian nama/ID, validasi ID unik & format email
-  Transaksi Peminjaman (validasi stok, kurangi stok otomatis)
-  Transaksi Pengembalian (update status, tambah stok otomatis)
-  Riwayat Transaksi lengkap
-  Dashboard statistik (total buku, anggota, buku dipinjam)
-  Logging setiap transaksi ke `log.txt` dengan timestamp
-  Custom Exception: `BukuTidakTersediaException`, `StokHabisException`, `DataTidakValidException`
-  Abstract Class `BaseEntity` & Interface `Exportable`
-  Collections: `ArrayList` & `HashMap` untuk caching di Controller
-  JDBC dengan `PreparedStatement` + `try-with-resources` (anti SQL Injection)
-  Bonus: Login System, Perhitungan Denda Keterlambatan (Rp 1.000/hari > 7 hari), Cetak Struk Peminjaman

## Catatan
- Log aktivitas otomatis tersimpan di `log.txt` (dibuat di working directory saat aplikasi dijalankan).
- Kolom "Estimasi Denda" di tab Kembalikan Buku dihitung otomatis berdasarkan tanggal pinjam vs hari ini.
