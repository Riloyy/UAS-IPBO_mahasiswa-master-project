package com.ubl.datamahasiswa.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("Aplikasi Manajemen Data Mahasiswa - Universitas Budi Luhur");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1180, 720);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(980, 620));

        TiledBackgroundPanel content = new TiledBackgroundPanel();
        content.setLayout(new BorderLayout());
        setContentPane(content);

        content.add(new HeaderPanel(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(20, 28, 24, 28));

        StudentTablePanel tablePanel = new StudentTablePanel();
        DetailSidePanel sidePanel = new DetailSidePanel();

        center.add(tablePanel, BorderLayout.CENTER);
        center.add(sidePanel, BorderLayout.EAST);
        content.add(center, BorderLayout.CENTER);

        tablePanel.addSelectionListener(s -> {
            sidePanel.showStudent(s);
            content.setTheme(s);
        });
    }
}
