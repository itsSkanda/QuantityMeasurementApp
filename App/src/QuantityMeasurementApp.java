interface IMeasurable {
    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
}

enum VolumeUnit implements IMeasurable {
    LITRE(1.0, "Litre"),
    MILLILITRE(0.001, "Millilitre"),
    GALLON(3.78541, "Gallon");

    private final double factor;
    private final String name;

    VolumeUnit(double factor, String name) {
        this.factor = factor;
        this.name = name;
    }

    public double getConversionFactor() { return factor; }
    public double convertToBaseUnit(double value) { return value * factor; }
    public double convertFromBaseUnit(double baseValue) { return baseValue / factor; }
    public String getUnitName() { return name; }
}

class Quantity<U extends IMeasurable> {
    private final double value;
    private final U unit;
    private static final double EPSILON = 0.0001;

    public Quantity(double value, U unit) {
        if (unit == null) throw new IllegalArgumentException();
        this.value = value;
        this.unit = unit;
    }

    public Quantity<U> convertTo(U targetUnit) {
        double base = unit.convertToBaseUnit(value);
        double converted = targetUnit.convertFromBaseUnit(base);
        return new Quantity<>(converted, targetUnit);
    }

    public Quantity<U> add(Quantity<U> other) {
        return add(other, this.unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        validate(other, targetUnit);
        double baseSum = unit.convertToBaseUnit(value) + other.unit.convertToBaseUnit(other.value);
        double result = targetUnit.convertFromBaseUnit(baseSum);
        return new Quantity<>(round(result), targetUnit);
    }

    public Quantity<U> subtract(Quantity<U> other) {
        return subtract(other, this.unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        validate(other, targetUnit);
        double baseDiff = unit.convertToBaseUnit(value) - other.unit.convertToBaseUnit(other.value);
        double result = targetUnit.convertFromBaseUnit(baseDiff);
        return new Quantity<>(round(result), targetUnit);
    }

    public double divide(Quantity<U> other) {
        validate(other, this.unit);
        double divisor = other.unit.convertToBaseUnit(other.value);
        if (Math.abs(divisor) < EPSILON) throw new ArithmeticException();
        return unit.convertToBaseUnit(value) / divisor;
    }

    private void validate(Quantity<U> other, U targetUnit) {
        if (other == null || targetUnit == null) throw new IllegalArgumentException();
        if (!unit.getClass().equals(other.unit.getClass())) throw new IllegalArgumentException();
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quantity<?> q)) return false;
        if (!unit.getClass().equals(q.unit.getClass())) return false;
        double a = unit.convertToBaseUnit(value);
        double b = q.unit.convertToBaseUnit(q.value);
        return Math.abs(a - b) < EPSILON;
    }

    public String toString() {
        return "Quantity(" + value + ", " + unit.getUnitName() + ")";
    }
}

public class QuantityMeasurementApp {
    public static void main(String[] args) {
        Quantity<VolumeUnit> v1 = new Quantity<>(5.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(500.0, VolumeUnit.MILLILITRE);

        System.out.println(v1.subtract(v2));
        System.out.println(v1.subtract(v2, VolumeUnit.MILLILITRE));
        System.out.println(v1.divide(new Quantity<>(2.0, VolumeUnit.LITRE)));
    }
}
