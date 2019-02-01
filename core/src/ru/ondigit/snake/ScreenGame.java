package ru.ondigit.snake;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ScreenGame extends ScreenAdapter {

    private SpriteBatch batch;
    private Texture snakeHead;
    private static final float MOVE_TIME = 1F;
    private float timer = MOVE_TIME;
    private static final int SNAKE_MOVEMENT = 32;
    private int snakeX = 0, snakeY = 0;
    private static final int RIGHT = 0;
    private static final int LEFT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;
    private int direction = RIGHT;
    private boolean lPressed;
    private boolean rPressed;
    private boolean uPressed;
    private boolean dPressed;

    @Override
    public void show() {
        batch = new SpriteBatch();
        snakeHead = new Texture(Gdx.files.internal("snakehead.png"));
    }

    @Override
    public void render(float delta) {
        queryInput();
        timer -= delta;
        if(timer <=0) {
            timer = MOVE_TIME;
            moveSnake();
        }
        checkForOutOfBounds();
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(snakeHead,snakeX,snakeY);
        batch.end();
    }

    private void checkForOutOfBounds() {
        if (snakeX >= Gdx.graphics.getWidth()) {
            snakeX = 0;
        }
        if (snakeX < 0) {
            snakeX = Gdx.graphics.getWidth() - SNAKE_MOVEMENT;
        }
        if (snakeY >= Gdx.graphics.getHeight()) {
            snakeY = 0;
        }
        if (snakeY < 0) {
            snakeY = Gdx.graphics.getHeight() - SNAKE_MOVEMENT;
        }
    }

    private void moveSnake() {
        switch (direction) {
            case RIGHT: {
                snakeX += SNAKE_MOVEMENT;
                break;
            }
            case LEFT: {
                snakeX -= SNAKE_MOVEMENT;
                break;
            }
            case UP: {
                snakeY += SNAKE_MOVEMENT;
                break;
            }
            case DOWN: {
                snakeY -= SNAKE_MOVEMENT;
                break;
            }
        }
    }

    private void queryInput() {
        lPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        rPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        uPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
        dPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);

        if (lPressed) direction = LEFT;
        if (rPressed) direction = RIGHT;
        if (uPressed) direction = UP;
        if (dPressed) direction = DOWN;
    }
}
