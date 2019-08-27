package ch.epfl.javass.jass;

import ch.epfl.javass.jass.Card.Color;

import java.util.Map;

/**
 * Makes sure that a player takes a minimum amount of time before playing.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public final class PacedPlayer implements Player {
    private Player underlyingPlayer;
    private long minTime;

    /**
     * Constructor of PacedPlayer.
     *
     * @param underlyingPlayer the actual player
     * @param minTime the minimum time before the player can play
     */
    public PacedPlayer(Player underlyingPlayer, double minTime) {
        this.underlyingPlayer = underlyingPlayer;
        this.minTime = (long) (minTime * 1000.0);
    }

    @Override
    public Color chooseTrump(CardSet hand, boolean canPass) {
        return underlyingPlayer.chooseTrump(hand, canPass);
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        // Same as method from interface, BUT with a time constraint.
        long startTime = System.currentTimeMillis();
        Card card = underlyingPlayer.cardToPlay(state, hand);
        long endTime = System.currentTimeMillis();
        long elapsed = endTime - startTime;
        if (elapsed < minTime) {
            try {
                Thread.sleep(minTime - elapsed);
            } catch (InterruptedException e) {
                // ignore
            }
        }
        return card;
    }
    
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        underlyingPlayer.setPlayers(ownId, playerNames);
    }
    
    @Override
    public void updateHand(CardSet newHand) {
        underlyingPlayer.updateHand(newHand);
    }
    
    @Override
    public void setTrump(Color trump) {
        underlyingPlayer.setTrump(trump);
    }
    
    @Override
    public void updateTrick(Trick newTrick) {
        underlyingPlayer.updateTrick(newTrick);
    }
    
    @Override
    public void updateScore(Score score) {
        underlyingPlayer.updateScore(score);
    }
    
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        underlyingPlayer.setWinningTeam(winningTeam);
    }
}
