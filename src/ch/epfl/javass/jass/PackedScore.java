package ch.epfl.javass.jass;

/**
 * Static methods to manipulate scores packed into a long.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public final class PackedScore {
    public final static long INITIAL = 0L;

    // this private constructor makes the class non-instantiable
    private PackedScore() {}

    /**
     * Checks if the given packed score corresponds to an actual score.
     *
     * @param pkScore the score packed into a long
     * @return true if the packed score is valid, false if not
     */
    public static boolean isValid(long pkScore) {
        int turnTricks1 = turnTricks(pkScore, TeamId.TEAM_1);
        int turnPoints1 = turnPoints(pkScore, TeamId.TEAM_1);
        int gamePoints1 = gamePoints(pkScore, TeamId.TEAM_1);
        int turnTricks2 = turnTricks(pkScore, TeamId.TEAM_2);
        int turnPoints2 = turnPoints(pkScore, TeamId.TEAM_2);
        int gamePoints2 = gamePoints(pkScore, TeamId.TEAM_2);
        long unusedBits1 = pkScore & 0xff00_0000L;
        long unusedBits2 = pkScore & 0xff00_0000_0000_0000L;

        return turnTricksIsValid(turnTricks1) && turnTricksIsValid(turnTricks2)
            && turnPointsIsValid(turnPoints1) && turnPointsIsValid(turnPoints2)
            && gamePointsIsValid(gamePoints1) && gamePointsIsValid(gamePoints2)
            && unusedBits1 == 0 && unusedBits2 == 0;
    }
    
    private static boolean turnTricksIsValid(int turnTricks) {
        return turnTricks >= 0 && turnTricks <= Jass.TRICKS_PER_TURN;
    }
    
    private static boolean turnPointsIsValid(int turnPoints) {
        return turnPoints >= 0 && turnPoints <= 257;
    }
    
    private static boolean gamePointsIsValid(int gamePoints) {
        return gamePoints >= 0 && gamePoints <= 2000;
    }

    /**
     * Returns the packed version of a score.
     *
     * @param turnTricks1 the number of tricks gained by the first team in the current turn
     * @param turnPoints1 the number of points scored by the first team in the current turn
     * @param gamePoints1 the number of points scored by the first team in the game, excluding the current turn
     * @param turnTricks2 the number of tricks gained by the second team in the current turn
     * @param turnPoints2 the number of points scored by the second team in the current turn
     * @param gamePoints2 the number of points scored by the second team in the game, excluding the current turn
     * @return the corresponding packed version
     */
    public static long pack(int turnTricks1, int turnPoints1, int gamePoints1, int turnTricks2, int turnPoints2, int gamePoints2) {
        return ((long) gamePoints2 << 45) | ((long) turnPoints2 << 36) | ((long) turnTricks2 << 32) | (gamePoints1 << 13) | (turnPoints1 << 4) | turnTricks1;
    }

    /**
     * Returns the number of tricks gained by a team in the current turn.
     *
     * @param pkScore the packed version of the score
     * @param t the team
     * @return the number of tricks
     */
    public static int turnTricks(long pkScore, TeamId t) {
        if (t == TeamId.TEAM_1) {
            return (int) (pkScore & 0xf);
        }
        return (int) ((pkScore & 0xf_0000_0000L) >> 32);
    }

    /**
     * Returns the number of points scored by a team in the current turn.
     *
     * @param pkScore the packed version of the score
     * @param t the team
     * @return the number of points
     */
    public static int turnPoints(long pkScore, TeamId t) {
        if (t == TeamId.TEAM_1) {
            return (int) ((pkScore & 0x1ff0) >> 4);
        }
        return (int) ((pkScore & 0x1ff0_0000_0000L) >> 36);
    }

    /**
     * Returns the number of points scored by a team in the game, excluding the current turn.
     *
     * @param pkScore the packed version of the score
     * @param t the team
     * @return the number of points
     */
    public static int gamePoints(long pkScore, TeamId t) {
        if (t == TeamId.TEAM_1) {
            return (int) ((pkScore & 0xff_e000) >> 13);
        }
        return (int) ((pkScore & 0xff_e000_0000_0000L) >> 45);
    }

    /**
     * Returns the number of points scored by a team in the game, including the current turn.
     *
     * @param pkScore the packed version of the score
     * @param t the team
     * @return the number of points
     */
    public static int totalPoints(long pkScore, TeamId t) {
        int points = gamePoints(pkScore, t) + turnPoints(pkScore, t);
        return points;
    }

    /**
     * Adds the result of an additional trick to the score.
     * If one of the team has gained all of the tricks in the turn, this method also adds the match additional points.
     *
     * @param pkScore the packed version of the score
     * @param winningTeam the team that gained the trick
     * @param trickPoints the number of points in the trick
     * @return the modified packed score
     */
    public static long withAdditionalTrick(long pkScore, TeamId winningTeam, int trickPoints) {
        pkScore = increaseTurnTricks(pkScore, winningTeam);
        pkScore = increaseTurnPoints(pkScore, winningTeam, trickPoints);
        for (TeamId teamId : TeamId.ALL) {
            if (turnTricks(pkScore, teamId) == Jass.TRICKS_PER_TURN) {
                pkScore = increaseTurnPoints(pkScore, teamId, Jass.MATCH_ADDITIONAL_POINTS);
                break;
            }
        }
        return pkScore;
    }

    /**
     * Returns the initial score of the next turn.
     * The number of points scored by each team in the current turn is added to their total number of points.
     * The number of gained tricks and the number of points of the current turn are then set to 0.
     *
     * @param pkScore the packed version of the score
     * @return the modified packed score
     */
    public static long nextTurn(long pkScore) {
        int totalPoints1 = totalPoints(pkScore, TeamId.TEAM_1);
        int totalPoints2 = totalPoints(pkScore, TeamId.TEAM_2);
        pkScore = pack(0, 0, totalPoints1, 0, 0, totalPoints2);
        return pkScore;
    }

    /**
     * Returns a text representation of the current score.
     *
     * @param pkScore the packed version of the score
     * @return the text representation
     */
    public static String toString(long pkScore) {
        String score1 = "(" + turnTricks(pkScore, TeamId.TEAM_1) + "," + turnPoints(pkScore, TeamId.TEAM_1) + "," + gamePoints(pkScore, TeamId.TEAM_1) + ")";
        String score2 = "(" + turnTricks(pkScore, TeamId.TEAM_2) + "," + turnPoints(pkScore, TeamId.TEAM_2) + "," + gamePoints(pkScore, TeamId.TEAM_2) + ")";
        return score1 + "/" + score2;
    }

    private static long increaseTurnTricks(long pkScore, TeamId winningTeam) {
        if(winningTeam == TeamId.TEAM_1) {
            return pkScore + 1L;
        }
        return pkScore + (1L << 32);
    }

    private static long increaseTurnPoints(long pkScore, TeamId winningTeam, int trickPoints) {
        if(winningTeam == TeamId.TEAM_1) {
            return pkScore + ((long) trickPoints << 4);
        }
        return pkScore + ((long) trickPoints << 36);
    }
}
