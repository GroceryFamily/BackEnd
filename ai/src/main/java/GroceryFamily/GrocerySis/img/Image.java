package GroceryFamily.GrocerySis.img;

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