package narnia;

public class BallPosition {
	
	private float x;
	private float y;
	private float radius;
	private float speedY;
	private float speedX;
	
	public BallPosition(Ball ball) {
		this.x = ball.x;
		this.y = ball.y;
		this.radius = ball.radius;
		this.speedY = ball.speedY;
		this.speedX = ball.speedX;
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
	
	public float getSpeedY() {
		return speedX;
	}
	
	public float getSpeedX() {
		return speedY;
	}
	
	public String toString() {
		return (""+speedY);
	}

}
