package ch.epfl.javass.sigcheck;

import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

public final class SignatureChecks_5 {
    private SignatureChecks_5() {}

    @SuppressWarnings("unused")
    void checkTurnState() {
        TurnState s;
        Color c = null;
        Score o = null;
        PlayerId p = null;
        CardSet cs = null;
        Card cd = null;
        Trick t;
        long l = 0;
        int i = 0;
        boolean b;
        s = TurnState.initial(c, o, p);
        s = TurnState.ofPackedComponents(l, l, i);
        l = s.packedScore();
        l = s.packedUnplayedCards();
        i = s.packedTrick();
        o = s.score();
        cs = s.unplayedCards();
        t = s.trick();
        b = s.isTerminal();
        p = s.nextPlayer();
        s = s.withNewCardPlayed(cd);
        s = s.withTrickCollected();
        s = s.withNewCardPlayedAndTrickCollected(cd);
    }

    @SuppressWarnings("unused")
    void checkPlayer(Player p) {
        TurnState s = null;
        CardSet cs = null;
        Card c;
        Color co = null;
        PlayerId pi = null;
        Trick t = null;
        TeamId ti = null;
        Score sc = null;
        Map<PlayerId, String> pn = null;
        c = p.cardToPlay(s, cs);
        p.setPlayers(pi, pn);
        p.updateHand(cs);
        p.setTrump(co);
        p.updateTrick(t);
        p.updateScore(sc);
        p.setWinningTeam(ti);
    }

    void checkPacedPlayer() {
        Player p = null;
        double d = 0;
        p = new PacedPlayer(p, d);
    }

    @SuppressWarnings("unused")
    void checkJassGame() {
        JassGame g;
        long l = 0;
        Map<PlayerId, Player> ps = null;
        Map<PlayerId, String> pn = null;
        boolean b;
        g = new JassGame(l, ps, pn);
        b = g.isGameOver();
        g.advanceToEndOfNextTrick();
    }
}
