package GroceryFamily.GrocerySis.img;

import GroceryFamily.GroceryElders.domain.Detail;
import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.GrocerySis.GrocerySisConfig;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.String.format;

@Component
public class ImageLoader {
    private static final String FORMAT = "png";

    private final Path directory;

    ImageLoader(GrocerySisConfig config) {
        this.directory = config.images;
    }

    public boolean exists(Product product) {
        return Files.exists(file(product, false));
    }

    @SneakyThrows
    public BufferedImage load(Product product) {
        var url = product.details.get(Detail.IMAGE);
        if (url == null) return null;
        var file = file(product, false);
        if (!Files.exists(file)) {
            file = file(product, true);
            var image = ImageIO.read(new URI(url).toURL());
            var convertedImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            convertedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
            boolean canWrite = ImageIO.write(convertedImage, FORMAT, file.toFile());
            if (!canWrite) throw new RuntimeException(format("Failed to persist image '%s", url));
        }
        return ImageIO.read(file.toFile());
    }

    private Path file(Product product, boolean provideDirectory) {
        var subdirectory = directory.resolve(product.namespace);
        var fileName = product.code + "." + FORMAT;
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