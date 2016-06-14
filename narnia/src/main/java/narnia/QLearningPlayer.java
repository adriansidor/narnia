package narnia;

import narnia.utils.MoveType;

/**
 * Created by Radosław on 14.06.2016.
 */
public class QLearningPlayer implements BallMoveDriver{

    private final boolean RANDOM_MOVES = true;

    private MoveType curentMove;

    private int currentReward;

    public void move(Ball ball, ContainerBox box, BallPosition[] positionVector) {

        if(RANDOM_MOVES){
            curentMove = Utils.getRandomMove();
        }else {

        }

        Ball nextMove = ball.copy();
        Utils.checkMove(nextMove,box);
        Utils.moveBall(nextMove,curentMove);



        currentReward = Utils.getReward(new GameState(nextMove,positionVector));

        updateNetworkFunction(new GameState(nextMove,positionVector),new GameState(ball.copy(),positionVector));


        ball.y = nextMove.y;
        Utils.checkMove(ball,box);
    }

    private void updateNetworkFunction(GameState newGameState, GameState oldGameState) {

    }
}
