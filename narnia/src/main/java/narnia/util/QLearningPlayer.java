package narnia.util;

import narnia.*;

/**
 * Created by Radosław on 15.06.2016.
 */
public class QLearningPlayer implements BallMoveDriver {


    private static final boolean RANDOM_MOVES = true;
    private static final double PENALTY = -10000;

    private GameNetwork network = GameNetwork.getInstance();

    private MoveType lastMove = MoveType.DO_NOT_MOVE;
    private double curentReward;

    public void move(Ball ball, ContainerBox box, BallPosition[] positionVector) {

        MoveType curentMove;
        if(RANDOM_MOVES) {
            curentMove = Utils.getRandomMove();
        }else {
        }

        GameState actual = new GameState(ball.copy(),positionVector,MoveType.DO_NOT_MOVE,box);

        Ball nextMove = Utils.moveBall(ball.copy(),curentMove);

        GameState newState = new GameState(nextMove.copy(),positionVector,curentMove,box);

        this.curentReward = getReward(actual);

        Utils.moveBall(ball,curentMove);
        Utils.checkMove(ball,box);
        lastMove = curentMove;
    }

    private double getReward(GameState actual) {
        Ball ball = actual.getBall().copy();
        double rewartResult = 0;
        for (BallPosition ballPosition : actual.getBallPositions()) {
            if (actual.getBall().detectCollision(ballPosition)) {
                return PENALTY;
            }else {
                rewartResult+=updateReward(ball,ballPosition);
            }
        }
        return rewartResult;
    }

    private double updateReward(Ball ball, BallPosition ballPosition) {
        return 0;
    }
}
