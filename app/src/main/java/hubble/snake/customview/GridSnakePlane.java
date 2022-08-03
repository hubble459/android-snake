package hubble.snake.customview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import hubble.snake.Point;
import hubble.snake.Snake;

import static hubble.snake.Direction.DOWN;
import static hubble.snake.Direction.LEFT;
import static hubble.snake.Direction.RIGHT;
import static hubble.snake.Direction.UP;

public class GridSnakePlane extends View {
    private final int BLOCK_SIZE = 50;
    private final Rect block = new Rect(0, 0, BLOCK_SIZE, BLOCK_SIZE);

    private Paint paint;
    private Snake snake;
    private AlertDialog deadDialog;

    private boolean moved;
    private float oldX, oldY;
    private int rows, columns;
    private int[][] grid;

    public GridSnakePlane(Context context) {
        super(context);
        init();
    }

    public GridSnakePlane(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GridSnakePlane(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.paint = new Paint();
    }

    private void resetGrid() {
        grid = new int[rows][columns];
    }


    private void getValues() {
        snake = new Snake(new Snake.OnSnakeMove() {
            @Override
            public void moved() {
                resetGrid();
                fillSnakePoints();
                invalidate();
            }

            @Override
            public void dead() {
                new AlertDialog.Builder(getContext())
                        .setTitle("Game Over")
                        .setMessage(snake.getScore() + " points!")
                        .show();
            }
        }, rows, columns);
        fillSnakePoints();
    }

    private void fillSnakePoints() {
        if (snake != null) {
            for (Point point : snake.getPoints()) {
                grid[point.row][point.column] = 1;
            }
            Point candy = snake.getCandy();
            grid[candy.row][candy.column] = 2;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (snake == null) {
            getValues();
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (grid[i][j] == 1) {
                    if (paint.getColor() != Color.CYAN) {
                        paint.setColor(Color.CYAN);
                    }
                } else if (grid[i][j] == 2) {
                    if (paint.getColor() != Color.GREEN) {
                        paint.setColor(Color.GREEN);
                    }
                } else if (paint.getColor() != Color.MAGENTA) {
                    paint.setColor(Color.MAGENTA);
                }
                canvas.drawRect(block.right * j, block.bottom * i, block.right + block.right * j, block.bottom + block.bottom * i, paint);

            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (snake != null) {
            if (snake.isPaused()) {
                snake.setPaused(false);
                return super.onTouchEvent(event);
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    oldX = event.getX();
                    oldY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    moved = true;
                    break;
                case MotionEvent.ACTION_UP:
                    if (!moved) {
                        break;
                    }

                    float newX = event.getX();
                    float newY = event.getY();

                    int deltaX = (int) (oldX - newX);
                    int deltaY = (int) (oldY - newY);

                    if (Math.abs(deltaY) > Math.abs(deltaX)) {
                        if (oldY < newY) {
                            snake.setDirection(DOWN);
                        } else {
                            snake.setDirection(UP);
                        }
                    } else {
                        if (oldX < newX) {
                            snake.setDirection(RIGHT);
                        } else {
                            snake.setDirection(LEFT);
                        }
                    }
                    moved = false;
                    break;
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i("uwu", "onMeasure: measuring");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        columns = w / BLOCK_SIZE;
        rows = h / BLOCK_SIZE;
        resetGrid();

        setMeasuredDimension(columns * BLOCK_SIZE, rows * BLOCK_SIZE);
    }
}
