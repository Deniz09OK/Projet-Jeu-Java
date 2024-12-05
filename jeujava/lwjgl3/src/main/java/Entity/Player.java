package Entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import jeu2d.Main2D.GamePanel;
import jeu2d.Main2D.KeyHandler;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.objects.RectangleMapObject; // Correct import
import com.badlogic.gdx.maps.objects.PolygonMapObject; // Add import for PolygonMapObject
import com.badlogic.gdx.math.Intersector; // Add import for Intersector
import com.badlogic.gdx.math.Polygon; // Add import for Polygon
import com.badlogic.gdx.graphics.glutils.ShapeRenderer; // Add import for ShapeRenderer
import com.badlogic.gdx.graphics.Color; // Ensure Color is imported
import com.badlogic.gdx.graphics.OrthographicCamera; // Add import for OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer; // Add import for TiledMapTileLayer

public class Player extends Entity {

    GamePanel gp;
    KeyHandler keyH;
    private int x; // Position X du joueur
    private int y; // Position Y du joueur
    private int hp; // Points de vie du joueur
    private boolean i; // Rename x to i
    private boolean coffeeInvincible; // Add coffee invincibility variable
    private TextureRegion up1, down1, left1, right1;
    private static final int TILE_SIZE = 48; // Taille de la tuile
    private int mapWidth; // Add map width
    private int mapHeight; // Add map height
    private int speed; // Vitesse de déplacement du joueur
    private boolean isJumping; // Indique si le joueur est en train de sauter
    private boolean isFalling; // Indique si le joueur est en train de tomber
    private double gravity = 0.5; // Réduire la gravité
    private double velocityY = 0;
    private double jumpVelocity = 12.0; // Ajuster la force du saut
    private final double maxFallSpeed = 8.0; // Réduire la vitesse max de chute
    private int initialY; // Position initiale Y avant le saut
    private long spacePressedTime; // Add variable to track space bar press time
    private long lastDebugTime = 0; // Add variable to track last debug message time
    private TiledMap map; // Add reference to the map
    private ShapeRenderer shapeRenderer; // Add ShapeRenderer
    private boolean canJump = true; // Ajouter cette variable
    private long lastLogTime = 0; // Add variable to track last log time


    // Constantes pour le saut
    private static final double JUMP_VELOCITY = 20.0; // Decrease for a shorter jump
    private static final double JUMP_GRAVITY = 0.8;   // Decrease for a slower fall
    private static final double MAX_FALL_SPEED = 8.0; // Decrease the max fall speed

    // Constantes pour le déplacement
    private static final int MOVE_SPEED = 5; // Increase the movement speed

    public Player(GamePanel gp, KeyHandler keyH, int mapWidth, int mapHeight, TiledMap map) {
        this.gp = gp;
        this.keyH = keyH;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.map = map; // Initialize map reference
        this.shapeRenderer = new ShapeRenderer(); // Initialize ShapeRenderer
        setDefaultValues(); // Initialisation des valeurs par défaut
        getPlayerImage(); // Chargement des images du joueur
    }

    public void setDefaultValues() {
        this.x = 216; // Position X par défaut
        this.y = 410; // Position Y par défaut, moved even higher
        this.hp = 150; // Points de vie par défaut
        this.i = false; // Initialize invincibility
        this.coffeeInvincible = false; // Initialize coffee invincibility
        this.direction = "right"; // Initialize direction
        this.speed = 2; // Réduire la vitesse par défaut
        this.velocityY = 0;
        System.out.println("Default values set -> y: " + y);
    }

    public void getPlayerImage() {
        up1 = new TextureRegion(new Texture("assets/Player/devant.png"));
        down1 = new TextureRegion(new Texture("assets/Player/derriere.png"));
        left1 = new TextureRegion(new Texture("assets/Player/gauche.png"));
        right1 = new TextureRegion(new Texture("assets/Player/droite.png"));
    }

    public void update() {
        if (map == null) return;

        // Handle horizontal movement first
        moveHorizontal();

        // Handle jumping only when space is pressed
        if (keyH.spacePressed && !isJumping && canJump) {
            velocityY = JUMP_VELOCITY;
            isJumping = true;
            canJump = false;
        }

        // Apply vertical movement
        velocityY -= JUMP_GRAVITY;
        if (velocityY < -MAX_FALL_SPEED) {
            velocityY = -MAX_FALL_SPEED;
        }
        
        int newY = y + (int)velocityY;
        if (!checkCollisionAt(x, newY)) {
            y = newY;
        } else {
            handleCollision();
        }

        // Reset jump when landing
        if (checkCollisionAt(x, y - 1)) {
            isJumping = false;
            canJump = true;
            velocityY = 0;
        } else {
            isJumping = true; // Ensure the player keeps falling if not on a platform or collision
        }

        // Death condition
        if (velocityY < 0 && (x >= 0 && x <= 4452 && y <= 10)) {
            hp = 0;
            System.out.println("Game Over: Player fell out of bounds");
        }

        // Log coordinates every 5 seconds
        if (System.currentTimeMillis() - lastLogTime >= 5000) {
            System.out.println("Player coordinates: x=" + x + ", y=" + y);
            lastLogTime = System.currentTimeMillis();
        }
    }

    private boolean isGrounded() {
        // Check directly below player
        int footY = y - 2;
        // Check both left and right foot positions
        return (checkCollisionAt(x, footY) || 
                checkCollisionAt(x + TILE_SIZE - 1, footY) || 
                isTileUnderPlayer());
    }

    private boolean isBetweenCollisionIDs(int id1, int id2) {
        float[][] collisionCoords = {
            {0, 406}, {896, 160}, {1294, 978}, {954, 382},
            {1310, 156}, {2010, 316}, {2496, 320}, {3934, 194}
        };

        // Ensure id1 and id2 are within the bounds of the array
        id1 = Math.max(0, Math.min(id1, collisionCoords.length - 1));
        id2 = Math.max(0, Math.min(id2, collisionCoords.length - 1));

        for (int i = id1; i <= id2; i++) {
            if (x == collisionCoords[i][0] && y == collisionCoords[i][1]) {
                return true;
            }
        }
        return false;
    }

    private boolean checkCollision() {
        MapObjects objects = map.getLayers().get("colisions").getObjects();
        Rectangle playerRect = getBounds();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (playerRect.overlaps(rect)) {
                    return true;
                }
            } else if (object instanceof PolygonMapObject) {
                Polygon polygon = ((PolygonMapObject) object).getPolygon();
                if (Intersector.overlapConvexPolygons(polygon, new Polygon(new float[]{
                    playerRect.x, playerRect.y,
                    playerRect.x + playerRect.width, playerRect.y,
                    playerRect.x + playerRect.width, playerRect.y + playerRect.height,
                    playerRect.x, playerRect.y + playerRect.height
                }))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isTileUnderPlayer() {
        int tileX = x / TILE_SIZE;
        int tileY = (y - 1) / TILE_SIZE; // Vérifier juste en dessous du joueur
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("Calque de Tuiles 1");
        if (layer == null) return false;
        
        // Vérifier les trois points sous le joueur
        return layer.getCell(tileX, tileY) != null || 
               layer.getCell(tileX + 1, tileY) != null ||
               layer.getCell((int)(tileX + 0.5f), tileY) != null;
    }

    private boolean isGroundAhead(int nextX, int y) {
        MapObjects objects = map.getLayers().get("colisions").getObjects();
        Rectangle futurePos = new Rectangle(nextX, y - 1, TILE_SIZE, TILE_SIZE);
        
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (futurePos.overlaps(rect)) {
                    return true;
                }
            } else if (object instanceof PolygonMapObject) {
                Polygon polygon = ((PolygonMapObject) object).getPolygon();
                if (Intersector.overlapConvexPolygons(polygon, new Polygon(new float[]{
                    futurePos.x, futurePos.y,
                    futurePos.x + futurePos.width, futurePos.y,
                    futurePos.width, futurePos.y + futurePos.height,
                    futurePos.x, futurePos.y + futurePos.height
                }))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void moveHorizontal() {
        int newX = x; // Mémoriser la position actuelle

        if (keyH.leftPressed) {
            direction = "left";
            newX = x - MOVE_SPEED;
        } else if (keyH.rightPressed) { // Changé 'if' en 'else if' pour éviter le double mouvement
            direction = "right";
            newX = x + MOVE_SPEED;
        }

        // Vérifier les limites de la carte et les collisions
        if (newX >= 0 && newX <= mapWidth - TILE_SIZE) {
            if (map.getLayers().get("colisions") != null && !checkCollisionAt(newX, y)) {
                x = newX; // Appliquer le mouvement uniquement s'il n'y a pas de collision
            }
        }
    }

    private boolean checkCollisionAt(int newX, int newY) {
        if (map.getLayers().get("colisions") == null) {
            return false; // Return false if the colisions layer is null
        }
        Rectangle futurePos = new Rectangle(newX, newY, TILE_SIZE, TILE_SIZE);
        MapObjects objects = map.getLayers().get("colisions").getObjects();
        
        // Add side collision checks
        boolean hasCollision = false;
        
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (futurePos.overlaps(rect)) {
                    hasCollision = true;
                    break;
                }
            } else if (object instanceof PolygonMapObject) {
                Polygon polygon = ((PolygonMapObject) object).getPolygon();
                if (Intersector.overlapConvexPolygons(polygon, new Polygon(new float[]{
                    futurePos.x, futurePos.y,
                    futurePos.x + futurePos.width, futurePos.y,
                    futurePos.x + futurePos.width, futurePos.y + futurePos.height,
                    futurePos.x, futurePos.y + futurePos.height
                }))) {
                    hasCollision = true;
                    break;
                }
            }
        }
        
        return hasCollision;
    }

    private void handleCollision() {
        if (velocityY < 0) {
            // Arrêter la chute immédiatement et placer le joueur sur le sol
            while (checkCollisionAt(x, y)) {
                y++;
            }
            velocityY = 0;
            isJumping = false;
            canJump = true;
        } else if (velocityY > 0) {
            // Collision avec le plafond
            while (checkCollisionAt(x, y)) {
                y--;
            }
            velocityY = 0;
        }
        // Ensure player is not stuck in collision
        if (checkCollisionAt(x, y)) {
            System.out.println("Player stuck in collision at: x=" + x + ", y=" + y);
            // Move player out of collision
            while (checkCollisionAt(x, y)) {
                y++;
            }
            velocityY = 0;
            isJumping = false;
            canJump = true;
        }
    }

    public void draw(SpriteBatch batch) {
        TextureRegion image = null;
        switch (direction) {
            case "up":
                image = up1;
                break;
            case "down":
                image = down1;
                break;
            case "left":
                image = left1;
                break;
            case "right":
                image = right1;
                break;
        }
        batch.draw(image, x, y, TILE_SIZE, TILE_SIZE);
    }

    public int getHP() {
        return hp;
    }

    public void takeDamage(int damage) {
        if (!i && !coffeeInvincible) {
            hp -= damage;
        }
    }

    public void setI(boolean status) {
        i = status;
    }

    public boolean isI() {
        return i;
    }

    public void setCoffeeInvincible(boolean status) {
        coffeeInvincible = status;
    }

    public boolean isCoffeeInvincible() {
        return coffeeInvincible;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, TILE_SIZE, TILE_SIZE);
    }

    public int getY() {
        return y;
    }

    public void increaseHP(int amount) {
        hp += amount;
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return TILE_SIZE;
    }

    public int getHeight() {
        return TILE_SIZE;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setJumping(boolean isJumping) {
        this.isJumping = isJumping;
    }

    public boolean isFalling() {
        return isFalling;
    }

    public void setFalling(boolean isFalling) {
        this.isFalling = isFalling;
    }

    public void setMap(TiledMap map) {
        this.map = map;
    }

    public boolean isOnEndLevelCollision() {
        if (map.getLayers().get("colisions") == null) {
            return false; // Return false if the colisions layer is null
        }
        MapObjects objects = map.getLayers().get("colisions").getObjects();
        Rectangle playerRect = getBounds();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (playerRect.overlaps(rect) && object.getProperties().containsKey("endLevel")) {
                    return true;
                }
            } else if (object instanceof PolygonMapObject) {
                Polygon polygon = ((PolygonMapObject) object).getPolygon();
                if (Intersector.overlapConvexPolygons(polygon, new Polygon(new float[]{
                    playerRect.x, playerRect.y,
                    playerRect.x + playerRect.width, playerRect.y,
                    playerRect.x + playerRect.width, playerRect.y + playerRect.height,
                    playerRect.x, playerRect.y + playerRect.height
                })) && object.getProperties().containsKey("endLevel")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isOnVictoryCollision() {
        if (map == null) return false;
        
        MapObjects objects = map.getLayers().get("colisions") != null ? map.getLayers().get("colisions").getObjects() : null;
        if (objects == null) return false;

        Rectangle playerRect = getBounds();
        
        for (MapObject object : objects) {
            if (object.getName() != null && object.getName().equals("0")) {
                if (object instanceof PolygonMapObject) {
                    Polygon polygon = ((PolygonMapObject) object).getPolygon();
                    if (Intersector.overlapConvexPolygons(polygon, new Polygon(new float[]{
                        playerRect.x, playerRect.y,
                        playerRect.x + playerRect.width, playerRect.y,
                        playerRect.x + playerRect.width, playerRect.y + playerRect.height,
                        playerRect.x, playerRect.y + playerRect.height
                    }))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isOnRightmostCollision() {
        MapObjects objects = map.getLayers().get("colisions").getObjects();
        Rectangle playerRect = getBounds();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (playerRect.overlaps(rect) && rect.x + rect.width >= mapWidth - TILE_SIZE) {
                    return true;
                }
            } else if (object instanceof PolygonMapObject) {
                Polygon polygon = ((PolygonMapObject) object).getPolygon();
                if (Intersector.overlapConvexPolygons(polygon, new Polygon(new float[]{
                    playerRect.x, playerRect.y,
                    playerRect.x + playerRect.width, playerRect.y,
                    playerRect.x + playerRect.width, playerRect.y + playerRect.height,
                    playerRect.x, playerRect.y + playerRect.height
                })) && polygon.getBoundingRectangle().x + polygon.getBoundingRectangle().width >= mapWidth - TILE_SIZE) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isWithinEndCoordinates() {
        return x >= 4216 && x <= 4752 && y == 321;
    }

    public boolean isWithinFinalVictoryCoordinates() {
        return x >= 4751 && x <= 4752 && y >= 291 && y <= 436;
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    public void advanceToFinalMap() {
        this.hp = 150;
    }
}