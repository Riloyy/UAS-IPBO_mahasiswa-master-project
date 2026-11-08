package com.ubl.datamahasiswa;

import com.ubl.datamahasiswa.dao.StudentDAO;
import com.ubl.datamahasiswa.database.Database;
import com.ubl.datamahasiswa.model.Student;
import com.ubl.datamahasiswa.report.StudentReportGenerator;
import com.ubl.datamahasiswa.ui.MainFrame;
import java.io.File;
import java.util.List;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class App {

    private App() {
    }

    public static void main(String[] args) throws Exception {
        // Render pada resolusi native (1:1) agar gambar tidak di-scale OS
        // (yang menyebabkan blur/pixel pada logo).
        System.setProperty("sun.java2d.uiScale", "1.0");
        System.setProperty("sun.java2d.d3d", "false");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // gunakan LAF bawaan bila sistem tidak tersedia
        }

        Database.init();

        if (args.length > 0 && "--test".equals(args[0])) {
            runSelfTest();
            return;
        }
        if (args.length > 0 && "--shot".equals(args[0])) {
            runScreenshot(args.length > 1 ? args[1] : "app_shot.png");
            return;
        }
        if (args.length > 0 && "--shot-selected".equals(args[0])) {
            runSelectedRowScreenshot(args.length > 1 ? args[1] : "app_shot_sel.png");
            return;
        }
        if (args.length > 0 && "--shot-report".equals(args[0])) {
            runReportDialogScreenshot(args.length > 1 ? args[1] : "report_shot.png");
            return;
        }

        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }

    /** Uji headless: seed CRUD + laporan PDF (tanpa membuka UI). */
    private static void runSelfTest() {
        try {
            StudentDAO dao = new StudentDAO();

            List<Student> all = dao.findAll();
            System.out.println("Data awal: " + all.size() + " mahasiswa");
            for (Student s : all) {
                System.out.println("  " + s.getNim() + " - " + s.getNama());
            }

            boolean inserted = dao.insert(new Student("9999999999", "Test Mahasiswa"));
            boolean updated = dao.update("9999999999", new Student("9999999999", "Test Mahasiswa Edit"));
            List<Student> range = dao.findByRange("2311510392", "2311510578");
            boolean deleted = dao.delete("9999999999");
            System.out.println("CRUD insert/update/range/delete: "
                    + inserted + "/" + updated + "/" + range.size() + "/" + deleted);

            String path = StudentReportGenerator.generate(range, "2311510392", "2311510578");
            System.out.println("PDF dibuat: " + path);

            boolean ok = inserted && updated && deleted
                    && range.size() == 4
                    && all.size() == 8
                    && new File(path).exists()
                    && new File(path).length() > 0;
            System.out.println(ok ? "SELFTEST OK" : "SELFTEST GAGAL");
            if (!ok) {
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /** Render tampilan UI ke file PNG secara offscreen (untuk verifikasi visual). */
    private static void runScreenshot(String outPath) {
        try {
            final File out = new File(outPath);
            SwingUtilities.invokeAndWait(() -> {
                try {
                    MainFrame frame = new MainFrame();
                    frame.pack();
                    frame.setSize(1024, Math.max(640, frame.getHeight()));
                    frame.validate();
                    paintToPng(frame.getContentPane(), frame.getWidth(), frame.getHeight(), out);
                    frame.dispose();
                    System.out.println("Screenshot: " + out.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /** Screenshot dengan satu baris tabel dipilih (debug warna seleksi/hover). */
    private static void runSelectedRowScreenshot(String outPath) {
        try {
            final File out = new File(outPath);
            SwingUtilities.invokeAndWait(() -> {
                try {
                    MainFrame frame = new MainFrame();
                    frame.pack();
                    frame.setSize(1024, Math.max(640, frame.getHeight()));
                    frame.validate();
                    javax.swing.JTable table = findTable(frame.getContentPane());
                    if (table != null) {
                        table.setRowSelectionInterval(2, 2);
                        System.out.println("selection: " + table.getSelectedRow()
                                + " renderer columns=" + table.getColumnModel().getColumnCount());
                        System.out.println("isSelectedIndex(2) = " + table.getSelectionModel().isSelectedIndex(2));
                        var r = table.getCellRenderer(2, 0);
                        var comp = r.getTableCellRendererComponent(table, "X", true, false, 2, 0);
                        System.out.println("rendererClass=" + r.getClass().getName());
                        System.out.println("bg(sel) = " + comp.getBackground());
                    } else {
                        System.out.println("TABLE TIDAK DITEMUKAN");
                    }
                    paintToPng(frame.getContentPane(), frame.getWidth(), frame.getHeight(), out);
                    frame.dispose();
                    System.out.println("Screenshot: " + out.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static javax.swing.JTable findTable(java.awt.Container c) {
        for (java.awt.Component comp : c.getComponents()) {
            if (comp instanceof javax.swing.JTable jt) {
                return jt;
            }
            if (comp instanceof java.awt.Container nested) {
                javax.swing.JTable t = findTable(nested);
                if (t != null) {
                    return t;
                }
            }
        }
        return null;
    }

    /** Render dialog rentang NIM ke file PNG secara offscreen. */
    private static void runReportDialogScreenshot(String outPath) {
        try {
            final File out = new File(outPath);
            SwingUtilities.invokeAndWait(() -> {
                try {
                    com.ubl.datamahasiswa.ui.ReportDialog dlg =
                            new com.ubl.datamahasiswa.ui.ReportDialog(null);
                    dlg.pack();
                    dlg.validate();
                    paintToPng(dlg.getContentPane(), Math.max(590, dlg.getWidth()), dlg.getHeight(), out);
                    dlg.dispose();
                    System.out.println("Screenshot: " + out.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void paintToPng(java.awt.Component c, int w, int h, File out) throws Exception {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        var g = img.createGraphics();
        c.setSize(w, h);
        c.doLayout();
        c.printAll(g);
        g.dispose();
        ImageIO.write(img, "png", out);
    }
}
