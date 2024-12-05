package Entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Entity {
    // Déclaration des variables de position
    public int X, Y; // Coordonnées X et Y de l'entité
    public int speed; // Vitesse de déplacement de l'entité
    // Déclaration des images pour les différentes directions
    public TextureRegion up1, up2; // Images pour la direction "haut"
    public TextureRegion down1, down2; // Images pour la direction "bas"
    public TextureRegion left1, left2; // Images pour la direction "gauche"
    public TextureRegion right1, right2; // Images pour la direction "droite"
    // Déclaration de la direction actuelle de l'entité
    public String direction; // Direction actuelle de l'entité (haut, bas, gauche, droite)
}
