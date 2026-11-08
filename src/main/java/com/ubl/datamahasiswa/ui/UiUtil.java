package com.ubl.datamahasiswa.ui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

public final class UiUtil {

    private UiUtil() {
    }

    public static BufferedImage loadLogo() {
        try (InputStream in = UiUtil.class.getResourceAsStream("/images/logo_ubl.png")) {
            if (in == null) {
                throw new IllegalStateException("Resource logo tidak ditemukan: /images/logo_ubl.png");
            }
            BufferedImage img = ImageIO.read(in);
            if (img == null) {
                throw new IllegalStateException("Format logo tidak dapat dibaca: /images/logo_ubl.png");
            }
            return img;
        } catch (Exception e) {
            throw new IllegalStateException("Gagal memuat logo UBL: " + e.getMessage(), e);
        }
    }

    public static BufferedImage scale(BufferedImage src, int targetW, int targetH) {
        BufferedImage out = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.drawImage(src, 0, 0, targetW, targetH, null);
        g2.dispose();
        return out;
    }

    /** Skala dengan proporsi asli, muat di dalam kotak maxW x maxH (tidak melar). */
    public static BufferedImage fit(BufferedImage src, int maxW, int maxH) {
        double ratio = Math.min((double) maxW / src.getWidth(), (double) maxH / src.getHeight());
        int w = Math.max(1, (int) Math.round(src.getWidth() * ratio));
        int h = Math.max(1, (int) Math.round(src.getHeight() * ratio));
        return scale(src, w, h);
    }

    public static BufferedImage loadPhoto(com.ubl.datamahasiswa.model.Student s, int size) {
        if (s != null && s.getFotoPath() != null && !s.getFotoPath().trim().isEmpty()) {
            java.io.File f = new java.io.File(s.getFotoPath());
            if (f.exists() && f.isFile()) {
                try (java.io.InputStream in = new java.io.FileInputStream(f)) {
                    BufferedImage img = javax.imageio.ImageIO.read(in);
                    if (img != null) {
                        return scale(cropSquare(img), size, size);
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return ProfilePhotoFactory.get(s == null ? "" : s.getNim(),
                s == null ? "" : s.getNama(), size);
    }

    public static BufferedImage cropSquare(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();
        if (w == h) return src;
        int side = Math.min(w, h);
        int x = (w - side) / 2;
        int y = (h - side) / 2;
        return src.getSubimage(x, y, side, side);
    }
}
