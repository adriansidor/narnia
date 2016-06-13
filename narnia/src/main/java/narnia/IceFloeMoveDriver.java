package narnia;

public class IceFloeMoveDriver implements BallMoveDriver{

	public void move(Ball ball, ContainerBox box) {
		float ballMinX = box.minX + ball.radius;
		float ballMinY = box.minY + ball.radius;
		float ballMaxX = box.maxX - ball.radius;
		float ballMaxY = box.maxY - ball.radius;

		// Calculate the ball's new position
		ball.x += ball.speedX;
		ball.y += ball.speedY;
		// Check if the ball moves over the bounds. If so, adjust the position
		// and speed.
		/*if (ball.x < ballMinX) {
			ball.speedX = -ball.speedX; // Reflect along normal
			ball.x = ballMinX; // Re-position the ball at the edge
		} else if (ball.x > ballMaxX) {
			ball.speedX = -ball.speedX;
			ball.x = ballMaxX;
		}*/
		// May cross both x and y bounds
		if (ball.y < ballMinY) {
			ball.speedY = -ball.speedY;
			ball.y = ballMinY;
		} else if (ball.y > ballMaxY) {
			ball.speedY = -ball.speedY;
			ball.y = ballMaxY;
		}
	}

}
