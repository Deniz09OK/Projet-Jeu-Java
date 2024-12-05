package Entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import jeu2d.Main2D.GamePanel;

public class Enemy extends Entity {

    GamePanel gp;
    private int x; // Position X de l'ennemi
    private int y; // Position Y de l'ennemi
    private int speed; // Vitesse de déplacement de l'ennemi
    private String direction; // Direction de l'ennemi
    private int hp; // Points de vie de l'ennemi
    private TextureRegion up1, down1, left1, right1;
    private static final int TILE_SIZE = 48; // Taille de la tuile
    private int startX, endX; // Ajouter les coordonnées de début et de fin X

    public Enemy(GamePanel gp, int startX, int startY, int endX) {
        this.gp = gp;
        this.x = startX;
        this.y = startY;
        this.startX = startX;
        this.endX = endX;
        this.speed = 3; // Augmenter la vitesse par défaut de l'ennemi
        this.direction = startX > endX ? "left" : "right"; // Définir la direction initiale en fonction des coordonnées de début et de fin
        this.hp = 50; // Points de vie par défaut de l'ennemi
        getEnemyImage(); // Chargement des images de l'ennemi
    }

    public void getEnemyImage() {
        up1 = new TextureRegion(new Texture("assets/Enemy/Hurt.png"));
        down1 = new TextureRegion(new Texture("assets/Enemy/Hurt.png"));
        left1 = new TextureRegion(new Texture("assets/Enemy/Hurt.png"));
        right1 = new TextureRegion(new Texture("assets/Enemy/Hurt.png"));
    }

    public void update() {
        // Logique de déplacement de l'ennemi
        if (direction.equals("left")) {
            x -= speed; // Déplacement vers la gauche
            if (x <= endX) {
                direction = "right"; // Changer de direction si le bord gauche est atteint
            }
        } else if (direction.equals("right")) {
            x += speed; // Déplacement vers la droite
            if (x >= startX) {
                direction = "left"; // Changer de direction si le bord droit est atteint
            }
        }
    }

    public void draw(SpriteBatch batch) {
        TextureRegion image = null;
        switch (direction) {
            case "up":
                image = up1; // Image pour la direction haut
                break;
            case "down":
                image = down1; // Image pour la direction bas
                break;
            case "left":
                image = left1; // Image pour la direction gauche
                break;
            case "right":
                image = right1; // Image pour la direction droite
                break;
        }
        batch.draw(image, x, y, TILE_SIZE, TILE_SIZE); // Dessiner l'image de l'ennemi
    }

    public void takeDamage(int damage) {
        hp -= damage; // Réduire les points de vie de l'ennemi
        if (hp <= 0) {
            // Gérer la mort de l'ennemi (par exemple, retirer du jeu, jouer une animation, etc.)
        }
    }

    public int getHP() {
        return hp; // Retourner les points de vie de l'ennemi
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, TILE_SIZE, TILE_SIZE); // Retourner les limites de l'ennemi
    }
    
    public int getY() {
        return y; // Retourner la position Y de l'ennemi
    }

    public int getX() {
        return x; // Retourner la position X de l'ennemi
    }
}