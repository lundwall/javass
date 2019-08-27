package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Main class to launch a local jass game.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public class LocalMain extends Application {

    List<String> defaultNames = Arrays.asList("Aline", "Bastien", "Colette", "David");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> params = getParameters().getRaw();
        Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);
        Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);

        // setting random seeds
        Random rnd = new Random();
        if (params.size() == 5) {
            try {
                rnd = new Random(Long.parseLong(params.get(4)));
            } catch (IllegalArgumentException e) {
                errorSeed(params.get(4));
            }
        } else if (params.size() != 4) {
            errorNumberOfArgs();
        }
        long jassGameSeed = rnd.nextLong();
        long[] playerSeeds = new long[4];
        for (int i = 0; i < 4; i++) {
            playerSeeds[i] = rnd.nextLong();
        }

        for (int i = 0; i < 4; i++) {
            // player names
            String name = "";
            if (params.get(i).length() == 1 || (!(params.get(i).charAt(0) == 'h')
                    && params.get(i).length() > 2 && params.get(i).charAt(2) == ':')) {
                name = defaultNames.get(i);
            } else {
                if (params.get(i).charAt(0) == 'h' && params.get(i).length() > 2
                        && params.get(i).contains(":")) {
                    name = params.get(i).substring(2);
                } else if (params.get(i).length() > 2 && params.get(i).contains(":")) {
                    name = params.get(i).substring(2, params.get(i).lastIndexOf(':'));
                }
            }
            if (name.isEmpty()) {
                errorInvalidName(params.get(i));
            }
            playerNames.put(PlayerId.ALL.get(i), name);
            // create simulated MCTS player
            if (params.get(i).charAt(0) == 's') {
                int iterations = 10_000;
                if (!(params.get(i).charAt(params.get(i).length()-1) == ':'
                        || params.get(i).length() == 1)) {
                    try {
                        iterations = Integer.parseInt(params.get(i)
                                .substring(params.get(i).lastIndexOf(':') + 1));
                    } catch(NumberFormatException e) {
                        errorIterations(params.get(i));
                    }
                }
                try {
                    MctsPlayer mctsPlayer = new MctsPlayer(PlayerId.ALL.get(i), playerSeeds[i],
                                                           iterations);
                    players.put(PlayerId.ALL.get(i), new PacedPlayer(mctsPlayer, 2));
                } catch (IllegalArgumentException e) {
                    errorIterations(params.get(i));
                }
            }
            // create human player with graphical interface
            else if(params.get(i).charAt(0) == 'h') {
                if(params.get(i).contains(":") && params.get(i).replaceFirst(":", "").contains(":")) {
                    errorTooManyComponents(params.get(i));
                }
                players.put(PlayerId.ALL.get(i), new GraphicalPlayerAdapter());
            }
            // create remote player
            else if(params.get(i).charAt(0) == 'r') {
                String IPAddress;
                if(params.get(i).length() == 1
                        || params.get(i).charAt(params.get(i).length() - 1) == ':') {
                    IPAddress = "localhost";
                } else {
                    IPAddress = params.get(i).substring(params.get(i).lastIndexOf(':') + 1);
                }
                try {
                    players.put(PlayerId.ALL.get(i), new RemotePlayerClient(IPAddress));
                } catch(IOException e) {
                    errorConnection(params.get(i));
                }
            }
            // lance une erreur
            else {
                errorFirstCharacter(params.get(i));
            }
        }

        Thread gameThread = new Thread(() -> {
            JassGame game = new JassGame(jassGameSeed, players, playerNames);
            while (!game.isGameOver()) {
                game.advanceToEndOfNextTrick();
                try { Thread.sleep(1000); } catch (Exception e) {}
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }

    private void errorInvalidName(String badWord) {
        System.err.println("Erreur : spécification de joueur invalide : " + badWord);
        System.exit(1);
    }

    private void errorNumberOfArgs() {
        System.err.println("Utilisation: java ch.epfl.javass.LocalMain <j1>…<j4> [<graine>]");
        System.err.println("où :");
        System.err.println("<jn> spécifie le joueur n, ainsi:");
        System.err.println("  h:<nom>  un joueur humain nommé <nom>");
        System.err.println("  h un joueur humain");
        System.err.println("  r:<nom>:<adresseIP> un joueur distant nommé <nom> et ayant comme adresse IP <adresseIP>");
        System.err.println("  r:<nom> un joueur distant nommé <nom> en local");
        System.err.println("  r::<adresseIP> un joueur distant ayant comme adresse IP <adresseIP>");
        System.err.println("  r un joueur distant en local");
        System.err.println("  s:<nom>:<iterations> un joueur simulé nommé <nom> et jouant <iterations> parties aléatoires par coup");
        System.err.println("  s:<nom> un joueur simulé nommé <nom> et jouant 10 000 parties aléatoires par coup");
        System.err.println("  s::<iterations> un joueur simulé jouant <iterations> parties aléatoires par coup");
        System.err.println("  s un joueur simulé jouant 10 000 parties aléatoires par coup");
        System.exit(1);
    }

    private void errorFirstCharacter(String badWord) {
        System.err.println("Erreur : spécification de joueur invalide");
        System.err.println("La première composante de " + badWord + " doit être h, r ou s");
        System.exit(1);
    }

    private void errorTooManyComponents(String badWord) {
        System.err.println("Erreur : spécification de joueur invalide");
        System.err.println("Trop de paramètres dans " + badWord);
        System.exit(1);
    }

    private void errorConnection(String badWord) {
        System.err.println("Erreur : la connexion au joueur a échoué");
        System.err.println("Relancez le programme du joueur suivant en vérifiant son adresse IP : " + badWord);
        System.exit(1);
    }

    private void errorSeed(String badWord) {
        System.err.println("Erreur : graine invalide");
        System.err.println("La graine dans " + badWord + " doit être un entier positif");
        System.exit(1);
    }

    private void errorIterations(String badWord) {
        System.err.println("Erreur : Nombre d'iterations invalide");
        System.err.println("Le nombre d'iterations dans " + badWord + " doit être un entier positif supérieur ou égal à 10");
        System.exit(1);
    }
}
