package ch.epfl.javass.gui;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public final class TrumpBean {
    private SimpleBooleanProperty mustChooseTrump;
    private SimpleBooleanProperty canPass;

    public TrumpBean() {
        mustChooseTrump = new SimpleBooleanProperty(false);
        canPass = new SimpleBooleanProperty(false);
    }

    public ReadOnlyBooleanProperty mustChooseTrump() {
        return mustChooseTrump;
    }

    public void setMustChooseTrump(boolean newMustChooseTrump) {
        mustChooseTrump.set(newMustChooseTrump);
    }

    public ReadOnlyBooleanProperty canPass() {
        return canPass;
    }

    public void setCanPass(boolean newCanPass) {
        canPass.set(newCanPass);
    }
}
