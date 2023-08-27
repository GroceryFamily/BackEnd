package GroceryFamily.GrocerySis.img;

import GroceryFamily.GroceryElders.domain.Detail;
import GroceryFamily.GroceryElders.domain.Product;
import GroceryFamily.GrocerySis.GrocerySisConfig;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URI;

@Component
public class ImageLoader {
    private final ImageStorage rawStorage;
    private final ImageStorage trimmedStorage;
    private final ImageStorage squaredStorage;
    private final ImageStorage resizedStorage;

    ImageLoader(GrocerySisConfig config) {
        rawStorage = new ImageStorage(config.rawImages);
        trimmedStorage = new ImageStorage(config.trimmedImages);
        squaredStorage = new ImageStorage(config.squaredImages);
        resizedStorage = new ImageStorage(config.resizedImages);
    }

    public boolean exists(Product product) {
        return rawStorage.exists(product.namespace, product.code);
    }

    @SneakyThrows
    public BufferedImage raw(Product product) {
        if (!rawStorage.exists(product.namespace, product.code)) {
            var url = product.details.get(Detail.IMAGE);
            if (url == null) return null;
            var raw = ImageIO.read(new URI(url).toURL());
            var convertedImage = new BufferedImage(raw.getWidth(), raw.getHeight(), raw.getType());
            convertedImage.createGraphics().drawImage(raw, 0, 0, Color.WHITE, null);
            rawStorage.save(product.namespace, product.code, raw);
        }
        return rawStorage.load(product.namespace, product.code);
    }

    public BufferedImage trimmed(Product product) {
        if (!trimmedStorage.exists(product.namespace, product.code)) {
            var raw = raw(product);
            if (raw == null) return null;
            var trimmed = new Image(raw, raw.getRGB(0, 0)).trim().image;
            trimmedStorage.save(product.namespace, product.code, trimmed);
        }
        return trimmedStorage.load(product.namespace, product.code);
    }

    public BufferedImage squared(Product product) {
        if (!squaredStorage.exists(product.namespace, product.code)) {
            var trimmed = trimmed(product);
            if (trimmed == null) return null;
            var squared = new Image(trimmed, Color.WHITE.getRGB()).square().image;
            squaredStorage.save(product.namespace, product.code, squared);
        }
        return squaredStorage.load(product.namespace, product.code);
    }

    public BufferedImage resized(Product product) {
        if (!resizedStorage.exists(product.namespace, product.code)) {
            var squared = squared(product);
            if (squared == null) return null;
            var resized = new Image(squared, Color.WHITE.getRGB()).resize(128).image;
            resizedStorage.save(product.namespace, product.code, resized);
        }
        return resizedStorage.load(product.namespace, product.code);
    }
}