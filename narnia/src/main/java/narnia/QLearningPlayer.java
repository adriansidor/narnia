package narnia;

import narnia.utils.MoveType;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;

/**
 * Created by Radosław on 14.06.2016.
 */
public class QLearningPlayer implements BallMoveDriver {

    private static final double GAMMA = 0.97;
    private static final double BETA_T = 0.1;
    private final boolean RANDOM_MOVES = true;

    private MoveType curentMove;

    private MoveType lastMove = MoveType.DO_NOT_MOVE;

    private int curentReward;

    private GameNetwork network = GameNetwork.getInstance();

    double[] oldOut = new double[3];

    public void move(Ball ball, ContainerBox box, BallPosition[] positionVector) {

        oldOut = network.getOut();

        if (RANDOM_MOVES) {
            curentMove = Utils.getRandomMove();
        } else {
//          move by network
            curentMove = network.getMoveByState(new GameState(ball.copy(), positionVector));
        }

        Ball nextMove = ball.copy();
        Utils.checkMove(nextMove, box);
        Utils.moveBall(nextMove, curentMove);


        curentReward = Utils.getReward(new GameState(nextMove, positionVector));

        updateNetworkFunction(new GameState(nextMove, positionVector), new GameState(ball.copy(), positionVector));


        ball.y = nextMove.y;
        Utils.checkMove(ball, box);

        lastMove = curentMove;
    }

    private void updateNetworkFunction(GameState newGameState, GameState oldGameState) {
        double maxByNextStates = getMaxByStates(newGameState);
        maxByNextStates = GAMMA * maxByNextStates;
        curentReward /= 11;
        double Qxtut = network.predictByInput(oldGameState, lastMove)[lastMove.getMove()];

        double bracket = ((double)curentReward+(GAMMA*maxByNextStates)-Qxtut);
        double updatedVal = Qxtut+(BETA_T*bracket);


        oldOut[lastMove.getMove()] = updatedVal;

        double [] oldIn = Utils.getInputByBallAndPositionVector(lastMove,oldGameState);

        DataSet toLearn = getDataSet(oldIn,oldOut);

        network.learn(toLearn);

    }

    private DataSet getDataSet(double[] in, double[] out){
        DataSet dataSet = new DataSet(16);
        dataSet.addRow(new DataSetRow(in,out));
        return dataSet;
    }

    private double getMaxByStates(GameState newGameState) {
//        TODO add reward
        double a = network.predictByInput(newGameState, MoveType.UP)[MoveType.UP.getMove()];
        double b = network.predictByInput(newGameState, MoveType.DO_NOT_MOVE)[MoveType.DO_NOT_MOVE.getMove()];
        double c = network.predictByInput(newGameState, MoveType.DOWN)[MoveType.DOWN.getMove()];
        return Math.max(Math.max(a, b), c);
    }
}
