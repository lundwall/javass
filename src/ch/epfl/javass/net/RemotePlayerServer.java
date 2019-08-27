package ch.epfl.javass.net;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumMap;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Represents the server of a player, which waits for a connection and acts as instructed.
 *
 * @author Pablo Stebler (302328)
 * @author Marc Lundwall (297665)
 */
public final class RemotePlayerServer {
    
    Player player;

    /**
     * Constructor of the remote player server.
     *
     * @param player the local Player, whose behavior controls this remote server
     */
    public RemotePlayerServer(Player player) {
        this.player = player;
    }

    /**
     * Loop to listen to commands, update the game's state and answer requests.
     * Stops running when the game is over.
     */
    public void run() {
        try (ServerSocket ss = new ServerSocket(5108)) {
            Socket s = ss.accept();
            BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream(), US_ASCII));
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), US_ASCII));
            JassCommand command;

            do {
                String line = r.readLine();
                String[] args = StringSerializer.split(" ", line);
                command = JassCommand.valueOf(args[0]);

                // send the chosen trump
                if (command == JassCommand.CHTR) {
                    long packedHand = StringSerializer.deserializeLong(args[1]);
                    CardSet hand = CardSet.ofPacked(packedHand);
                    boolean canPass = StringSerializer.deserializeBoolean(args[2]);
                    Color trump = player.chooseTrump(hand, canPass);
                    int trumpIndex;
                    if (trump == null) {
                        trumpIndex = Color.COUNT;
                    } else {
                        trumpIndex = trump.ordinal();
                    }
                    w.write(StringSerializer.serializeInt(trumpIndex));
                    w.write('\n');
                    w.flush();
                }
                // sends the card chosen to be played
                else if (command == JassCommand.CARD) {
                    String[] stateArgs = StringSerializer.split(",", args[1]);
                    long score = StringSerializer.deserializeLong(stateArgs[0]);
                    long unplayedCards = StringSerializer.deserializeLong(stateArgs[1]);
                    int trick = StringSerializer.deserializeInt(stateArgs[2]);
                    TurnState state = TurnState.ofPackedComponents(score, unplayedCards, trick);
                    long packedHand = StringSerializer.deserializeLong(args[2]);
                    CardSet hand = CardSet.ofPacked(packedHand);
                    Card card = player.cardToPlay(state, hand);
                    int packedCard = card.packed();
                    w.write(StringSerializer.serializeInt(packedCard));
                    w.write('\n');
                    w.flush();
                }
                // gets information about the other players' names
                else if (command == JassCommand.PLRS) {
                    int ownIdIndex = StringSerializer.deserializeInt(args[1]);
                    PlayerId ownId = PlayerId.ALL.get(ownIdIndex);
                    String[] playerNamesRaw = StringSerializer.split(",", args[2]);
                    EnumMap<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
                    for (PlayerId playerId : PlayerId.values()) {
                        String playerName = StringSerializer.deserializeString(playerNamesRaw[playerId.ordinal()]);
                        playerNames.put(playerId, playerName);
                    }
                    player.setPlayers(ownId, playerNames);
                }
                // informs the player of his updated hand
                else if (command == JassCommand.HAND) {
                    long packedHand = StringSerializer.deserializeLong(args[1]);
                    CardSet hand = CardSet.ofPacked(packedHand);
                    player.updateHand(hand);
                }
                // informs the player of the trump color
                else if (command == JassCommand.TRMP) {
                    int trumpIndex = StringSerializer.deserializeInt(args[1]);
                    Color trump = Color.ALL.get(trumpIndex);
                    player.setTrump(trump);
                }
                // updates the state of the trick
                else if (command == JassCommand.TRCK) {
                    int packedTrick = StringSerializer.deserializeInt(args[1]);
                    Trick trick = Trick.ofPacked(packedTrick);
                    player.updateTrick(trick);
                }
                // updates the current scores
                else if (command == JassCommand.SCOR) {
                    long packedScore = StringSerializer.deserializeLong(args[1]);
                    Score score = Score.ofPacked(packedScore);
                    player.updateScore(score);
                }
                // gives the winning team when the game is over
                else if (command == JassCommand.WINR) {
                    int teamIndex = StringSerializer.deserializeInt(args[1]);
                    TeamId team = TeamId.ALL.get(teamIndex);
                    player.setWinningTeam(team);
                }
            } while (command != JassCommand.WINR); // end loop when game is over

        } catch (IOException e) {
            System.out.println("Erreur d'E/S.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur de décodage.");
        }
    }
    
}
