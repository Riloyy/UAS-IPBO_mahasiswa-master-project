package com.ubl.datamahasiswa.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

public class RoundedButton extends JButton {

    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);

    private final Color base;
    private final Color hover;
    private final Color pressed;
    private boolean hovered;
    private boolean down;

    public RoundedButton(String text, Color baseColor, Color textColor) {
        super(text);
        this.base = baseColor;
        this.hover = mix(baseColor, 0.16f);
        this.pressed = darken(baseColor, 0.10f);

        setFont(BUTTON_FONT);
        setForeground(textColor);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        FontMetrics fm = getFontMetrics(getFont());
        setPreferredSize(new Dimension(fm.stringWidth(text) + 36, 38));
        setMaximumSize(getPreferredSize());
        setMinimumSize(getPreferredSize());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                down = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                down = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                down = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color c = down ? pressed : (hovered ? hover : base);
        g2.setColor(c);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
        g2.dispose();
        super.paintComponent(g);
    }

    private static Color mix(Color src, float f) {
        return new Color(
            src.getRed() + Math.round((255 - src.getRed()) * f),
            src.getGreen() + Math.round((255 - src.getGreen()) * f),
            src.getBlue() + Math.round((255 - src.getBlue()) * f));
    }

    private static Color darken(Color src, float f) {
        return new Color(
            Math.max(0, Math.round(src.getRed() * (1f - f))),
            Math.max(0, Math.round(src.getGreen() * (1f - f))),
            Math.max(0, Math.round(src.getBlue() * (1f - f))));
    }
}
