package narnia;

import narnia.utils.MoveType;

/**
 * Created by Radosław on 14.06.2016.
 */
public class QLearningPlayer implements BallMoveDriver{

    private final boolean RANDOM_MOVES = true;

    private MoveType curentMove;

    public void move(Ball ball, ContainerBox box, BallPosition[] positionVector) {

        if(RANDOM_MOVES){
            curentMove = Utils.getRandomMove();
        }else {

        }

        Utils.moveBall(ball,curentMove);
        Utils.checkMove(ball,box);

    }
}
