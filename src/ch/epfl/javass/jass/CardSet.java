package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;

import java.util.List;

/**
 * Represents a set of distinct cards.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public final class CardSet {
    private final long packed;

    private CardSet(long packed) {
        this.packed = packed;
    }

    public static final CardSet EMPTY = new CardSet(PackedCardSet.EMPTY);
    public static final CardSet ALL_CARDS = new CardSet(PackedCardSet.ALL_CARDS);

    /**
     * Returns a card set of a given list of cards.
     *
     * @param cards the list of Cards to be included in the card set
     * @return a CardSet object containing the chosen cards
     */
    public static CardSet of(List<Card> cards) {
        CardSet cardSet = EMPTY;
        for(Card c : cards) {
            cardSet = cardSet.add(c);
        }
        return cardSet;
    }

    /**
     * Returns a card set from a packed card set.
     *
     * @param packed the packed card set to be included in the card set
     * @return a CardSet object containing the cards from the packed card set
     */
    public static CardSet ofPacked(long packed) {
        Preconditions.checkArgument(PackedCardSet.isValid(packed));
        return new CardSet(packed);
    }

    /**
     * Getter for the packed version of this CardSet.
     *
     * @return the packed card set
     */
    public long packed() {
        return packed;
    }

    /**
     * Checks if this CardSet is empty.
     *
     * @return true if the CardSet is empty
     */
    public boolean isEmpty() {
        return PackedCardSet.isEmpty(packed);
    }

    /**
     * Gives the size of this CardSet.
     *
     * @return the number of cards contained in this CardSet
     */
    public int size() {
        return PackedCardSet.size(packed);
    }

    /**
     * Getter for a given card contained in this CardSet.
     *
     * @param index the index (in the packed card set) of the chosen card (index 0 is the lowest bit worth 1)
     * @return the selected card as a Card object
     */
    public Card get(int index) {
        return Card.ofPacked(PackedCardSet.get(packed, index));
    }

    /**
     * Returns this CardSet with an additional card.
     *
     * @param card to be added to the card set
     * @return this CardSet object, with the chosen card added
     */
    public CardSet add(Card card) {
        return ofPacked(PackedCardSet.add(packed, card.packed()));
    }

    /**
     * Returns this CardSet without a specified card.
     *
     * @param card to be removed from the card set
     * @return this CardSet object, with the chosen card removed
     */
    public CardSet remove(Card card) {
        return ofPacked(PackedCardSet.remove(packed, card.packed()));
    }

    /**
     * Checks if a card is contained in this CardSet.
     *
     * @param card to be checked
     * @return true if the card is in the CardSet
     */
    public boolean contains(Card card) {
        return PackedCardSet.contains(packed, card.packed());
    }

    /**
     * Returns the complement of this CardSet.
     *
     * @return this CardSet with all bits representing cards flipped
     */
    public CardSet complement() {
        return ofPacked(PackedCardSet.complement(packed));
    }

    /**
     * Returns the union of this CardSet with another CardSet.
     *
     * @param that the other CardSet with which the union is to be taken
     * @return a CardSet containing the union of both CardSets
     */
    public CardSet union(CardSet that) {
        return ofPacked(PackedCardSet.union(packed, that.packed));
    }

    /**
     * Returns the intersection of this CardSet with another CardSet.
     *
     * @param that the other CardSet with which the intersection is to be taken
     * @return a CardSet containing the intersection of both CardSets
     */
    public CardSet intersection(CardSet that) {
        return ofPacked(PackedCardSet.intersection(packed, that.packed));
    }

    /**
     * Returns the difference between this CardSet and another CardSet.
     *
     * @param that the other CardSet with which the difference is to be taken
     * @return a CardSet containing the cards from this CardSet that are not in the other CardSet
     */
    public CardSet difference(CardSet that) {
        return ofPacked(PackedCardSet.difference(packed, that.packed));
    }

    /**
     * Returns only the cards from this CardSet of a specific color.
     *
     * @param color the color of the cards to be extracted
     * @return a CardSet containing only the cards from this CardSet of the target color
     */
    public CardSet subsetOfColor(Card.Color color) {
        return ofPacked(PackedCardSet.subsetOfColor(packed, color));
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass()==CardSet.class && packed==((CardSet) obj).packed;
    }

    @Override
    public String toString() {
        return PackedCardSet.toString(packed);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(packed);
    }
}
