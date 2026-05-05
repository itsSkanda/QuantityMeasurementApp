enum LengthUnit {
    FEET(1.0),
    INCHES(1.0 / 12.0),
    YARDS(3.0),
    CENTIMETERS(0.393701 / 12.0);

    private final double toFeetFactor;

    LengthUnit(double toFeetFactor) {
        this.toFeetFactor = toFeetFactor;
    }

    public double convertToBaseUnit(double value) {
        return value * toFeetFactor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / toFeetFactor;
    }
}

public class QuantityMeasurementApp {

    static class QuantityLength {
        private final double value;
        private final LengthUnit unit;

        public QuantityLength(double value, LengthUnit unit) {
            if (unit == null || !Double.isFinite(value)) throw new IllegalArgumentException();
            this.value = value;
            this.unit = unit;
        }

        public QuantityLength convertTo(LengthUnit targetUnit) {
            if (targetUnit == null) throw new IllegalArgumentException();
            double base = unit.convertToBaseUnit(value);
            double result = targetUnit.convertFromBaseUnit(base);
            return new QuantityLength(result, targetUnit);
        }

        public QuantityLength add(QuantityLength other) {
            if (other == null) throw new IllegalArgumentException();
            return add(other, this.unit);
        }

        public QuantityLength add(QuantityLength other, LengthUnit targetUnit) {
            if (other == null || targetUnit == null) throw new IllegalArgumentException();
            double sumBase = this.unit.convertToBaseUnit(this.value)
                    + other.unit.convertToBaseUnit(other.value);
            double result = targetUnit.convertFromBaseUnit(sumBase);
            return new QuantityLength(result, targetUnit);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            QuantityLength other = (QuantityLength) obj;
            double thisBase = this.unit.convertToBaseUnit(this.value);
            double otherBase = other.unit.convertToBaseUnit(other.value);
            return Double.compare(thisBase, otherBase) == 0;
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    public static void main(String[] args) {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength q2 = new QuantityLength(12.0, LengthUnit.INCHES);

        System.out.println(q1.convertTo(LengthUnit.INCHES));
        System.out.println(q1.add(q2, LengthUnit.FEET));
        System.out.println(new QuantityLength(36.0, LengthUnit.INCHES)
                .equals(new QuantityLength(1.0, LengthUnit.YARDS)));
        System.out.println(new QuantityLength(1.0, LengthUnit.YARDS)
                .add(new QuantityLength(3.0, LengthUnit.FEET), LengthUnit.YARDS));
        System.out.println(new QuantityLength(2.54, LengthUnit.CENTIMETERS)
                .convertTo(LengthUnit.INCHES));
        System.out.println(new QuantityLength(5.0, LengthUnit.FEET)
                .add(new QuantityLength(0.0, LengthUnit.INCHES), LengthUnit.FEET));

        System.out.println(LengthUnit.FEET.convertToBaseUnit(12.0));
        System.out.println(LengthUnit.INCHES.convertToBaseUnit(12.0));
    }
}
