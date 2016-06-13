package narnia;

import narnia.q_learning.GameNetwork;
import narnia.q_learning.GameState;
import narnia.q_learning.MoveType;
import narnia.q_learning.Utils;

import java.util.Random;

/**
 * Created by Radosław on 13.06.2016.
 */
public class NewQLearning implements BallMoveDriver {

    private GameState lastState;
    private double[] lastOut = new double[GameNetwork.OUTPUT_NEURON_NUMBER];
    private double[] lastIn = new double[GameNetwork.INPUT_NEURON_NUMBER];
    private MoveType lastMove = MoveType.DO_NOT_MOVE;

    private GameNetwork gameNetwork = GameNetwork.getInstance();

    public void move(Ball ball, ContainerBox box, BallPosition[] positionVector) {
        lastState = new GameState(ball, positionVector);
        lastOut = gameNetwork.getOutput();
        lastIn = Utils.getInputByBallAndPositionVector(lastMove, lastState);

//        get a player move
        Random random = new Random();
        int randx = random.nextInt();

        if (randx % 5 == 0) {
            lastMove = getMoveByNetwork();
        }else {
            lastMove = Utils.getRandomMove();
        }



        cheeckBounds(ball, box);

    }


    private void cheeckBounds(Ball ball, ContainerBox box) {
        float ballMinX = box.getMinX() + ball.getRadius();
        float ballMinY = box.getMinY() + ball.getRadius();
        float ballMaxX = box.getMaxX() - ball.getRadius();
        float ballMaxY = box.getMaxY() - ball.getRadius();

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

    public MoveType getMoveByNetwork() {
        double[] input = Utils.getInputByBallAndPositionVector(lastMove,lastState);
        return gameNetwork.getMove(input);
    }


}
