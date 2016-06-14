package narnia;

import narnia.q_learning.GameNetwork;
import narnia.q_learning.GameState;
import narnia.q_learning.MoveType;

/**
 * Created by Radosław on 14.06.2016.
 */
public class QLearningPlayer implements BallMoveDriver {

    private static final boolean RANDOM_MOVES = true;
    private static final float STEP_UNIT = 10;

    private GameNetwork network = GameNetwork.getInstance();


    MoveType curentMove = null;
    private float beforeMove;
    private MoveType lastMove;
    private int curentReward;

    public void move(Ball ball, ContainerBox box, BallPosition[] positionVector) {

        if (RANDOM_MOVES) {
            curentMove = Utils.getRandomMove();
        } else {
//            moves from network
        }

        Ball newBallPosition = Utils.moveBall(ball.copy(), curentMove);
        Utils.cheeckBounds(newBallPosition, box);
//
        this.curentReward = Utils.getReward(new GameState(newBallPosition, positionVector));

        updateQFunction(ball.copy(), curentMove, newBallPosition, positionVector);

//
        ball.y = newBallPosition.y;
        Utils.cheeckBounds(ball, box);
    }

    private void updateQFunction(Ball beforeMove, MoveType curentMove, Ball newBallPosition, BallPosition[] positionVector) {
        double[] networkInput = Utils.getInputByBallAndPositionVector(curentMove, new GameState(beforeMove, positionVector));
        double[] networkOutpuy = getNetworkOut(beforeMove, newBallPosition, positionVector, curentMove);
    }

    private double[] getNetworkOut(Ball beforeMove, Ball newBallPosition, BallPosition[] positionVector, MoveType curentMove) {
        double[] networkOut = network.getOutput();
        double toUpdateVal = networkOut[curentMove.getMove()];
        double maxNextStep = getMaxOfNextMove(new GameState(newBallPosition, positionVector));
        return null;
    }

    private double getMaxOfNextMove(GameState gameState) {
        return Utils.getMaxQFunction(gameState, curentReward);
    }



}
