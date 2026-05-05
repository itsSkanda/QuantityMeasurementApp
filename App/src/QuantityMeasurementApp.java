interface IMeasurable {
    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
}

enum LengthUnit implements IMeasurable {
    FEET(1.0), INCHES(1.0 / 12.0), YARDS(3.0), CENTIMETERS(0.0328084);

    private final double factor;

    LengthUnit(double factor) {
        this.factor = factor;
    }

    public double getConversionFactor() {
        return factor;
    }

    public double convertToBaseUnit(double value) {
        return value * factor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / factor;
    }

    public String getUnitName() {
        return name();
    }
}

enum WeightUnit implements IMeasurable {
    KILOGRAM(1.0), GRAM(0.001);

    private final double factor;

    WeightUnit(double factor) {
        this.factor = factor;
    }

    public double getConversionFactor() {
        return factor;
    }

    public double convertToBaseUnit(double value) {
        return value * factor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / factor;
    }

    public String getUnitName() {
        return name();
    }
}

enum VolumeUnit implements IMeasurable {
    LITRE(1.0), MILLILITRE(0.001), GALLON(3.78541);

    private final double factor;

    VolumeUnit(double factor) {
        this.factor = factor;
    }

    public double getConversionFactor() {
        return factor;
    }

    public double convertToBaseUnit(double value) {
        return value * factor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / factor;
    }

    public String getUnitName() {
        return name();
    }
}

class Quantity<U extends IMeasurable> {
    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        if (unit == null || Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException();
        }
        this.value = value;
        this.unit = unit;
    }

    public Quantity<U> convertTo(U targetUnit) {
        double base = unit.convertToBaseUnit(value);
        double converted = targetUnit.convertFromBaseUnit(base);
        return new Quantity<>(round(converted), targetUnit);
    }

    public Quantity<U> add(Quantity<U> other) {
        return add(other, this.unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        double base1 = unit.convertToBaseUnit(value);
        double base2 = other.unit.convertToBaseUnit(other.value);
        double sum = base1 + base2;
        double result = targetUnit.convertFromBaseUnit(sum);
        return new Quantity<>(round(result), targetUnit);
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Quantity<?> q)) return false;
        if (this.unit.getClass() != q.unit.getClass()) return false;
        double base1 = this.unit.convertToBaseUnit(this.value);
        double base2 = q.unit.convertToBaseUnit(q.value);
        return Double.compare(base1, base2) == 0;
    }

    public int hashCode() {
        double base = unit.convertToBaseUnit(value);
        return Double.hashCode(base);
    }

    public String toString() {
        return "Quantity(" + value + ", " + unit.getUnitName() + ")";
    }
}

public class QuantityMeasurementApp {
    public static void main(String[] args) {
        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        Quantity<VolumeUnit> v3 = new Quantity<>(1.0, VolumeUnit.GALLON);

        System.out.println(v1.equals(v2));
        System.out.println(v1.convertTo(VolumeUnit.MILLILITRE));
        System.out.println(v1.add(v2));
        System.out.println(v3.convertTo(VolumeUnit.LITRE));
        System.out.println(v1.add(v3, VolumeUnit.MILLILITRE));
    }
}
