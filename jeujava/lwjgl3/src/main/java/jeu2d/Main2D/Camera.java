package jeu2d.Main2D;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import Entity.Player;

public class Camera {
    private OrthographicCamera camera;

    public Camera(float viewportWidth, float viewportHeight) {
        this.camera = new OrthographicCamera(viewportWidth, viewportHeight);
        this.camera.position.set(viewportWidth / 2, viewportHeight / 2, 0);
        this.camera.update();
    }

    public void update(Player player, int mapWidth, int mapHeight) {
        Vector2 playerPosition = new Vector2(player.getX(), player.getY());
        camera.position.set(playerPosition.x, playerPosition.y, 0);
        camera.position.x = Math.max(camera.viewportWidth / 2, camera.position.x);
        camera.position.x = Math.min(mapWidth - camera.viewportWidth / 2, camera.position.x);
        camera.position.y = Math.max(camera.viewportHeight / 2, camera.position.y);
        camera.position.y = Math.min(mapHeight - camera.viewportHeight / 2, camera.position.y);
        camera.update();
    }

    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}