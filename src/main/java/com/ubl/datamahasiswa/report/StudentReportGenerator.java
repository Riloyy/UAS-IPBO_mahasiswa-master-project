package com.ubl.datamahasiswa.report;

import com.ubl.datamahasiswa.model.Student;
import com.ubl.datamahasiswa.ui.UiUtil;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public final class StudentReportGenerator {

    private StudentReportGenerator() {
    }

    /**
     * Membuat laporan PDF dari daftar mahasiswa (hasil WHERE rentang NIM)
     * ke folder reports/, lalu mengembalikan path absolut filenya.
     */
    public static String generate(List<Student> students, String nimAwal, String nimAkhir) throws Exception {
        InputStream jrxmlStream = StudentReportGenerator.class
                .getResourceAsStream("/StudentReport.jrxml");
        if (jrxmlStream == null) {
            throw new IllegalStateException("Template StudentReport.jrxml tidak ditemukan di resources.");
        }
        JasperReport report;
        String path;
        try (InputStream in = jrxmlStream) {
            report = JasperCompileManager.compileReport(in);
        }

        Map<String, Object> params = new HashMap<>();
        BufferedImage logo = UiUtil.loadLogo();
        params.put("LOGO_IMG", logo);
        params.put("NIM_AWAL", nimAwal);
        params.put("NIM_AKHIR", nimAkhir == null ? "( sampai NIM terakhir )" : nimAkhir);

        JasperPrint print = JasperFillManager.fillReport(report, params,
                new JRBeanCollectionDataSource(students));

        java.io.File outDir = new java.io.File("reports");
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "Laporan_Mahasiswa_" + nimAwal + "_"
                + (nimAkhir == null ? "ALL" : nimAkhir) + "_" + ts + ".pdf";
        path = "reports" + java.io.File.separator + fileName;

        JasperExportManager.exportReportToPdfFile(print, path);
        return new java.io.File(path).getAbsolutePath();
    }
}
