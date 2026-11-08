package com.ubl.datamahasiswa.ui;

import com.ubl.datamahasiswa.model.Student;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Window;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class DetailSidePanel extends WhiteCardPanel {

    private static final Color NAVY = new Color(0x0D2038);
    private static final Color GRAY = new Color(0x6E7B8A);

    private final JLabel avatarLabel = new JLabel();
    private final JLabel nimLabel = new JLabel();
    private final JLabel nameLabel = new JLabel();
    private final JLabel emptyLabel = new JLabel("Pilih mahasiswa untuk melihat detail");

    private Student currentStudent;

    public DetailSidePanel() {
        this(null);
    }

    public DetailSidePanel(Student s) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(20, 16, 20, 16));
        setPreferredSize(new Dimension(260, 480));
        setMaximumSize(new Dimension(260, Integer.MAX_VALUE));

        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarLabel.setPreferredSize(new Dimension(200, 200));
        avatarLabel.setMinimumSize(new Dimension(200, 200));
        nimLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyLabel.setForeground(GRAY);
        emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        if (s == null) {
            clear();
        } else {
            showStudent(s);
        }
    }

    public void showStudent(Student s) {
        if (s == null) {
            clear();
            return;
        }
        currentStudent = s;
        removeAll();

        avatarLabel.setIcon(new ImageIcon(UiUtil.loadPhoto(s, 200)));
        add(Box.createVerticalStrut(8));
        add(avatarLabel);
        add(Box.createVerticalStrut(12));

        nimLabel.setText(s.getNim() == null ? "" : s.getNim());
        nimLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nimLabel.setForeground(NAVY);
        add(nimLabel);
        add(Box.createVerticalStrut(2));

        nameLabel.setText(s.getNama() == null ? "" : s.getNama());
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        nameLabel.setForeground(Color.BLACK);
        add(nameLabel);
        add(Box.createVerticalStrut(12));

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(0xDCE9E2));
        add(sep);
        add(Box.createVerticalStrut(12));

        add(detailRow("Jurusan: " + (s.getJurusan() == null ? "-" : s.getJurusan())));
        add(Box.createVerticalStrut(4));
        add(detailRow("Angkatan: " + (s.getAngkatan() == null ? "-" : s.getAngkatan())));
        add(Box.createVerticalStrut(4));
        add(detailRow("Status: " + (s.getStatus() == null ? "-" : s.getStatus())));

        add(Box.createVerticalStrut(12));
        RoundedButton btnPhoto = new RoundedButton("Lihat Foto", NAVY, Color.WHITE);
        btnPhoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPhoto.addActionListener(e -> showPhotoDialog());
        add(btnPhoto);

        add(Box.createVerticalGlue());

        revalidate();
        repaint();
    }

    public void clear() {
        currentStudent = null;
        removeAll();

        JLabel ph = new JLabel(new ImageIcon(makePlaceholder(200)));
        ph.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createVerticalGlue());
        add(ph);
        add(Box.createVerticalStrut(12));
        add(emptyLabel);
        add(Box.createVerticalGlue());

        revalidate();
        repaint();
    }

    private JLabel detailRow(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        l.setForeground(Color.BLACK);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private void showPhotoDialog() {
        if (currentStudent == null) return;
        BufferedImage img = loadFullPhoto(currentStudent);
        if (img == null) {
            JOptionPane.showMessageDialog(this, "Foto tidak tersedia.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int max = 500;
        BufferedImage shown = (img.getWidth() <= max && img.getHeight() <= max)
                ? img
                : UiUtil.fit(img, max, max);

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Foto - " + (currentStudent.getNama() == null ? "" : currentStudent.getNama()), Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JLabel lbl = new JLabel(new ImageIcon(shown));
        lbl.setBorder(new EmptyBorder(12, 12, 12, 12));
        dlg.getContentPane().add(lbl);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private static BufferedImage loadFullPhoto(Student s) {
        if (s != null && s.getFotoPath() != null && !s.getFotoPath().trim().isEmpty()) {
            File f = new File(s.getFotoPath());
            if (f.exists() && f.isFile()) {
                try (java.io.InputStream in = new java.io.FileInputStream(f)) {
                    BufferedImage img = ImageIO.read(in);
                    if (img != null) return img;
                } catch (Exception ignored) {
                }
            }
        }
        return ProfilePhotoFactory.get(s == null ? "" : s.getNim(),
                s == null ? "" : s.getNama(), 400);
    }

    private static BufferedImage makePlaceholder(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(0xD8E4DE));
        g.fillOval(0, 0, size, size);
        g.setColor(new Color(0x9BB5AA));
        g.drawOval(0, 0, size - 1, size - 1);
        g.dispose();
        return img;
    }
}
