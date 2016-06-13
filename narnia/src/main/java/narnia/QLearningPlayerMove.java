package narnia;

import narnia.q_learning.GameNetwork;
import narnia.q_learning.MoveType;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;

import java.util.Random;

/**
 * Created by Radosław on 13.06.2016.
 */
public class QLearningPlayerMove implements BallMoveDriver {

    private static final int STEP_UNIT = 10;

    private GameNetwork gameNetwork = GameNetwork.getInstance();
    private float beforeMove;
    private MoveType lastMove = MoveType.DO_NOT_MOVE;
    private MoveType actualMove = MoveType.DO_NOT_MOVE;
    private double[] actualQOut = new double[GameNetwork.OUTPUT_NEURON_NUMBER];
    private double[] actualQIn = new double[GameNetwork.INPUT_NEURON_NUMBER];



    public void move(Ball ball, ContainerBox box, BallPosition[] positionVector) {


//        Random random = new Random();
//        int randomInt = random.nextInt();
//
//        this.actualQOut = gameNetwork.getOutput();
//        this.actualQIn = getInputByBallAndPositionVector(lastMove,ball,positionVector);
//
//        if (randomInt % 7 == 0) {
//            this.actualMove = moveByNetwork(ball, positionVector);
//        } else {
//            this.actualMove = getRandomMove(ball);
//        }
//
//        cheeckBounds(ball,box);
//
//        int actualReward = getRaward(ball.copy(), positionVector);
//
//        predictMoves(ball.copy(), positionVector.clone(), actualReward, this.actualMove);

        cheeckBounds(ball,box);

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

    private MoveType moveByNetwork(Ball ball, BallPosition[] positionVector) {
        double[] networkInput = getInputByBallAndPositionVector(MoveType.DO_NOT_MOVE, ball, positionVector);
        MoveType moveType = gameNetwork.getMove(networkInput);
        makeMove(ball, moveType);
        return moveType;
    }

    private double[] getInputByBallAndPositionVector(MoveType moveType, Ball ball, BallPosition[] positionVector) {
        double[] result = new double[GameNetwork.INPUT_NEURON_NUMBER];
        switch (moveType) {
            case UP:
                result[0] = 1;
                break;
            case DO_NOT_MOVE:
                result[1] = 1;
                break;
            case DOWN:
                result[2] = 1;
                break;
        }
        result[3] = ball.x;
        result[4] = ball.y;
        result[5] = ball.getSpeed();
        int i = 6;
        int j = 0;
        while (i < GameNetwork.INPUT_NEURON_NUMBER) {
            BallPosition ballPosition = positionVector[j++];
            if (ballPosition != null) {
                result[i++] = ballPosition.getX();
                result[i++] = ballPosition.getY();
            } else {
                result[i++] = 0;
                result[i++] = 0;
            }
        }
        return result;
    }

    private DataSet createDataSet(double[] input, double[] out) {
        DataSet dataSet = new DataSet(GameNetwork.INPUT_NEURON_NUMBER);
        dataSet.addRow(new DataSetRow(input, out));
        return dataSet;
    }

    private void predictMoves(Ball copy, BallPosition[] clone, int actualReward, MoveType moveType) {
        assert moveType != null;

    }

    private MoveType getRandomMove(Ball ball) {
        Random random = new Random();
        int randInt = random.nextInt(3);
        MoveType moveType = null;
        switch (randInt) {
            case 0:
                moveType = MoveType.UP;
                break;
            case 1:
                moveType = MoveType.DO_NOT_MOVE;
                break;
            case 2:
                moveType = MoveType.DOWN;
                break;
        }

        makeMove(ball, moveType);

        return moveType;
    }

    private void makeMove(Ball ball, MoveType moveType) {
        assert moveType != null;
        this.beforeMove = ball.y;
        switch (moveType) {
            case UP:
                ball.y += STEP_UNIT;
                break;
            case DO_NOT_MOVE:
                break;
            case DOWN:
                ball.y -= STEP_UNIT;
                break;
        }
        this.lastMove = moveType;
    }


    private int getRaward(Ball player, BallPosition[] positionVector) {
        for (BallPosition ballPosition : positionVector) {
            if (player.detectCollision(ballPosition)) {
                return -10;
            }
        }
        return 1;
    }
}
