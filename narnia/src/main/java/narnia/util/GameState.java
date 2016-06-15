package narnia.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import narnia.Ball;
import narnia.BallPosition;
import narnia.ContainerBox;

/**
 * Created by Radosław on 15.06.2016.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GameState {
    private Ball ball;
    private BallPosition[] ballPositions;
    private MoveType moveInState;
    ContainerBox containerBox;

}
