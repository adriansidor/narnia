package narnia.q_learning;

import lombok.*;
import narnia.Ball;
import narnia.BallPosition;

/**
 * Created by Radosław on 13.06.2016.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class GameState {
    private Ball player;
    private BallPosition[] ballPositions;
}
