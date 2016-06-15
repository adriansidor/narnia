package narnia;

import narnia.util.DictanceVector;
import narnia.util.GameNetwork;
import narnia.util.GameState;
import narnia.util.MoveType;

import java.util.Random;

/**
 * Created by Radosław on 15.06.2016.
 */
public class Utils {

    public static final float STEP_UNIT = 20;



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
            ball.direction = 1;

        } else if (ball.y > ballMaxY) {
            ball.speedY = -ball.speedY;
            ball.y = ballMaxY;
            ball.direction = 0;
        }
    }

    public static Ball moveBall(Ball ball, MoveType moveType) {
        assert moveType != null;

        switch (moveType) {
            case UP:
                ball.y -= STEP_UNIT;
                break;
            case DO_NOT_MOVE:
                break;
            case DOWN:
                ball.y += STEP_UNIT;
                break;
        }
        return ball;
    }

    public static double[] getInputByState(GameState gameState) {
        double [] input= new double[GameNetwork.NUMBER_OF_INPUT_NEURON];
//        TODO do this
        Ball ball = gameState.getBall();
        MoveType moveType = gameState.getMoveInState();
        ContainerBox cb = gameState.getContainerBox();
        switch (moveType) {
            case UP:
                input[0] = 1;
                break;
            case DO_NOT_MOVE:
                input[1] = 1;
                break;
            case DOWN:
                input[2] = 1;
                break;
        }

        int i = 3;
        for(BallPosition bp: gameState.getBallPositions()){
            if(bp!=null){
                DictanceVector dv = DictanceVector.getDictanceVector(ball,bp);
                input[i++] = dv.getX();
                input[i++] = dv.getY();
                input[i++] = bp.getDirection();
            }else {
                input[i++] = cb.maxX;
                input[i++] = cb.maxY;
                input[i++] = 0;
            }
        }
        return input;
    }

    public static MoveType moveByNetwork(GameState gameState) {
        double[] in = getInputByState(gameState);
        GameNetwork network = GameNetwork.getInstance();
        double[] out = network.predict(in);
        MoveType moveType = MoveType.UP;
        double a,b,c;
        a = out[0];
        b = out[1];
        c = out[2];
        if(a>b&&a>c){
            moveType =  MoveType.UP;
        }
        if(b>a&&b>c){
            moveType =  MoveType.DO_NOT_MOVE;
        }if (c>a&&c>b){
            moveType=  MoveType.DOWN;
        }
        return moveType;
    }
}
