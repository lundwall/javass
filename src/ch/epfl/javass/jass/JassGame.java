package ch.epfl.javass.jass;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Represents a game of Jass.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public final class JassGame {
    private Random shuffleRng;
    private Map<PlayerId, Player> players;
    private Map<PlayerId, String> playerNames;
    private boolean isGameOver;
    private List<Card> deck;
    private Map<PlayerId, CardSet> hands;
    private PlayerId trumpChooser;
    private PlayerId firstPlayer;
    private TurnState turnState;

    /**
     * Constructor for a JassGame.
     *
     * @param rngSeed the seed of the PRNG
     * @param players the played IDs associated to the Player objects
     * @param playerNames the played IDs associated to their names
     */
    public JassGame(long rngSeed, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames) {
        Random rng = new Random(rngSeed);
        shuffleRng = new Random(rng.nextLong());
        this.players = new EnumMap<PlayerId, Player>(players);
        this.playerNames = new EnumMap<PlayerId, String>(playerNames);
        for (Map.Entry<PlayerId, Player> entry : this.players.entrySet()) {
            PlayerId playerId = entry.getKey();
            Player player = entry.getValue();
            player.setPlayers(playerId, this.playerNames);
        }
        isGameOver = false;
        deck = new ArrayList<Card>();
        for (Color color : Color.ALL) {
            for (Rank rank : Rank.ALL) {
                deck.add(Card.of(color, rank));
            }
        }
        hands = new EnumMap<PlayerId, CardSet>(PlayerId.class);
        distributeCards();
        firstPlayer = PlayerId.PLAYER_1;
        for (int i = 0; i < 4; i++) {
            PlayerId player = PlayerId.ALL.get(i);
            CardSet hand = hands.get(player);
            if (hand.contains(Card.of(Color.DIAMOND, Rank.SEVEN))) {
                firstPlayer = player;
                break;
            }
        }
        trumpChooser = firstPlayer;
        startTurn(Score.INITIAL);
    }

    /**
     * Checks if the game is over.
     *
     * @return true only if the game is finished
     */
    public boolean isGameOver() {
        return isGameOver;
    }

    /**
     * Advances the game state until the end of the next trick.
     */
    public void advanceToEndOfNextTrick() {
        if (isGameOver) {
            return;
        }
        if (turnState.trick().isFull()) {
            turnState = turnState.withTrickCollected();
            for (Player player : players.values()) {
                player.updateScore(turnState.score());
            }
            for (TeamId teamId : TeamId.ALL) {
                if (turnState.score().totalPoints(teamId) >= Jass.WINNING_POINTS) {
                    for (Player player : players.values()) {
                        player.setWinningTeam(teamId);
                    }
                    isGameOver = true;
                    return;
                }
            }
            if (turnState.isTerminal()) {
                distributeCards();
                firstPlayer = PlayerId.ALL.get((firstPlayer.ordinal() + 1) & 0x3);
                startTurn(turnState.score().nextTurn());
            } else {
                for (Player player : players.values()) {
                    player.updateTrick(turnState.trick());
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            PlayerId playerId = turnState.nextPlayer();
            Player currentPlayer = players.get(playerId);
            CardSet hand = hands.get(playerId);
            Card card = currentPlayer.cardToPlay(turnState, hand);
            CardSet playableCards = turnState.trick().playableCards(hand);
            if (!playableCards.contains(card)) {
                card = playableCards.get(shuffleRng.nextInt(playableCards.size()));
            }
            CardSet newHand = hand.remove(card);
            hands.replace(playerId, newHand);
            currentPlayer.updateHand(newHand);
            turnState = turnState.withNewCardPlayed(card);
            for (Player player : players.values()) {
                player.updateTrick(turnState.trick());
            }
        }
    }
    
    private void distributeCards() {
        Collections.shuffle(deck, shuffleRng);
        hands.clear();
        for (int i = 0; i < 4; i++) {
            PlayerId player = PlayerId.ALL.get(i);
            int start = Jass.HAND_SIZE * i;
            int end = start + Jass.HAND_SIZE;
            CardSet hand = CardSet.of(deck.subList(start, end));
            hands.put(player, hand);
            players.get(player).updateHand(hand);
        }
    }

    private void startTurn(Score score) {
        PlayerId cPlayerId = trumpChooser;
        Player cPlayer = players.get(cPlayerId);
        CardSet hand = hands.get(cPlayerId);
        Color trump = cPlayer.chooseTrump(hand, true);
        if (trump == null) {
            cPlayerId = PlayerId.ALL.get((cPlayerId.ordinal() + 2) & 0x3);
            cPlayer = players.get(cPlayerId);
            hand = hands.get(cPlayerId);
            trump = cPlayer.chooseTrump(hand, false);
        }
        trumpChooser = PlayerId.ALL.get((trumpChooser.ordinal() + 1) & 0x3);
        turnState = TurnState.initial(trump, score, firstPlayer);
        for (Player player : players.values()) {
            player.setTrump(trump);
            player.updateScore(turnState.score());
            player.updateTrick(turnState.trick());
        }
    }
}
