package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a team of a jass game.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public enum TeamId {
    TEAM_1,
    TEAM_2;
    
    public static final List<TeamId> ALL = Collections.unmodifiableList(Arrays.asList(values()));
    public static final int COUNT = 2;
    
    /**
     * Returns the other team.
     *
     * @return the other team
     */
    public TeamId other() {
        if (this == TEAM_1) {
            return TEAM_2;
        }
        return TEAM_1;
    }
}
