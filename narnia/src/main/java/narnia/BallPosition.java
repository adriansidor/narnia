package narnia;

public class BallPosition {
	
	private float x;
	private float y;
	private float radius;
	private float speed;
	
	public BallPosition(Ball ball) {
		this.x = ball.x;
		this.y = ball.y;
		this.radius = ball.radius;
		this.speed = (float)Math.sqrt(Math.pow(ball.speedX, 2)+Math.pow(ball.speedY, 2));
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
	
	public float getSpeed() {
		return speed;
	}
	
	public String toString() {
		return (x + " " + y);
	}

}
