public class QuantityMeasurementApp {

    enum LengthUnit {
        FEET(1.0),
        INCHES(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(0.393701 / 12.0);

        private final double toFeet;

        LengthUnit(double toFeet) {
            this.toFeet = toFeet;
        }

        public double toBase(double value) {
            return value * toFeet;
        }

        public double fromBase(double baseValue) {
            return baseValue / toFeet;
        }
    }

    static class QuantityLength {
        private final double value;
        private final LengthUnit unit;

        public QuantityLength(double value, LengthUnit unit) {
            if (unit == null || !Double.isFinite(value)) throw new IllegalArgumentException();
            this.value = value;
            this.unit = unit;
        }

        private double toFeet() {
            return unit.toBase(value);
        }

        public QuantityLength convertTo(LengthUnit targetUnit) {
            double converted = convert(this.value, this.unit, targetUnit);
            return new QuantityLength(converted, targetUnit);
        }

        public QuantityLength add(QuantityLength other) {
            if (other == null) throw new IllegalArgumentException();
            double sumBase = this.toFeet() + other.toFeet();
            double resultValue = unit.fromBase(sumBase);
            return new QuantityLength(resultValue, this.unit);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            QuantityLength other = (QuantityLength) obj;
            return Double.compare(this.toFeet(), other.toFeet()) == 0;
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    public static double convert(double value, LengthUnit source, LengthUnit target) {
        if (source == null || target == null || !Double.isFinite(value)) {
            throw new IllegalArgumentException();
        }
        double base = source.toBase(value);
        return target.fromBase(base);
    }

    public static QuantityLength add(QuantityLength q1, QuantityLength q2) {
        if (q1 == null || q2 == null) throw new IllegalArgumentException();
        return q1.add(q2);
    }

    public static QuantityLength add(double v1, LengthUnit u1, double v2, LengthUnit u2, LengthUnit target) {
        if (u1 == null || u2 == null || target == null || !Double.isFinite(v1) || !Double.isFinite(v2)) {
            throw new IllegalArgumentException();
        }
        double baseSum = u1.toBase(v1) + u2.toBase(v2);
        double result = target.fromBase(baseSum);
        return new QuantityLength(result, target);
    }

    public static void main(String[] args) {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength q2 = new QuantityLength(12.0, LengthUnit.INCHES);
        System.out.println(q1.add(q2));

        QuantityLength q3 = new QuantityLength(12.0, LengthUnit.INCHES);
        QuantityLength q4 = new QuantityLength(1.0, LengthUnit.FEET);
        System.out.println(q3.add(q4));

        System.out.println(add(1.0, LengthUnit.YARDS, 3.0, LengthUnit.FEET, LengthUnit.YARDS));

        QuantityLength q5 = new QuantityLength(2.54, LengthUnit.CENTIMETERS);
        QuantityLength q6 = new QuantityLength(1.0, LengthUnit.INCHES);
        System.out.println(q5.add(q6));
    }
}
