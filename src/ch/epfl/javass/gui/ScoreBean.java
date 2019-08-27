package ch.epfl.javass.gui;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.EnumMap;

/**
 * Bean containing all of the information about the score.
 *
 * @author Pablo Stebler (302328)
 * @author Marc Lundwall (297665)
 */
public final class ScoreBean {
    private EnumMap<TeamId, SimpleIntegerProperty> turnPoints;
    private EnumMap<TeamId, SimpleIntegerProperty> gamePoints;
    private EnumMap<TeamId, SimpleIntegerProperty> totalPoints;
    private SimpleObjectProperty<TeamId> winningTeam;

    /**
     * Constructor of this ScoreBean, setting up all the enum maps of information
     */
    public ScoreBean() {
        turnPoints = new EnumMap<>(TeamId.class);
        gamePoints = new EnumMap<>(TeamId.class);
        totalPoints = new EnumMap<>(TeamId.class);
        for (TeamId teamId : TeamId.ALL) {
            turnPoints.put(teamId, new SimpleIntegerProperty());
            gamePoints.put(teamId, new SimpleIntegerProperty());
            totalPoints.put(teamId, new SimpleIntegerProperty());
        }
        winningTeam = new SimpleObjectProperty<TeamId>();
    }

    /**
     * Getter for the turn points property.
     *
     * @param teamId the team from which the turn points are given
     * @return the corresponding property
     */
    public ReadOnlyIntegerProperty turnPointsProperty(TeamId teamId) {
        return turnPoints.get(teamId);
    }

    /**
     * Setter for the turn points property.
     *
     * @param teamId the team from which the turn points are set
     * @param newTurnPoints the new amount of turn points
     */
    public void setTurnPoints(TeamId teamId, int newTurnPoints) {
        turnPoints.get(teamId).set(newTurnPoints);
    }

    /**
     * Getter for the game points property.
     *
     * @param teamId the team from which the game points are given
     * @return the corresponding property
     */
    public ReadOnlyIntegerProperty gamePointsProperty(TeamId teamId) {
        return gamePoints.get(teamId);
    }

    /**
     * Setter for the game points property.
     *
     * @param teamId the team from which the game points are set
     * @param newGamePoints the new amount of game points
     */
    public void setGamePoints(TeamId teamId, int newGamePoints) {
        gamePoints.get(teamId).set(newGamePoints);
    }

    /**
     * Getter for the total points property.
     *
     * @param teamId the team from which the total points are given
     * @return the corresponding property
     */
    public ReadOnlyIntegerProperty totalPointsProperty(TeamId teamId) {
        return totalPoints.get(teamId);
    }

    /**
     * Setter for the total points property.
     *
     * @param teamId the team from which the total points are set
     * @param newTotalPoints the new amount of total points
     */
    public void setTotalPoints(TeamId teamId, int newTotalPoints) {
        totalPoints.get(teamId).set(newTotalPoints);
    }

    /**
     * Getter for the winning team property.
     *
     * @return the corresponding property
     */
    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() {
        return winningTeam;
    }

    /**
     * Setter for the winning team property.
     *
     * @param winningTeam the team who won
     */
    public void setWinningTeam(TeamId winningTeam) {
        this.winningTeam.set(winningTeam);
    }
}
