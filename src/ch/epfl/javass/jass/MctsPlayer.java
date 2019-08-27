package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

import java.util.SplittableRandom;

/**
 * Player equipped with Monte Carlo Tree Search for better moves.
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public class MctsPlayer implements Player {
    private PlayerId ownId;
    private long rngSeed;
    private int iterations;
    public boolean winning;

    private static final double PASS_THRESHOLD = 108.0;

    /**
     * Constructor for MctsPlayer.
     * @param ownId      the PlayerID of the player
     * @param rngSeed    the seed for all random events
     * @param iterations the number of random matches to be carried out (same as the number of terminal leaves)
     */
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        Preconditions.checkArgument(iterations >= Jass.HAND_SIZE);
        this.ownId = ownId;
        this.rngSeed = rngSeed;
        this.iterations = iterations;
        this.winning = false;
    }

    @Override
    public Color chooseTrump(CardSet hand, boolean canPass) {
        double maxScore = 0.0;
        Color bestTrump = null;
        for (Color trump : Color.ALL) {
            TurnState state = TurnState.initial(trump, Score.INITIAL, ownId);
            double score = cardToPlayImpl(state, hand.packed()).totalPointsOverTurns;
            if (score > maxScore) {
                maxScore = score;
                bestTrump = trump;
            }
        }
        if (canPass && maxScore < PASS_THRESHOLD) {
            return null;
        }
        return bestTrump;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Node bestChild = cardToPlayImpl(state, hand.packed());
        int futureTrick = bestChild.turnState.packedTrick();
        return Card.ofPacked(PackedTrick.card(futureTrick, PackedTrick.size(futureTrick) - 1));
    }

    // gives the node of the best child, from which the card can be extracted for cardToPlay,
    // and the score can be extracted for chooseTrump
    public Node cardToPlayImpl(TurnState state, long hand) {
        SplittableRandom rng = new SplittableRandom(rngSeed);
        Node rootNode = new Node(state, hand, ownId.team(), ownId);
        while (rootNode.turns < iterations) { // runs <iteration> times the simulation
            Node[] path = new Node[36]; // path is as array (for performance reasons) of all nodes traveled to reach a terminal leaf
            path[0] = rootNode;
            int i;
            int ret = 0; // the value returned by addNode of a node
            for (i = 0; ret == 0; i++) { // when ret is 0, the path has not yet reached a terminal node
                ret = path[i].addNode(i + 1, path, hand, ownId); // repeat until a new node actually needs to be created
            }
            if (ret == -1) { // when a new node is created
                i++; // adjust the index of the last node (not incremented when created)
            }
            Node lastNode = path[i - 1]; // this is the node after which a random game is carried out
            TurnState lastTurnState = lastNode.turnState;
            long gameScore = finalRandomGameScore(lastTurnState, hand, rng); // score of a random game, carried out after the last node's turnState
            for (int j = 0; j < i; j++) { // adds the score to all nodes leading to the random game's initial node
                Node node = path[j];
                node.addToTotalPoints(PackedScore.turnPoints(gameScore, node.team));
            }
        }
        return rootNode.children[rootNode.bestChild(0.0)];
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        if (winningTeam == ownId.team()) {
            winning = true;
        }
    }

    // returns the cards that are allowed to be played, chosen according to the cards already in the MctsPlayer's hand
    private static long playableCards(TurnState state, long hand, PlayerId ownId) {
        if(state.nextPlayer() == ownId) {
            hand = PackedCardSet.intersection(state.packedUnplayedCards(), hand);
        } else {
            hand = PackedCardSet.difference(state.packedUnplayedCards(), hand);
        }
        return PackedTrick.playableCards(state.packedTrick(), hand);
    }

    // simulates a random game after a specified TurnState, and gives the final score
    private long finalRandomGameScore(TurnState turnState, long hand, SplittableRandom rng) {
        if (PackedTrick.isFull(turnState.packedTrick())) {
            turnState = turnState.withTrickCollected();
        }
        while (!turnState.isTerminal()) {
            long possiblePlayableCards = playableCards(turnState, hand, ownId);
            int card = PackedCardSet.get(possiblePlayableCards, rng.nextInt(PackedCardSet.size(possiblePlayableCards)));
            turnState = TurnState.ofPackedComponents(turnState.packedScore(), PackedCardSet.remove(turnState.packedUnplayedCards(), card), PackedTrick.withAddedCard(turnState.packedTrick(), card));
            if (PackedTrick.isFull(turnState.packedTrick())) {
                turnState = turnState.withTrickCollected();
            }
        }
        return turnState.packedScore();
    }

    // a Node represents a "state" of the game, a vertex in the "Monte Carlo Tree"
    private final static class Node {
        private TurnState turnState;
        private TeamId team; // the team is stored inside the node for performance reasons
        private int numChildren; // number of current existing children of the node
        private Node[] children; // an array of those children
        private long cardsWithoutNodes; // the playable cards not yet represented in a node
        private int totalPoints; // of the team leading to this node
        private int turns; // the number of turns randomly finished (= the number of nodes after this node)
        private double totalPointsOverTurns; // the average score of the team leading to this node,
        // stored directly to avoid computing it every time it is used
        private double oneOverSqrtTurns; // pre-computed to speed up the bestChild algorithm

        // Constructor of the node, which needs a turnState, a hand, a team, and a PlayerID.
        private Node(TurnState turnState, long hand, TeamId team, PlayerId ownId) {
            this.turnState = turnState;
            this.team = team;
            if (turnState.trick().isFull()) { // setting up cardWithoutNodes with the appropriate cards
                if (PackedTrick.isLast(turnState.packedTrick())) {
                    cardsWithoutNodes = PackedCardSet.EMPTY;
                } else {
                    TurnState emptiedTurnState = turnState.withTrickCollected();
                    cardsWithoutNodes = playableCards(emptiedTurnState, hand, ownId);
                }
            } else {
                cardsWithoutNodes = playableCards(turnState, hand, ownId);
            }
            numChildren = 0; // a newly-created node has no children
            children = new Node[PackedCardSet.size(cardsWithoutNodes)]; // declaration of the array of children,
            // with the maximum possible size
            totalPoints = 0;
            turns = 0;
            totalPointsOverTurns = 0;
            oneOverSqrtTurns = 0;
        }

        // Tries to add a new node, returns an int representing the situation:
        // 0 when there is no
        private int addNode(int i, Node[] path, long hand, PlayerId ownId) {
            if (turnState.isTerminal()) {
                return -2;
            }
            Node child = children[bestChild(40.0)];
            if (child == null) {
                int cardToBePlayed = PackedCardSet.get(cardsWithoutNodes, 0);
                TurnState newTurnState;
                TeamId team;
                if (PackedTrick.isFull(turnState.packedTrick())) {
                    // creation of a new turnState for the new node, after a full trick
                    newTurnState = TurnState.ofPackedComponents(turnState.withTrickCollected().packedScore(), // pkScore
                            PackedCardSet.remove(turnState.withTrickCollected().packedUnplayedCards(), cardToBePlayed), // pkUnplayedCards
                            PackedTrick.withAddedCard(turnState.withTrickCollected().packedTrick(), cardToBePlayed)); // pkTricks
                    team = turnState.withTrickCollected().nextPlayer().team();
                } else {
                    // creation of a new turnState for the new node, with an added card
                    newTurnState = TurnState.ofPackedComponents(turnState.packedScore(), // pkScore
                            PackedCardSet.remove(turnState.packedUnplayedCards(), cardToBePlayed), // pkUnplayedCards
                            PackedTrick.withAddedCard(turnState.packedTrick(), cardToBePlayed)); // pkTricks
                    team = turnState.nextPlayer().team(); // the team of the new node
                }
                Node newNode = new Node(newTurnState, hand, team, ownId);
                children[numChildren] = newNode;
                numChildren++;
                cardsWithoutNodes = PackedCardSet.remove(cardsWithoutNodes, PackedCardSet.get(cardsWithoutNodes, 0));
                path[i] = newNode;
                return -1;
            }
            path[i] = child;
            return 0;
        }

        // Method called to add a number of points to this node.
        private void addToTotalPoints(int points) {
            totalPoints += points;
            turns++;
            totalPointsOverTurns = (double)totalPoints / turns; // recomputing these values only once to improve performance
            oneOverSqrtTurns = 1.0 / Math.sqrt((double) turns);
        }

        // Returns the index of the most "promising" child.
        private int bestChild(double constant) {
            if (numChildren < children.length) { // if a child is not yet created, return its index
                return numChildren;
            }
            int bestCandidate = 0;
            double bestScore = 0;
            double score = 0;
            double numerator = constant * Math.sqrt(2.0 * Math.log((double) turns)); // pre-computing the numerator of the formula
            for (int i = 0; i < numChildren; i++) {
                Node child = children[i];
                score = child.childScore(numerator);
                if (score > bestScore) {
                    bestScore = score;
                    bestCandidate = i;
                }
            }
            return bestCandidate;
        }

        // Computes the score of a child according to the formula, with a given numerator.
        private double childScore(double numerator) {
            return totalPointsOverTurns + numerator * oneOverSqrtTurns;
        }
    }
}

