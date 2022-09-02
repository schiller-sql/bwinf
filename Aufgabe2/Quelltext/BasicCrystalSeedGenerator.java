import java.util.ArrayList;
import java.util.List;

public class BasicCrystalSeedGenerator implements CrystalSeedGenerator {
    private final int howManyCrystals;
    private final double minCrystalSeedDistance;
    private final List<CrystalSeed> alreadyGeneratedSeeds;

    public BasicCrystalSeedGenerator(int howManyCrystals, double minCrystalSeedDistance) {
        assert minCrystalSeedDistance >= 1 : "crystals should be allowed to be in the same location";

        this.howManyCrystals = howManyCrystals;
        this.minCrystalSeedDistance = minCrystalSeedDistance;
        alreadyGeneratedSeeds = new ArrayList<>(howManyCrystals);
    }

    public BasicCrystalSeedGenerator(int howManyCrystals) {
        this(howManyCrystals, 1);
    }

    private static double randomDoubleBiggerThanZero() {
        double random = Math.random();
        while (random == 0) {
            random = Math.random();
        }
        return random;
    }

    private CrystalSeed generateNewCrystal(int width, int height) {
        return new CrystalSeed(
                randomDoubleBiggerThanZero(),
                randomDoubleBiggerThanZero(),
                randomDoubleBiggerThanZero(),
                randomDoubleBiggerThanZero(),
                Math.random(),
                (int) (Math.random() * width),
                (int) (Math.random() * height)
        );
    }

    private boolean toCloseExistingSeed(int x, int y) {
        if (minCrystalSeedDistance == 1) {
            for (CrystalSeed seed : alreadyGeneratedSeeds) {
                if (seed.x() == x && seed.y() == y) {
                    return true;
                }
            }
        } else {
            for (CrystalSeed seed : alreadyGeneratedSeeds) {
                int xDistance = Math.abs(x - seed.x());
                int yDistance = Math.abs(y - seed.y());
                double distance = Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
                if (distance < minCrystalSeedDistance) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean generateNextCrystalSeed(int width, int height) {
        if (howManyCrystals == alreadyGeneratedSeeds.size()) {
            return false;
        }
        CrystalSeed newSeed;
        do {
            newSeed = generateNewCrystal(width, height);
        } while (toCloseExistingSeed(newSeed.x(), newSeed.y()));
        alreadyGeneratedSeeds.add(newSeed);
        return true;
    }

    @Override
    public CrystalSeed getGeneratedCrystalSeed() {
        return alreadyGeneratedSeeds.get(alreadyGeneratedSeeds.size() - 1);
    }
}
