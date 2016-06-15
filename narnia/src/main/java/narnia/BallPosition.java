package narnia;

import lombok.Data;

@Data
public class BallPosition {
	
	private float x;
	private float y;
	private float radius;
	private float direction;
	
	public BallPosition(Ball ball) {
		this.x = ball.x;
		this.y = ball.y;
		this.radius = ball.radius;
		this.direction = ball.direction;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public String toString() {
		return (x + " " + y);
	}

}
