package narnia;

import narnia.utils.MoveType;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;

/**
 * Created by Radosław on 14.06.2016.
 */
public class QLearningPlayer implements BallMoveDriver {

    private final boolean RANDOM_MOVES = true;


    private static final double GAMMA = 0.97;
    private static final double BETA_T = 0.05;

    private MoveType curentMove;

    private MoveType lastMove = MoveType.DO_NOT_MOVE;

    private double curentReward;

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

//        if(RANDOM_MOVES) {
            updateNetworkFunction(new GameState(nextMove, positionVector), new GameState(ball.copy(), positionVector));
//        }

        ball.y = nextMove.y;
        Utils.checkMove(ball, box);

        lastMove = curentMove;
    }

    private void updateNetworkFunction(GameState newGameState, GameState oldGameState) {
        double maxByNextStates = getMaxByStates(newGameState);
        maxByNextStates = GAMMA * maxByNextStates;
        curentReward /= 2;
        double Qxtut = network.predictByInput(oldGameState, lastMove)[lastMove.getMove()];

        double bracket = (curentReward+(GAMMA*maxByNextStates)-Qxtut);
        if(bracket<0){
            int i;
        }
        double updatedVal = Qxtut+(BETA_T*bracket);


        oldOut[lastMove.getMove()] = updatedVal;

        double [] oldIn = Utils.getInputByBallAndPositionVector(lastMove,oldGameState);

        DataSet toLearn = getDataSet(oldIn,oldOut);

        network.learn(toLearn);

        double[] newOut = network.predictByInput(newGameState,curentMove);

        System.out.println();
    }

    private DataSet getDataSet(double[] in, double[] out){
        DataSet dataSet = new DataSet(16);
        dataSet.addRow(new DataSetRow(in,out));
        return dataSet;
    }

    private double getMaxByStates(GameState newGameState) {
//        TODO add reward if colision  = 0
        double a, b ,c;
        if(Utils.getReward(new GameState(Utils.moveBall(newGameState.getPlayer().copy(),MoveType.UP),newGameState.getBallPositions()))==-10){
            a = 0;
        }else {
            a = network.predictByInput(newGameState, MoveType.UP)[MoveType.UP.getMove()];
        }
        if(Utils.getReward(new GameState(Utils.moveBall(newGameState.getPlayer().copy(),MoveType.DO_NOT_MOVE),newGameState.getBallPositions()))==-10){
            b = 0;
        }else {
            b = network.predictByInput(newGameState, MoveType.DO_NOT_MOVE)[MoveType.DO_NOT_MOVE.getMove()];
        }
        if(Utils.getReward(new GameState(Utils.moveBall(newGameState.getPlayer().copy(),MoveType.DOWN),newGameState.getBallPositions()))==-10){
            c = 0;
        }else {
            c = network.predictByInput(newGameState, MoveType.DOWN)[MoveType.DOWN.getMove()];
        }
        return Math.max(Math.max(a, b), c);
    }
}
