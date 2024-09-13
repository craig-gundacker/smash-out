package smash;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;


public class ScoreBoard extends HBox
{
    private String name = null;
    private final Label lblPtsPerBrick;
    private int ptsPerBrick;
    private final Label lblTotalPts;
    private int totalPts;
    private final Label lblNumLives;
    private int numLives;
    private final int LIVES = 5;
    private final static int BASE_PTS_PER_BRICK = 100;
    private final static int MULTIPLIER = 10;
    
    /*
    Constructs new score board
    */
    public ScoreBoard()
    {
        ptsPerBrick = BASE_PTS_PER_BRICK;
        lblPtsPerBrick = new Label("Points Per Brick: " + ptsPerBrick);
        totalPts = 0;
        lblTotalPts = new Label("Total Points: " + totalPts);
        numLives = LIVES;
        lblNumLives = new Label("Number of Lives: " + numLives);
        this.setPadding(new Insets(0, 0, 10, 0));
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(lblPtsPerBrick, lblTotalPts, lblNumLives);
    }
    
    /*
    Restores scoreboard
    */
    public ScoreBoard(String name, int ptsPerBrick, int totalPts, int numLives)
    {
        this.name = name;
        this.ptsPerBrick = ptsPerBrick;
        lblPtsPerBrick = new Label("Points Per Brick: " + ptsPerBrick);
        this.totalPts = totalPts;
        lblTotalPts = new Label("Total Points: " + totalPts);
        this.numLives = numLives;
        lblNumLives = new Label("Number of Lives: " + numLives);
        this.setPadding(new Insets(0, 0, 2, 0));
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(lblPtsPerBrick, lblTotalPts, lblNumLives);        
    }
    
    public void reset()
    {
        ptsPerBrick = 0;
        lblPtsPerBrick.setText("Points Per Brick: " + ptsPerBrick);
        totalPts = 0;
        lblTotalPts.setText("Total Points: " + totalPts);
        numLives = LIVES;
        lblNumLives.setText("Number of Lives: " + numLives);        
    }
    
    public void continueGame()
    {
        ptsPerBrick = 0;
        lblPtsPerBrick.setText("Points Per Brick: " + ptsPerBrick); 
        numLives = LIVES;
        lblNumLives.setText("Number of Lives: " + numLives);
    }
    
    public void adjustPtsPerBrick(int numBalls)
    {
        ptsPerBrick += BASE_PTS_PER_BRICK;
        if (numBalls <= 1)
        {
            ptsPerBrick = BASE_PTS_PER_BRICK;
        }
        else
        {
            ptsPerBrick = (numBalls * MULTIPLIER) + BASE_PTS_PER_BRICK; 
        }
        lblPtsPerBrick.setText("Points Per Brick: " + ptsPerBrick);        
    }
    
    public void decNumLives()
    {
        numLives--;
        lblNumLives.setText("Number of Lives: " + numLives);
    }

    public void incTotalPts()
    {
        totalPts = totalPts + ptsPerBrick;
        lblTotalPts.setText("Total Points: " + totalPts);        
    }
    
    public String getName()
    {
        return name;
    }
    
    public int getPointsPerBrick()
    {
        return ptsPerBrick;
    }
    
    public int getTotalPts() {
        return totalPts;
    }
    
    public int getNumLives()
    {
        return numLives;
    }
    
}
