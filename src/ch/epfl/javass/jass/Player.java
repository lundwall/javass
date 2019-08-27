package ch.epfl.javass.jass;

import ch.epfl.javass.jass.Card.Color;

import java.util.Map;

/**
 * Interface with methods for the different players.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public interface Player {
    /**
     * Gives the card that the player wants to play.
     *
     * @param state the current TurnState
     * @param hand the current CardSet of the player
     * @return the Card object representing the card to be played
     */
    Card cardToPlay(TurnState state, CardSet hand);

    /**
     * Chooses the trump color.
     *
     * @param hand the hand of the player
     * @param canPass whether it is possible for the player to pass
     * @return the color of trump
     */
    Color chooseTrump(CardSet hand, boolean canPass);

    /**
     * Informs the player of the identities and names of all the players.
     *
     * @param ownId the PlayerID of the player
     * @param playerNames a map linking the IDs of the players and their names
     */
    default void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {}

    /**
     * Informs the player of his new hand at every change.
     *
     * @param newHand the CardSet containing the new cards of the player
     */
    default void updateHand(CardSet newHand) {}

    /**
     * Informs the player of the current trump color.
     *
     * @param trump the Color of trump cards
     */
    default void setTrump(Color trump) {}

    /**
     * Changes a trick (when a new card played, or the trick is finished and replaced by new a new empty trick).
     *
     * @param newTrick the new trick, as a Trick object
     */
    default void updateTrick(Trick newTrick) {}

    /**
     * Changes a score (when a trick is collected).
     *
     * @param score the new score, represented by a Score object
     */
    default void updateScore(Score score) {}

    /**
     * Sets the winning team (which reached at least 1000 points).
     *
     * @param winningTeam the identity of the winning team
     */
    default void setWinningTeam(TeamId winningTeam) {}
}
