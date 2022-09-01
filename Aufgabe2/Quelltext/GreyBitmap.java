public class GreyBitmap {
    static private final byte zero = -128;

    final private byte[][] bitmap;
    final private int width, height;

    public GreyBitmap(int width, int height) {
        this.width = width;
        this.height = height;
        bitmap = new byte[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bitmap[y][x] = zero;
            }
        }
    }

    byte get(int x, int y) {
        return bitmap[y][x];
    }

    void setRaw(int x, int y, byte value) {
        bitmap[y][x] = value;
    }

    void set(int x, int y, double value) {
        int greyValue = (int) Math.ceil(value * 255);
        byte byteGreyValue = (byte) (greyValue - 128);
        bitmap[y][x] = byteGreyValue;
    }

    boolean isZero(int x, int y) {
        return bitmap[y][x] == zero;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
