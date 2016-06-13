package narnia;

public class BallPosition {
	
	private float x;
	private float y;
	
	public BallPosition(Ball ball) {
		this.x = ball.x;
		this.y = ball.y;
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