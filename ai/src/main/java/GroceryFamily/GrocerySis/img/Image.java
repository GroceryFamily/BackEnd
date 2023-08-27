package GroceryFamily.GrocerySis.img;

import java.awt.*;
import java.awt.image.BufferedImage;

class Image {
    final BufferedImage image;
    private final int bgColor;

    Image(BufferedImage image, int bgColor) {
        this.image = image;
        this.bgColor = bgColor;
    }

    Image trim() {
        int x0 = x0Trimmed();
        int x1 = x1Trimmed();
        int y0 = y0Trimmed();
        int y1 = y1Trimmed();
        var newImage = image.getSubimage(x0, y0, x1 - x0, y1 - y0);
        return new Image(newImage, bgColor);
    }

    Image square() {
        int w = image.getWidth();
        int h = image.getHeight();
        int s = Math.max(w, h);
        var newImage = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        var drawer = newImage.createGraphics();
        drawer.setBackground(new Color(bgColor));
        drawer.clearRect(0, 0, s, s);
        drawer.drawImage(image, (s - w) / 2, (s - h) / 2, new Color(bgColor), null);
//        drawer.drawImage(image, 0, 0, new Color(bgColor, false), null);
        return new Image(newImage, bgColor);
    }

    Image resize(int newSize) {
        var resized = new BufferedImage(newSize, newSize, image.getType());
        var g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(image, 0, 0, newSize, newSize, 0, 0, image.getWidth(), image.getHeight(), null);
        g.dispose();
        return new Image(resized, bgColor);
    }

    Image gray() {
        var newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = newImage.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return new Image(newImage, bgColor);
    }

    private int x0Trimmed() {
        var x = 0;
        while (x < image.getWidth()) {
            for (int y = 0; y < image.getHeight(); ++y) {
                if (image.getRGB(x, y) != bgColor) return x;
            }
            ++x;
        }
        throw new IllegalStateException("Empty image");
    }

    private int x1Trimmed() {
        var x = image.getWidth() - 1;
        while (x >= 0) {
            for (int y = 0; y < image.getHeight(); ++y) {
                if (image.getRGB(x, y) != bgColor) return x;
            }
            --x;
        }
        throw new IllegalStateException("Empty image");
    }

    private int y0Trimmed() {
        var y = 0;
        while (y < image.getHeight()) {
            for (int x = 0; x < image.getWidth(); ++x) {
                if (image.getRGB(x, y) != bgColor) return y;
            }
            ++y;
        }
        throw new IllegalStateException("Empty image");
    }

    private int y1Trimmed() {
        var y = image.getHeight() - 1;
        while (y >= 0) {
            for (int x = 0; x < image.getWidth(); ++x) {
                if (image.getRGB(x, y) != bgColor) return y;
            }
            --y;
        }
        throw new IllegalStateException("Empty image");
    }
}