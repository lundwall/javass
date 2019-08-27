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
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Represents the client of a player, informing a remote server of the state of the game,
 * and listening to the card it chooses to play.
 *
 * @author Pablo Stebler (302328)
 * @author Marc Lundwall (297665)
 */
public final class RemotePlayerClient implements Player, AutoCloseable {
    private Socket s;
    private BufferedReader r;
    private BufferedWriter w;

    /**
     * Constructor for the remote player client
     * @param hostname the IP address of the remote player server
     * @throws IOException to give a customized error message to the end user
     */
    public RemotePlayerClient(String hostname) throws IOException {
        s = new Socket(hostname, 5108);
        r = new BufferedReader(new InputStreamReader(s.getInputStream(), US_ASCII));
        w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), US_ASCII));
    }

    @Override
    public Color chooseTrump(CardSet hand, boolean canPass) {
        String CHTR = JassCommand.CHTR.name();
        Color trump = null;
        try {
            w.write(StringSerializer.join(" ", CHTR, StringSerializer.serializeLong(hand.packed()), StringSerializer.serializeBoolean(canPass)));
            w.write('\n');
            w.flush();
            int trumpIndex = StringSerializer.deserializeInt(r.readLine());
            if (trumpIndex < Color.COUNT) {
                trump = Card.Color.ALL.get(trumpIndex);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trump;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        String CARD = JassCommand.CARD.name();
        String serializedScore = StringSerializer.serializeLong(state.packedScore());
        String serializedUnplayedCards = StringSerializer.serializeLong(state.packedUnplayedCards());
        String serializedTrick = StringSerializer.serializeInt(state.packedTrick());
        try {
            w.write(StringSerializer.join(" ",
                    CARD,
                    StringSerializer.join(",",
                            serializedScore,
                            serializedUnplayedCards,
                            serializedTrick),
                    StringSerializer.serializeLong(hand.packed())));
            w.write('\n');
            w.flush();
            int pkCard = StringSerializer.deserializeInt(r.readLine());
            return Card.ofPacked(pkCard);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        String PLRS = JassCommand.PLRS.name();
        String[] serializedPlayers = new String[4];
        for(PlayerId player : PlayerId.values()) {
            serializedPlayers[player.ordinal()] = StringSerializer.serializeString(playerNames.get(player));
        }
        String serializedIDordinal = StringSerializer.serializeInt(ownId.ordinal());
        try {
            w.write(StringSerializer.join(" ",
                    PLRS,
                    serializedIDordinal,
                    StringSerializer.join(",",
                            serializedPlayers[0],
                            serializedPlayers[1],
                            serializedPlayers[2],
                            serializedPlayers[3])));
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void updateHand(CardSet newHand) {
        String HAND = JassCommand.HAND.name();
        String serializedHand = StringSerializer.serializeLong(newHand.packed());
        try {
            w.write(StringSerializer.join(" ",
                    HAND,
                    serializedHand));
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void setTrump(Card.Color trump) {
        String TRMP = JassCommand.TRMP.name();
        String serializedTrumpOrdinal = StringSerializer.serializeInt(trump.ordinal());
        try {
            w.write(StringSerializer.join(" ",
                    TRMP,
                    serializedTrumpOrdinal));
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void updateTrick(Trick newTrick) {
        String TRCK = JassCommand.TRCK.name();
        String serializedTrick = StringSerializer.serializeInt(newTrick.packed());
        try {
            w.write(StringSerializer.join(" ",
                    TRCK,
                    serializedTrick));
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void updateScore(Score score) {
        String SCOR = JassCommand.SCOR.name();
        String serializedScore = StringSerializer.serializeLong(score.packed());
        try {
            w.write(StringSerializer.join(" ",
                    SCOR,
                    serializedScore));
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        String WINR = JassCommand.WINR.name();
        String serializedWinner = StringSerializer.serializeInt(winningTeam.ordinal());
        try {
            w.write(StringSerializer.join(" ",
                    WINR,
                    serializedWinner));
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() throws Exception {
        w.close();
        r.close();
        s.close();
    }
}
