package ru.ondigit.snake;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ScreenGame extends ScreenAdapter {

    private SpriteBatch batch;
    private Texture snakeHead;
    private static final float MOVE_TIME = 0.25F;
    private float timer = MOVE_TIME;
    private static final int SNAKE_MOVEMENT = 32;
    public int snakeX = 0, snakeY = 0;
    private static final int RIGHT = 0;
    private static final int LEFT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;
    private int direction = RIGHT;
    private boolean lPressed;
    private boolean rPressed;
    private boolean uPressed;
    private boolean dPressed;
    private Texture apple;
    private Texture snakeBody;
    private boolean appleAvailable = false;
    private int appleX, appleY;
    private Array<BodyPart> bodyParts;
    private int snakeXbeforeUpdate = 0, snakeYbeforeUpdate = 0;
    private boolean directionSet;
    private enum STATE {
        PLAYING, GAME_OVER
    }
    private STATE state = STATE.PLAYING;
    private static final String GAME_OVER_TEXT = "Game Over... Tap space to restart!";
    private GlyphLayout layout;
    private BitmapFont bitmapFont;
    private static final float WORLD_WIDTH = 640;
    private static final float WORLD_HEIGHT = 480;
    private Viewport viewport;
    private Camera camera;
    private int score = 0;
    private static final int POINTS_PER_APPLE = 20;
    private GlyphLayout scoreBounds;

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(WORLD_WIDTH /2, WORLD_HEIGHT / 2,0);
        camera.update();
        viewport = new FitViewport(WORLD_WIDTH,WORLD_HEIGHT,camera);
        layout = new GlyphLayout();
        scoreBounds = new GlyphLayout();
        batch = new SpriteBatch();
        bitmapFont = new BitmapFont();
        snakeHead = new Texture(Gdx.files.internal("snakehead.png"));
        snakeBody = new Texture(Gdx.files.internal("snakebody.png"));
        bodyParts = new Array<BodyPart>();
        apple = new Texture(Gdx.files.internal("apple.png"));
    }

    @Override
    public void render(float delta) {
        switch (state) {
            case PLAYING: {
                queryInput();
                updateSnake(delta);
                checkAppleCollision();
                checkAndPlaceApple();
                break;
            }
            case GAME_OVER: {
                checkForRestart();
            }
        }
        clearScreen();
        draw();
    }

    public void updateSnake(float delta) {
            timer -= delta;
            if(timer <=0) {
                timer = MOVE_TIME;
                moveSnake();
                checkForOutOfBounds();
                updateBodyPartsPosition();
                checkSnakeBodyCollision();
                directionSet = false;
            }

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
        snakeXbeforeUpdate = snakeX;
        snakeYbeforeUpdate = snakeY;

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

        if (lPressed) updateDirection(LEFT);
        if (rPressed) updateDirection(RIGHT);
        if (uPressed) updateDirection(UP);
        if (dPressed) updateDirection(DOWN);
    }

    private void checkAndPlaceApple() {
        if (!appleAvailable) {
            do {
                appleX = MathUtils.random(Gdx.graphics.getWidth() / SNAKE_MOVEMENT - 1) * SNAKE_MOVEMENT;
                appleY = MathUtils.random(Gdx.graphics.getHeight() / SNAKE_MOVEMENT - 1) * SNAKE_MOVEMENT;
                appleAvailable = true;
            } while(appleX == snakeX && appleY == snakeY);
        }
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void draw() {
        batch.begin();

        if (state == STATE.GAME_OVER) {
            layout.setText(bitmapFont,GAME_OVER_TEXT);
            bitmapFont.setColor(new Color(0f,0f,1f,1f));
            bitmapFont.draw(batch,GAME_OVER_TEXT, (viewport.getWorldWidth() - layout.width) / 2,
                    (viewport.getWorldHeight() - layout.height) / 2);
        }
        drawScore();
        for (BodyPart bodyPart : bodyParts) {
            bodyPart.draw(batch);
        }

        batch.draw(snakeHead,snakeX,snakeY);
        if (appleAvailable) {
            batch.draw(apple,appleX,appleY);
        }
        batch.end();
    }

    private void checkAppleCollision() {
        if (appleAvailable && snakeY == appleY && snakeX == appleX) {
            BodyPart bodyPart = new BodyPart(snakeBody);
            bodyPart.updateBodyPosition(snakeX,snakeY);
            bodyParts.insert(0,bodyPart);
            addToScore();
            appleAvailable = false;
        }
    }

    private void updateBodyPartsPosition() {
        if (bodyParts.size > 0) {
            BodyPart bodyPart = bodyParts.removeIndex(0);
            bodyPart.updateBodyPosition(snakeXbeforeUpdate,snakeYbeforeUpdate);
            bodyParts.add(bodyPart);
        }
    }

    private void updateIfNotOppositeDirection(int newSnakeDirection, int oppositeDirection) {
        if (newSnakeDirection != oppositeDirection || bodyParts.size == 0) {
            direction = newSnakeDirection;
        }
    }

    private void updateDirection(int newSnakeDirection) {
        if (!directionSet && direction != newSnakeDirection) {
            switch (direction) {
                case LEFT: {
                    updateIfNotOppositeDirection(newSnakeDirection, RIGHT);
                    break;
                }
                case RIGHT: {
                    updateIfNotOppositeDirection(newSnakeDirection, LEFT);
                    break;
                }
                case UP: {
                    updateIfNotOppositeDirection(newSnakeDirection, DOWN);
                    break;
                }
                case DOWN: {
                    updateIfNotOppositeDirection(newSnakeDirection, UP);
                    break;
                }
            }
        }
    }

    private void checkSnakeBodyCollision() {
        for (BodyPart bodyPart : bodyParts) {
            if (bodyPart.getX() == snakeX && bodyPart.getY() == snakeY) {
                state = STATE.GAME_OVER;
            }
        }
    }

    private void checkForRestart() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) doRestart();
    }

    private void doRestart() {
        state = STATE.PLAYING;
        bodyParts.clear();
        direction = RIGHT;
        directionSet = false;
        timer = MOVE_TIME;
        score = 0;
        snakeX = 0;
        snakeY = 0;
        snakeXbeforeUpdate = 0;
        snakeYbeforeUpdate = 0;
        appleAvailable = false;
    }

    private void addToScore() {
        score += POINTS_PER_APPLE;
    }

    private void drawScore() {
        if (state == STATE.PLAYING) {
            String scoreAsString = Integer.toString(score);
            scoreBounds.setText(bitmapFont, scoreAsString);
            bitmapFont.draw(batch, scoreAsString, (Gdx.graphics.getWidth() - scoreBounds.width) / 2,
                    (4 * Gdx.graphics.getHeight() /5) - scoreBounds.height /2);
        }
    }
}
