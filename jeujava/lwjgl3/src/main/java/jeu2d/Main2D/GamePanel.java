package jeu2d.Main2D;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx; // Ajouter cette instruction d'importation
import com.badlogic.gdx.graphics.Color; // Assurez-vous que Color est importé
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import Entity.Enemy;
import Entity.Player;
import Objet.Item;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Input; // Ajouter cette instruction d'importation
import com.badlogic.gdx.graphics.Texture; // Add import for Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Add import for SpriteBatch
import com.badlogic.gdx.InputAdapter; // Add import for InputAdapter

public class GamePanel extends ApplicationAdapter {
    private Camera camera; // Utiliser la nouvelle classe Camera
    private SpriteBatch batch;
    private Player player;
    private Enemy enemy1, enemy2;
    private Item item;
    private boolean bananaCollected = false;
    private boolean coffeeCollected = false;
    private long iEndTime = 0;
    private long coffeeInvincibilityEndTime = 0;
    private KeyHandler keyH;
    private Map map; // Ajouter la carte
    private BitmapFont font; // Ajouter BitmapFont
    private int mapWidth; // Ajouter mapWidth
    private int mapHeight; // Ajouter mapHeight
    private boolean showLevelPrompt = false; // Ajouter un drapeau pour afficher l'invite de niveau
    private boolean waitingForResponse = false; // Ajouter un drapeau pour attendre la réponse du joueur
    private Enemy enemy3, enemy4, enemy5, enemy6, enemy7; // Add more Yellow Simple Depop Profile Picture (4).png


    private boolean item1Collected = false, item2Collected = false, item3Collected = false, item4Collected = false; // Add more item flags
    private Texture initialImage; // Add a Texture for the initial image
    private boolean initialImageShown = false; // Add a flag to check if the initial image is shown
    private boolean gameStarted = false; // Add a flag to check if the game has started

    @Override
    public void create() {
        camera = new Camera(768, 576); // Initialiser la nouvelle classe Camera
        batch = new SpriteBatch();
        keyH = new KeyHandler();
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (!gameStarted && initialImageShown) {
                    gameStarted = true;
                    Gdx.input.setInputProcessor(keyH); // Set KeyHandler as the input processor after the game starts
                }
                return true;
            }
        });

        mapWidth = 150 * 32; // Largeur de la carte en pixels (150 tuiles * 32 pixels par tuile)
        mapHeight = 20 * 32; // Hauteur de la carte en pixels (20 tuiles * 32 pixels par tuile)
        
        player = new Player(this, keyH, mapWidth, mapHeight, null); // Initialiser le joueur avec les dimensions de la carte et une carte nulle
        map = new Map(player); // Initialiser la carte avec le joueur
        map.show(); // Afficher la carte
        player.setMap(map.getTiledMap()); // Définir la carte pour le joueur

        initializeEnemiesAndItems();

        font = new BitmapFont(); // Initialiser BitmapFont
        font.setColor(Color.WHITE); // Définir la couleur de la police

        initialImage = new Texture("assets/Image-jeu/Yellow Simple Depop Profile Picture (2).png"); // Load the initial image
        batch = new SpriteBatch(); // Initialize the SpriteBatch
    }

    private void initializeEnemiesAndItems() {
        // Vérifie si la carte est le niveau final
        if (map.isFinalLevel()) {
            // Ajouter plus d'ennemis sur la carte finale
            enemy1 = new Enemy(this, 800, 350, 400);
            enemy2 = new Enemy(this, 2000, 320, 1500);
            enemy3 = new Enemy(this, 3000, 350, 2200);
            enemy4 = new Enemy(this, 4000, 320, 3600);
            enemy5 = new Enemy(this, 5000, 320, 4600);
            enemy6 = new Enemy(this, 6000, 320, 5600);
            enemy7 = new Enemy(this, 7000, 320, 6600);
            item = new Item(48);
    
            // Moins d'objets sur la carte finale
            item1Collected = false;
            item2Collected = false;
            item3Collected = false;
            item4Collected = false;
            bananaCollected = false;
            coffeeCollected = false;
        } else {
            // Ajouter plus d'ennemis sur la carte initiale
            enemy1 = new Enemy(this, 800, 350, 400);
            enemy2 = new Enemy(this, 2000, 320, 1500);
            enemy3 = new Enemy(this, 3000, 350, 2200);
            enemy4 = new Enemy(this, 4000, 320, 3600);
            enemy5 = new Enemy(this, 5000, 320, 4600);
            enemy6 = new Enemy(this, 6000, 320, 5600);
            enemy7 = new Enemy(this, 7000, 320, 6600);
            item = new Item(48);
    
            // Moins d'objets sur la carte initiale
            item1Collected = false;
            item2Collected = false;
            item3Collected = false;
            item4Collected = false;
            bananaCollected = false;
            coffeeCollected = false;
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.BLACK); // Clear the screen with black color

        if (!gameStarted) {
            batch.begin();
            batch.draw(initialImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Draw the initial image
            batch.end();
            initialImageShown = true;
            return;
        }

        camera.update(player, mapWidth, mapHeight); // Passer les dimensions de la carte à la mise à jour de la caméra
        batch.setProjectionMatrix(camera.getCamera().combined);

        map.render(Gdx.graphics.getDeltaTime()); // Rendre la carte

        batch.begin();
        player.draw(batch);
        if (enemy1 != null) enemy1.draw(batch);
        if (enemy2 != null) enemy2.draw(batch);
        if (enemy3 != null) enemy3.draw(batch); // Draw more enemies
        if (enemy4 != null) enemy4.draw(batch);
        if (enemy5 != null) enemy5.draw(batch);
        if (enemy6 != null) enemy6.draw(batch);
        if (enemy7 != null) enemy7.draw(batch);

        // Nouveau positionnement des items
        if (!bananaCollected) item.drawBanana(batch, 600, 450);
        if (!coffeeCollected) item.drawCoffee(batch, 2500, 450);  // Un seul café placé stratégiquement
        batch.end();

        // Supprimer l'appel à drawCollisionPolygons
        // player.drawCollisionPolygons(); // Dessiner les polygones de collision

        batch.begin();
        drawHUD(batch);
        drawAttributes(batch); // Dessiner les attributs
        if (showLevelPrompt) {
            drawLevelPrompt(batch); // Dessiner l'invite de niveau
        }
        batch.end();

        if (!waitingForResponse) {
            update(); // S'assurer que la mise à jour est appelée dans render
        }
    }

    private void resetItems() {
        bananaCollected = false;
        coffeeCollected = false;
        item1Collected = false;
        item2Collected = false;
        item3Collected = false;
        item4Collected = false;
    }

    public void update() {
        player.update();
        
        // Vérifier si le joueur est dans les coordonnées de fin
        if (player.isWithinEndCoordinates()) {
            if (!map.isFinalLevel()) {
                map.switchMap("assets/Carte/niveaufinal.tmx"); // Switch to the final map
                player.setX(216); // Définir la position du joueur aux coordonnées de départ de la carte finale
                player.setY(410);
                player.setMap(map.getTiledMap()); // S'assurer que le joueur a la nouvelle carte
                map.setFinalLevel(true); // Définir le drapeau pour indiquer le niveau final
                resetItems(); // Ajouter l'appel à resetItems() ici
                player.advanceToFinalMap(); // Reset HP when advancing to final map
                player.setCoffeeInvincible(false); // Reset coffee invincibility
                initializeEnemiesAndItems(); // Initialize enemies and items for the final map
            } else {
                // Afficher le message de victoire et terminer le jeu
                System.out.println("Final Victory!");
                Gdx.app.exit();
            }
        }

        // Vérifier la condition de victoire
        if (player.isOnVictoryCollision()) {
            if (!map.isFinalLevel()) {
                map.switchMap("assets/Carte/niveaufinal.tmx"); // Switch to the final map
                player.setX(216); // Définir la position du joueur aux coordonnées de départ de la carte finale
                player.setY(410);
                player.setMap(map.getTiledMap()); // S'assurer que le joueur a la nouvelle carte
                map.setFinalLevel(true); // Définir le drapeau pour indiquer le niveau final
                resetItems(); // Ajouter l'appel à resetItems() ici
                player.advanceToFinalMap(); // Reset HP when advancing to final map
                player.setCoffeeInvincible(false); // Reset coffee invincibility
                initializeEnemiesAndItems(); // Initialize enemies and items for the final map
            }
        }

        // Vérifier la condition de victoire finale
        if (map.isFinalLevel() && player.isWithinFinalVictoryCoordinates()) {
            System.out.println("Final Victory!");
            Gdx.app.exit(); // Quitter le jeu
        }

        if (enemy1 != null) enemy1.update();
        if (enemy2 != null) enemy2.update();
        if (enemy3 != null) enemy3.update(); // Update more enemies
        if (enemy4 != null) enemy4.update();
        if (enemy5 != null) enemy5.update();
        if (enemy6 != null) enemy6.update();
        if (enemy7 != null) enemy7.update();
        checkCollisions();
        checkItemCollisions();
        
        if (player.getHP() <= 0) {
            gameOver();
        }
        
        if (System.currentTimeMillis() > iEndTime) {
            player.setI(false);
        }
        if (System.currentTimeMillis() > coffeeInvincibilityEndTime) {
            player.setCoffeeInvincible(false);
        }

        // Gérer la réponse du joueur à l'invite de niveau
        if (showLevelPrompt && waitingForResponse) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
                map.switchMap("assets/Carte/niveaufinal.tmx"); // Switch to the final map
                player.setX(216); // Définir la position du joueur aux coordonnées de départ de la carte finale
                player.setY(410);
                player.setMap(map.getTiledMap()); // S'assurer que le joueur a la nouvelle carte
                map.setFinalLevel(true); // Définir le drapeau pour indiquer le niveau final
                map.addCollisionObjects(map.getFinalMapCollisions()); // Ajouter des objets de collision de la carte finale
                showLevelPrompt = false; // Masquer l'invite de niveau
                waitingForResponse = false; // Arrêter d'attendre la réponse
                player.setCoffeeInvincible(false); // Reset coffee invincibility
                initializeEnemiesAndItems(); // Initialize enemies and items for the final map
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
                showLevelPrompt = false; // Masquer l'invite de niveau
                waitingForResponse = false; // Arrêter d'attendre la réponse
            }
        }
    }

    private void drawLevelPrompt(SpriteBatch batch) {
        font.draw(batch, "Voulez-vous passer au niveau 2 ? (Y/N)", camera.getCamera().position.x - 100, camera.getCamera().position.y);
    }

    private void checkVictory(SpriteBatch batch) {
        // Vérifier si le joueur est sur la collision de fin de niveau
        if (player.isOnEndLevelCollision()) {
            if (map.isFinalLevel()) {
                font.draw(batch, "Victoire!", camera.getCamera().position.x - 50, camera.getCamera().position.y);
                Gdx.app.exit(); // Quitter le jeu après avoir affiché le message de victoire
            } else {
                showLevelPrompt = true; // Afficher l'invite de niveau
                waitingForResponse = true; // Attendre la réponse du joueur
            }
        }
    }

    private void constrainPlayerToMap() {
        // S'assurer que le joueur reste dans les limites de la carte
        if (player.getX() < 0) player.setX(0);
        if (player.getY() < 0) player.setY(0);
        if (player.getX() > mapWidth - player.getWidth()) player.setX(mapWidth - player.getWidth());
        if (player.getY() > mapHeight - player.getHeight()) player.setY(mapHeight - player.getHeight());
    }

    public void checkCollisions() {
        if (!player.isI() && !player.isCoffeeInvincible()) {
            Enemy[] enemies = {enemy1, enemy2, enemy3, enemy4, enemy5, enemy6, enemy7};
            for (Enemy enemy : enemies) {
                if (enemy != null && player.getBounds().overlaps(enemy.getBounds())) {
                    if (player.isJumping() && player.getY() > enemy.getY()) {
                        // Le joueur saute sur l'ennemi
                        enemy.takeDamage(100);
                        if (enemy.getHP() <= 0) {
                            if (enemy == enemy1) enemy1 = null;
                            if (enemy == enemy2) enemy2 = null;
                            if (enemy == enemy3) enemy3 = null;
                            if (enemy == enemy4) enemy4 = null;
                            if (enemy == enemy5) enemy5 = null;
                            if (enemy == enemy6) enemy6 = null;
                            if (enemy == enemy7) enemy7 = null;
                        }
                    } else {
                        // L'ennemi touche le joueur
                        player.takeDamage(50);
                        player.setI(true);
                        iEndTime = System.currentTimeMillis() + 1000; // 1 seconde d'invincibilité
                        System.out.println("Player hit! HP: " + player.getHP()); // Debug message
                    }
                }
            }
        }
    }

    public void checkItemCollisions() {
        // Ajuster les coordonnées de collision pour correspondre aux nouvelles positions
        if (!bananaCollected && player.getBounds().overlaps(new Rectangle(600, 450, 48, 48))) {
            player.increaseHP(50);
            bananaCollected = true;
        }
        if (!coffeeCollected && player.getBounds().overlaps(new Rectangle(2500, 450, 48, 48))) {
            coffeeCollected = true;
            player.setCoffeeInvincible(true);
            coffeeInvincibilityEndTime = System.currentTimeMillis() + 10000;
        }
    }

    public void drawHUD(SpriteBatch batch) {
        // Cette méthode peut être vide maintenant car nous avons déplacé l'affichage dans drawAttributes
    }

    public void drawAttributes(SpriteBatch batch) {
        // Dessiner les HP du joueur en haut à gauche
        font.draw(batch, 
            "Player HP: " + player.getHP() + "/150", 
            camera.getCamera().position.x - 384, 
            camera.getCamera().position.y + 246
        );

        // Dessiner le statut d'invincibilité du café s'il est actif
        if (player.isCoffeeInvincible()) {
            long remainingTime = (coffeeInvincibilityEndTime - System.currentTimeMillis()) / 1000;
            font.draw(batch, 
                "Coffee Power: ACTIVE (" + remainingTime + "s)", 
                camera.getCamera().position.x - 384, 
                camera.getCamera().position.y + 226
            );
        }
    }

    public void gameOver() {
        // Logique de fin de jeu
        System.out.println("Game Over");
        Gdx.app.exit();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public Camera getCamera() {
        return camera;
    }

    public Player getPlayer() {
        return player;
    }

    public Map getMap() {
        return map;
    }
}