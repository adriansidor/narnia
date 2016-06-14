package narnia;

import narnia.utils.MoveType;

import java.util.Random;

/**
 * Created by Radosław on 14.06.2016.
 */
public class Utils {


    private static final float STEP_UNIT = 10;

    public static void checkMove(Ball ball, ContainerBox box){
        float ballMinX = box.minX + ball.radius;
        float ballMinY = box.minY + ball.radius;
        float ballMaxX = box.maxX - ball.radius;
        float ballMaxY = box.maxY - ball.radius;

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
    }

    public static MoveType getRandomMove() {
        Random random = new Random();
        int randInt = random.nextInt(3);
        MoveType moveType = null;
        switch (randInt) {
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
        return moveType;
    }

    public static Ball moveBall(Ball ball, MoveType moveType) {
        assert moveType != null;

        switch (moveType) {
            case UP:
                ball.y += STEP_UNIT;
                break;
            case DO_NOT_MOVE:
                break;
            case DOWN:
                ball.y -= STEP_UNIT;
                break;
        }
        return ball;
    }

    public static int getReward(GameState gameState) {
        for (BallPosition ballPosition : gameState.getBallPositions()) {
            if (gameState.getPlayer().detectCollision(ballPosition)) {
                return -10;
            }
        }
        return 1;
    }



    public static double[] getInputByBallAndPositionVector(MoveType moveType, GameState gameState) {
        double[] result = new double[GameNetwork.INPUT_NEURON_NUMBER];
        switch (moveType) {
            case UP:
                result[0] = 1;
                break;
            case DO_NOT_MOVE:
                result[1] = 1;
                break;
            case DOWN:
                result[2] = 1;
                break;
        }
        Ball ball = gameState.getPlayer();
        result[3] = ball.getX();
        result[4] = ball.getY();
        result[5] = ball.getSpeed();
        int i = 6;
        int j = 0;
        while (i < GameNetwork.INPUT_NEURON_NUMBER) {
            BallPosition ballPosition = gameState.getBallPositions()[j++];
            if (ballPosition != null) {
                result[i++] = ballPosition.getX();
                result[i++] = ballPosition.getY();
            } else {
                result[i++] = 0;
                result[i++] = 0;
            }
        }
        return result;
    }
}
