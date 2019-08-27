package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a single card of a 36-card deck.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public final class Card {
    private final int packed;

    private Card(int packed) {
        this.packed = packed;
    }

    /**
     * Returns the Card object of a card of given color and rank.
     *
     * @param color the color of the card
     * @param rank the rank of the card
     * @return the constructed Card object
     */
    public static Card of(Color color, Rank rank) {
        return new Card(PackedCard.pack(color, rank));
    }

    /**
     * Returns the Card object of a card packed into an int.
     *
     * @param packed the packed card
     * @return the constructed Card object
     */
    public static Card ofPacked(int packed) {
        Preconditions.checkArgument(PackedCard.isValid(packed));
        return new Card(packed);
    }

    /**
     * Returns the packed version of the card.
     *
     * @return the card packed into an int
     */
    public int packed() {
        return packed;
    }

    /**
     * Returns the color of the card.
     *
     * @return the card's color
     */
    public Color color() {
        return PackedCard.color(packed);
    }

    /**
     * Returns the rank of the card.
     *
     * @return the card's rank
     */
    public Rank rank() {
        return PackedCard.rank(packed);
    }

    /**
     * Checks if a card is higher-ranked than another card, given the trump color.
     *
     * @param trump the color of the trump cards
     * @param that the second card, with which this card is comparing itself to
     * @return true if this card is higher than the other card, false otherwise
     */
    public boolean isBetter(Color trump, Card that) {
        return PackedCard.isBetter(trump, packed, that.packed());
    }

    /**
     * Returns the number of points which the card is worth.
     *
     * @param trump the color of trump cards
     * @return the amount of points this card brings
     */
    public int points(Color trump) {
        return PackedCard.points(trump, packed);
    }

    /**
     * Checks if this card is the same as another card.
     *
     * @param that the other card to be compared with
     * @return true if both cards are equal (when they are the same when packed into an int)
     */
    @Override
    public boolean equals(Object that) {
        if (that == null) {
            return false;
        }
        return packed == ((Card) that).packed;
    }

    /**
     * Returns the packed version of the card.
     *
     * @return the card packed into an int
     */
    @Override
    public int hashCode() {
        return packed;
    }

    @Override
    public String toString() {
        return PackedCard.toString(packed);
    }

    /**
     * Enumeration represents the color of the card.
      */
    public enum Color {
        
        SPADE("\u2660"),
        HEART("\u2661"),
        DIAMOND("\u2662"),
        CLUB("\u2663");
        
        private String symbol;
        
        private Color(String symbol) {
            this.symbol = symbol;
        }

        public static final List<Color> ALL = Collections.unmodifiableList(Arrays.asList(values()));
        
        public static final int COUNT = 4;

        @Override
        public String toString() {
            return symbol;
        }
    }

    /**
     * Enumeration represents the rank of the card.
     */
    public enum Rank {
        SIX("6"),
        SEVEN("7"),
        EIGHT("8"),
        NINE("9"),
        TEN("10"),
        JACK("J"),
        QUEEN("Q"),
        KING("K"),
        ACE("A");
        
        private String symbol;
        
        private Rank(String symbol) {
            this.symbol = symbol;
        }
        
        public static final List<Rank> ALL = Collections.unmodifiableList(Arrays.asList(values()));

        public static final int COUNT = 9;

        @Override
        public String toString() {
            return symbol;
        }

        /**
         * Position of a trump card's rank.
         *
         * @return a card's position in increasing order of importance, from 0 for a 6, to 8 for a Jack
         */
        public int trumpOrdinal() {
            switch(symbol) {
                case "6" : return 0;
                case "7" : return 1;
                case "8" : return 2;
                case "10" : return 3;
                case "Q" : return 4;
                case "K" : return 5;
                case "A" : return 6;
                case "9" : return 7;
                case "J" : return 8;
                default : return 0;
            }
        }
    }
}
