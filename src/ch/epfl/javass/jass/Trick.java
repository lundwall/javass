package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;

/**
 * Represents the current trick of a jass game.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public final class Trick {
    private final int packed;

    public final static Trick INVALID = new Trick(PackedTrick.INVALID);

    private Trick(int packed) {
        this.packed = packed;
    }

    /**
     * Creates a new trick from its packed version.
     *
     * @param packed the packed version
     * @return the corresponding trick
     */
    public static Trick ofPacked(int packed) {
        Preconditions.checkArgument(PackedTrick.isValid(packed));
        return new Trick(packed);
    }

    /**
     * Returns the packed version of the trick.
     *
     * @return the packed version of the trick
     */
    public int packed() {
        return packed;
    }

    /**
     * Returns the first trick of a turn with the specified parameters.
     *
     * @param trump the trump
     * @param firstPlayer the first player of the trick
     * @return the trick
     */
    public static Trick firstEmpty(Card.Color trump, PlayerId firstPlayer) {
        return ofPacked(PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * Returns the emptied next trick.
     *
     * @return the packed version of the next trick, or INVALID if the current trick is the last of the turn
     */
    public Trick nextEmpty() {
        if(!isFull()) {
            throw new IllegalStateException();
        }
        int nextEmpty = PackedTrick.nextEmpty(packed);
        if (nextEmpty == PackedTrick.INVALID) {
            return INVALID;
        }
        return ofPacked(nextEmpty);
    }
    
    /**
     * Checks if the given trick is the last of the turn.
     *
     * @return true if the trick is the last, false if not
     */
    public boolean isLast() {
        return PackedTrick.isLast(packed);
    }

    /**
     * Checks if the given trick is empty.
     *
     * @return true if the trick is empty, false is not
     */
    public boolean isEmpty() {
        return PackedTrick.isEmpty(packed);
    }

    /**
     * Checks if the given trick is full.
     *
     * @return true if the trick is full, false if not
     */
    public boolean isFull() {
        return PackedTrick.isFull(packed);
    }

    /**
     * Returns the number of cards in the trick.
     *
     * @return the number of cards [0, 4]
     */
    public int size() {
        return PackedTrick.size(packed);
    }

    /**
     * Returns the trump of the trick.
     *
     * @return the trump
     */
    public Card.Color trump() {
        return PackedTrick.trump(packed);
    }

    /**
     * Returns the nth player of the trick.
     *
     * @param index the index
     * @return the player
     */
    public PlayerId player(int index) {
        if(index < 0 || index >= 4) {
            throw new IndexOutOfBoundsException();
        }
        return PackedTrick.player(packed, index);
    }

    /**
     * Returns the index of the trick in the turn.
     *
     * @return the index, [0, 8]
     */
    public int index() {
        return PackedTrick.index(packed);
    }

    /**
     * Returns the nth card of the trick.
     *
     * @param index the index of the card
     * @return the card
     */
    public Card card(int index) {
        if(index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }
        return Card.ofPacked(PackedTrick.card(packed, index));
    }

    /**
     * Returns a trick with an added card.
     *
     * @param card the card
     * @return the modified trick
     */
    public Trick withAddedCard(Card card) {
        if(isFull()) {
            throw new IllegalStateException();
        }
        return ofPacked(PackedTrick.withAddedCard(packed, card.packed()));
    }

    /**
     * Returns the color of the first card of the trick.
     *
     * @return the color
     */
    public Card.Color baseColor() {
        if(isEmpty()) {
            throw new IllegalStateException();
        }
        return PackedTrick.baseColor(packed);
    }

    /**
     * Returns the currently playable cards from a hand.
     *
     * @param hand the hand
     * @return the playable cards
     */
    public CardSet playableCards(CardSet hand) {
        if(isFull()) {
            throw new IllegalStateException();
        }
        return CardSet.ofPacked(PackedTrick.playableCards(packed, hand.packed()));
    }

    /**
     * Returns the number of points in the trick.
     *
     * @return the number of points
     */
    public int points() {
        return PackedTrick.points(packed);
    }

    /**
     * Returns the player that is currently winning the trick.
     *
     * @return the player
     */
    public PlayerId winningPlayer() {
        if(isEmpty()) {
            throw new IllegalStateException();
        }
        return PackedTrick.winningPlayer(packed);
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass()==Trick.class && packed==((Trick) obj).packed;
    }

    @Override
    public int hashCode() {
        return packed;
    }

    @Override
    public String toString() {
        return PackedTrick.toString(packed);
    }
}
