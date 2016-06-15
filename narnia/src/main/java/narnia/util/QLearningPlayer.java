package narnia.util;

import narnia.*;

import static org.encog.persist.PersistConst.SIGMA;
import static sun.security.x509.X509CertImpl.SIG;

/**
 * Created by Radosław on 15.06.2016.
 */
public class QLearningPlayer implements BallMoveDriver {


    private static final boolean RANDOM_MOVES = true;
    private static final double PENALTY = -10000;
    private static final double BETA_T = 0.1;
    private static final double SIGMA_ = 0.97;

    private GameNetwork network = GameNetwork.getInstance();

    private MoveType lastMove = MoveType.DO_NOT_MOVE;
    private double curentReward;
    private MoveType curentMove = MoveType.DO_NOT_MOVE;

    public void move(Ball ball, ContainerBox box, BallPosition[] positionVector) {


        if (RANDOM_MOVES) {
            curentMove = Utils.getRandomMove();
        } else {

        }

        GameState actualState = new GameState(ball.copy(), positionVector, curentMove, box);

        Ball nextBallPos = Utils.moveBall(ball.copy(), curentMove);
        Utils.checkMove(nextBallPos, box);

        GameState newState = new GameState(nextBallPos.copy(), positionVector, curentMove, box);

        this.curentReward = getReward(actualState, newState);

        updateLearningFunction(actualState, newState);

        Utils.moveBall(ball, curentMove);
        Utils.checkMove(ball, box);
        lastMove = curentMove;

    }

    private void updateLearningFunction(GameState actualState, GameState newState) {
        MoveType moveType = actualState.getMoveInState();
        double newMaxQ = maxOfNextMovesByAllStates(newState);
        double[] actInp = Utils.getInputByState(actualState);
        double[] out = network.predict(actInp);
        double actualVal = out[moveType.getMove()];
        double tuUpdate = actualVal+BETA_T*(curentReward+SIGMA_*newMaxQ-actualVal);
        out[moveType.getMove()] = tuUpdate;
        network.train(actualState,out);
    }

    private double maxOfNextMovesByAllStates(GameState newState) {
        double maxUp = getMaxByMoveType(newState, MoveType.UP);
        double maxDontMove = getMaxByMoveType(newState, MoveType.DO_NOT_MOVE);
        double maxDown = getMaxByMoveType(newState, MoveType.DOWN);
        return Math.max(Math.max(maxDontMove, maxDown), maxUp);
    }

    private double getMaxByMoveType(GameState newState, MoveType moveType) {
        Ball ball = newState.getBall();
        Utils.moveBall(ball, moveType);
        if (isEndOfEpizot(ball, newState.getBallPositions())) {
            return 0;
        } else {
            double[] out = network.predict(Utils.getInputByState(
                    new GameState(ball, newState.getBallPositions(), moveType, newState.containerBox)));
            System.out.println();
            return Math.max(Math.max(out[0], out[1]), out[2]);
        }
    }


    private double getReward(GameState actual, GameState newState) {
        Ball actualBall = actual.getBall().copy();
        Ball newBall = newState.getBall().copy();
        double rewartResult = 0;
        for (BallPosition ballPosition : actual.getBallPositions()) {
            if (actual.getBall().detectCollision(ballPosition)) {
                return PENALTY;
            } else {
                rewartResult += updateReward(actualBall, newBall, ballPosition);
            }
        }
        return rewartResult;
    }

    private boolean isEndOfEpizot(Ball ball, BallPosition[] positions) {
        for (BallPosition ballPosition : positions) {
            if (ball.detectCollision(ballPosition)) {
                return true;
            }
        }
        return false;
    }

    private double updateReward(Ball actualBall, Ball newBall, BallPosition ballPosition) {
        double res = 0;
        DictanceVector actualDistance = DictanceVector.getDictanceVector(actualBall, ballPosition);
        DictanceVector newDistance = DictanceVector.getDictanceVector(newBall, ballPosition);

        if (newDistance.getY() - actualDistance.getY() > 0) {
            res = 5;
        } else {
            res = -1;
        }

        if (ballPosition != null && actualBall.getDirection() == ballPosition.getDirection()) {
            res -= 2;
        } else {
            res += 2;
        }

        double multipler = getMultiplerByDistance(newDistance.getX());


        return res * multipler;
    }

    private double getMultiplerByDistance(float y) {
        if (y >= 0 && y < 200) return 0.9;
        if (y >= 200 && y < 400) return 0.6;
        if (y >= 400 && y < 600) return 0.4;
        else return 0.3;
    }


}
