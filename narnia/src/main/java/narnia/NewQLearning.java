package narnia;

import narnia.q_learning.GameNetwork;
import narnia.q_learning.GameState;
import narnia.q_learning.MoveType;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;

import java.util.Random;

/**
 * Created by Radosław on 13.06.2016.
 */
public class NewQLearning implements BallMoveDriver {

    private static final int STEP_UNIT = 10;
    private static final int MAGIC_NUMBER = 11;
    private GameState lastState;
    private double[] lastOut = new double[GameNetwork.OUTPUT_NEURON_NUMBER];
    private double[] lastIn = new double[GameNetwork.INPUT_NEURON_NUMBER];
    private MoveType curentMove = MoveType.DO_NOT_MOVE;
    private MoveType lastMove = MoveType.DO_NOT_MOVE;


    private GameNetwork gameNetwork = GameNetwork.getInstance();

    private float beforeMove;
    private static int rewart = 0;

    private final static double GAMMA = 0.9;

    public static int iter = 0;

    public void move(Ball ball, ContainerBox box, BallPosition[] positionVector) {
        lastState = new GameState(ball, positionVector);
        lastOut = gameNetwork.getOutput();

//        get a player move
        Random random = new Random();
        int randx = random.nextInt();

        iter++;

        if (randx % 5 == 0 || iter>100000) {
            curentMove = getMoveByNetwork();
        }else {
            curentMove = Utils.getRandomMove();
        }

        lastIn = Utils.getInputByBallAndPositionVector(curentMove, lastState);

//        Ball newBallPosition = moveBall(ball.copy(), curentMove);
//
//        int curentReval = getReward(new GameState(newBallPosition,positionVector.clone()));
//
//        updateQFunction(new GameState(newBallPosition,positionVector.clone()),curentReval);
//
////        register new state as actual
//        ball.y = newBallPosition.y;
//        cheeckBounds(ball, box);

    }

    private void updateQFunction(GameState gameState, int curentReval) {
        double maxOfAllDecision = Utils.maxQ(gameState,curentReval);
        int movePosition = curentMove.getMove();
        double updatedQ = lastOut[movePosition] + ((double) curentReval/MAGIC_NUMBER+(GAMMA*movePosition)-lastOut[movePosition]);
        double [] toLear = lastOut;
        toLear[movePosition] = updatedQ;
        DataSet dataSet = createDataSet(lastIn,toLear);
        gameNetwork.learn(dataSet);
    }

    private DataSet createDataSet(double[] lastIn, double[] toLear) {
        DataSet dataSet = new DataSet(GameNetwork.INPUT_NEURON_NUMBER);
        dataSet.addRow(new DataSetRow(lastIn,toLear));
        return dataSet;
    }

    private int getReward(GameState gameState) {
        for (BallPosition ballPosition : gameState.getBallPositions()) {
            if (gameState.getPlayer().detectCollision(ballPosition)) {
                return -10;
            }
        }
        return 1;
    }

//    private Ball moveBall(Ball ball, MoveType moveType) {
//        assert moveType != null;
//        this.beforeMove = ball.y;
//        switch (moveType) {
//            case UP:
//                ball.y += STEP_UNIT;
//                break;
//            case DO_NOT_MOVE:
//                break;
//            case DOWN:
//                ball.y -= STEP_UNIT;
//                break;
//        }
//        this.lastMove = moveType;
//        return ball;
//    }


//    private void cheeckBounds(Ball ball, ContainerBox box) {
//        float ballMinX = box.getMinX() + ball.getRadius();
//        float ballMinY = box.getMinY() + ball.getRadius();
//        float ballMaxX = box.getMaxX() - ball.getRadius();
//        float ballMaxY = box.getMaxY() - ball.getRadius();
//
//        if (ball.x < ballMinX) {
//            ball.speedX = -ball.speedX; // Reflect along normal
//            ball.x = ballMinX; // Re-position the ball at the edge
//        } else if (ball.x > ballMaxX) {
//            ball.speedX = -ball.speedX;
//            ball.x = ballMaxX;
//        }
//        // May cross both x and y bounds
//        if (ball.y < ballMinY) {
//            ball.speedY = -ball.speedY;
//            ball.y = ballMinY;
//        } else if (ball.y > ballMaxY) {
//            ball.speedY = -ball.speedY;
//            ball.y = ballMaxY;
//        }
//    }

    public MoveType getMoveByNetwork() {
        double[] input = Utils.getInputByBallAndPositionVector(curentMove,lastState);
        return gameNetwork.getMove(input);
    }


}
