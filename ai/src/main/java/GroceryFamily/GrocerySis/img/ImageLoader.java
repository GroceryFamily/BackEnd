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
    private final ImageStorage transformedStorage;

    ImageLoader(GrocerySisConfig config) {
        rawStorage = new ImageStorage(config.rawImages);
        transformedStorage = new ImageStorage(config.transformedImages);
    }

    public boolean exists(Product product) {
        return rawStorage.exists(product.namespace, product.code);
    }

    @SneakyThrows
    public BufferedImage raw(Product product) {
        var url = product.details.get(Detail.IMAGE);
        if (url == null) return null;
        if (!rawStorage.exists(product.namespace, product.code)) {
            var image = ImageIO.read(new URI(url).toURL());
            var convertedImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            convertedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
            rawStorage.save(product.namespace, product.code, convertedImage);
        }
        return rawStorage.load(product.namespace, product.code);
    }

    public BufferedImage transformed(Product product) {
        var image = raw(product);
        if (image == null) return null;
        var transformed = new Image(image, image.getRGB(0, 0)).trim().image;
        transformedStorage.save(product.namespace, product.code, transformed);
        return transformed;
    }
}