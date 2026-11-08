package com.ubl.datamahasiswa.dao;

import com.ubl.datamahasiswa.database.Database;
import com.ubl.datamahasiswa.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    private static final String COLS = "nim, nama, jurusan, angkatan, status, foto_path";

    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM mahasiswa ORDER BY nim ASC";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal memuat data mahasiswa: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Student> search(String keyword) {
        List<Student> list = new ArrayList<>();
        String pat = "%" + keyword + "%";
        String sql = "SELECT " + COLS + " FROM mahasiswa WHERE nim LIKE ? OR nama LIKE ? OR jurusan LIKE ? ORDER BY nim ASC";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pat);
            ps.setString(2, pat);
            ps.setString(3, pat);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal mencari data mahasiswa: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Student> findByRange(String nimAwal, String nimAkhir) {
        List<Student> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT " + COLS + " FROM mahasiswa WHERE nim >= ?");
        if (nimAkhir != null) {
            sql.append(" AND nim <= ?");
        }
        sql.append(" ORDER BY nim ASC");
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, nimAwal);
            if (nimAkhir != null) {
                ps.setString(2, nimAkhir);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal memuat data laporan: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean exists(String nim) {
        String sql = "SELECT 1 FROM mahasiswa WHERE nim = ?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nim);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal memeriksa NIM: " + e.getMessage(), e);
        }
    }

    public boolean insert(Student s) {
        String sql = "INSERT INTO mahasiswa (nim, nama, jurusan, angkatan, status, foto_path) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, s, 1);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal menyimpan data mahasiswa: " + e.getMessage(), e);
        }
    }

    public boolean update(String nimLama, Student s) {
        String sql = "UPDATE mahasiswa SET nim = ?, nama = ?, jurusan = ?, angkatan = ?, status = ?, foto_path = ? WHERE nim = ?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bind(ps, s, 1);
            ps.setString(7, nimLama);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal memperbarui data mahasiswa: " + e.getMessage(), e);
        }
    }

    public boolean delete(String nim) {
        String sql = "DELETE FROM mahasiswa WHERE nim = ?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nim);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal menghapus data mahasiswa: " + e.getMessage(), e);
        }
    }

    private void bind(PreparedStatement ps, Student s, int start) throws SQLException {
        ps.setString(start, s.getNim());
        ps.setString(start + 1, s.getNama());
        ps.setString(start + 2, s.getJurusan());
        ps.setString(start + 3, s.getAngkatan());
        ps.setString(start + 4, s.getStatus());
        ps.setString(start + 5, s.getFotoPath());
    }

    private Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setNim(rs.getString("nim"));
        s.setNama(rs.getString("nama"));
        s.setJurusan(rs.getString("jurusan"));
        s.setAngkatan(rs.getString("angkatan"));
        s.setStatus(rs.getString("status"));
        s.setFotoPath(rs.getString("foto_path"));
        if (s.getJurusan() == null) s.setJurusan("Informatika");
        if (s.getAngkatan() == null) s.setAngkatan("2023");
        if (s.getStatus() == null) s.setStatus("Aktif");
        return s;
    }
}
