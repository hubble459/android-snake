package hubble.snake;

public enum Direction {
    LEFT(0),
    UP(1),
    RIGHT(2),
    DOWN(3);

    private int dir;

    Direction(int dir) {
        this.dir = dir;
    }

    public Direction getOpposite() {
        if (dir == 0) {
            return RIGHT;
        } else if (dir == 1) {
            return DOWN;
        } else if (dir == 2) {
            return LEFT;
        } else {
            return UP;
        }
    }
}
