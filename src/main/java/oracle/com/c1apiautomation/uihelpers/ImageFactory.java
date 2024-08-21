package oracle.com.c1apiautomation.uihelpers;

import javafx.scene.image.Image;
import oracle.com.c1apiautomation.MainApplication;

import java.io.InputStream;
import java.util.Objects;

import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.Map;

public class ImageFactory {
    private static final Map<String, Image> imageCache = new HashMap<>();
    private static final double DEFAULT_WIDTH = 16;
    private static final double DEFAULT_HEIGHT = 16;

    // Method with width and height parameters
    public static ImageView getImageView(String imageName, double width, double height) {
        Image image = imageCache.computeIfAbsent(imageName, ImageFactory::loadImage);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        return imageView;
    }

    // Overloaded method with default width and height
    public static ImageView getImageView(String imageName) {
        return getImageView(imageName, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private static Image loadImage(String imageName) {
        String imagePath = "images/" + imageName;
        InputStream imageStream = MainApplication.class.getResourceAsStream(imagePath);

        if (imageStream == null) {
            System.err.println("Image not found: " + imagePath);
            return new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(imagePath))); // fallback image
        }

        return new Image(imageStream);
    }
}


//public class ImageFactory {
//
//
//    public static Image GetImage(String imageName) {
//        if(!IsImageExist(imageName))
//        {
//            throw new MissingResourceException("Image '" + imageName + "' not found in 'resources/images/ folder", MainApplication.class.getName(), imageName);
//        }
//        Image image; image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("images/" + imageName)));
//        return image;
//    }
//
//    private static boolean IsImageExist(String imageName) {
//        var resource = MainApplication.class.getResource("images/"+ imageName);
//        return resource != null;
//    }
//}
