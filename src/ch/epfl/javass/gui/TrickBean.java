package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Bean containing all the information about the trick.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public final class TrickBean {
    private SimpleObjectProperty<Color> trump;
    private ObservableMap<PlayerId, Card> trick;
    private SimpleObjectProperty<PlayerId> winningPlayer;

    /**
     * Constructor for the bean, taking no parameters.
     */
    public TrickBean() {
        trump = new SimpleObjectProperty<Color>();
        trick = FXCollections.observableHashMap();
        winningPlayer = new SimpleObjectProperty<PlayerId>();
    }

    /**
     * Getter for the trump color's property.
     *
     * @return the corresponding property
     */
    public ReadOnlyObjectProperty<Color> trumpProperty() {
        return trump;
    }

    /**
     * Setter for the trump color.
     *
     * @param newTrump the trump color
     */
    public void setTrump(Color newTrump) {
        trump.set(newTrump);
    }

    /**
     * Getting for an unmodifiable map linking a player to his played card.
     *
     * @return the map
     */
    public ObservableMap<PlayerId, Card> trick() {
        return FXCollections.unmodifiableObservableMap(trick);
    }

    /**
     * Setter for the Trick object.
     *
     * @param newTrick the trick to be set to this property
     */
    public void setTrick(Trick newTrick) {
        int size = newTrick.size();
        for (int i = 0; i < size; i++) {
            trick.put(newTrick.player(i), newTrick.card(i));
        }
        for (int i = size; i < 4; i++) {
            trick.put(newTrick.player(i), null);
        }
        if (newTrick.size() == 0) {
            winningPlayer.set(null);
        } else {
            winningPlayer.set(newTrick.winningPlayer());
        }
    }

    /**
     * Getter for the winning player property.
     *
     * @return the corresponding winning player
     */
    public ReadOnlyObjectProperty<PlayerId> winningPlayerProperty() {
        return winningPlayer;
    }
}
