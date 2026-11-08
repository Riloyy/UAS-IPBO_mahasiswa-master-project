package com.ubl.datamahasiswa.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProfilePhotoFactory {
    private static final Map<String, BufferedImage> CACHE = new ConcurrentHashMap<>();
    private static final Color[] PALETTE = new Color[] {
        new Color(0xE53935), new Color(0xD81B60), new Color(0x8E24AA), new Color(0x5E35B1),
        new Color(0x3949AB), new Color(0x1E88E5), new Color(0x039BE5), new Color(0x00ACC1),
        new Color(0x00897B), new Color(0x43A047), new Color(0x7CB342), new Color(0xC0CA33),
        new Color(0xFDD835), new Color(0xFB8C00), new Color(0xF4511E)
    };

    public static BufferedImage get(String nim, String nama, int size) {
        String key = nim + ":" + size;
        BufferedImage cached = CACHE.get(key);
        if (cached != null) return cached;
        BufferedImage img = render(nim, nama, size);
        CACHE.put(key, img);
        return img;
    }

    private static BufferedImage render(String nim, String nama, int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Color bg = PALETTE[(nim.hashCode() & 0x7fffffff) % PALETTE.length];

        // inner shadow/ring
        int inset = Math.max(1, size / 16);
        g.setColor(bg.darker());
        g.fillOval(0, 0, size, size);
        g.setColor(bg);
        g.fillOval(inset, inset, size - 2 * inset, size - 2 * inset);

        // initials
        String initials = initialsOf(nama);
        int fontSize = Math.max(10, size / 2);
        g.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        int x = (size - fm.stringWidth(initials)) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(initials, x, y);

        g.dispose();
        return img;
    }

    private static String initialsOf(String nama) {
        if (nama == null || nama.trim().isEmpty()) return "??";
        String[] parts = nama.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++) {
            if (!parts[i].isEmpty()) sb.append(Character.toUpperCase(parts[i].charAt(0)));
        }
        if (sb.length() == 0) return "??";
        if (sb.length() == 1) sb.append(sb.charAt(0));
        return sb.toString();
    }
}
