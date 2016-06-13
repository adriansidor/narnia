package narnia;

import lombok.Getter;

public class BallPosition {

    private float x;
    private float y;
    @Getter
    private float radius;

    public BallPosition(Ball ball) {
        this.x = ball.x;
        this.y = ball.y;
        this.radius = ball.radius;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String toString() {
        return (x + " " + y);
    }

}
