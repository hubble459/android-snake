package hubble.snake;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

import static hubble.snake.Direction.RIGHT;

public class Snake {
    private int size; // updates per second
    private long speed;
    private Direction direction = RIGHT;
    private final Random random;
    private Point candy;
    private final Queue<Point> points;
    private final int COL_MAX, ROW_MAX;
    private final OnSnakeMove listener;
    private final ValueAnimator valueAnimator;
    private int last;

    public Snake(@NonNull OnSnakeMove listener, int maxRow, int maxCol) {
        this.points = new ArrayDeque<>();
        this.listener = listener;
        this.random = new Random();

        COL_MAX = maxCol - 1;
        ROW_MAX = maxRow - 1;

        resetSnake();
        placeCandy();

        valueAnimator = new ValueAnimator();
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setIntValues(0, 2, 4, 6, 8);
        valueAnimator.setDuration(1000 - speed);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.addUpdateListener(animation -> {
            int av = (int) animation.getAnimatedValue();
            if (last != av && av % 2 == 0) {
                last = av;
                move();
            }
        });
        valueAnimator.start();
        valueAnimator.pause();
    }

    private void resetSnake() {
        if (points != null) {
            points.clear();
            size = 3;
            for (int i = size - 1; i >= 0; i--) {
                points.offer(new Point(ROW_MAX / 2, COL_MAX / 2 - i));
            }
        }
    }

    public void setPaused(boolean pause) {
        if (pause) {
            valueAnimator.pause();
        } else {
            valueAnimator.resume();
        }
    }

    public boolean isPaused() {
        return valueAnimator.isPaused();
    }

    private void placeCandy() {
        int row;
        int col;
        do {
            row = random.nextInt(ROW_MAX);
            col = random.nextInt(COL_MAX);
        } while (spotOccupied(row, col));

        candy = new Point(row, col);
    }

    public Point getCandy() {
        return candy;
    }

    private boolean spotOccupied(int row, int col) {
        if (points != null) {
            for (Point point : points) {
                if (point.row == row && point.column == col) {
                    return true;
                }
            }
        }
        return false;
    }

    public Queue<Point> getPoints() {
        return points;
    }

    public void move() {
        if (points == null || points.isEmpty()) return;

        Point head = (Point) points.toArray()[points.size() - 1];
        int row = head.row;
        int column = head.column;

        switch (direction) {
            case UP:
                row -= 1;
                if (row < 0) {
                    row = ROW_MAX;
                }
                break;
            case DOWN:
                row += 1;
                if (row > ROW_MAX) {
                    row = 0;
                }
                break;
            case LEFT:
                column -= 1;
                if (column < 0) {
                    column = COL_MAX;
                }
                break;
            case RIGHT:
                column += 1;
                if (column > COL_MAX) {
                    column = 0;
                }
        }

        if (points.size() >= size) {
            points.poll();
        }
        Point point = new Point(row, column);
        if (hitSnake(point)) {
            listener.dead();
            resetSnake();
        } else {
            checkCandy(point);
            points.offer(point);
        }

        listener.moved();
    }

    private boolean hitSnake(Point point) {
        if (points != null) {
            for (Point p : points) {
                if (p.equals(point)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getScore() {
        return size - 3;
    }

    private void checkCandy(Point point) {
        if (candy != null && candy.equals(point)) {
            placeCandy();
            size += 1;
            if (speed == 0) {
                speed = 5;
            } else {
                speed += 5;
            }
            valueAnimator.setDuration(Math.max(1, 1000 - speed));
        }
    }

    public void setDirection(Direction direction) {
        if (this.direction.getOpposite() == direction) return;
        this.direction = direction;
    }

    public interface OnSnakeMove {
        void moved();

        void dead();
    }
}
