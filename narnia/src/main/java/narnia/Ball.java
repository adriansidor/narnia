package narnia;

import java.awt.*;
import java.util.Formatter;
import java.util.Random;

/**
 * The bouncing ball.
 */
public class Ball {
	float x, y; // Ball's center x and y (package access)
	float speedX, speedY; // Ball's speed per step in x and y (package access)
	float radius; // Ball's radius (package access)
	private Color color; // Ball's color
	private static final Color DEFAULT_COLOR = Color.BLUE;
	public BallMoveDriver driver;

	/**
	 * Constructor: For user friendliness, user specifies velocity in speed and
	 * moveAngle in usual Cartesian coordinates. Need to convert to speedX and
	 * speedY in Java graphics coordinates for ease of operation.
	 */
	public Ball(float x, float y, float radius, float speedX, float speedY
			, Color color, BallMoveDriver driver) {
		this.x = x;
		this.y = y;
		this.speedX = speedX;
		this.speedY = speedY;
		this.radius = radius;
		this.color = color;
		this.driver = driver;
	}

	/** Constructor with the default color */
	public Ball(float x, float y, float radius, float speed,
			float angleInDegree, BallMoveDriver driver) {
		this(x, y, radius, speed, angleInDegree, DEFAULT_COLOR, driver);
	}

	/** Draw itself using the given graphics context. */
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillOval((int) (x - radius), (int) (y - radius), (int) (2 * radius),
				(int) (2 * radius));
	}

	/**
	 * Make one move, check for collision and react accordingly if collision
	 * occurs.
	 * 
	 * @param box
	 *            : the container (obstacle) for this ball.
	 */
	public void moveOneStepWithCollisionDetection(ContainerBox box,
			BallPosition[] positionVector) {
		driver.move(this, box, positionVector);
	}

	public void moveBaseOnAction(int action, ContainerBox box) {
		float ballMinX = box.minX + this.radius;
		float ballMinY = box.minY + this.radius;
		float ballMaxX = box.maxX - this.radius;
		float ballMaxY = box.maxY - this.radius;

		switch (action) {
		// move up
		case 0:
			this.y -= this.speedY;
			break;
		// move down
		case 2:
			this.y += this.speedY;
			break;
		// case 1 is not moving so we dont change ball.y
		}
		if (this.x < ballMinX) {
			this.speedX = -this.speedX; // Reflect along normal
			this.x = ballMinX; // Re-position the ball at the edge
		} else if (this.x > ballMaxX) {
			this.speedX = -this.speedX;
			this.x = ballMaxX;
		}
		if (this.y < ballMinY) {
			this.y = ballMinY;
		} else if (this.y > ballMaxY) {
			this.y = ballMaxY;
		}
	}

	public boolean detectCollision(Ball ball) {
		double dx = Math.pow((this.x - ball.x), 2);
		double dy = Math.pow((this.y - ball.y), 2);
		double distance = Math.sqrt(dx + dy);
		if (distance <= (this.radius + ball.radius)) {
			return true;
		}
		return false;
	}

	public boolean detectCollision(BallPosition ballPosition) {
		double dx = Math.pow((this.x - ballPosition.getX()), 2);
		double dy = Math.pow((this.y - ballPosition.getY()), 2);
		double distance = Math.sqrt(dx + dy);
		if (distance <= (this.radius + ballPosition.getRadius())) {
			return true;
		}
		return false;
	}

	/** Return the magnitude of speed. */
	public float getSpeed() {
		return (float) Math.sqrt(speedX * speedX + speedY * speedY);
	}

	/** Return the direction of movement in degrees (counter-clockwise). */
	public float getMoveAngle() {
		return (float) Math.toDegrees(Math.atan2(-speedY, speedX));
	}

	/** Return mass */
	public float getMass() {
		return radius * radius * radius / 1000f; // Normalize by a factor
	}

	/** Return the kinetic energy (0.5mv^2) */
	public float getKineticEnergy() {
		return 0.5f * getMass() * (speedX * speedX + speedY * speedY);
	}

	/** Describe itself. */
	public String toString() {
		sb.delete(0, sb.length());
		formatter.format("@(%3.0f,%3.0f) r=%3.0f V=(%2.0f,%2.0f) "
				+ "S=%4.1f \u0398=%4.0f KE=%3.0f", x, y, radius, speedX,
				speedY, getSpeed(), getMoveAngle(), getKineticEnergy()); // \u0398
																			// is
																			// theta
		return sb.toString();
	}

	// Re-use to build the formatted string for toString()
	private StringBuilder sb = new StringBuilder();
	private Formatter formatter = new Formatter(sb);
}