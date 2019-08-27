package ch.epfl.javass.jass;

import ch.epfl.javass.jass.Card.Color;

import java.util.StringJoiner;

/**
 * Static methods to manipulate tricks packed into an int.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public final class PackedTrick {
    public static final int INVALID = 0xffff_ffff;

    private PackedTrick() {}
    
    /**
     * Checks if the given packed trick corresponds to an actual trick.
     *
     * @param pkTrick the trick packed into a int
     * @return true if the packed score is valid, false if not
     */
    public static boolean isValid(int pkTrick) {
        boolean c0 = size(pkTrick) == 0
            && cardIsInvalid(pkTrick, 1)
            && cardIsInvalid(pkTrick, 2)
            && cardIsInvalid(pkTrick, 3);
        boolean c1 = size(pkTrick) == 1
            && cardIsInvalid(pkTrick, 2)
            && cardIsInvalid(pkTrick, 3);
        boolean c2 = size(pkTrick) == 2
            && cardIsInvalid(pkTrick, 3);
        boolean c3 = size(pkTrick) == 3;
        boolean c4 = size(pkTrick) == 4;
        return (c0 || c1 || c2 || c3 || c4) && index(pkTrick) < Jass.TRICKS_PER_TURN;
    }
    
    /**
     * Returns the first trick of a turn with the specified parameters.
     *
     * @param trump the trump
     * @param firstPlayer the first player of the trick
     * @return the packed version of the trick
     */
    public static int firstEmpty(Color trump, PlayerId firstPlayer) {
        return (trump.ordinal() << 30) 
            | (firstPlayer.ordinal() << 28)
            | 0x00ff_ffff;
    }
    
    /**
     * Returns the emptied next trick.
     *
     * @param pkTrick the packed version of the current trick
     * @return the packed version of the next trick, or INVALID if the current trick is the last of the turn
     */
    public static int nextEmpty(int pkTrick) {
        if (isLast(pkTrick)) {
            return INVALID;
        }
        int player = winningPlayer(pkTrick).ordinal();
        return ((pkTrick | 0xff_ffff) & 0xcfff_ffff | (player << 28)) + (1 << 24);
    }
    
    /**
     * Checks if the given trick is the last of the turn.
     *
     * @param pkTrick the packed version of the trick
     * @return true if the trick is the last, false if not
     */
    public static boolean isLast(int pkTrick) {
        return index(pkTrick) == Jass.TRICKS_PER_TURN - 1;
    }
    
    /**
     * Checks if the given trick is empty.
     *
     * @param pkTrick the packed version of the trick
     * @return true if the trick is empty, false is not
     */
    public static boolean isEmpty(int pkTrick) {
        return size(pkTrick) == 0;
    }
    
    /**
     * Checks if the given trick is full.
     *
     * @param pkTrick the packed version of the trick
     * @return true if the trick is full, false if not
     */
    public static boolean isFull(int pkTrick) {
        return size(pkTrick) == 4;
    }
    
    
    /**
     * Returns the number of cards in the trick.
     *
     * @param pkTrick the packed version of the trick
     * @return the number of cards [0, 4]
     */
    public static int size(int pkTrick) {
        if (cardIsInvalid(pkTrick, 0)) {
            return 0;
        } else if (cardIsInvalid(pkTrick, 1)) {
            return 1;
        } else if (cardIsInvalid(pkTrick, 2)) {
            return 2;
        } else if (cardIsInvalid(pkTrick, 3)) {
            return 3;
        }
        return 4;
    }
    
    /**
     * Returns the trump of the trick.
     *
     * @param pkTrick the packed version of the trick
     * @return the trump
     */
    public static Color trump(int pkTrick) {
        int enumIndex = (pkTrick >> 30) & 0x3;
        return Color.ALL.get(enumIndex);
    }
    
    /**
     * Returns the nth player of the trick.
     *
     * @param pkTrick the packed version of the trick
     * @param index the index
     * @return the player
     */
    public static PlayerId player(int pkTrick, int index) {
        int enumIndex = (((pkTrick & 0x3000_0000) >> 28) + index) & 0x3;
        return PlayerId.ALL.get(enumIndex);
    }
    
    /**
     * Returns the index of the trick in the turn.
     *
     * @param pkTrick the packed version of the trick
     * @return the index, [0, 8]
     */
    public static int index(int pkTrick) {
        return (pkTrick & 0x0f00_0000) >> 24;
    }
    
    /**
     * Returns the nth card of the trick.
     *
     * @param pkTrick the packed version of the trick
     * @param index the index of the card
     * @return the packed version of the card
     */
    public static int card(int pkTrick, int index) {
        return (pkTrick >> (6 * index)) & 0x3f;
    }
    
    /**
     * Returns a trick with an added card.
     *
     * @param pkTrick the packed version of the trick
     * @param pkCard the packed version of the card
     * @return the packed version of the modified trick
     */
    public static int withAddedCard(int pkTrick, int pkCard) {
        int size = size(pkTrick);
        int mask = ~(0x3f << (6 * size));
        return pkTrick & mask | (pkCard << (6 * size));
    }
    
    /**
     * Returns the color of the first card of the trick.
     *
     * @param pkTrick the packed version of the trick
     * @return the color
     */
    public static Color baseColor(int pkTrick) {
        return PackedCard.color(card(pkTrick, 0));
    }
    
    /**
     * Returns the currently playable cards from a hand.
     * @param pkTrick the packed version of the trick
     * @param pkHand the packed version of the hand (PackedCardSet)
     * @return the packed version of the playable cards
     */
    public static long playableCards(int pkTrick, long pkHand) {
        if (size(pkTrick) == 0 | PackedCardSet.size(pkHand) == 1) {
            return pkHand;
        }
        Color baseColor = baseColor(pkTrick);
        Color trump = trump(pkTrick);
        long playableCards = PackedCardSet.subsetOfColor(pkHand, baseColor);
        long trumpJ = 0b0000_0000_0010_0000L << (trump.ordinal() << 4);
        long trumpCards = PackedCardSet.subsetOfColor(pkHand, trump);
        if (playableCards == 0 || playableCards == trumpJ) {
            playableCards |= PackedCardSet.difference(pkHand, trumpCards);
        }
        if (baseColor.equals(trump)) {
            return playableCards;
        }
        int winningCard = card(pkTrick, winningPos(pkTrick));
        if (PackedCard.color(winningCard).equals(trump)) {
            playableCards |= (PackedCardSet.trumpAbove(winningCard) & pkHand);
        } else {
            playableCards |= trumpCards;
        }
        if (playableCards == 0) {
            return pkHand;
        }
        return playableCards;
    }
    
    /**
     * Returns the number of points in the trick.
     *
     * @param pkTrick the packed version of the trick
     * @return the number of points
     */
    public static int points(int pkTrick) {
        int points = 0;
        for (int i = 0; i < size(pkTrick); i++) {
            points += PackedCard.points(trump(pkTrick), card(pkTrick, i));
        }
        if (isLast(pkTrick)) {
            points += 5;
        }
        return points;
    }
    
    /**
     * Returns the player that is currently winning the trick.
     *
     * @param pkTrick the packed version of the trick
     * @return the player
     */
    public static PlayerId winningPlayer(int pkTrick) {
        return player(pkTrick, winningPos(pkTrick));
    }
    
    /**
     * Returns a text representation of the trick.
     *
     * @param pkTrick the packed version of the trick
     * @return the text representation
     */
    public static String toString(int pkTrick) {
        StringJoiner j = new StringJoiner(",", "{", "}");
        for (int i = 0; i < 4; i++) {
            int pkCard = card(pkTrick, i);
            if (pkCard == PackedCard.INVALID) {
                break;
            } else {
                j.add(PackedCard.toString(pkCard));
            }
        }
        return j.toString();
    }
    
    private static boolean cardIsInvalid(int pkTrick, int index) {
        return card(pkTrick, index) == PackedCard.INVALID;
    }
    
    private static int winningPos(int pkTrick) {
        Color trump = trump(pkTrick);
        int winningPos = 0;
        int winningCard = card(pkTrick, 0);
        for (int i = 1; i < 4; i++) {
            int pkCard = card(pkTrick, i);
            if (pkCard == PackedCard.INVALID) {
                break;
            } else {
                if (PackedCard.isBetter(trump, pkCard, winningCard)) {
                    winningPos = i;
                    winningCard = pkCard;
                }
            }
        }
        return winningPos;
    }
}
