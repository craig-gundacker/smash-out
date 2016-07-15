package smash;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
Holds the game controls and their associated event handlers.  Contains game data
for use when saving game state.
*/
public class Game
{
    private static RadioButton rdbAttackOn = new RadioButton("On");
    private final RadioButton rdbAttackOff = new RadioButton("Off");
    protected static Stage stage = new Stage();
    private final Scene scene;
    private final String css = this.getClass().getResource("resources/style_sheet.css").toExternalForm();
    private String userName = null;
    private final ScoreBoard scoreBoard;
    private final BallPane ballPane;
    private final double SCENE_WIDTH = 1000;
    private final double SCENE_HEIGHT = 675;
    private final double MAX_SPEED = 20;
    private final ScrollBar sbSpeed = new ScrollBar();
    protected static final double INIT_SPEED = 6;
    private static final String HELP_MESSAGE = "Add a ball to start play (add up to ten balls).  "
            + "Use the mouse to control the paddle. Keep the balls up in the air smashing bricks. "
            + "Adjust ball speed and paddle size.  Select attack mode and avoid the lighting bolts. "
            + "Earn enough points and you make the top scorer list.  Save game if you want to come back "
            + "to it later.  That's about it.";
    
    //Creates a new game
    public Game()
    {        
        BorderPane root = new BorderPane();
        scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        scene.getStylesheets().add(css);
        
        scoreBoard = new ScoreBoard();
        boolean autoStart = true;
        ballPane = new BallPane(scene, scoreBoard, autoStart);
        ballPane.setUpBricks();
        ballPane.addPaddle(scene.getWidth()/2, scene.getHeight());
        sbSpeed.setValue(INIT_SPEED);
        rdbAttackOn.setSelected(true);
        
        root.setCenter(ballPane);
        root.setBottom(createGameDataPane());
        stage.setTitle("SmashOut");
        stage.setResizable(false);
        stage.setScene(scene);
    }
    
    //Loads a saved game
    public Game(SavedGame savedGame)
    {
        BorderPane root = new BorderPane();
        scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT); 
        scene.getStylesheets().add(css);
        
        userName = savedGame.getName();
        int ptsPerBrick = savedGame.getPointsPerBrick();
        int totalPts = savedGame.getTotalPoints();
        int numLivesLeft = savedGame.getNumLivesRemaining();
        
        scoreBoard = new ScoreBoard(userName, ptsPerBrick, totalPts, numLivesLeft);
        boolean autoStart = false;
        
        ballPane = new BallPane(scene, scoreBoard, autoStart);
        ballPane.restoreBricks(savedGame.getBrickGrid());
        ballPane.restorePaddle(savedGame.getPaddleData());
        ballPane.restoreBalls(savedGame.getListRestoredBalls());
        ballPane.restoreBolts(savedGame.getListRestoredBolts());
        sbSpeed.setValue(savedGame.getBallSpeedData());
        rdbAttackOn.setSelected(savedGame.getAttackMode());
        if (!rdbAttackOn.isSelected())
        {
            rdbAttackOff.setSelected(true);
        }
            
        root.setCenter(ballPane);
        root.setBottom(createGameDataPane());
        stage.setTitle("SmashOut");
        stage.setResizable(false);
        stage.setScene(scene);        
    }
    
    public void show()
    {
        stage.show();
    }
    
    public void showAndWait()
    {
        stage.show();
        ButtonType btnOk = new ButtonType("OK");
        Alert confirmStart = new Alert(AlertType.CONFIRMATION);
        confirmStart.setTitle("Smash");
        confirmStart.setHeaderText(null);
        confirmStart.setContentText("Click OK to start");
        confirmStart.getButtonTypes().setAll(btnOk);
        Optional<ButtonType> result = confirmStart.showAndWait();
        try
        {
            if (result.get() == btnOk)
            {
                ballPane.play();
            }
        }
        catch (NoSuchElementException ex)
        {
            ballPane.play();
        }
    }
    
    private Node createGameDataPane()
    {        
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(createButtonsPane());
        borderPane.setCenter(createControlsPane());
        borderPane.setBottom(scoreBoard);
        return borderPane;
    }
    
    private Node createControlsPane()
    {
        Label lblAdjustSpeed = new Label("Adjust Ball Speed: ");
        sbSpeed.setMax(MAX_SPEED);
        sbSpeed.setMin(0);
        sbSpeed.setMinHeight(30);
        sbSpeed.setMaxWidth(scene.getWidth()/4);
        ballPane.rateProperty().bind(sbSpeed.valueProperty());  

        Label lblReSizePaddle = new Label("Resize Paddle: ");
        Button btnIncPaddle = new Button("+");
        btnIncPaddle.setOnAction(e -> ballPane.makePaddleBigger());
        Button btnDecPaddle = new Button("-");
        btnDecPaddle.setOnAction(e -> ballPane.makePaddleSmaller());
        
        Label lblAttack = new Label("Attack Mode: ");
        ToggleGroup tgAttackMode = new ToggleGroup();
        rdbAttackOn.setToggleGroup(tgAttackMode);
        rdbAttackOff.setToggleGroup(tgAttackMode);        
               
        Separator sepVerOne = new Separator(Orientation.VERTICAL);
        sepVerOne.getStyleClass().add("vertical-separator");
        sepVerOne.getStyleClass().add("line");
        Separator sepVerTwo = new Separator(Orientation.VERTICAL);
        sepVerTwo.getStyleClass().add("vertical-separator");
        sepVerTwo.getStyleClass().add("line");

        Hyperlink hlHelp = new Hyperlink("How to Play");
        hlHelp.setPadding(new Insets(0, 50, 0, 50));
        hlHelp.setId("hyperlink");
        hlHelp.setOnAction(e ->
        {
            displayHelpDialog();
        });
        
        HBox hbControls = new HBox(15);
        hbControls.getChildren().addAll(lblAdjustSpeed, sbSpeed, sepVerOne, lblReSizePaddle, btnIncPaddle, 
                                        btnDecPaddle, sepVerTwo, lblAttack, rdbAttackOn, rdbAttackOff, hlHelp);
        hbControls.setPadding(new Insets(0, 0, 5, 0));
        hbControls.setAlignment(Pos.CENTER);
        
        return hbControls;
    }
    
    private Node createButtonsPane()
    {
        Separator sepHor = new Separator(Orientation.HORIZONTAL);
        sepHor.setMinWidth(scene.getWidth());
        sepHor.getStyleClass().add("line");
        sepHor.getStyleClass().add("horizontal-separator");
        HBox hbSeparator = new HBox();
        hbSeparator.setAlignment(Pos.CENTER);
        hbSeparator.getChildren().add(sepHor);
        
        Button btnAdd = new Button("Add Ball");
        btnAdd.setOnAction(e -> ballPane.addBall());    
        
        Button btnSubtract = new Button("Delete Ball");
        btnSubtract.setOnAction(e -> ballPane.removeBall());   
        
//        Button btnNewGame = new Button("New Game");
//        btnNewGame.setOnAction(e ->
//        {
//            boolean quitGame = false;
//            startSelectionProcess(quitGame);
//        });
        
        Button btnQuit = new Button("Quit Game");
        btnQuit.setOnAction(e -> 
        {      
            boolean quitGame = true;
            processGameQuit(quitGame);
        });
         
        HBox hbButtons = new HBox(7.5);
        hbButtons.getChildren().addAll(btnAdd, btnSubtract, btnQuit);
        hbButtons.setAlignment(Pos.CENTER);
        hbButtons.setPadding(new Insets(0, 0, 5, 0));

        VBox vb = new VBox(10);
        vb.getChildren().addAll(hbSeparator, hbButtons);
        return vb;
    }
    
    public void displayHelpDialog()
    {
        ballPane.pause();
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText(null);
        alert.setContentText(HELP_MESSAGE);
        Optional<ButtonType> result = alert.showAndWait();
        try
        {
            if (result.get().equals(ButtonType.OK))
            {
                ballPane.play();
            }
        }
        catch(NoSuchElementException ex)
        {
           ballPane.play(); 
        }
    }
    
    public void processGameQuit(boolean quitGame)
    {
        ballPane.pause();
        ButtonType result = confirmStopPlay();
        if (result.equals(ButtonType.OK))
        {
            try
            {
                GameIO.processQuit(this, quitGame);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        else
        {
            ballPane.play();
        }        
    }
    
    private ButtonType confirmStopPlay()
    {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure?");
        alert.showAndWait();
        ButtonType result = alert.getResult();
        return result;
    }
    
    public void startNewGame()
    {
        ballPane.clearBallList();
        ballPane.clearBrickList();
        ballPane.getChildren().clear();
        scoreBoard.reset();
        ballPane.clearBalls();
        ballPane.setUpBricks();
        ballPane.addPaddle(scene.getWidth()/2, scene.getHeight());
        ballPane.play();        
    }
    
    public void quitGame()
    {
        stage.close();
        LoaderStage loader = new LoaderStage();
        loader.start(new Stage());
    }
    
    public static boolean isAttackSelected()
    {
        return rdbAttackOn.isSelected();
    }
    
    public String getUserName()
    {
        return userName;
    }
    
    public int getNumBalls()
    {
        return ballPane.getNumBalls();
    }
    
    public int getPointsPerBrick()
    {
        return scoreBoard.getPointsPerBrick();
    }
    
    public int getTotalPoints()
    {
        return scoreBoard.getTotalPts();
    }
    
    public int getNumLives()
    {
        return scoreBoard.getNumLives();
    }
    
    public List<Ball> getListBall()
    {
        return ballPane.getListBall();
    }
    
    public List<Bolt> getListBolt()
    {
        return ballPane.getListBolt();
    }
    
    public boolean[][] getGridBrick()
    {
        return ballPane.getBrickGrid();
    }
    
    public Paddle getPaddle()
    {
        return ballPane.getPaddle();
    }
    
    public double getBallSpeed()
    {
        return sbSpeed.getValue();
    }
}