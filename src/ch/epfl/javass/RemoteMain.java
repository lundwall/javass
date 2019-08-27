package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class to launch a remote jass game.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public class RemoteMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        RemotePlayerServer server = new RemotePlayerServer(new GraphicalPlayerAdapter());
        Thread gameThread = new Thread(() -> server.run());
        gameThread.setDaemon(true);
        gameThread.start();
        System.out.println("La partie commencera à la connexion du client...");
    }
}