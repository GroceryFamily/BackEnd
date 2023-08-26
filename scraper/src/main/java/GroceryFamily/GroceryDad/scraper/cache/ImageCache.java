package GroceryFamily.GroceryDad.scraper.cache;

import GroceryFamily.GroceryDad.GroceryDadConfig;
import GroceryFamily.GroceryDad.scraper.model.Link;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.apache.commons.lang3.StringUtils.substringAfter;

@Component
public class ImageCache {
    private static final String FORMAT = "png";
    private static final String FILE_NAME = "image." + FORMAT;


    private final Path directory;

    ImageCache(GroceryDadConfig config) {
        this.directory = config.cacheDirectory;
    }

    public boolean exists(String platform, Link link) {
        return Files.exists(subdirectory(platform, link).resolve(FILE_NAME));
    }

    @SneakyThrows
    public void save(String platform, Link link, String url) {
        BufferedImage image = ImageIO.read(new URI(url).toURL());

        final BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        convertedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);

        Path file = subdirectory(platform, link).resolve(FILE_NAME);
        boolean canWrite = ImageIO.write(convertedImage, FORMAT, file.toFile());
        if (!canWrite) {
            throw new RuntimeException("Failed to find image writer");
        }
    }

    @SneakyThrows
    private String format(String url) {
        HttpURLConnection connection = (HttpURLConnection) new URI(url).toURL().openConnection();
        connection.setRequestMethod("HEAD");
        connection.connect();
        String contentType = connection.getContentType();
        connection.disconnect();
        return substringAfter(contentType, "/");
    }

    private Path subdirectory(String platform, Link link) {
        var subdirectory = directory.resolve(platform);
        for (var code : link.codePath()) {
            subdirectory = subdirectory.resolve(code);
        }
        return subdirectory;
    }
}