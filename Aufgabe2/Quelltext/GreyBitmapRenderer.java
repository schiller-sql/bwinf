import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GreyBitmapRenderer {
    final private GreyBitmap bitmap;

    private static final String fileDirPath = "Aufgabe2/Ausgabedateien/";
    private static final String filePath = fileDirPath + "krystal.png";

    public GreyBitmapRenderer(GreyBitmap bitmap) {
        this.bitmap = bitmap;
    }

    private RenderedImage createImage() {
        BufferedImage image = new BufferedImage(bitmap.getWidth(),
                bitmap.getHeight(),
                BufferedImage./*TYPE_BYTE_GRAY*//*TYPE_INT_ARGB*/TYPE_3BYTE_BGR
        );
        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                byte rawGreyValue = bitmap.get(x, y);
                int greyValue = rawGreyValue + 128;
                int redValue = greyValue << 16;
                int greenValue = greyValue << 8;
                int rgbValue = redValue + greenValue + greyValue;
                image.setRGB(x, y, rgbValue);
            }
        }
        return image;
    }

    void render() throws IOException {
        RenderedImage image = createImage();
        if (!new File(fileDirPath).exists()) {
            Files.createDirectory(Path.of(fileDirPath));
        }
        File file = new File(filePath);
        ImageIO.write(image, "PNG", file);
    }
}
