package ru.ondigit.snake;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class BodyPart {
    private int x,y;
    private Texture texture;
    private ScreenGame screen;

    public BodyPart(Texture texture) {
        this.texture = texture;
        screen = new ScreenGame();
    }

    public void updateBodyPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Batch batch) {
        if (!(screen.snakeX == x && screen.snakeY == y)) {
            batch.draw(texture,x,y);
        }
    }
}
