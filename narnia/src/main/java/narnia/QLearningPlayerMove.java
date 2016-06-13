package narnia;

import narnia.q_learning.GameNetwork;
import narnia.q_learning.MoveType;

import java.util.Random;

/**
 * Created by Radosław on 13.06.2016.
 */
public class QLearningPlayerMove implements BallMoveDriver {

    private static final int STEP_UNIT = 10;

    private GameNetwork gameNetwork = GameNetwork.getInstance();


    public void move(Ball ball, ContainerBox box, BallPosition[] positionVector) {
        float ballMinX = box.getMinX() + ball.getRadius();
        float ballMinY = box.getMinY() + ball.getRadius();
        float ballMaxX = box.getMaxX() - ball.getRadius();
        float ballMaxY = box.getMaxY() - ball.getRadius();

        Random random = new Random();
        int randomInt = random.nextInt();


        if(randomInt%7==0){
            moveByNetwork(ball);
        }else {
            getRandomMove(ball);
        }
        // and speed.
        if (ball.x < ballMinX) {
            ball.speedX = -ball.speedX; // Reflect along normal
            ball.x = ballMinX; // Re-position the ball at the edge
        } else if (ball.x > ballMaxX) {
            ball.speedX = -ball.speedX;
            ball.x = ballMaxX;
        }
        // May cross both x and y bounds
        if (ball.y < ballMinY) {
            ball.speedY = -ball.speedY;
            ball.y = ballMinY;
        } else if (ball.y > ballMaxY) {
            ball.speedY = -ball.speedY;
            ball.y = ballMaxY;
        }

        int actualReward = getRaward(ball.copy(),positionVector);

        predictMoves(ball.copy(),positionVector.clone(),actualReward);



    }

    private MoveType moveByNetwork(Ball ball) {
        MoveType moveType = gameNetwork.getMove();
        makeMove(ball,moveType);
        return moveType;
    }

    private void predictMoves(Ball copy, BallPosition[] clone, int actualReward) {

    }

    private void getRandomMove(Ball ball) {
        Random random = new Random();
        int randInt = random.nextInt(3);
        MoveType moveType = null;
        switch (randInt){
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

        makeMove(ball, moveType);

    }

    private void makeMove(Ball ball, MoveType moveType) {
        assert moveType!=null;
        switch (moveType){
            case UP:
                ball.y+=STEP_UNIT;
                break;
            case DO_NOT_MOVE:
                break;
            case DOWN:
                ball.y-=STEP_UNIT;
                break;
        }
    }


    private int getRaward(Ball player, BallPosition[] positionVector) {
        for (BallPosition ballPosition : positionVector) {
            if (player.detectCollision(ballPosition)) {
                return -10;
            }
        }
        return 0;
    }
}
