package jeu2d.Main2D;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Gdx;
import Entity.Player;

public class Map implements Screen {
    private Camera camera;
    private SpriteBatch batch;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private Player player;
    private boolean isFinalLevel = false; // Add a flag to check if it's the final level
    private TiledMap finalMap; // Add reference to the final map
    private TiledMap initialMap; // Add reference to the initial map
    private MapObjects initialMapCollisions; // Add reference to initial map collisions
    private MapObjects finalMapCollisions; // Add reference to final map collisions
    private Texture backgroundTextureMorgane; // Add background texture for Morgane
    private Texture backgroundTextureMarvin; // Add background texture for Marvin
    private Music musicMorgane; // Add music for Morgane's map
    private Music musicMarvin; // Add music for Marvin's map

    public Map(Player player) {
        this.player = player;
        loadMaps(); // Load maps and collision objects during initialization
    }

    private void loadMaps() {
        // Load the initial map
        initialMap = new TmxMapLoader().load("assets/Carte/niveaumorgane.tmx");
        // Load the final map
        finalMap = new TmxMapLoader().load("assets/Carte/niveaufinal.tmx");

        // Load collision objects for both maps
        if (initialMap.getLayers().get("colisions") != null) {
            initialMapCollisions = initialMap.getLayers().get("colisions").getObjects();
        }
        if (finalMap.getLayers().get("colision marvin") != null) {
            finalMapCollisions = finalMap.getLayers().get("colision marvin").getObjects();
        }

        // Load background textures
        backgroundTextureMorgane = new Texture("assets/Carte/Conception 5 (2) (1).png"); // Ensure this path is correct
        backgroundTextureMarvin = new Texture("assets/Carte/Designer (3) (1).jpeg"); // Ensure this path is correct

        // Load background music with correct paths
        musicMorgane = Gdx.audio.newMusic(Gdx.files.internal("assets/Musique/Daft Punk - Veridis Quo (Official Video).mp3"));
        musicMarvin = Gdx.audio.newMusic(Gdx.files.internal("assets/Musique/Daft Punk - Robot Rock (Official Video).mp3"));

        // Set the initial map as the current map
        tiledMap = initialMap;
    }

    @Override
    public void show() {
        camera = new Camera(768, 576); // Use the new Camera class
        batch = new SpriteBatch();

        // Charge la carte à partir du dossier assets
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        playMusic(); // Play the appropriate music when the map is shown
    }

    @Override
    public void render(float delta) {
        camera.update(player, 150 * 32, 20 * 32); // Pass map dimensions to camera update
        tiledMapRenderer.setView(camera.getCamera());
        
        batch.setProjectionMatrix(camera.getCamera().combined);
        batch.begin();

        // Draw the repeating background texture
        if (isFinalLevel) {
            drawBackground(batch, backgroundTextureMarvin);
        } else {
            drawBackground(batch, backgroundTextureMorgane);
        }

        batch.end();

        // Render the tiled map layers on top of the background
        tiledMapRenderer.render();

        batch.begin();
        // Dessinez vos éléments de jeu ici
        batch.end();
    }

    private void drawBackground(SpriteBatch batch, Texture backgroundTexture) {
        int mapWidth = 150 * 32; // Map width in pixels
        int mapHeight = 20 * 32; // Map height in pixels
        for (int x = 0; x < mapWidth; x += backgroundTexture.getWidth()) {
            for (int y = 0; y < mapHeight; y += backgroundTexture.getHeight()) {
                batch.draw(backgroundTexture, x, y);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        tiledMap.dispose();
        tiledMapRenderer.dispose();
        initialMap.dispose(); // Dispose of the initial map
        finalMap.dispose(); // Dispose of the final map
        backgroundTextureMorgane.dispose(); // Dispose of the background texture for Morgane
        backgroundTextureMarvin.dispose(); // Dispose of the background texture for Marvin
        musicMorgane.dispose();
        musicMarvin.dispose();
        // Make sure to dispose of music resources
        if (musicMorgane != null) musicMorgane.dispose();
        if (musicMarvin != null) musicMarvin.dispose();
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public void switchMap(String mapPath) {
        if (mapPath.equals("assets/Carte/niveaufinal.tmx")) {
            tiledMap = finalMap;
            // Vérifiez si la couche de collisions existe
            if (finalMap.getLayers().get("colisions") == null) {
                System.out.println("Warning: Loading collision layer from 'colision marvin'");
                // Utiliser la couche "colision marvin" si "colisions" n'existe pas
                addCollisionObjects(finalMapCollisions);
            }
            isFinalLevel = true;
        } else {
            tiledMap = initialMap;
            addCollisionObjects(initialMapCollisions);
            isFinalLevel = false;
        }
        tiledMapRenderer.setMap(tiledMap);
        player.setMap(tiledMap);
        playMusic(); // Play the appropriate music when switching maps
    }

    public void addCollisionObjects(MapObjects collisionObjects) {
        if (collisionObjects != null) {
            if (tiledMap.getLayers().get("colisions") != null) {
                MapObjects targetObjects = tiledMap.getLayers().get("colisions").getObjects();
                // Remove all existing objects
                while (targetObjects.getCount() > 0) {
                    targetObjects.remove(targetObjects.get(0));
                }
                // Add new collision objects
                copyPolygons(collisionObjects, targetObjects);
            }
        }
    }

    private void copyPolygons(MapObjects sourceObjects, MapObjects targetObjects) {
        for (MapObject object : sourceObjects) {
            if (object instanceof PolygonMapObject) {
                PolygonMapObject polygonObject = (PolygonMapObject) object;
                PolygonMapObject newPolygonObject = new PolygonMapObject(polygonObject.getPolygon());
                newPolygonObject.setName(polygonObject.getName());
                targetObjects.add(newPolygonObject);
            }
        }
    }

    private void playMusic() {
        if (isFinalLevel) {
            musicMorgane.stop();
            musicMarvin.play();
            musicMarvin.setLooping(true);
            musicMarvin.setVolume(1.0f); // Volume à 100% (valeur entre 0.0f et 1.0f)
        } else {
            musicMarvin.stop();
            musicMorgane.play();
            musicMorgane.setLooping(true);
            musicMorgane.setVolume(1.0f); // Volume à 100% (valeur entre 0.0f et 1.0f)
        }
    }

    public boolean isFinalLevel() {
        return isFinalLevel;
    }

    public void setFinalLevel(boolean isFinalLevel) {
        this.isFinalLevel = isFinalLevel;
    }

    public MapObjects getFinalMapCollisions() {
        return finalMapCollisions;
    }
}