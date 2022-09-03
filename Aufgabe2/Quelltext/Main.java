import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        GreyBitmap bitmap = new GreyBitmap(10, 10);
        bitmap.set(5, 5, 1);
        new GreyBitmapRenderer(bitmap).renderToFile();
    }
}