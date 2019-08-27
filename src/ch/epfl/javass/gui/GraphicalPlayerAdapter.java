package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import javafx.application.Platform;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Permet d'adapter l'interface graphique pour en faire un joueur.
 * 
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public final class GraphicalPlayerAdapter implements Player {
    TrumpBean trumpBean;
    ScoreBean scoreBean;
    TrickBean trickBean;
    HandBean handBean;
    GraphicalPlayer graphicalPlayer;
    ArrayBlockingQueue<Card> cardQueue = new ArrayBlockingQueue<Card>(1);
    ArrayBlockingQueue<Integer> trumpQueue = new ArrayBlockingQueue<Integer>(1);

    /**
     * Constructor of the Adapter, initializing the beans, takes no arguments.
     */
    public GraphicalPlayerAdapter() {
        this.trumpBean = new TrumpBean();
        this.scoreBean = new ScoreBean();
        this.trickBean = new TrickBean();
        this.handBean = new HandBean();
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Platform.runLater(() -> handBean.setPlayableCards(state.trick().playableCards(hand)));
        // takes the card that was placed in the queue when clicked
        try {
            Card card = cardQueue.take();
            Platform.runLater(() -> handBean.setPlayableCards(CardSet.EMPTY));
            return card;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Color chooseTrump(CardSet hand, boolean canPass) {
        Platform.runLater(() -> {
            trumpBean.setCanPass(canPass);
            trumpBean.setMustChooseTrump(true);
        });
        try {
            Color trump = null;
            int trumpIndex = trumpQueue.take();
            if (trumpIndex < Color.COUNT) {
                trump = Color.ALL.get(trumpIndex);
            }
            Platform.runLater(() -> { trumpBean.setMustChooseTrump(false); });
            return trump;
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        // creates the graphical player when everything is known about the other players
        graphicalPlayer = new GraphicalPlayer(ownId, playerNames, trumpBean, scoreBean, trickBean, handBean, trumpQueue, cardQueue);
        Platform.runLater(() -> graphicalPlayer.createStage().show());
    }

    @Override
    public void updateHand(CardSet newHand) {
        Platform.runLater(() -> handBean.setHand(newHand));
    }

    @Override
    public void setTrump(Color trump) {
        Platform.runLater(() -> trickBean.setTrump(trump));
    }

    @Override
    public void updateTrick(Trick newTrick) {
        Platform.runLater(() -> trickBean.setTrick(newTrick));
    }

    @Override
    public void updateScore(Score score) {
        Platform.runLater(() -> { 
            for (TeamId teamId : TeamId.ALL) {
                scoreBean.setTurnPoints(teamId, score.turnPoints(teamId));
                scoreBean.setGamePoints(teamId, score.gamePoints(teamId));
                scoreBean.setTotalPoints(teamId, score.totalPoints(teamId));
            }
        });
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        Platform.runLater(() -> scoreBean.setWinningTeam(winningTeam));
    }
}
