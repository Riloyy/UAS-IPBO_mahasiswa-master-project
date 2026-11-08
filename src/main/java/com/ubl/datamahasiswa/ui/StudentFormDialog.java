package com.ubl.datamahasiswa.ui;

import com.ubl.datamahasiswa.dao.StudentDAO;
import com.ubl.datamahasiswa.model.Student;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.awt.Window;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class StudentFormDialog extends javax.swing.JDialog {

    private static final Color NAVY = new Color(0x0D2038);
    private static final Color STEEL = new Color(0x3B6EA5);
    private static final Color TEXT = new Color(0x22333B);
    private static final Color GRAY = new Color(0x6E7B8A);
    private static final Color FIELD_BORDER = new Color(0xAFCFC2);

    private final StudentDAO dao = new StudentDAO();
    private final Student existing;

    private JTextField nimField;
    private JTextField namaField;
    private JTextField jurusanField;
    private JTextField angkatanField;
    private JComboBox<String> statusCombo;
    private JLabel photoPreview;
    private String fotoPath;
    private boolean saved;

    public StudentFormDialog(Window owner, Student existing) {
        super(owner, existing == null ? "Tambah Mahasiswa" : "Edit Mahasiswa",
                ModalityType.APPLICATION_MODAL);
        this.existing = existing;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(22, 26, 22, 26));
        setContentPane(root);

        JLabel heading = new JLabel(existing == null ? "Data Mahasiswa Baru" : "Edit: "
                + existing.getNama());
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(NAVY);
        root.add(heading, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);

        // photo preview
        JPanel photoPanel = new JPanel();
        photoPanel.setOpaque(false);
        photoPanel.setLayout(new BoxLayout(photoPanel, BoxLayout.Y_AXIS));
        photoPreview = new JLabel();
        photoPreview.setPreferredSize(new Dimension(110, 110));
        photoPreview.setMinimumSize(new Dimension(110, 110));
        photoPreview.setMaximumSize(new Dimension(110, 110));
        photoPreview.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        RoundedButton btnPickPhoto = new RoundedButton("Pilih Foto", STEEL, Color.WHITE);
        btnPickPhoto.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        btnPickPhoto.addActionListener(e -> onPickPhoto());
        photoPanel.add(photoPreview);
        photoPanel.add(Box.createVerticalStrut(8));
        photoPanel.add(btnPickPhoto);
        body.add(photoPanel, BorderLayout.WEST);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 12, 6, 0);
        gc.anchor = GridBagConstraints.WEST;

        nimField = new JTextField(14);
        namaField = new JTextField(20);
        jurusanField = new JTextField(20);
        angkatanField = new JTextField(8);
        statusCombo = new JComboBox<>(new String[]{"Aktif", "Cuti", "Lulus", "Nonaktif"});

        styleField(nimField);
        styleField(namaField);
        styleField(jurusanField);
        styleField(angkatanField);

        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        statusCombo.setPreferredSize(new Dimension(200, 40));
        statusCombo.setBorder(new javax.swing.border.LineBorder(FIELD_BORDER, 1, true));

        int r = 0;
        addRow(form, gc, r++, "NIM", nimField);
        addRow(form, gc, r++, "Nama Lengkap", namaField);
        addRow(form, gc, r++, "Jurusan", jurusanField);
        addRow(form, gc, r++, "Angkatan", angkatanField);
        addRow(form, gc, r++, "Status", statusCombo);

        body.add(form, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);

        if (existing != null) {
            nimField.setText(existing.getNim());
            namaField.setText(existing.getNama());
            jurusanField.setText(existing.getJurusan() == null ? "" : existing.getJurusan());
            angkatanField.setText(existing.getAngkatan() == null ? "" : existing.getAngkatan());
            statusCombo.setSelectedItem(existing.getStatus() == null ? "Aktif" : existing.getStatus());
            fotoPath = existing.getFotoPath();
        } else {
            jurusanField.setText("Informatika");
            angkatanField.setText("2023");
            statusCombo.setSelectedItem("Aktif");
        }
        refreshPhotoPreview();

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        RoundedButton btnSave = new RoundedButton("Simpan", NAVY, Color.WHITE);
        RoundedButton btnCancel = new RoundedButton("Batal", new Color(0xD8E4DE), NAVY);
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave());
        buttons.add(btnCancel);
        buttons.add(btnSave);
        root.add(buttons, BorderLayout.SOUTH);

        pack();
        setSize(Math.max(getWidth(), 560), getHeight() + 10);
        setLocationRelativeTo(owner);
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        f.setPreferredSize(new Dimension(f.getColumns() * 10, 40));
        f.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(FIELD_BORDER, 1, true),
            new EmptyBorder(6, 10, 6, 10)));
    }

    private void addRow(JPanel form, GridBagConstraints gc, int row, String label, java.awt.Component field) {
        gc.gridx = 0;
        gc.gridy = row;
        gc.weightx = 0;
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(TEXT);
        form.add(l, gc);
        gc.gridx = 1;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        form.add(field, gc);
        gc.fill = GridBagConstraints.NONE;
    }

    private void onPickPhoto() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Gambar (jpg, png, gif)", "jpg", "jpeg", "png", "gif"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            fotoPath = f.getAbsolutePath();
            refreshPhotoPreview();
        }
    }

    private void refreshPhotoPreview() {
        Student tmp = new Student();
        tmp.setNim(nimField.getText().trim().isEmpty() ? "0000000000" : nimField.getText().trim());
        tmp.setNama(namaField.getText().trim().isEmpty() ? "Mahasiswa" : namaField.getText().trim());
        tmp.setFotoPath(fotoPath);
        BufferedImage img = UiUtil.loadPhoto(tmp, 110);
        photoPreview.setIcon(new ImageIcon(img));
    }

    public boolean isSaved() {
        return saved;
    }

    private void onSave() {
        String nim = nimField.getText().trim();
        String nama = namaField.getText().trim();
        String jurusan = jurusanField.getText().trim();
        String angkatan = angkatanField.getText().trim();
        String status = (String) statusCombo.getSelectedItem();

        if (nim.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIM tidak boleh kosong.", "Validasi", JOptionPane.WARNING_MESSAGE);
            nimField.requestFocus();
            return;
        }
        if (!nim.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this,
                    "NIM harus tepat 10 digit angka.\nContoh: 2311510420", "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            nimField.requestFocus();
            return;
        }
        boolean nimChanged = existing == null || !nim.equals(existing.getNim());
        if (nimChanged && dao.exists(nim)) {
            JOptionPane.showMessageDialog(this, "NIM " + nim + " sudah terdaftar.", "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            nimField.requestFocus();
            return;
        }
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong.", "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            namaField.requestFocus();
            return;
        }
        if (jurusan.isEmpty()) {
            jurusan = "Informatika";
        }
        if (angkatan.isEmpty()) {
            angkatan = "2023";
        }

        try {
            Student s = new Student(nim, nama, jurusan, angkatan, status, fotoPath);
            if (existing == null) {
                dao.insert(s);
                JOptionPane.showMessageDialog(this,
                        "Data mahasiswa berhasil ditambahkan:\nNIM      : " + nim
                                + "\nNama     : " + nama
                                + "\nJurusan  : " + jurusan
                                + "\nAngkatan : " + angkatan
                                + "\nStatus   : " + status,
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } else {
                dao.update(existing.getNim(), s);
                JOptionPane.showMessageDialog(this,
                        "Perubahan data mahasiswa berhasil disimpan:\nNIM      : " + nim
                                + "\nNama     : " + nama
                                + "\nJurusan  : " + jurusan
                                + "\nAngkatan : " + angkatan
                                + "\nStatus   : " + status,
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            }
            saved = true;
            dispose();
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Gagal Simpan", JOptionPane.ERROR_MESSAGE);
        }
    }
}
