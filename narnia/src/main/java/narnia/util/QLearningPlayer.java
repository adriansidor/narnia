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

        this.curentReward = getReward(actual,newState);

        Utils.moveBall(ball,curentMove);
        Utils.checkMove(ball,box);
        lastMove = curentMove;
    }

    private double getReward(GameState actual,GameState newState) {
        Ball actualBall = actual.getBall().copy();
        Ball newBall = newState.getBall().copy();
        double rewartResult = 0;
        for (BallPosition ballPosition : actual.getBallPositions()) {
            if (actual.getBall().detectCollision(ballPosition)) {
                return PENALTY;
            }else {
                rewartResult+=updateReward(actualBall,newBall,ballPosition);
            }
        }
        return rewartResult;
    }

    private double updateReward(Ball actualBall, Ball newBall, BallPosition ballPosition) {
        double res = 0;
        DictanceVector actualDistance = DictanceVector.getDictanceVector(actualBall,ballPosition);
        DictanceVector newDistance = DictanceVector.getDictanceVector(newBall,ballPosition);

        if(newDistance.getY()-actualDistance.getY()>0){
            res = 5;
        }else {
            res = -1;
        }

        if(ballPosition!=null&&actualBall.getDirection()==ballPosition.getDirection()){
            res-=2;
        }else {
            res+=2;
        }

        double multipler = getMultiplerByDistance(newDistance.getX());



        return res*multipler;
    }

    private double getMultiplerByDistance(float y) {
        if(y>=0&&y<200) return 0.9;
        if(y>=200&&y<400) return 0.6;
        if(y>=400&&y<600) return 0.4;
        else return 0.3;
    }


}
