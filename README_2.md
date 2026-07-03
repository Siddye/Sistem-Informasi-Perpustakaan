# Dokumentasi Lengkap Arsitektur & Struktur Proyek

**Library Management System (Sistem Informasi Perpustakaan)**

Dokumen ini merupakan penjelasan komprehensif (Deep-Dive) mengenai cara kerja sistem, pola desain arsitektur yang digunakan, struktur database, serta alur data antar komponen.

---

## 1. Pola Desain (Design Pattern): MVC + DAO

Aplikasi ini tidak ditulis dalam satu _class_ raksasa (monolitik), melainkan menerapkan pola arsitektur **MVC (Model-View-Controller)** yang dikombinasikan dengan **DAO (Data Access Object)**. Kombinasi ini sangat lazim dalam pengembangan aplikasi Java untuk menjamin kode yang _modular_, mudah dipelihara (_maintainable_), dan mudah dikembangkan (_scalable_).

### Diagram Arsitektur

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

```text
  [ VIEW ] <====(Input/Output)====> [ CONTROLLER ] <====(Objek)====> [ DAO ] <====(SQL)====> [ DATABASE MySQL ]
(GUI Swing)                       (Logika Bisnis)                     (Akses DB)
```

1. **VIEW**: Layar interaksi pengguna. Tidak ada logika pemrosesan data (seperti validasi atau _query_ SQL) di lapisan ini. Hanya berisi panel, tabel, tombol, dan kotak input.
2. **CONTROLLER**: Penghubung utama. Menerima aksi klik dari VIEW, memvalidasi input, membungkus data menjadi _Model_, lalu memanggil instruksi di DAO. Controller juga menerima hasil dari DAO lalu memberitahu View untuk memperbarui antarmuka (contoh: me-refresh tabel).
3. **DAO**: Lapisan khusus untuk berkomunikasi dengan server database. Segala jenis operasi instruksi SQL (`SELECT`, `INSERT`, `UPDATE`, `DELETE`) diletakkan eksklusif di sini.
4. **MODEL**: Kelas pembungkus (_container_) data. Data dari aplikasi dibungkus menjadi Objek Model sebelum dikirim ke database, dan data dari database dikembalikan ke aplikasi dalam bentuk Objek Model.

---

## 2. Bedah Struktur Direktori dan File Code

Berikut adalah penjelasan fungsi detail dari setiap _package_ dan baris kode di dalam direktori utama (`src/`):

### 📂 `src/model/` (Representasi Tabel Database)

- **`BaseEntity.java`**: _Abstract Class_ yang berisi atribut wajib milik bersama (seperti `createdAt` atau ID). Kelas entitas lain melakukan warisan (`extends`) ke kelas induk ini.
- **`Buku.java`**: Representasi (Mapping) dari tabel `buku`. Memiliki properti seperti `kodeBuku`, `judul`, `pengarang`, dan `stok`.
- **`Anggota.java`**: Representasi dari tabel `anggota`. Berisi atribut profil pendaftar.
- **`Peminjaman.java`**: Representasi transaksi penyewaan. Objek ini merelasikan antara ID Peminjam (Anggota) dengan Buku yang disewa.
- **`Exportable.java`**: Sebuah _Interface_ yang mewajibkan metode `toCSV()`. Kelas yang mengimplementasikan ini bisa mengekspor isi tabelnya ke dalam format file `.csv`.

### 📂 `src/dao/` (Query Database MySQL)

- **`BukuDAO.java`**: Mengeksekusi query spesifik buku, seperti fungsi `getAllBuku()`, `insertBuku()`, `updateBuku()`, dan `deleteBuku()`.
- **`AnggotaDAO.java`**: Menangani operasi CRUD spesifik untuk tabel anggota perpustakaan.
- **`PeminjamanDAO.java`**: Berisi eksekusi logika kompleks. Tidak hanya memasukkan baris peminjaman (INSERT), tetapi bertugas mengubah (`UPDATE`) status peminjaman (menjadi KEMBALI) dan menghitung lalu memanipulasi jumlah (stok) buku yang tersisa.

### 📂 `src/controller/` (Logika & Aturan Bisnis)

- **`BukuController.java` & `AnggotaController.java`**: Fungsi-fungsi pemicu. Jika ada field judul buku yang dibiarkan kosong, controller inilah yang memblokir dan mengirim peringatan ke layar pengguna sebelum data sampai ke database.
- **`PeminjamanController.java`**: Otak dari proses transaksi. Memiliki aturan bisnis: "Apakah Anggota Valid?", "Apakah Stok Buku > 0?". Apabila syarat terpenuhi, maka DAO akan diperintahkan untuk menyimpan transaksi.

### 📂 `src/view/` (Antarmuka Java Swing)

- **`LoginFrame.java`**: Pintu gerbang sekuritas pertama (Authentication) aplikasi.
- **`MainFrame.java`**: Jendela induk (wadah utama aplikasi) tempat menempelkan navigasi menu samping/atas (Dashboard, Buku, dll).
- **`DashboardPanel.java`**: Halaman pertama yang dilihat setelah login, menampilkan statistik (_Total Buku, Total Peminjam_).
- **`BukuPanel.java`, `AnggotaPanel.java`, `PeminjamanPanel.java`**: Halaman pengelola tabel CRUD. Memuat elemen GUI seperti tabel (`JTable`), form (`JTextField`), dan tombol aksi.

### 📂 `src/exception/` (Manajemen Error Terstruktur)

- **`StokHabisException.java`**: Dilemparkan ketika pengguna ingin meminjam buku namun logika Controller mendeteksi `stok == 0`.
- **`BukuTidakTersediaException.java`**: Terjadi ketika pencarian Kode Buku (ID) tidak ditemukan dalam sistem.
- **`DataTidakValidException.java`**: Pengaman (_Safeguard_) untuk memastikan format input benar (mencegah teks kosong, atau injeksi nilai yang merusak database).

### 📂 `src/util/` (Komponen Utilitas/Bantuan Umum)

- **`DBConnection.java`**: Memanfaatkan _Design Pattern Singleton_. Pola ini menjaga agar aplikasi **hanya menggunakan 1 (_satu_) jalur koneksi (session)** ke Database MySQL melalui driver `mysql-connector-j`. Hal ini mencegah aplikasi memakan memori berlebih yang bisa membuat _server down_ (Memory Leak).
- **`LoggerUtil.java`**: Otomatis mencatat setiap pesan sistem atau jejak kode error (_stack trace_) ke dalam file `log.txt`. Sangat esensial untuk mencari letak kerusakan (_debugging_).
- **`FileExporter.java`**: Fungsi bantu statis (_static utils_) pengolahan I/O (Input-Output) yang mengubah tipe data dari Java List menjadi baris-baris _String_ yang kemudian ditulis/disimpan ke file.

---

## 3. Skema Relasi Database (`database_setup.sql`)

Basis data menggunakan MySQL (`perpustakaan_db`) yang dirancang memiliki **3 Tabel Utama yang saling terhubung (Relational Database)**:

1. **`buku`**: Gudang informasi buku. Kolom utama (_Primary Key_) = `kode_buku` (Tipe String/Varchar).
2. **`anggota`**: Gudang pengguna/peminjam. Kolom utama (_Primary Key_) = `id_anggota` (Varchar).
3. **`peminjaman`**: Berfungsi sebagai **Tabel Transaksi (_Junction Table_)**.
   - _Primary Key_ = `id_pinjam` (Integer dengan sifat Auto_Increment/naik otomatis).
   - _Foreign Key 1_ = `kode_buku` (Berikatan dengan identitas dari tabel buku).
   - _Foreign Key 2_ = `id_anggota` (Berikatan dengan identitas tabel anggota).
   - Memiliki kolom `status` khusus dengan tipe `ENUM` yang isinya hanya boleh: `'DIPINJAM'` atau `'KEMBALI'`.

> **Kebijakan UPDATE CASCADE:** Constraint database pada _Foreign Key_ dibuat sebagai `ON UPDATE CASCADE`. Artinya, apabila admin mengubah Kode Buku atau ID Anggota dari Master Tabel, otomatis semua histori laporan di tabel peminjaman akan ikut berubah selaras dengan ID baru, mencegah rusaknya (_Orphan_) data.

---

## 4. Simulasi Alur Kerja (Workflow) - Skenario Pinjam Buku

Untuk memahami urutan pergerakan data (Data Flow), cermati kronologi **"Saat Tombol Proses Pinjam Ditekan"** berikut ini:

1. **[VIEW] Interaksi Form**: Pengguna mengetik `Kode Buku` dan `ID Anggota` di halaman `PeminjamanPanel`, kemudian menekan tombol eksekusi.
2. **[CONTROLLER] Pemeriksaan & Validasi**:
   - `PeminjamanController` merespons klik tersebut.
   - Controller melakukan kueri awal ke `BukuDAO` (menanyakan sisa stok buku saat ini).
3. **[EXCEPTION] Penanganan Situasi Khusus**:
   - Apabila sisa `stok == 0`, Controller menahan proses penyimpanan dan **melemparkan** objek `StokHabisException`.
   - `PeminjamanPanel` menangkap _exception_ ini lalu mengubahnya menjadi _Pop-Up Dialog Alert_ berbunyi: _"Peminjaman gagal, Stok buku sedang habis!"_.
4. **[DAO] Eksekusi SQL Berangkai**:
   - Apabila lolos validasi, Controller memerintahkan `PeminjamanDAO` beroperasi.
   - `PeminjamanDAO` menjalankan 2 (dua) perintah SQL terpisah secara berkesinambungan:
     - `INSERT INTO peminjaman (...)` $\rightarrow$ Mencatat nama peminjam dan buku ke tabel transaksi.
     - `UPDATE buku SET stok = stok - 1 WHERE kode_buku = ...` $\rightarrow$ Segera memotong fisik jumlah buku yang ada di rak (tabel Buku).
5. **[VIEW] Sinkronisasi Layar**: DAO melaporkan bahwa operasi SQL sukses ke Controller. Controller kemudian memerintahkan `PeminjamanPanel` untuk mereset _form_ input dan memperbarui (memuat ulang) tabel di layar pengguna dengan daftar peminjaman yang baru.

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

- CRUD Buku (tambah/edit/hapus/lihat), pencarian judul/pengarang, export CSV, validasi stok
- CRUD Anggota, pencarian nama/ID, validasi ID unik & format email
- Transaksi Peminjaman (validasi stok, kurangi stok otomatis)
- Transaksi Pengembalian (update status, tambah stok otomatis)
- Riwayat Transaksi lengkap
- Dashboard statistik (total buku, anggota, buku dipinjam)
- Logging setiap transaksi ke `log.txt` dengan timestamp
- Custom Exception: `BukuTidakTersediaException`, `StokHabisException`, `DataTidakValidException`
- Abstract Class `BaseEntity` & Interface `Exportable`
- Collections: `ArrayList` & `HashMap` untuk caching di Controller
- JDBC dengan `PreparedStatement` + `try-with-resources` (anti SQL Injection)
- Bonus: Login System, Perhitungan Denda Keterlambatan (Rp 1.000/hari > 7 hari), Cetak Struk Peminjaman

## Catatan

- Log aktivitas otomatis tersimpan di `log.txt` (dibuat di working directory saat aplikasi dijalankan).
- Kolom "Estimasi Denda" di tab Kembalikan Buku dihitung otomatis berdasarkan tanggal pinjam vs hari ini.
