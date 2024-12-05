package jeu2d.Main2D;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class Main {
    private static GamePanel gamePanel;

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("EpiQuest");
        config.setWindowedMode(1024, 768); // Changer la taille de la fenêtre
        config.setResizable(false);
        gamePanel = new GamePanel();
        new Lwjgl3Application(gamePanel, config); // Start with GamePanel
    }

    public static void checkLevelCompletion() {
        if (gamePanel.getPlayer().isWithinEndCoordinates() && !gamePanel.getMap().isFinalLevel()) {
            gamePanel.getPlayer().advanceToFinalMap();
            gamePanel.getMap().switchMap("assets/Carte/niveaufinal.tmx");
        }
    }
}