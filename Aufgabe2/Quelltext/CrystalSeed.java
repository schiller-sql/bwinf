/**
 * The seed of a crystal and its (the crystals) properties.
 * <p>
 * The orientation is supposed be 0 <= x < 1 and the speeds 0 < x <= 1.
 */
public record CrystalSeed(double leftSpeed, double rightSpeed, double topSpeed, double bottomSpeed, double orientation,
                          int x, int y) {
}
