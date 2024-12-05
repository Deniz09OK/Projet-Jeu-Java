package jeu2d.Main2D;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class KeyHandler extends InputAdapter {

    // Variables pour suivre l'état des touches pressées
    public boolean leftPressed, rightPressed, spacePressed, upPressed, downPressed;

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE) {
            spacePressed = true;
        }
        if (keycode == Input.Keys.LEFT) {
            leftPressed = true;
        }
        if (keycode == Input.Keys.RIGHT) {
            rightPressed = true;
        }
        if (keycode == Input.Keys.UP) {
            upPressed = true;
        }
        if (keycode == Input.Keys.DOWN) {
            downPressed = true;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.SPACE) {
            spacePressed = false;
        }
        if (keycode == Input.Keys.LEFT) {
            leftPressed = false;
        }
        if (keycode == Input.Keys.RIGHT) {
            rightPressed = false;
        }
        if (keycode == Input.Keys.UP) {
            upPressed = false;
        }
        if (keycode == Input.Keys.DOWN) {
            downPressed = false;
        }
        return true;
    }
}