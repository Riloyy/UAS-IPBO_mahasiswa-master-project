# Aplikasi Manajemen Data Mahasiswa

Aplikasi desktop Java Swing untuk mengelola data mahasiswa, dengan pencetakan laporan PDF berbasis JasperReports dan penyimpanan data SQLite.

## Teknologi

- Java JDK 21
- Maven 3.x
- Java Swing (UI)
- SQLite (JDBC)
- JasperReports 6.21.3

## Fitur

- Tambah, edit, hapus data mahasiswa
- Pencarian mahasiswa
- Tabel dengan status berwarna (Aktif, Cuti, Nonaktif, Lulus)
- Preview foto mahasiswa
- Cetak laporan PDF berdasarkan rentang NIM
- Autocomplete/saran NIM pada form cetak laporan
- Border rapi pada tabel laporan PDF

## Persyaratan

- JDK 21
- Maven 3.x

## Cara Menjalankan

1. Clone repository:
   ```
   git clone https://github.com/Riloyy/UAS_student-master-project.git
   cd UAS_student-master-project
   ```
2. Compile:
   ```
   mvn clean compile
   ```
3. Jalankan aplikasi:
   ```
   mvn exec:java
   ```

## Struktur Proyek

```
src/main/java/com/ubl/datamahasiswa/   # Source code
src/main/resources/                    # Resource (jrxml, images)
captures/                              # Screenshot aplikasi
reports/                               # Output laporan PDF
```

## Catatan

- Database SQLite (`mahasiswa.db`) tidak disertakan (di-ignore). Aplikasi akan membuat/menggunakan database sesuai konfigurasi `Database.java`.
- Laporan PDF hasil cetak tersimpan di folder `reports/`.
