package narnia.util;

import lombok.Data;
import narnia.Ball;
import narnia.BallPosition;

/**
 * Created by Radosław on 15.06.2016.
 */
@Data
public class DictanceVector {
    private float x;
    private float y;

    public static DictanceVector getDictanceVector(Ball ball, BallPosition ballPosition){
        DictanceVector dictanceVector = new DictanceVector();
        dictanceVector.x = ballPosition.getX() - ball.getX();
        dictanceVector.y = ballPosition.getY() - ball.getY();
        return dictanceVector;
    }
}
