package com.ubl.datamahasiswa.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Header tetap: banner dark navy berisi logo UBL (di-render HD
 * dengan interpolasi bicubic + anti-aliasing) + teks vektor Swing.
 */
public class HeaderPanel extends JPanel {

    private static final Color NAVY = new Color(0x0D2038);
    private static final Color SUBTITLE = new Color(0xAFC2D8);
    private static final int HEIGHT = 144;
    private static final int LOGO_H = 128;

    private final BufferedImage logoImg;

    public HeaderPanel() {
        BufferedImage logo = UiUtil.loadLogo();
        logoImg = UiUtil.fit(logo, 200, LOGO_H);

        JLabel titleLabel = new JLabel("UNIVERSITAS BUDI LUHUR");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Aplikasi Manajemen Data Mahasiswa");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(SUBTITLE);

        JPanel textStack = new JPanel();
        textStack.setOpaque(false);
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        textStack.add(titleLabel);
        textStack.add(Box.createVerticalStrut(2));
        textStack.add(subtitleLabel);

        // placeholder label to push text right of logo
        JLabel spacer = new JLabel();
        spacer.setPreferredSize(new Dimension(logoImg.getWidth() + 18, 1));

        setLayout(new FlowLayout(FlowLayout.LEFT, 18, (HEIGHT - LOGO_H) / 2));
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        add(logoLabel);
        add(textStack);

        setPreferredSize(new Dimension(120, HEIGHT));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, HEIGHT));
        setMinimumSize(new Dimension(0, HEIGHT));
        setBackground(NAVY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setColor(NAVY);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
