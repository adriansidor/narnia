package narnia;

import lombok.*;

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
