package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;

/**
 * Represents the current score of both team of a jass game.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public final class Score {
    public static final Score INITIAL = new Score(PackedScore.INITIAL);
    
    private long packed;
    
    private Score(long packed) {
        this.packed = packed;
    }
    
    /**
     * Creates a new score from its packed version.
     *
     * @param packed the packed version
     * @return the corresponding score
     */
    public static Score ofPacked(long packed) {
        Preconditions.checkArgument((PackedScore.isValid(packed)));
        return new Score(packed);
    }
    
    /**
     * Returns the packed version of the score.
     *
     * @return the packed version of the score
     */
    public long packed() {
        return packed;
    }

    /**
     * Returns the number of tricks gained by a team in the current turn.
     *
     * @param t the team
     * @return the number of tricks
     */
    public int turnTricks(TeamId t) {
        return PackedScore.turnTricks(packed, t);
    }

    /**
     * Returns the number of points scored by a team in the current turn.
     *
     * @param t the team
     * @return the number of points
     */
    public int turnPoints(TeamId t) {
        return PackedScore.turnPoints(packed, t);
    }

    /**
     * Returns the number of points scored by a team in the game, excluding the current turn.
     *
     * @param t the team
     * @return the number of points
     */
    public int gamePoints(TeamId t) {
        return PackedScore.gamePoints(packed, t);
    }

    /**
     * Returns the number of points scored by a team in the game, including the current turn.
     * @param t the team
     * @return the number of points
     */
    public int totalPoints(TeamId t) {
        return PackedScore.totalPoints(packed, t);
    }

    /**
     * Adds the result of an additional trick to the score.
     * If one of the team has gained all of the tricks in the turn, this method also adds the match additional points.
     *
     * @param winningTeam the team that gained the trick
     * @param trickPoints the number of points in the trick
     * @return the modified score
     */
    public Score withAdditionalTrick(TeamId winningTeam, int trickPoints) {
        Preconditions.checkArgument(trickPoints >= 0);
        return new Score(PackedScore.withAdditionalTrick(packed, winningTeam, trickPoints));
    }
    
    /**
     * Returns the initial score of the next turn.
     * The number of points scored by each team in the current turn is added to their total number of points.
     * The number of gained tricks and the number of points of the current turn are then set to 0.
     *
     * @return the modified score
     */
    public Score nextTurn() {
        return new Score(PackedScore.nextTurn(packed));
    }
    
    @Override
    public boolean equals(Object that0) {
        if (that0 == null) {
            return false;
        }
        return packed == ((Score) that0).packed;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(packed);
    }
    
    @Override
    public String toString() {
        return PackedScore.toString(packed);
    }
}
