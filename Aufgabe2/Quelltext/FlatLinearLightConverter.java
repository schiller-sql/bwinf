public class FlatLinearLightConverter implements LightConverter {
    final private double belowMinusPercentage;
    final private double topMinusPercentage;

    public FlatLinearLightConverter(double belowMinusPercentage, double topMinusPercentage) {
        assert (belowMinusPercentage + topMinusPercentage < 1);
        this.belowMinusPercentage = belowMinusPercentage;
        this.topMinusPercentage = topMinusPercentage;
    }

    @Override
    public double convert(double baseGrey) {
        return baseGrey * (1 - belowMinusPercentage - topMinusPercentage) + belowMinusPercentage;
    }
}
