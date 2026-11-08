package com.ubl.datamahasiswa.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Panel putih dengan sudut membulat + bayangan halus,
 * ditempatkan di atas background yang bertabur logo.
 */
public abstract class WhiteCardPanel extends JPanel {

    private static final Color SHADOW = new Color(16, 42, 64, 40);
    private static final Color CARD = new Color(255, 255, 255, 180);
    private static final Color BORDER = new Color(0xE3EFE9, true);
    private static final int RADIUS = 20;

    protected WhiteCardPanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        g2.setColor(SHADOW);
        g2.fillRoundRect(5, 8, w - 10, h - 13, RADIUS, RADIUS);

        g2.setColor(CARD);
        g2.fillRoundRect(0, 0, w - 1, h - 1, RADIUS, RADIUS);

        g2.setColor(BORDER);
        g2.drawRoundRect(0, 0, w - 1, h - 1, RADIUS, RADIUS);

        g2.dispose();
        super.paintComponent(g);
    }
}
