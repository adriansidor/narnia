package narnia.q_learning;

import narnia.Ball;
import narnia.BallPosition;

import java.util.Random;

/**
 * Created by Radosław on 13.06.2016.
 */
public class Utils {


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
}
