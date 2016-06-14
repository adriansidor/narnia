package narnia.utils;

import lombok.Getter;

/**
 * Created by Radosław on 13.06.2016.
 */
public enum MoveType {
    UP(0), DOWN(2), DO_NOT_MOVE(1);

    @Getter
    private int move;

    MoveType(int move) {
        this.move = move;
    }


}
