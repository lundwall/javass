package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Creates a graphical interface for a selected player.
 *
 * @author Pablo Stebler (302328)
 * @author Marc Lundwall (297665)
 */
public final class GraphicalPlayer {
    private static final double ACTIVE_CARD_OPACITY = 1;
    private static final double INACTIVE_CARD_OPACITY = 0.2;
    private Scene scene;
    private ArrayBlockingQueue<Card> cardQueue;
    private ArrayBlockingQueue<Integer> trumpQueue;
    private ObservableMap<Color, Image> colorMap;

    /**
     * Constructor for this graphical player.
     *
     * @param player the player on which the interface is centered, and that is controlled by the interface
     * @param playerNames a map with the names of the players
     * @param scoreBean a bean with updated information concerning the actual score
     * @param trickBean a bean with updated information concerning the current trick
     * @param trumpBean a bean with updated information concerning the current trump selection
     * @param handBean a bean with updated information concerning the hand of the player
     * @param trumpQueue a queue, which will contain the trump chosen to be played by the interface's player
     * @param cardQueue a queue, which will contain the card chosen to be played by the interface's player
     */
    public GraphicalPlayer(PlayerId player,
                           Map<PlayerId, String> playerNames,
                           TrumpBean trumpBean,
                           ScoreBean scoreBean,
                           TrickBean trickBean,
                           HandBean handBean,
                           ArrayBlockingQueue<Integer> trumpQueue,
                           ArrayBlockingQueue<Card> cardQueue) {
        this.trumpQueue = trumpQueue;
        this.cardQueue = cardQueue;

        colorMap = FXCollections.observableHashMap();
        for (Color color : Color.ALL) {
            colorMap.put(color, new Image("/trump_" + color.ordinal() + ".png"));
        }

        // creating score, trick, trump selection and hand panes
        GridPane scorePane = createScorePane(playerNames, scoreBean);
        GridPane trickPane = createTrickPane(player, playerNames, trickBean);
        GridPane trumpPane = createTrumpPane(trumpBean);
        StackPane centerPane = new StackPane(trickPane, trumpPane);
        HBox playerCardsPane = createHandPane(handBean);

        // putting together score, trick, and hand into a "main pane"
        BorderPane mainPane = new BorderPane();
        mainPane.setTop(scorePane);
        mainPane.setCenter(centerPane);
        mainPane.setBottom(playerCardsPane);

        // stacking the victory pane on top of "root"
        StackPane victoryPane = createVictoryPane(playerNames, scoreBean);
        StackPane root = new StackPane(mainPane, victoryPane);

        scene = new Scene(root);
    }

    // create the score pane of the graphical user interface
    private GridPane createScorePane(Map<PlayerId, String> playerNames,
                                     ScoreBean scoreBean) {
        GridPane scorePane = new GridPane();

        // set the score elements of EACH team
        for (TeamId teamId : TeamId.ALL) {
            // names of the player of the team
            String n0 = playerNames.get(PlayerId.ALL.get(teamId.ordinal()));
            String n1 = playerNames.get(PlayerId.ALL.get(teamId.ordinal() + 2));
            Text names = new Text(n0 + " et " + n1 + " : ");
            // bind turnPoints to text (from scoreBean)
            Text turnPoints = new Text();
            turnPoints.textProperty().bind(Bindings.convert(scoreBean.turnPointsProperty(teamId)));
            // detect any change in turnPoints and bind it to text
            SimpleStringProperty diffPointsProperty = new SimpleStringProperty();
            scoreBean.turnPointsProperty(teamId).addListener((o, oV, nV) -> {
                int diff = nV.intValue() - oV.intValue();
                diffPointsProperty.set(diff>0 ? " (+" + diff + ")" : " (+0)");
            });
            Text diffPoints = new Text();
            diffPoints.textProperty().bind(diffPointsProperty);
            Text total = new Text(" / Total : ");
            // bind the gamePoints to text
            Text gamePoints = new Text();
            gamePoints.textProperty().bind(Bindings.convert(scoreBean.gamePointsProperty(teamId)));
            // add row to score pane with all these changing texts
            scorePane.addRow(teamId.ordinal(), names, turnPoints, diffPoints, total, gamePoints);
        }

        // set the appropriate style of the score pane
        scorePane.setStyle(""
            + "-fx-font: 16 Optima;"
            + "-fx-background-color: lightgray;"
            + "-fx-padding: 5px;"
            + "-fx-alignment: center;");

        return scorePane;
    }

    // create a box for one of the cards in the middle (current trick)
    private VBox createCardVBox(Map<PlayerId, String> playerNames,
                                TrickBean trickBean,
                                ObservableMap<Card, Image> map,
                                int playerIndex,
                                boolean textOnTop) {

        PlayerId playerId = PlayerId.ALL.get(playerIndex);
        Text name = new Text(playerNames.get(playerId));
        name.setStyle("-fx-font: 14 Optima;");
        // create appropriate rectangle with a specified style
        Rectangle rectangle = new Rectangle();
        rectangle.visibleProperty().bind(trickBean.winningPlayerProperty().isEqualTo(playerId));
        rectangle.setWidth(120);
        rectangle.setHeight(180);
        rectangle.setEffect(new GaussianBlur(4));
        rectangle.setStyle(""
            + "-fx-arc-width: 20;"
            + "-fx-arc-height: 20;"
            + "-fx-fill: transparent;"
            + "-fx-stroke: lightpink;"
            + "-fx-stroke-width: 5;"
            + "-fx-opacity: 0.5;");
        // create the image itself of the card
        ImageView image = new ImageView();
        image.imageProperty().bind(Bindings.valueAt(map, Bindings.valueAt(trickBean.trick(), playerId)));
        image.setFitWidth(120);
        image.setFitHeight(180);
        // stack the image on top of the rectangle
        StackPane stack = new StackPane(rectangle, image);
        // put the name of the player above or under the image, depending on the card's position
        VBox cardVBox;
        if (textOnTop) {
            cardVBox = new VBox(name, stack);
        } else {
            cardVBox = new VBox(stack, name);
        }
        cardVBox.setStyle(""
            + "-fx-alignment: center;"
            + "-fx-padding: 5px;");
        return cardVBox;
    }

    // create the middle portion of the interface : the cards of the current trick
    private GridPane createTrickPane(PlayerId player,
                                     Map<PlayerId, String> playerNames,
                                     TrickBean trickBean) {
        // maps of all the images for cards and trump colors
        ObservableMap<Card, Image> cardMap = FXCollections.observableHashMap();
        // initialise these maps
        for (Color color : Color.ALL) {
            for (Rank rank : Rank.ALL) {
                cardMap.put(Card.of(color, rank),
                            new Image("/card_" + color.ordinal() + "_" + rank.ordinal() + "_240.png"));
            }
        }
        // setting the trump color in the middle
        ImageView trump = new ImageView();
        trump.imageProperty().bind(Bindings.valueAt(colorMap, trickBean.trumpProperty()));
        trump.setFitWidth(101);
        trump.setFitHeight(101);
        // setting all of the cards one by one
        GridPane trickPane = new GridPane();
        VBox leftCard = createCardVBox(playerNames,
                trickBean,
                cardMap,
                (player.ordinal() + 3) % 4,
                true);
        VBox topCard = createCardVBox(playerNames,
                trickBean,
                cardMap,
                (player.ordinal() + 2) % 4,
                true);
        VBox bottomCard = createCardVBox(playerNames,
                trickBean,
                cardMap,
                player.ordinal(),
                false);
        VBox rightCard = createCardVBox(playerNames,
                trickBean,
                cardMap,
                (player.ordinal() + 1) % 4,
                true);
        // adding all of the above together and setting style
        trickPane.add(leftCard, 0, 0, 1, 3);
        trickPane.add(topCard, 1, 0);
        trickPane.add(trump, 1, 1);
        trickPane.add(bottomCard, 1, 2);
        trickPane.add(rightCard, 2, 0, 1, 3);
        trickPane.setStyle(""
            + "-fx-background-color: whitesmoke;"
            + "-fx-padding: 5px;"
            + "-fx-border-width: 3px 0;"
            + "-fx-border-style: solid;"
            + "-fx-border-color: gray;"
            + "-fx-alignment: center;");
        GridPane.setHalignment(trump, HPos.CENTER);
        return trickPane;
    }

    // create a pane for the selection of the trump color
    private GridPane createTrumpPane(TrumpBean trumpBean) {
        GridPane trumpPane = new GridPane();
        trumpPane.visibleProperty().bind(trumpBean.mustChooseTrump());
        for (Color color : Color.ALL) {
            ImageView image = new ImageView(colorMap.get(color));
            image.setFitWidth(101);
            image.setFitHeight(101);
            image.setOnMouseClicked(event -> {
                trumpQueue.add(color.ordinal());
            });
            trumpPane.add(image, color.ordinal(), 0);
        }
        Text passText = new Text("Passer");
        passText.visibleProperty().bind(trumpBean.canPass());
        passText.setOnMouseClicked(event -> {
            trumpQueue.add(4);
        });
        trumpPane.add(passText, 1, 1, 2, 1);
        trumpPane.setStyle(""
                + "-fx-background-color: whitesmoke;"
                + "-fx-padding: 5px;"
                + "-fx-font: 24 Optima;"
                + "-fx-alignment: center;");
        GridPane.setHalignment(passText, HPos.CENTER);
        return trumpPane;
    }

    // create the horizontal box where the hand of the player is visible (and can be interacted with)
    private HBox createHandPane(HandBean handBean) {
        HBox handPane = new HBox();
        handPane.setStyle(""
            + "-fx-background-color: lightgray;"
            + "-fx-spacing: 5px;"
            + "-fx-padding: 5px;"
            + "-fx-alignment: center;");
        // setting the map with every card's image
        ObservableMap<Card, Image> cardImageMap = FXCollections.observableHashMap();
        for (Color color : Color.ALL) {
            for (Rank rank : Rank.ALL) {
                Image image = new Image("/card_" + color.ordinal() + "_" + rank.ordinal() + "_160.png");
                cardImageMap.put(Card.of(color, rank), image);
            }
        }
        // setting every one of the cards in the horizontal box
        for (int i = 0; i<9; ++i) {
            final int finalInt = i;
            ImageView image = new ImageView();
            image.imageProperty().bind(Bindings.valueAt(cardImageMap, Bindings.valueAt(handBean.hand(), finalInt)));
            image.setFitWidth(80);
            image.setFitHeight(120);
            // making them clickable to choose the card to play
            image.setOnMouseClicked(event -> {
                cardQueue.add(handBean.hand().get(finalInt));
            });
            // making them faded to indicate that they are not playable
            BooleanProperty isPlayable = new SimpleBooleanProperty();
            isPlayable.bind(Bindings.createBooleanBinding(() ->
                    handBean.playableCards().contains(handBean.hand().get(finalInt)),
                    handBean.playableCards(),
                    handBean.hand()));
            image.opacityProperty().bind(Bindings
                    .when(isPlayable).then(ACTIVE_CARD_OPACITY)
                    .otherwise(INACTIVE_CARD_OPACITY));
            image.disableProperty().bind(isPlayable.not());
            handPane.getChildren().add(image);
        }
        return handPane;
    }

    // creating a pane with the victory messages
    private StackPane createVictoryPane(Map<PlayerId, String> playerNames, ScoreBean scoreBean) {
        // create the victory pane of Team 1
        BorderPane victoryPane1 = new BorderPane();
        String names1 = playerNames.get(PlayerId.PLAYER_1) + " et " + playerNames.get(PlayerId.PLAYER_3);
        Text text1 = new Text();
        text1.textProperty().bind(Bindings.format(names1
                + " ont gagné avec %d points contre %d.",
                scoreBean.totalPointsProperty(TeamId.TEAM_1),
                scoreBean.totalPointsProperty(TeamId.TEAM_2)));
        victoryPane1.setCenter(text1);
        victoryPane1.setStyle(""
            + "-fx-font: 16 Optima;"
            + "-fx-background-color: white;");

        //create the victory pane of Team 2
        BorderPane victoryPane2 = new BorderPane();
        String names2 = playerNames.get(PlayerId.PLAYER_2) + " et " + playerNames.get(PlayerId.PLAYER_4);
        Text text2 = new Text();
        text2.textProperty().bind(Bindings.format(names2
                + " ont gagné avec %d points contre %d.",
                scoreBean.totalPointsProperty(TeamId.TEAM_2),
                scoreBean.totalPointsProperty(TeamId.TEAM_1)));
        victoryPane2.setCenter(text2);
        victoryPane2.setStyle(""
            + "-fx-font: 16 Optima;"
            + "-fx-background-color: white;");

        // only show one of the victory panes when the relevant team is the winner
        victoryPane1.visibleProperty().bind(Bindings.equal(scoreBean.winningTeamProperty(), TeamId.TEAM_1));
        victoryPane2.visibleProperty().bind(Bindings.equal(scoreBean.winningTeamProperty(), TeamId.TEAM_2));

        // stack both victory panes together
        StackPane victoryPane = new StackPane(victoryPane1, victoryPane2);
        // let the user click through these panes to select his card
        victoryPane.setPickOnBounds(false);
        return victoryPane;
    }

    /**
     * Returns the stage of the graphical user interface.
     *
     * @return the stage, with the scene set
     */
    public Stage createStage() {
        Stage stage = new Stage();
        stage.setScene(scene);
        return stage;
    }
}
