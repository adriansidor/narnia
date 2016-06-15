package narnia.util;

import narnia.*;

/**
 * Created by Radosław on 15.06.2016.
 */
public class QLearningPlayer implements BallMoveDriver {


    private static final boolean RANDOM_MOVES = true;

    public void move(Ball ball, ContainerBox box, BallPosition[] positionVector) {

        MoveType moveType;
        if(RANDOM_MOVES) {
            moveType = Utils.getRandomMove();
        }else {

        }

        Utils.moveBall(ball,moveType);
        Utils.checkMove(ball,box);
    }
}
