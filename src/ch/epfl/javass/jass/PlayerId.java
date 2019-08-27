package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a player of a jass game.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public enum PlayerId {
    PLAYER_1,
    PLAYER_2,
    PLAYER_3,
    PLAYER_4;
    
    public static final List<PlayerId> ALL = Collections.unmodifiableList(Arrays.asList(values()));
    public static final int COUNT = 4;
    
    /**
     * Returns the team of the player.
     * PLAYER_1 and PLAYER_3 are in TEAM_1.
     * PLAYER_2 and PLAYER_4 are in TEAM_2.
     *
     * @return the team of the player
     */
    public TeamId team() {
        if (this == PLAYER_1 || this == PLAYER_3) {
            return TeamId.TEAM_1;
        }
        return TeamId.TEAM_2;
    }
}
