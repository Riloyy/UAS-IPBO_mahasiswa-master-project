package com.ubl.datamahasiswa.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {

    private static final String DB_URL = "jdbc:sqlite:mahasiswa.db";

    private Database() {
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void init() {
        String[][] seed = {
            {"2311510147", "Siti Rahmawati", "Informatika", "2023", "Aktif"},
            {"2311510392", "Budi Santoso", "Sistem Informasi", "2023", "Aktif"},
            {"2311510420", "Agus Budi Hartoyo", "Informatika", "2023", "Aktif"},
            {"2311510487", "Muhammad Ihsan Maulana", "Teknik Komputer", "2023", "Aktif"},
            {"2311510578", "Dwi Purwaningsih", "Informatika", "2023", "Cuti"},
            {"2311510628", "Ridho Ikhwan", "Sistem Informasi", "2023", "Aktif"},
            {"2311510669", "Chaisar Wildan", "Informatika", "2023", "Aktif"},
            {"2311510677", "Nindi Auziah", "Teknik Komputer", "2023", "Aktif"}
        };
        try (Connection conn = connect(); Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS mahasiswa ("
                    + "nim TEXT PRIMARY KEY NOT NULL, "
                    + "nama TEXT NOT NULL, "
                    + "jurusan TEXT, "
                    + "angkatan TEXT, "
                    + "status TEXT, "
                    + "foto_path TEXT)");
            // migrasi kolom untuk DB lama
            addColumnIfMissing(conn, "jurusan", "TEXT");
            addColumnIfMissing(conn, "angkatan", "TEXT");
            addColumnIfMissing(conn, "status", "TEXT");
            addColumnIfMissing(conn, "foto_path", "TEXT");

            boolean empty = true;
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM mahasiswa")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    empty = false;
                }
            }
            if (empty) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO mahasiswa (nim, nama, jurusan, angkatan, status, foto_path) VALUES (?, ?, ?, ?, ?, ?)")) {
                    for (String[] row : seed) {
                        ps.setString(1, row[0]);
                        ps.setString(2, row[1]);
                        ps.setString(3, row[2]);
                        ps.setString(4, row[3]);
                        ps.setString(5, row[4]);
                        ps.setString(6, null);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            } else {
                // backfill kolom baru untuk data lama yang null
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE mahasiswa SET jurusan = COALESCE(jurusan, 'Informatika'), "
                                + "angkatan = COALESCE(angkatan, '2023'), "
                                + "status = COALESCE(status, 'Aktif') "
                                + "WHERE jurusan IS NULL OR angkatan IS NULL OR status IS NULL")) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal menyiapkan database: " + e.getMessage(), e);
        }
    }

    private static void addColumnIfMissing(Connection conn, String column, String type) {
        try (Statement st = conn.createStatement()) {
            st.execute("ALTER TABLE mahasiswa ADD COLUMN " + column + " " + type);
        } catch (SQLException ignored) {
            // kolom sudah ada
        }
    }
}
