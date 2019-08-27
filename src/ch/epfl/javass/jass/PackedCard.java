package ch.epfl.javass.jass;

/**
 * Static methods to manipulate cards packed into an int.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public final class PackedCard {
    public static int INVALID = 0b111111;

    // private constructor to prevent ever instantiating this class
    private PackedCard() {}

    /**
     * Checks if the given packed card corresponds to an actual card.
     *
     * @param pkCard the card packed into an int
     * @return true if the packed card is valid, false if not
     */
    public static boolean isValid(int pkCard) {
        int rankNum = pkCard & 0xf;
        int unusedBitsNum = pkCard & 0xffff_ffc0;
        if(rankNum >= 0 && rankNum <= 8 && unusedBitsNum == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the packed version of a card of a given color and rank.
     *
     * @param c the color of the card
     * @param r the rank of the card
     * @return the corresponding packed version
     */
    public static int pack(Card.Color c, Card.Rank r) {
        return (c.ordinal() << 4) | r.ordinal();
    }

    /**
     * Returns the color of a packed card.
     *
     * @param pkCard the card, packed in an int
     * @return the color of this card
     */
    public static Card.Color color(int pkCard) {
        int colorNum = (pkCard & 0x30) >> 4;
        Card.Color color = Card.Color.values()[colorNum];
        return color;
    }

    /**
     * Returns the rank of a packed card.
     *
     * @param pkCard the card, packed in an int
     * @return the rank of this card
     */
    public static Card.Rank rank(int pkCard) {
        int rankNum = pkCard & 0xf;
        Card.Rank rank = Card.Rank.values()[rankNum];
        return rank;
    }

    /**
     * Checks if a card is higher-ranked than another, given the trump color.
     *
     * @param trump the color of the trump cards
     * @param pkCardL the first card
     * @param pkCardR the second card
     * @return true if the first card is better than the second card, false otherwise
     */
    public static boolean isBetter(Card.Color trump, int pkCardL, int pkCardR) {
        if(color(pkCardL) == trump) {
            if((color(pkCardR) != trump) || (rank(pkCardL).trumpOrdinal() > rank(pkCardR).trumpOrdinal())) {
                return true;
            } else {
                return false;
            }
        } else if((color(pkCardL) == color(pkCardR))) {
            if(rank(pkCardL).ordinal() > rank(pkCardR).ordinal()) {
                return true;
            }
            else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Returns how many points a card is worth, given the trump color.
     *
     * @param trump the color of the trump cards
     * @param pkCard the packed card
     * @return the amount of points this card brings
     */
    public static int points(Card.Color trump, int pkCard) {
        if(color(pkCard) == trump) {
            switch(rank(pkCard)) {
                case SIX : return 0;
                case SEVEN : return 0;
                case EIGHT : return 0;
                case NINE : return 14;
                case TEN : return 10;
                case JACK : return 20;
                case QUEEN : return 3;
                case KING : return 4;
                case ACE : return 11;
                default :  return 0;
            }
        } else {
            switch(rank(pkCard)) {
                case SIX : return 0;
                case SEVEN : return 0;
                case EIGHT : return 0;
                case NINE : return 0;
                case TEN : return 10;
                case JACK : return 2;
                case QUEEN : return 3;
                case KING : return 4;
                case ACE : return 11;
                default :  return 0;
            }
        }
    }

    /**
     * A compact representation of the card.
     *
     * @param pkCard the packed card
     * @return the symbol of the card's color, followed by the notation of its rank
     */
    public static String toString(int pkCard) {
        String cardColor = color(pkCard).toString();
        String cardRank = rank(pkCard).toString();
        return cardColor + cardRank;
    }
}
