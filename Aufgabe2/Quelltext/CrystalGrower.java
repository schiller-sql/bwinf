import java.util.ArrayList;
import java.util.List;

public class CrystalGrower {
//    private final double growFactor;
    private final CrystalSeed[][] crystals;
    private final List<CrystalSeed> crystalSeeds;
    private final double[][] crystalGrowthPercentages;
    private final int width, height;
    private final LightConverter lightConverter;
    private final CrystalSeedGenerator crystalSeedGenerator;

    public CrystalGrower(
            int width,
            int height,
//            double growFactor,
            LightConverter lightConverter,
            CrystalSeedGenerator crystalSeedGenerator
    ) {
        this.width = width;
        this.height = height;
        this.crystals = new CrystalSeed[height][width];
        this.crystalGrowthPercentages = new double[height][width];
//        this.growFactor = growFactor;
        this.lightConverter = lightConverter;
        this.crystalSeedGenerator = crystalSeedGenerator;
        crystalSeeds = new ArrayList<>();
    }

    public void sprinkleSeeds() {
        while (crystalSeedGenerator.generateNextCrystalSeed(width, height)) {
            CrystalSeed seed = crystalSeedGenerator.getGeneratedCrystalSeed();
            crystals[seed.y()][seed.x()] = seed;
            crystalGrowthPercentages[seed.y()][seed.x()] = 1;
            crystalSeeds.add(seed);
        }
    }

    // grow1: erst nach oben growen, dann zur seite, dann nach rechts
    // grow2: in alle richtungen gleich, diagonal ist durch schnitt zwischen links und rechts
    public boolean grow() {
        for (CrystalSeed seed : crystalSeeds) {
        }
        return true;
    }

    public void copyOnGreyBitmap(GreyBitmap bitmap) {
        copyOnGreyBitmap(bitmap, false);
    }

    public void copyOnGreyBitmap(GreyBitmap bitmap, boolean copyOnlyFullyGrownFields) {
        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                CrystalSeed crystal = crystals[y][x];
                double greyValue = lightConverter.convert(crystal.orientation());
                if (copyOnlyFullyGrownFields && crystalGrowthPercentages[y][x] != 1) {
                    continue;
                }
                bitmap.set(x, y, greyValue);
            }
        }
    }
}
