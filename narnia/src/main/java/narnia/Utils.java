package narnia;

import narnia.Ball;
import narnia.BallPosition;
import narnia.ContainerBox;
import narnia.q_learning.GameNetwork;
import narnia.q_learning.GameState;
import narnia.q_learning.MoveType;

import java.util.Random;

/**
 * Created by Radosław on 13.06.2016.
 */
public class Utils {

    static GameNetwork network = GameNetwork.getInstance();

    private static final float STEP_UNIT = 10;

    public static double[] getInputByBallAndPositionVector(MoveType moveType, GameState gameState) {
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
        Ball ball = gameState.getPlayer();
        result[3] = ball.getX();
        result[4] = ball.getY();
        result[5] = ball.getSpeed();
        int i = 6;
        int j = 0;
        while (i < GameNetwork.INPUT_NEURON_NUMBER) {
            BallPosition ballPosition = gameState.getBallPositions()[j++];
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


    public static MoveType getRandomMove() {
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
        return moveType;
    }

    public static double maxQ(GameState gameState,int reval) {
        if(reval==-10){
            return 0 ;
        }
        double a = getInputByBallAndPositionVector(MoveType.UP,gameState)[0];
        double b = getInputByBallAndPositionVector(MoveType.UP,gameState)[1];
        double c = getInputByBallAndPositionVector(MoveType.UP,gameState)[2];

        double max = a;
        if(b>a){
            max = b;
        }else if (c>b){
            max = c;
        }
        return max;
    }

    public static double getMaxQFunction(GameState gameState, int curentReward) {
        double a = getOutByMoveUP(gameState);
        double b = getOutByMOveDown(gameState);
        double c = getOutByDontMove(gameState);
        double max = a;
        if(b>a){
            max = b;
        }else if (c>b){
            max = c;
        }
        return max;
    }

    private static double getOutByDontMove(GameState gameState) {
        Ball ball = moveBall(gameState.getPlayer().copy(),MoveType.DOWN);
        int reward = getReward(new GameState(ball,gameState.getBallPositions()));
        if(reward==-10){
            return 0;
        }else {
            double[] in = getInputByBallAndPositionVector(MoveType.UP,new GameState(ball,gameState.getBallPositions()));
            double[] out = network.predict(in);
            return out[MoveType.DO_NOT_MOVE.getMove()];
        }
    }

    private static double getOutByMOveDown(GameState gameState) {
        Ball ball = moveBall(gameState.getPlayer().copy(),MoveType.DOWN);
        int reward = getReward(new GameState(ball,gameState.getBallPositions()));
        if(reward==-10){
            return 0;
        }else {
            double[] in = getInputByBallAndPositionVector(MoveType.UP,new GameState(ball,gameState.getBallPositions()));
            double[] out = network.predict(in);
            return out[MoveType.DOWN.getMove()];
        }
    }

    private static double getOutByMoveUP(GameState gameState) {
        Ball ball = moveBall(gameState.getPlayer().copy(),MoveType.UP);
        int reward = getReward(new GameState(ball,gameState.getBallPositions()));
        if(reward==-10){
            return 0;
        }else {
            double[] in = getInputByBallAndPositionVector(MoveType.UP,new GameState(ball,gameState.getBallPositions()));
            double[] out = network.predict(in);
            return out[MoveType.UP.getMove()];
        }
    }

    public static int getReward(GameState gameState) {
        for (BallPosition ballPosition : gameState.getBallPositions()) {
            if (gameState.getPlayer().detectCollision(ballPosition)) {
                return -10;
            }
        }
        return 1;
    }

    public static Ball moveBall(Ball ball, MoveType moveType) {
        assert moveType != null;

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
        return ball;
    }

    public static void cheeckBounds(Ball ball, ContainerBox box) {
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
}
