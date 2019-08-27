package ch.epfl.javass.jass;

import java.util.StringJoiner;

/**
 * Static methods to manipulate sets of cards, packed into a long.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public class PackedCardSet {
    public static final long EMPTY = 0L;
    public static final long ALL_CARDS = 0x01ff_01ff_01ff_01ffL;

    private static final short[] trumpAboveRank = {
        0b0000_0001_1111_1110, // 6
        0b0000_0001_1111_1100, // 7
        0b0000_0001_1111_1000, // 8
        0b0000_0000_0010_0000, // 9
        0b0000_0001_1110_1000, // 10
        0, // J
        0b0000_0001_1010_1000, // Q
        0b0000_0001_0010_1000, // K
        0b0000_0000_0010_1000, // A
    };
    
    private static final long[] colorMask = {
        0x0000_0000_0000_01ffL,
        0x0000_0000_01ff_0000L,
        0x0000_01ff_0000_0000L,
        0x01ff_0000_0000_0000L,
    };

    private PackedCardSet() {}

    /**
     * Checks if a packed card set is valid.
     *
     * @param pkCardSet the packed card set to be checked
     * @return true if it's possible, false if not
     */
    public static boolean isValid(long pkCardSet) {
        return (0xfe00_fe00_fe00_fe00L & pkCardSet) == 0;
    }

    /**
     * Returns the set of cards strictly stronger than a given card of trump color.
     *
     * @param pkCard the packed trump card as reference
     * @return the packed set of all cards better than the reference card
     */
    public static long trumpAbove(int pkCard) {
        int rank = pkCard & 0b1111;
        int color = pkCard & 0b110000;
        return (long)trumpAboveRank[rank] << color;
    }

    /**
     * Returns the set of cards containing a single given card.
     *
     * @param pkCard the packed card chosen
     * @return the set containing only this chosen card
     */
    public static long singleton(int pkCard) {
        return 1L << pkCard;
    }

    /**
     * Checks if a card set is empty (only 0s).
     *
     * @param pkCardSet the packed card set
     * @return true if this packed card set is empty
     */
    public static boolean isEmpty(long pkCardSet) {
        return pkCardSet == 0;
    }

    /**
     * Gives the number of cards of a card set.
     *
     * @param pkCardSet the packed card set
     * @return the size (number of 1s) of this card set
     */
    public static int size(long pkCardSet) {
        return Long.bitCount(pkCardSet);
    }

    /**
     * Returns the card of a selected position in a card set.
     *
     * @param pkCardSet the packed card set from which to extract the card
     * @param index the index of the card to be extracted (index 0 being the lowest bit worth 1)
     * @return a packed version of the target card
     */
    public static int get(long pkCardSet, int index) {
        for (int i = 0; i < index; i++) {
            pkCardSet ^= Long.lowestOneBit(pkCardSet);
        }
        int color = Long.numberOfTrailingZeros(pkCardSet) & 0xf0;
        int rank = Long.numberOfTrailingZeros(pkCardSet) & 0xf;
        return color | rank;
    }

    /**
     * Returns a card set containing an additional given card.
     *
     * @param pkCardSet the packed card set to be completed with another card
     * @param pkCard the packed card to be added to the card set
     * @return the packed card set with the extra card
     */
    public static long add(long pkCardSet, int pkCard) {
        return pkCardSet | singleton(pkCard);
    }

    /**
     * Returns a card set without a given card.
     *
     * @param pkCardSet the packed card set from which to remove a card
     * @param pkCard the packed card to be deleted from the card set
     * @return the packed card set without the card
     */
    public static long remove(long pkCardSet, int pkCard) {
        return pkCardSet & ~singleton(pkCard);
    }

    /**
     * Checks whether a card set contains a particular card.
     *
     * @param pkCardSet the packed card set to be checked
     * @param pkCard the packed card, whose presence in the card set needs to be verified
     * @return true only if the card set contains the card
     */
    public static boolean contains(long pkCardSet, int pkCard) {
        return (pkCardSet & singleton(pkCard)) != 0;
    }

    /**
     * Returns the complement card set of a given card set.
     *
     * @param pkCardSet the initial card set (packed)
     * @return a packed card set with all bits representing cards flipped
     */
    public static long complement(long pkCardSet) {
        return pkCardSet ^ ALL_CARDS;
    }

    /**
     * Returns the union of two card sets.
     *
     * @param pkCardSet1 the first packed card set
     * @param pkCardSet2 the second packed card set
     * @return a packed card set containing the union of the two card sets
     */
    public static long union(long pkCardSet1, long pkCardSet2) {
        return pkCardSet1 | pkCardSet2;
    }

    /**
     * Returns the intersection of two card sets.
     *
     * @param pkCardSet1 the first packed card set
     * @param pkCardSet2 the second packed card set
     * @return a packed card set containing the intersection of the two card sets
     */
    public static long intersection(long pkCardSet1, long pkCardSet2) {
        return pkCardSet1 & pkCardSet2;
    }

    /**
     * Returns the difference between two card sets.
     *
     * @param pkCardSet1 the first packed card set
     * @param pkCardSet2 the second packed card set
     * @return a packed card set containing all the cards from the first card set not in the second card set
     */
    public static long difference(long pkCardSet1, long pkCardSet2) {
        return pkCardSet1 & complement(pkCardSet2);
    }

    /**
     * Returns all cards from a card set of a given color.
     *
     * @param pkCardSet the packed card set from which to extract the cards
     * @param color the target color
     * @return a packed packed set containing only the card from the initial card set of the target color
     */
    public static long subsetOfColor(long pkCardSet, Card.Color color) {
        return pkCardSet & colorMask[color.ordinal()];
    }

    /**
     * Returns a text representation of all cards contained in a card set.
     *
     * @param pkCardSet the selected card set (packed)
     * @return a String with all the cards of the card set
     */
    public static String toString(long pkCardSet) {
        StringJoiner j = new StringJoiner(",", "{", "}");
        while (pkCardSet != 0) {
            int color = Long.numberOfTrailingZeros(pkCardSet) & 0xf0;
            int rank = Long.numberOfTrailingZeros(pkCardSet) & 0xf;
            j.add(PackedCard.toString(color | rank));
            pkCardSet ^= Long.lowestOneBit(pkCardSet);
        }
        return j.toString();
    }
}