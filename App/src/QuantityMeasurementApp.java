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

        private static QuantityLength addInternal(QuantityLength q1, QuantityLength q2, LengthUnit target) {
            double sumBase = q1.toFeet() + q2.toFeet();
            double result = target.fromBase(sumBase);
            return new QuantityLength(result, target);
        }

        public QuantityLength add(QuantityLength other) {
            if (other == null) throw new IllegalArgumentException();
            return addInternal(this, other, this.unit);
        }

        public QuantityLength add(QuantityLength other, LengthUnit targetUnit) {
            if (other == null || targetUnit == null) throw new IllegalArgumentException();
            return addInternal(this, other, targetUnit);
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

    public static QuantityLength add(QuantityLength q1, QuantityLength q2, LengthUnit target) {
        if (q1 == null || q2 == null || target == null) throw new IllegalArgumentException();
        return new QuantityLength(q1.toFeet() + q2.toFeet(), LengthUnit.FEET).convertTo(target);
    }

    public static QuantityLength add(double v1, LengthUnit u1, double v2, LengthUnit u2, LengthUnit target) {
        if (u1 == null || u2 == null || target == null || !Double.isFinite(v1) || !Double.isFinite(v2)) {
            throw new IllegalArgumentException();
        }
        double baseSum = u1.toBase(v1) + u2.toBase(v2);
        double result = target.fromBase(baseSum);
        return new QuantityLength(result, target);
    }

    public static double convert(double value, LengthUnit source, LengthUnit target) {
        if (source == null || target == null || !Double.isFinite(value)) {
            throw new IllegalArgumentException();
        }
        double base = source.toBase(value);
        return target.fromBase(base);
    }

    public static QuantityLength convertTo(QuantityLength q, LengthUnit target) {
        if (q == null || target == null) throw new IllegalArgumentException();
        double result = convert(q.value, q.unit, target);
        return new QuantityLength(result, target);
    }

    public static void main(String[] args) {
        QuantityLength q1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength q2 = new QuantityLength(12.0, LengthUnit.INCHES);

        System.out.println(q1.add(q2, LengthUnit.FEET));
        System.out.println(q1.add(q2, LengthUnit.INCHES));
        System.out.println(q1.add(q2, LengthUnit.YARDS));

        QuantityLength q3 = new QuantityLength(1.0, LengthUnit.YARDS);
        QuantityLength q4 = new QuantityLength(3.0, LengthUnit.FEET);
        System.out.println(q3.add(q4, LengthUnit.YARDS));

        QuantityLength q5 = new QuantityLength(36.0, LengthUnit.INCHES);
        QuantityLength q6 = new QuantityLength(1.0, LengthUnit.YARDS);
        System.out.println(q5.add(q6, LengthUnit.FEET));

        QuantityLength q7 = new QuantityLength(2.54, LengthUnit.CENTIMETERS);
        QuantityLength q8 = new QuantityLength(1.0, LengthUnit.INCHES);
        System.out.println(q7.add(q8, LengthUnit.CENTIMETERS));
    }
}
