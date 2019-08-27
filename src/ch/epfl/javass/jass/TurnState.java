package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * Represents the state of a turn.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public final class TurnState {
    private long packedScore;
    private long packedUnplayedCards;
    private int packedTrick;
    private boolean terminal;

    // Private constructor of TurnState.
    private TurnState(long packedScore, long packedUnplayedCards, int packedTrick, boolean terminal) {
        this.packedScore = packedScore;
        this.packedUnplayedCards = packedUnplayedCards;
        this.packedTrick = packedTrick;
        this.terminal = terminal;
    }

    /**
     * Constructs an instance of TurnState, according to a few parameters.
     *
     * @param trump the trump color as a Color object
     * @param score the current score in the turn as a Score object
     * @param firstPlayer the first player having played its card in the first trick, as a PlayerID
     * @return the corresponding TurnState object
     */
    public static TurnState initial(Color trump, Score score, PlayerId firstPlayer) {
        return new TurnState(score.packed(), PackedCardSet.ALL_CARDS, PackedTrick.firstEmpty(trump, firstPlayer), false);
    }

    /**
     * Constructs an instance of TurnState, according to a few packed parameters.
     *
     * @param pkScore the packed score of the turn
     * @param pkUnplayedCards the packed card set of all yet unplayed cards in the turn
     * @param pkTrick the current packed trick
     * @return the corresponding TurnState object
     * @throws IllegalArgumentException if one of the parameters are not possible
     */
    public static TurnState ofPackedComponents(long pkScore, long pkUnplayedCards, int pkTrick) {
        Preconditions.checkArgument(PackedScore.isValid(pkScore));
        Preconditions.checkArgument(PackedCardSet.isValid(pkUnplayedCards));
        Preconditions.checkArgument(PackedTrick.isValid(pkTrick));
        return new TurnState(pkScore, pkUnplayedCards, pkTrick, false);
    }

    /**
     * Getter for this turn's packed score.
     *
     * @return the packed version of this Turn's current score
     */
    public long packedScore() {
        return packedScore;
    }

    /**
     * Getter for this turn's packed set of yet unplayed cards.
     *
     * @return the packed version of the set of this Turn's yet unplayed cards
     */
    public long packedUnplayedCards() {
        return packedUnplayedCards;
    }

    /**
     * Getter for this turn's current packed trick.
     *
     * @return the packed version of this Turn's current trick
     */
    public int packedTrick() {
        return packedTrick;
    }

    /**
     * Getter for this turn's current score.
     *
     * @return the Score version of this Turn's current score
     */
    public Score score() {
        return Score.ofPacked(packedScore);
    }

    /**
     * Getter for this turn's yet unplayed cards.
     *
     * @return the CardSet version of this Turn's yet unplayed cards
     */
    public CardSet unplayedCards() {
        return CardSet.ofPacked(packedUnplayedCards);
    }

    /**
     * Getter for this turn's current trick.
     *
     * @return the Trick version of this Turn's current trick
     */
    public Trick trick() {
        return Trick.ofPacked(packedTrick);
    }

    /**
     * Returns whether this turn is finished.
     *
     * @return true only when the last trick has been played
     */
    public boolean isTerminal() {
        return terminal;
    }

    /**
     * Gives the identity of the player supposed to play the next card.
     *
     * @return the PlayerID of the next player
     * @throws IllegalStateException if the current trick is full
     */
    public PlayerId nextPlayer() {
        if (PackedTrick.isFull(packedTrick)) {
            throw new IllegalStateException();
        }
        int index = PackedTrick.size(packedTrick);
        return PackedTrick.player(packedTrick, index);
    }

    /**
     * Returns the state of the turn after a specific card is played.
     *
     * @param card the Card object representing the played card
     * @return the corresponding TurnState object
     */
    public TurnState withNewCardPlayed(Card card) {
        if (PackedTrick.isFull(packedTrick)) {
            throw new IllegalStateException();
        }
        long newPackedUnplayedCards = PackedCardSet.remove(packedUnplayedCards, card.packed());
        int newPackedTrick = PackedTrick.withAddedCard(packedTrick, card.packed());
        return new TurnState(packedScore, newPackedUnplayedCards, newPackedTrick, false);
    }

    /**
     * Returns the state of the turn after the current trick is collected.
     *
     * @return the corresponding TurnState object
     */
    public TurnState withTrickCollected() {
        if (!PackedTrick.isFull(packedTrick)) {
            throw new IllegalStateException();
        }
        PlayerId winningPlayer = PackedTrick.winningPlayer(packedTrick);
        TeamId winningTeam = winningPlayer.team();
        int trickPoints = PackedTrick.points(packedTrick);
        long newPackedScore = PackedScore.withAdditionalTrick(packedScore, winningTeam, trickPoints);
        int newPackedTrick = PackedTrick.nextEmpty(packedTrick);
        if (!PackedTrick.isValid(newPackedTrick)) {
            terminal = true;
        }
        return new TurnState(newPackedScore, packedUnplayedCards, newPackedTrick, terminal);
    }

    /**
     * Returns the state of the turn after a specific card is played, and the trick is collected.
     *
     * @param card the Card object representing the played card
     * @return the corresponding TurnState object
     */
    public TurnState withNewCardPlayedAndTrickCollected(Card card) {
        TurnState newTurnState = withNewCardPlayed(card);
        if (PackedTrick.isFull(newTurnState.packedTrick)) {
            return newTurnState.withTrickCollected();
        }
        return newTurnState;
    }
}
