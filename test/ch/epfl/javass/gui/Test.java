package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.SetChangeListener;

/**
 *
 */
public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) {
        HandBean hb = new HandBean();

        ListChangeListener<Card> listener = e -> System.out.println(e);
        hb.hand().addListener(listener);

        System.out.println("----HAND TEST----");
        CardSet h = CardSet.EMPTY.add(Card.of(Color.SPADE, Rank.SIX))
                .add(Card.of(Color.SPADE, Rank.NINE))
                .add(Card.of(Color.SPADE, Rank.JACK))
                .add(Card.of(Color.HEART, Rank.SEVEN))
                .add(Card.of(Color.HEART, Rank.ACE))
                .add(Card.of(Color.DIAMOND, Rank.KING))
                .add(Card.of(Color.DIAMOND, Rank.ACE))
                .add(Card.of(Color.CLUB, Rank.TEN))
                .add(Card.of(Color.CLUB, Rank.QUEEN));
        hb.setHand(h);
        while (!h.isEmpty()) {
            h = h.remove(h.get(0));
            hb.setHand(h);
        }

        SetChangeListener<Card> listenerPlayable = e -> System.out.println(e);
        hb.playableCards().addListener(listenerPlayable);

        System.out.println();
        System.out.println("----PLAYABLE CARDS TEST----");
        CardSet h2 = CardSet.EMPTY.add(Card.of(Color.SPADE, Rank.SIX))
                .add(Card.of(Color.SPADE, Rank.NINE))
                .add(Card.of(Color.SPADE, Rank.JACK))
                .add(Card.of(Color.HEART, Rank.SEVEN))
                .add(Card.of(Color.HEART, Rank.ACE))
                .add(Card.of(Color.DIAMOND, Rank.KING))
                .add(Card.of(Color.DIAMOND, Rank.ACE))
                .add(Card.of(Color.CLUB, Rank.TEN))
                .add(Card.of(Color.CLUB, Rank.QUEEN));
        int count = 0;
        while (!h2.isEmpty()) {
            if ((count & 1) == 0) {
                hb.setPlayableCards(h2);
                h2 = h2.remove(h2.get(0));
            } else {
                hb.setPlayableCards(CardSet.EMPTY);
            }
            count++;
        }

        System.out.println();
        System.out.println("----SCOREBEAN TEST----");
        ScoreBean sb = new ScoreBean();

        System.out.println();
        System.out.println("----Turns Points----");
        ChangeListener<Number> turn1 = (a, b, c) -> System.out
                .println(a + " a changé de : " + b + " à : " + c);
        sb.turnPointsProperty(TeamId.TEAM_1).addListener(turn1);
        sb.setTurnPoints(TeamId.TEAM_1, 20);
        sb.setTurnPoints(TeamId.TEAM_1, 0);

        ChangeListener<Number> turn2 = (a, b, c) -> System.out
                .println(a + " a changé de : " + b + " à : " + c);
        sb.turnPointsProperty(TeamId.TEAM_2).addListener(turn2);
        sb.setTurnPoints(TeamId.TEAM_2, 10);
        sb.setTurnPoints(TeamId.TEAM_2, 0);

        System.out.println();
        System.out.println("----Game Points----");
        ChangeListener<Number> game1 = (a, b, c) -> System.out
                .println(a + " a changé de : " + b + " à : " + c);
        sb.gamePointsProperty(TeamId.TEAM_1).addListener(game1);
        sb.setGamePoints(TeamId.TEAM_1, 130);
        sb.setGamePoints(TeamId.TEAM_1, 0);

        ChangeListener<Number> game2 = (a, b, c) -> System.out
                .println(a + " a changé de : " + b + " à : " + c);
        sb.gamePointsProperty(TeamId.TEAM_2).addListener(game2);
        sb.setGamePoints(TeamId.TEAM_2, 27);
        sb.setGamePoints(TeamId.TEAM_2, 0);

        System.out.println();
        System.out.println("----Total Points----");
        ChangeListener<Number> tot1 = (a, b, c) -> System.out
                .println(a + " a changé de : " + b + " à : " + c);
        sb.totalPointsProperty(TeamId.TEAM_1).addListener(tot1);
        sb.setTotalPoints(TeamId.TEAM_1, 512);
        sb.setTotalPoints(TeamId.TEAM_1, 624);

        ChangeListener<Number> tot2 = (a, b, c) -> System.out
                .println(a + " a changé de : " + b + " à : " + c);
        sb.totalPointsProperty(TeamId.TEAM_2).addListener(tot2);
        sb.setTotalPoints(TeamId.TEAM_2, 666);
        sb.setTotalPoints(TeamId.TEAM_2, 700);

        System.out.println();
        System.out.println("----Winning team----");

        ChangeListener<TeamId> wng = (a, b, c) -> System.out
                .println(a + " a changé de : " + b + " à : " + c);
        sb.winningTeamProperty().addListener(wng);
        sb.setWinningTeam(TeamId.TEAM_1);

        System.out.println();
        System.out.println("----TRICKBEAN TEST----");
        TrickBean tb = new TrickBean();

        System.out.println();
        System.out.println("----Trump----");
        ChangeListener<Color> trmp = (a, b, c) -> System.out
                .println(a + " a changé de : " + b + " à : " + c);
        tb.trumpProperty().addListener(trmp);
        tb.setTrump(Color.CLUB);
        tb.setTrump(Color.DIAMOND);

        System.out.println();
        System.out.println("----Trick----");
        MapChangeListener<PlayerId, Card> trick = e -> System.out.println(e);
        tb.trick().addListener(trick);
        ChangeListener<PlayerId> Player = (a, b, c) -> System.out.println(a + " a changé de : " + b + " à : " + c);
        tb.winningPlayerProperty().addListener(Player);
        
        Trick t = Trick.firstEmpty(tb.trumpProperty().get(), PlayerId.PLAYER_1);
        t = t.withAddedCard(Card.of(Color.CLUB, Rank.TEN));
        tb.setTrick(t);
        t = t.withAddedCard(Card.of(Color.CLUB, Rank.JACK));
        tb.setTrick(t);
        t = t.withAddedCard(Card.of(Color.CLUB, Rank.QUEEN));
        tb.setTrick(t);
        t = t.withAddedCard(Card.of(Color.CLUB, Rank.KING));
        tb.setTrick(t);
        t = t.nextEmpty();
        tb.setTrick(t);
        t = t.withAddedCard(Card.of(Color.CLUB, Rank.ACE));
        tb.setTrick(t);
    }
}
