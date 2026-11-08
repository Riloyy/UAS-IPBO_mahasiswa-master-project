package com.ubl.datamahasiswa.ui;

import com.ubl.datamahasiswa.model.Student;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JPanel;

/**
 * Content pane: background mint yang bukan polos - taburan logo UBL
 * asli (warna aslinya) semi transparan, disebar ke banyak titik
 * dengan rotasi acak per tile dan pergeseran agresif saat ganti
 * mahasiswa.
 */
public class TiledBackgroundPanel extends JPanel {

    private static final Color DEFAULT_BG = new Color(0xBFEBDC);
    private static final float TILE_ALPHA = 0.30f;
    private static final int TILE_SIZE = 240;
    private static final int GAP_X = 100;
    private static final int GAP_Y = 60;

    private final BufferedImage tile;
    private Color bg = DEFAULT_BG;
    private int tileOffset = 0;
    private int gapJitter = 0;
    private Student current = null;

    public TiledBackgroundPanel() {
        setOpaque(true);
        setBackground(bg);

        BufferedImage logo = UiUtil.loadLogo();
        int h = TILE_SIZE * logo.getHeight() / logo.getWidth();
        tile = UiUtil.scale(logo, TILE_SIZE, h);
    }

    public void setTheme(Student s) {
        current = s;
        if (s == null) {
            bg = DEFAULT_BG;
            tileOffset = 0;
            gapJitter = 0;
        } else {
            long hh = 0;
            for (int i = 0; i < s.getNim().length(); i++) {
                hh = hh * 31L + s.getNim().charAt(i);
            }
            hh = hh * 0x9E3779B97F4A7C15L;
            float hue = (float) ((hh & 0xFFFFFFFFL) / (double) 0x100000000L);
            bg = Color.getHSBColor(hue, 0.55f, 0.92f);
            tileOffset = (int) ((hh >>> 4) % 180) - 90;
            gapJitter = (int) ((hh >>> 12) % 60);
        }
        setBackground(bg);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (tile == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Composite old = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, TILE_ALPHA));

        long seed = (current == null) ? 0L : current.getNim().hashCode();
        seed = seed * 0x9E3779B97F4A7C15L + 0x12345L;
        Random rnd = new Random(seed);

        int stepX = TILE_SIZE + GAP_X + gapJitter;
        int stepY = tile.getHeight() + GAP_Y + (gapJitter / 2);
        boolean offsetRow = false;

        for (int y = 20 + tileOffset; y < getHeight() + tile.getHeight(); y += stepY) {
            int startX = (offsetRow ? -30 : 40) + tileOffset;
            for (int x = startX; x < getWidth() + tile.getWidth(); x += stepX) {
                double angle = rnd.nextDouble() * Math.PI * 2.0;
                int cx = x + tile.getWidth() / 2;
                int cy = y + tile.getHeight() / 2;
                AffineTransform oldT = g2.getTransform();
                g2.rotate(angle, cx, cy);
                g2.drawImage(tile, x, y, null);
                g2.setTransform(oldT);
            }
            offsetRow = !offsetRow;
        }
        g2.setComposite(old);
        g2.dispose();
    }
}
