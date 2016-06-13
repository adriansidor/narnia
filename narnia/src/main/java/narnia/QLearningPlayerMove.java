package narnia;

import narnia.Ball;
import narnia.BallMoveDriver;
import narnia.BallPosition;
import narnia.ContainerBox;
import narnia.q_learning.GameNetwork;

/**
 * Created by Radosław on 13.06.2016.
 */
public class QLearningPlayerMove implements BallMoveDriver {

    private GameNetwork gameNetwork = GameNetwork.getInstance();


    public void move(Ball ball, ContainerBox box, BallPosition[] positionVector) {
        float ballMinX = box.getMinX() + ball.getRadius();
        float ballMinY = box.getMinY() + ball.getRadius();
        float ballMaxX = box.getMaxX() - ball.getRadius();
        float ballMaxY = box.getMaxY() - ball.getRadius();





        // and speed.
        if (ball.x < ballMinX) {
            ball.speedX = -ball.speedX; // Reflect along normal
            ball.x = ballMinX; // Re-position the ball at the edge
        } else if (ball.x > ballMaxX) {
            ball.speedX = -ball.speedX;
            ball.x = ballMaxX;
        }
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
