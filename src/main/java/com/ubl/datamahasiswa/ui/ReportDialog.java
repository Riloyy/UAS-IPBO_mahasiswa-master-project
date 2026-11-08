package com.ubl.datamahasiswa.ui;

import com.ubl.datamahasiswa.dao.StudentDAO;
import com.ubl.datamahasiswa.model.Student;
import com.ubl.datamahasiswa.report.StudentReportGenerator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

/**
 * Form rentang NIM sebelum mencetak laporan PDF.
 *
 * Aturan validasi (hasil diskusi):
 * 1. Form 1 & 2 kosong            -> popup: minimal isi Form 1
 * 2. Form 1 kosong, Form 2 terisi -> popup: wajib isi Form 1 sebagai acuan awal
 * 3. Form 1 terisi, Form 2 kosong -> cetak dari Form 1 s/d NIM terakhir
 * 4. Keduanya terisi              -> rentang normal (BETWEEN)
 * 5. A = B                        -> 1 mahasiswa
 * 6. A > B                        -> auto-swap + popup info
 */
public class ReportDialog extends JDialog {

    private static final Color NAVY = new Color(0x0D2038);
    private static final Color AMBER = new Color(0xE8940F);
    private static final Color AMBER_TEXT = new Color(0x272100);
    private static final Color TEXT = new Color(0x22333B);
    private static final Color GRAY = new Color(0x6E7B8A);
    private static final Color FIELD_BORDER = new Color(0xAFCFC2);

    private final StudentDAO dao = new StudentDAO();

    private NimSuggestField nimAwalField;
    private NimSuggestField nimAkhirField;

    public ReportDialog(Window owner) {
        super(owner, "Cetak Laporan - Rentang NIM", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel();
        root.setBackground(Color.WHITE);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(22, 26, 22, 26));
        setContentPane(root);

        JLabel heading = new JLabel("Cetak Laporan Data Mahasiswa (PDF)");
        heading.setAlignmentX(LEFT_ALIGNMENT);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setForeground(NAVY);
        root.add(heading);

        JLabel desc = new JLabel("Rentang NIM ditentukan lewat query WHERE (NIM awal & NIM akhir).");
        desc.setAlignmentX(LEFT_ALIGNMENT);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        desc.setForeground(GRAY);
        root.add(desc);

        JPanel spacer1 = new JPanel();
        spacer1.setOpaque(false);
        spacer1.setMaximumSize(new java.awt.Dimension(1, 14));
        root.add(spacer1);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setAlignmentX(LEFT_ALIGNMENT);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 0, 6, 0);
        gc.anchor = GridBagConstraints.WEST;

        nimAwalField = NimSuggestField.fromDao(dao);
        nimAkhirField = NimSuggestField.fromDao(dao);

        gc.gridx = 0;
        gc.gridy = 0;
        form.add(label("Form 1 - NIM Awal (wajib)"), gc);
        gc.gridx = 1;
        form.add(nimAwalField, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        form.add(label("Form 2 - NIM Akhir (opsional)"), gc);
        gc.gridx = 1;
        form.add(nimAkhirField, gc);
        root.add(form);

        JLabel hint = new JLabel("Kosongkan Form 2 = cetak sampai NIM terakhir.");
        hint.setAlignmentX(LEFT_ALIGNMENT);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        hint.setForeground(GRAY);
        root.add(hint);

        JLabel hint2 = new JLabel("Format NIM: 10 digit angka, contoh 2311510420.");
        hint2.setAlignmentX(LEFT_ALIGNMENT);
        hint2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        hint2.setForeground(GRAY);
        root.add(hint2);

        JPanel spacer2 = new JPanel();
        spacer2.setOpaque(false);
        spacer2.setMaximumSize(new java.awt.Dimension(1, 16));
        root.add(spacer2);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setAlignmentX(LEFT_ALIGNMENT);
        buttons.setOpaque(false);
        RoundedButton btnPrint = new RoundedButton("Cetak Laporan (PDF)", NAVY, Color.WHITE);
        RoundedButton btnCancel = new RoundedButton("Batal", new Color(0xD8E4DE), NAVY);
        btnPrint.addActionListener(e -> onPrint());
        btnCancel.addActionListener(e -> dispose());
        buttons.add(btnCancel);
        buttons.add(btnPrint);
        root.add(buttons);

        pack();
        setSize(Math.max(getWidth(), 590), getHeight() + 8);
        setLocationRelativeTo(owner);
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(TEXT);
        return l;
    }

    private void onPrint() {
        String a = nimAwalField.getText().trim();
        String b = nimAkhirField.getText().trim();

        if (a.isEmpty() && b.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Minimal isi Form 1 (NIM awal).", "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (a.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Wajib isi Form 1 (NIM awal) sebagai acuan mulai.", "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!a.matches("\\d{10}") || (!b.isEmpty() && !b.matches("\\d{10}"))) {
            JOptionPane.showMessageDialog(this,
                    "NIM harus tepat 10 digit angka.\nContoh: 2311510420", "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String awal = a;
        String akhir;

        if (b.isEmpty()) {
            akhir = null;
        } else {
            akhir = b;
            int cmp = akhir.compareTo(awal);
            if (cmp < 0) {
                String t = awal;
                awal = akhir;
                akhir = t;
                JOptionPane.showMessageDialog(this,
                        "NIM awal lebih besar dari NIM akhir - rentang otomatis dibalik.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        List<Student> rows = dao.findByRange(awal, akhir);

        if (rows.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Tidak ada mahasiswa dalam rentang " + awal + " s/d "
                            + (akhir == null ? "(NIM terakhir)" : akhir) + ".\nPDF dengan halaman kosong tetap dibuat.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
        }

        try {
            String path = StudentReportGenerator.generate(rows, awal, akhir);
            JOptionPane.showMessageDialog(this,
                    "Laporan PDF berhasil dibuat:\n" + path, "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal membuat laporan:\n" + ex.getMessage(), "Gagal",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
