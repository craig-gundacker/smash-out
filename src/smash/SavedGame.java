package smash;

import java.util.List;
import java.util.Map;

/*
Encapsulates a saved game and provides methods for reading the saved game's 
relevant data
*/
public class SavedGame 
{
    private final String name;
    private final int pointsPerBrick;
    private final int totalPoints;
    private final int numLivesRemaining;
    private final List<Ball> alBall;
    private final List<Bolt> alBolt;
    private final boolean[][] brickGrid;
    private final Map<String, Double> hmPaddleData;
    private final double ballSpeed;
    private final boolean isAttack;

    public SavedGame(String name, int ptsBrick, int totalPts, int numLives, List<Ball> alBall,
            List<Bolt> alBolt, boolean[][] brickGrid, Map<String, Double> hmPaddle, 
            double ballSpeed, boolean isAttackModeOn)
    {
        this.name = name;
        this.pointsPerBrick = ptsBrick;
        this.totalPoints = totalPts;
        this.numLivesRemaining = numLives;
        this.alBall = alBall;
        this.alBolt = alBolt;
        this.brickGrid = brickGrid;
        this.hmPaddleData = hmPaddle;
        this.ballSpeed = ballSpeed;
        this.isAttack = isAttackModeOn;
    }
    
    public String getName()
    {
        return name;
    }
    
    public int getPointsPerBrick() {
        return pointsPerBrick;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getNumLivesRemaining() {
        return numLivesRemaining;
    }

    public List<Ball> getListRestoredBalls() {
        return alBall;
    }
    
    public List<Bolt> getListRestoredBolts()
    {
        return alBolt;
    }

    public boolean[][] getBrickGrid() {
        return brickGrid;
    }

    public Map<String, Double> getPaddleData()
    {
        return hmPaddleData;
    }

    public double getBallSpeedData()
    {
        return ballSpeed;
    }

    public boolean getAttackMode()
    {
        return isAttack;
    }
    
    
}
