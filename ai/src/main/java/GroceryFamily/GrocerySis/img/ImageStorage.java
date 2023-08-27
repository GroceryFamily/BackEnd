package GroceryFamily.GrocerySis.img;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.String.format;

class ImageStorage {
    private static final String FORMAT = "png";

    private final Path directory;

    ImageStorage(Path directory) {
        this.directory = directory;
    }

    public boolean exists(String namespace, String code) {
        return Files.exists(file(namespace, code, false));
    }

    public void save(String namespace, String code, BufferedImage image) {
        var file = file(namespace, code, true);
        boolean success;
        try {
            success = ImageIO.write(image, FORMAT, file.toFile());
        } catch (IOException e) {
            throw new RuntimeException(format("Failed to save image '%s::%s'", namespace, code), e);
        }
        if (!success) {
            throw new RuntimeException(format("Image writer not found for '%s::%s", namespace, code));
        }
    }

    public BufferedImage load(String namespace, String code) {
        var file = file(namespace, code, false);
        try {
            return ImageIO.read(file.toFile());
        } catch (IOException e) {
            throw new RuntimeException(format("Failed to load image '%s::%s'", namespace, code), e);
        }
    }

    private Path file(String namespace, String code, boolean provideDirectory) {
        var subdirectory = directory.resolve(namespace);
        var fileName = code + "." + FORMAT;
        return (provideDirectory ? provideDirectory(subdirectory) : subdirectory).resolve(fileName);
    }

    private static Path provideDirectory(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(format("Failed to create directory '%s'", directory), e);
            }
        }
        return directory;
    }
}