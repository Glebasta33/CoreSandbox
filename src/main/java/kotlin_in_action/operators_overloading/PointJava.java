package kotlin_in_action.operators_overloading;

public class PointJava {
    public int x, y;

    public PointJava(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public PointJava plus(PointJava other) {
        return new PointJava(this.x + other.x, this.y + other.y);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(x=" + this.x + ", y=" + this.y + ")";
    }
}
