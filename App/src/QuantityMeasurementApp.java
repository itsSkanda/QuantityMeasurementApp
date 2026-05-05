import java.util.function.DoubleBinaryOperator;

interface IMeasurable {
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double value);
}

enum LengthUnit implements IMeasurable {
    FEET(12.0), INCHES(1.0);

    private final double toBaseFactor;

    LengthUnit(double toBaseFactor) {
        this.toBaseFactor = toBaseFactor;
    }

    public double convertToBaseUnit(double value) {
        return value * toBaseFactor;
    }

    public double convertFromBaseUnit(double value) {
        return value / toBaseFactor;
    }
}

enum WeightUnit implements IMeasurable {
    KILOGRAM(1000.0), GRAM(1.0);

    private final double toBaseFactor;

    WeightUnit(double toBaseFactor) {
        this.toBaseFactor = toBaseFactor;
    }

    public double convertToBaseUnit(double value) {
        return value * toBaseFactor;
    }

    public double convertFromBaseUnit(double value) {
        return value / toBaseFactor;
    }
}

enum VolumeUnit implements IMeasurable {
    LITRE(1000.0), MILLILITRE(1.0);

    private final double toBaseFactor;

    VolumeUnit(double toBaseFactor) {
        this.toBaseFactor = toBaseFactor;
    }

    public double convertToBaseUnit(double value) {
        return value * toBaseFactor;
    }

    public double convertFromBaseUnit(double value) {
        return value / toBaseFactor;
    }
}

class Quantity<U extends IMeasurable> {
    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        if (unit == null || !Double.isFinite(value)) {
            throw new IllegalArgumentException();
        }
        this.value = value;
        this.unit = unit;
    }

    private enum ArithmeticOperation {
        ADD((a, b) -> a + b),
        SUBTRACT((a, b) -> a - b),
        DIVIDE((a, b) -> {
            if (b == 0) throw new ArithmeticException();
            return a / b;
        });

        private final DoubleBinaryOperator op;

        ArithmeticOperation(DoubleBinaryOperator op) {
            this.op = op;
        }

        double compute(double a, double b) {
            return op.applyAsDouble(a, b);
        }
    }

    private void validateArithmeticOperands(Quantity<U> other, U targetUnit, boolean targetRequired) {
        if (other == null) throw new IllegalArgumentException();
        if (this.unit == null || other.unit == null) throw new IllegalArgumentException();
        if (!this.unit.getClass().equals(other.unit.getClass())) throw new IllegalArgumentException();
        if (!Double.isFinite(this.value) || !Double.isFinite(other.value)) throw new IllegalArgumentException();
        if (targetRequired && targetUnit == null) throw new IllegalArgumentException();
    }

    private double performBaseArithmetic(Quantity<U> other, ArithmeticOperation op) {
        double baseThis = this.unit.convertToBaseUnit(this.value);
        double baseOther = other.unit.convertToBaseUnit(other.value);
        return op.compute(baseThis, baseOther);
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    public Quantity<U> add(Quantity<U> other) {
        return add(other, this.unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        validateArithmeticOperands(other, targetUnit, true);
        double base = performBaseArithmetic(other, ArithmeticOperation.ADD);
        double result = targetUnit.convertFromBaseUnit(base);
        return new Quantity<>(round(result), targetUnit);
    }

    public Quantity<U> subtract(Quantity<U> other) {
        return subtract(other, this.unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        validateArithmeticOperands(other, targetUnit, true);
        double base = performBaseArithmetic(other, ArithmeticOperation.SUBTRACT);
        double result = targetUnit.convertFromBaseUnit(base);
        return new Quantity<>(round(result), targetUnit);
    }

    public double divide(Quantity<U> other) {
        validateArithmeticOperands(other, null, false);
        return performBaseArithmetic(other, ArithmeticOperation.DIVIDE);
    }

    public String toString() {
        return "Quantity(" + value + ", " + unit + ")";
    }
}

public class QuantityMeasurementApp {
    public static void main(String[] args) {
        Quantity<LengthUnit> l1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(6.0, LengthUnit.INCHES);
        System.out.println(l1.subtract(l2));
        System.out.println(l1.subtract(l2, LengthUnit.INCHES));
        System.out.println(l1.divide(new Quantity<>(2.0, LengthUnit.FEET)));

        Quantity<WeightUnit> w1 = new Quantity<>(10.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(5000.0, WeightUnit.GRAM);
        System.out.println(w1.subtract(w2));
        System.out.println(w1.divide(new Quantity<>(5.0, WeightUnit.KILOGRAM)));

        Quantity<VolumeUnit> v1 = new Quantity<>(5.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(500.0, VolumeUnit.MILLILITRE);
        System.out.println(v1.subtract(v2));
        System.out.println(v1.divide(new Quantity<>(10.0, VolumeUnit.LITRE)));
    }
}
