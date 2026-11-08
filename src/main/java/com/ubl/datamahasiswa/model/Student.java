package com.ubl.datamahasiswa.model;

public class Student {

    private String nim;
    private String nama;
    private String jurusan;
    private String angkatan;
    private String status;
    private String fotoPath;

    public Student() {
    }

    public Student(String nim, String nama) {
        this.nim = nim;
        this.nama = nama;
        this.jurusan = "Informatika";
        this.angkatan = "2023";
        this.status = "Aktif";
        this.fotoPath = null;
    }

    public Student(String nim, String nama, String jurusan, String angkatan, String status, String fotoPath) {
        this.nim = nim;
        this.nama = nama;
        this.jurusan = jurusan;
        this.angkatan = angkatan;
        this.status = status;
        this.fotoPath = fotoPath;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }

    public String getAngkatan() {
        return angkatan;
    }

    public void setAngkatan(String angkatan) {
        this.angkatan = angkatan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFotoPath() {
        return fotoPath;
    }

    public void setFotoPath(String fotoPath) {
        this.fotoPath = fotoPath;
    }
}
