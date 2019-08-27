package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Jass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import static javafx.collections.FXCollections.unmodifiableObservableList;
import static javafx.collections.FXCollections.unmodifiableObservableSet;

/**
 * Bean containing all of the information about the hand.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public final class HandBean {
    private ObservableList<Card> hand;
    private ObservableSet<Card> playableCards;

    /**
     * Constructor for the bean, taking no parameters.
     */
    public HandBean() {
        hand = FXCollections.observableArrayList(new Card[Jass.HAND_SIZE]);
        playableCards = FXCollections.observableSet();
    }

    /**
     * Getter for the list of cards in the hand of the player.
     *
     * @return the unmodifiable version of the list of cards
     */
    public ObservableList<Card> hand() {
        return unmodifiableObservableList(hand);
    }

    /**
     * Setter for the hand of the player.
     *
     * @param newHand the new hand of the player
     */
    public void setHand(CardSet newHand) {
        if (newHand.size() == Jass.HAND_SIZE) {
            for (int i = 0; i < Jass.HAND_SIZE; i++) {
                hand.set(i, newHand.get(0));
                newHand = newHand.remove(newHand.get(0));
            }
        } else {
            for (int i = 0; i < Jass.HAND_SIZE; i++) {
                if (hand.get(i) != null && !newHand.contains(hand.get(i))) {
                    hand.set(i, null);
                }
            }
        }
    }

    /**
     * Getter for the playable cards of the player.
     *
     * @return an unmodifiable version of the set of all the playable cards
     */
    public ObservableSet<Card> playableCards() {
        return unmodifiableObservableSet(playableCards);
    }

    /**
     * Setter for the playable cards.
     *
     * @param newPlayableCards the new card set of playable cards
     */
    public void setPlayableCards(CardSet newPlayableCards) {
        playableCards.clear();
        int size = newPlayableCards.size();
        for(int i = 0; i < size; i++) {
            playableCards.add(newPlayableCards.get(0));
            newPlayableCards = newPlayableCards.remove(newPlayableCards.get(0));
        }
    }
}
