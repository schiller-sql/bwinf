public interface CrystalSeedGenerator {
    boolean generateNextCrystalSeed(int width, int height);
    CrystalSeed getGeneratedCrystalSeed();
}
