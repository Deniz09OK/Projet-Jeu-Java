package Objet;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Item {
    private Texture image1;
    private Texture image2; // Ajout de l'image pour le café
    private int tileSize;

    public Item(int tileSize) {
        this.tileSize = tileSize;
        image1 = new Texture("assets/Item/potion1.png");
        image2 = new Texture("assets/Item/potion2.png");
    }

    // Méthode pour dessiner la banane
    public void drawBanana(SpriteBatch batch, int x, int y) {
        batch.draw(image1, x, y, tileSize, tileSize);
    }

    // Méthode pour dessiner le café
    public void drawCoffee(SpriteBatch batch, int x, int y) {
        batch.draw(image2, x, y, tileSize, tileSize);
    }
}
