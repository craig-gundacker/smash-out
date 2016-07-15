package smash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.util.Duration;

/*
Pane where the game action occurs
*/
class BallPane extends Pane
{
    private final Scene scene;
    private final ScoreBoard scoreBoard;
    private final ScoresIO topScoresIO;
    private final transient Timeline animation;
    private final double DURATION_MILLIS = 50;
    private Paddle paddle;
    private static final double MAX_PADDLE_WIDTH_FACTOR = 1.5;
    private static final double MIN_PADDLE_WIDTH_FACTOR = .5;
    private static final double PADDLE_CHANGE_AMOUNT = 5.0;
    protected static final double PADDLE_WIDTH = 125;
    protected static final double PADDLE_HEIGHT = 12.5;
    private static final int NUM_ROWS = 5;
    private static final int NUM_COLUMNS = 10;
    private final double BRICK_WIDTH; 
    private static final double BRICK_HEIGHT = 30;
    private static final double OPACITY = .5;
    private final double brickZone;
    private int numBalls;
    private final int MAX_BALLS = 10;
    private final int MIN_RADIUS = 10;
    private final int RADIUS_FACTOR = 15;
    private final double AF = 1.2; //Ball acceleration factor
    private final double DF = 1.25; //Ball deceleration factor
    private static final double MOVE_PARAMETER = 1.5;
    private static final double MOVE_RESET = 1;
    private static final double MOVE_RANDOM = .75;
    private int timer = 0;
    private final ArrayList<Brick> alBrick;
    private final boolean[][] brickGrid = new boolean[getNUM_ROWS()][getNUM_COLUMNS()];
    private final ArrayList<Ball> alBall;
    private final ArrayList<Bolt> alBolt;
    private final int SHOOT_BOLT_INTERVAL = 400;

    public BallPane(Scene scene, ScoreBoard scoreBoard, boolean autoStart)
    {
        this.scene = scene;
        this.scoreBoard = scoreBoard;
        BRICK_WIDTH = scene.getWidth() / NUM_COLUMNS;
        brickZone = scene.getHeight() - (NUM_ROWS * BRICK_HEIGHT);
        alBrick = new ArrayList<>();
        alBall = new ArrayList<>();
        alBolt = new ArrayList<>();
        numBalls = 0;        

        topScoresIO = new ScoresIO(this.scoreBoard);
        
        animation = new Timeline(new KeyFrame(Duration.millis(DURATION_MILLIS), e -> move()));
        animation.setCycleCount(Timeline.INDEFINITE);
        if (autoStart)
        {
            animation.play();                    
        }
    }

    public void setUpBricks()
    {
        for (int i = 0; i < getNUM_ROWS(); i++)
        {
            for (int j = 0; j < getNUM_COLUMNS(); j++)
            {
                int gridRow = i;
                int gridCol = j;
                double screenX = j * BRICK_WIDTH;
                double screenY = i * BRICK_HEIGHT;
                addBrick(gridRow, gridCol, screenX, screenY, BRICK_WIDTH, BRICK_HEIGHT);
                brickGrid[i][j] = true;
            }
        }
    }
    
    /*
    Restores bricks from saved game
    */
    public void restoreBricks(boolean[][] brickGrid)
    {
        for (int i = 0; i < getNUM_ROWS(); i++)
        {
            for (int j = 0; j < getNUM_COLUMNS(); j++)
            {
                if (brickGrid[i][j])
                {
                    double screenX = j * BRICK_WIDTH;
                    double screenY = i * BRICK_HEIGHT;
                    addBrick(i, j, screenX, screenY, BRICK_WIDTH, BRICK_HEIGHT);
                    this.brickGrid[i][j] = true;
                }
            }
        }        
    }

    public void addBrick(int gridRow, int gridCol, double x, double y, double width, double height)
    {
        Color color = new Color(Math.random(), Math.random(), Math.random(), OPACITY);
        Brick brick = new Brick(gridRow, gridCol, x, y, width, height, color);
        getChildren().add(brick);
        alBrick.add(brick);
    }

    public void addPaddle(double x, double y)
    {
        Color color = new Color(Math.random(), Math.random(), Math.random(), OPACITY);
        paddle = new Paddle(x, y, color);
        getChildren().add(paddle);
        paddle.yProperty().bind(heightProperty().divide(1.075));
    }
    
    /*
    Restores paddle from saved game
    */
    public void restorePaddle(Map<String, Double> hmPaddleData)
    {
        double screenX = hmPaddleData.get(GameIO.PADDLE_PARTS[0]);
        double screenY = hmPaddleData.get(GameIO.PADDLE_PARTS[1]);
        double width = hmPaddleData.get(GameIO.PADDLE_PARTS[2]);
        double height = hmPaddleData.get(GameIO.PADDLE_PARTS[3]);
        double red = hmPaddleData.get(GameIO.PADDLE_PARTS[4]);
        double green = hmPaddleData.get(GameIO.PADDLE_PARTS[5]);
        double blue = hmPaddleData.get(GameIO.PADDLE_PARTS[6]);
        double opacity = hmPaddleData.get(GameIO.PADDLE_PARTS[7]);
        Color restoredColor = Color.color(red, green, blue, opacity);
        paddle = new Paddle(screenX, screenY, width, height, restoredColor);
        getChildren().add(paddle);
        paddle.yProperty().bind(heightProperty().divide(1.075));    
    }

    public void makePaddleBigger()
    {      
        if (paddle.getWidth() <= paddle.getInitWidth() * MAX_PADDLE_WIDTH_FACTOR)
        {    
            paddle.setWidth(paddle.getWidth() + PADDLE_CHANGE_AMOUNT);
            paddle.setX(paddle.getX() - (PADDLE_CHANGE_AMOUNT / 2));                
        }
    }

    public void makePaddleSmaller()
    {
        if (paddle.getWidth() >= paddle.getInitWidth() * MIN_PADDLE_WIDTH_FACTOR)
        {     
            paddle.setWidth(paddle.getWidth() - PADDLE_CHANGE_AMOUNT);
            paddle.setX(paddle.getX() + (PADDLE_CHANGE_AMOUNT / 2));                
        }
    }

    public void addBall()
    {
        if (getNumBalls() <= MAX_BALLS)
        {
            double radius =  MIN_RADIUS + Math.random() * RADIUS_FACTOR;
            double centerX = getWidth() * Math.random();
            double centerY = getHeight()/2;
            Color color = new Color(Math.random(), Math.random(), Math.random(), OPACITY);
            Ball ball = new Ball(centerX, centerY, radius, color);
            getChildren().add(ball);
            alBall.add(ball);
            numBalls++;
            scoreBoard.adjustPtsPerBrick(numBalls);            
        }
    }

    public void removeBall()
    {
        if (getChildren().size() > 0)
        {
            boolean removed = false;
            int index = getChildren().size() - 1;
            while (removed == false  && index >= 0)
            {
                Node node = getChildren().get(index);
                index--;
                if (node instanceof Ball)
                {
                    getChildren().remove(node);
                    alBall.remove(alBall.size() - 1);
                    numBalls--;
                    scoreBoard.adjustPtsPerBrick(numBalls);
                    removed = true;
                }
            }
        }
    }

    public void clearBalls()
    {
        numBalls = 0;
    }

    public void clearBallList()
    {
        alBall.clear();
    }
    
    /*
    Restore balls from saved game
    */
    public void restoreBalls(List<Ball> list)
    {
        for (Ball restoredBall: list)
        {
            getChildren().add(restoredBall);           
            alBall.add(restoredBall);
            numBalls++;
        }
    }
    
    /*
    Restore bolts from saved game
    */
    public void restoreBolts(List<Bolt> bolts)
    {
        for (Bolt restoredBolt: bolts)
        {
            getChildren().add(restoredBolt);
            alBolt.add(restoredBolt);
        }
    }

    public int getNumBalls() {
        return numBalls;
    }
    
    public List<Ball> getListBall()
    {
        return alBall;
    }
    
    public List<Bolt> getListBolt()
    {
        return alBolt;
    }

    public void clearBrickList()
    {
        alBrick.clear();
    }

    public void play()
    {
        animation.play();
    }

    public void pause()
    {
        animation.pause();
    }

    public DoubleProperty rateProperty()
    {
        return animation.rateProperty();
    }

    public final void move()
    {              
        if (Game.isAttackSelected())
        {
            shootBolt();
            moveBolt();
        }
        
        Iterator<Ball> outsideIterator = alBall.iterator();
        while (outsideIterator.hasNext())
        {
            Ball ball = outsideIterator.next();
            boolean keepChecking = true;
            double radiusBall = ball.getRadius();
            double centerXBall = ball.getCenterX();
            double centerYBall = ball.getCenterY();

            if (sideWallCollision(centerXBall, radiusBall))
            {
                moveXDir(ball);
                keepChecking = false;
            }                
            if (keepChecking && insideBrickZone(centerYBall, radiusBall)) //checks if ball is inside brickzone
            {
                if(brickCollisionOccurs(ball))
                {
                    keepChecking = false;
                }
            }
            if (keepChecking && centerYBall < radiusBall) //detect collision with top wall
            {
                moveYDir(ball);
                keepChecking = false;
            }       
            if (keepChecking && contactPaddleX(centerXBall))
            {
                if (contactPaddleY(centerYBall, radiusBall))
                {
                    moveYDir(ball);
                    ball.dy = ball.dy - 1;
                    keepChecking = false;
                }
            }
            if (keepChecking && movesPastPaddle(centerYBall, radiusBall))
            {
                this.getChildren().remove(ball);
                outsideIterator.remove();
                numBalls--;
                scoreBoard.adjustPtsPerBrick(numBalls);
                scoreBoard.decNumLives();
                if (scoreBoard.getNumLives() <= 0)
                {
                    gameOverSub();
                }
                keepChecking = false;
            }
            
            if (keepChecking)
            {
                //check for collisions with other balls
                Iterator<Ball> insideIterator = alBall.iterator();
                while (keepChecking && insideIterator.hasNext())
                {
                    Ball otherBall = insideIterator.next();
                    if (!ball.equals(otherBall))
                    {                   
                        double radiusOtherBall = otherBall.getRadius();
                        double centerXOtherBall = otherBall.getCenterX();
                        double centerYOtherBall = otherBall.getCenterY();
                        double radCombined = radiusBall + radiusOtherBall;

                        double xSide = Math.abs(centerXBall - centerXOtherBall);
                        double ySide = Math.abs(centerYBall - centerYOtherBall);
                        double zSide = Math.sqrt(Math.pow(xSide, 2) + Math.pow(ySide, 2));

                        if (radCombined > zSide) //edge of balls collided
                        {
                            keepChecking = false;
                            if (ball.dy <= 0 && otherBall.dy <= 0) //both balls moving "up"
                            {
                                if (ball.dx < 0 && otherBall.dx < 0) //both balls moving "left"
                                {
                                    if (centerXBall <= centerXOtherBall) //ball2 trailing
                                    {
                                        accel(ball);
                                        decel(otherBall);  
                                    }
                                    else //ball1 trailing
                                    {
                                        accel(otherBall);
                                        decel(ball);
                                    }
                                }
                                else //both balls moving "right"
                                {
                                    if (centerXBall <= centerXOtherBall) //ball1 trailing
                                    {
                                        accel(otherBall);
                                        decel(ball);
                                    }
                                    else //ball2 trailing
                                    {
                                        accel(ball);
                                        decel(otherBall);
                                    }
                                }
                            }
                            else if (ball.dy > 0 && otherBall.dy > 0) //both balls moving "down"
                            {
                                if (ball.dx < 0 && otherBall.dx < 0) //both balls moving "left"
                                {
                                    if (centerXBall <= centerXOtherBall) //ball2 trailing
                                    {
                                        accel(ball);
                                        decel(otherBall);
                                    }
                                    else //ball1 trailing
                                    {
                                        accel(otherBall);
                                        decel(ball);
                                    }
                                }
                                else //both balls moving "right"
                                {
                                    if (centerXBall <= centerXOtherBall) //ball1 trailing
                                    {
                                        accel(otherBall);
                                        decel(ball);
                                    }
                                    else //ball2 trailing
                                    {
                                        accel(ball);
                                        decel(otherBall);
                                    }
                                }
                            }
                            else //balls moving in opposite direction
                            {
                                moveXDir(ball);
                                moveYDir(ball);
                                moveXDir(otherBall);
                                moveYDir(otherBall);
                            }
                        }                                
                    }   
                }
            }
            ball.setCenterX(ball.getCenterX() + ball.dx);
            ball.setCenterY(ball.getCenterY() + ball.dy);
        }
    }
    
    private boolean sideWallCollision(double centerX, double radius)
    {
        return centerX < radius || centerX > getWidth() - radius;
    }
    
    private boolean insideBrickZone(double centerY, double radius)
    {
        return centerY - radius <= brickZone;
    }
    
    private boolean contactPaddleX(double centerX)
    {
        return centerX <= (paddle.getX() + paddle.getWidth()) && centerX >= paddle.getX();
    }
    
    private boolean contactPaddleY(double centerY, double radius)
    {
        return centerY + radius >= paddle.getY();
    }
    
    private boolean movesPastPaddle(double centerY, double radius)
    {
        return centerY + radius >= getHeight();
    }
    
    /*
    Accelerates ball
    */
    private void accel(Ball ball)
    {
        ball.dx = ball.dx * AF;
        ball.dy = ball.dy * AF;         
    }
    
    /*
    Decelerates ball
    */
    private void decel(Ball ball)
    {
        ball.dx = ball.dx / DF;
        ball.dy = ball.dy / DF;         
    }
    
    private void shootBolt()
    {
        timer++;
        if (timer > SHOOT_BOLT_INTERVAL)
        {
            //if ball array is not empty
            if (!alBall.isEmpty())
            {
                //get a random ball from the ball array
                int alBallSize = alBall.size();
                int randomIndex = (int)(alBallSize * Math.random());
                Ball boltBirthingBall = alBall.get(randomIndex);
                double centerX = boltBirthingBall.getCenterX();
                double centerY = boltBirthingBall.getCenterY();

                if (centerY < getHeight()/1.5)
                {
                    makeBolt(centerX, centerY);
                }                  
            }
            timer = 0;
        }         
    }
    
    private void makeBolt(double centerX, double centerY)
    {
        Bolt bolt = new Bolt(centerX, centerY); //add a bolt to pane at centerX/centerY ball pos
        alBolt.add(bolt);    //add bolt to bolt array
        getChildren().add(bolt);    //add bolt to pane observable list         
    }
    
    private void moveBolt()
    {
        Iterator<Bolt> iterator = alBolt.iterator();
        while (iterator.hasNext())
        {
            Bolt bolt = iterator.next();
            boolean boltRemoved = false;
            bolt.setCenterY(bolt.getCenterY() + 1); //add 1 to bolt yPos
            if (isBoltInPaddleRange(bolt))  //is bottom of bolt at same yPos as paddle
            {
                if (isBoltContactingPaddle(bolt))
                {
                    getChildren().remove(bolt);
                    iterator.remove();
                    boltRemoved = true;
                    paddle.setColor(Color.color(Math.random(), Math.random(), Math.random()));
                    scoreBoard.decNumLives();
                    if (scoreBoard.getNumLives() <= 0)
                    {
                        gameOverSub();
                    }                        
                }
            }
            
            if (isBoltOffScreen(bolt) && !boltRemoved)
            {
                getChildren().remove(bolt);
                iterator.remove();
            }
        }
    }
    
    private boolean isBoltInPaddleRange(Bolt bolt)
    {
        return bolt.getCenterY() >= paddle.getY();
    }
    
    private boolean isBoltContactingPaddle(Bolt bolt)
    {
        return bolt.getCenterX() >= paddle.getX() && bolt.getCenterX() + bolt.getRadiusX() <= paddle.getX() + paddle.getWidth();
    }
    
    private boolean isBoltOffScreen(Bolt bolt)
    {
        return bolt.getCenterY() - bolt.getRadiusY() > getHeight();
    }

    public void moveYDir(Ball ball)
    {
        if (ball.dy < -MOVE_PARAMETER)
        {
            ball.dy = MOVE_RESET;
        }
        else if (ball.dy > MOVE_PARAMETER)
        {
            ball.dy = -MOVE_RESET;
        }
        else
        {
            ball.dy = -ball.dy * (MOVE_RANDOM + Math.random());
        }            
    }

    public void moveXDir(Ball ball)
    {
        if (ball.dx < -MOVE_PARAMETER)
        {
            ball.dx = MOVE_RESET;
        }
        else if (ball.dx > MOVE_PARAMETER)
        {
            ball.dx = -MOVE_RESET;
        }
        else
        {
            ball.dx = -ball.dx * (MOVE_RANDOM + Math.random());
        }           
    }

    public boolean brickCollisionOccurs(Ball ball)
    {
        boolean brickRemoved = false;
        int index = 0;
        while (!brickRemoved && index < alBrick.size())
        {
            Brick brick = alBrick.get(index);
            double llx = brick.getX();
            double lly = brick.getY() + brick.getHeight();
            double width = brick.getWidth();
            double centerX = ball.getCenterX();
            double centerY = ball.getCenterY();
            double radius = ball.getRadius();

            if (centerX <= (llx + width) && centerX >= llx)
            {
                if (centerY - radius < lly)
                {
                    brickRemoved = removeBrick(brick);
                    moveYDir(ball);
                }
            }
            index += 1;
        }
        return brickRemoved;
    }
    
    private boolean removeBrick(Brick brick)
    {
        this.getChildren().remove(brick);
        alBrick.remove(brick);
        brickGrid[brick.getGridRow()][brick.getGridCol()] = false;
        scoreBoard.incTotalPts();
        if (alBrick.isEmpty())
        {     
            allBricksSmashedSub();
        }
        return true;
    }
    
    private void allBricksSmashedSub()
    {
        animation.pause();
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Bricks Removed");
        alert.setHeaderText(null);
        alert.setContentText("All bricks smashed");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.show(); 
        ObjectProperty<ButtonType> resultProperty = alert.resultProperty();
        resultProperty.addListener(e ->
        {
            resetGame(false);
            play();
        });
    }
    
    private void gameOverSub()
    {
        String gameName = scoreBoard.getName();
        if (gameName != null) //Game in progress loaded from file
        {
            //GameIO.addGameToDeletionList(gameName); //Current game is a saved game that requires deletion
            try
            {
                GameIO.deleteGameDir(gameName);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
          
        animation.pause();
        Alert alert = new Alert(AlertType.NONE);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText("Start new game? ");
        ButtonType btnYes = new ButtonType("Yes");
        ButtonType btnNo = new ButtonType("No");
        alert.getButtonTypes().addAll(btnYes, btnNo);
        alert.initOwner(Game.stage);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.show();
   
        ObjectProperty<ButtonType> resultProperty = alert.resultProperty();
        resultProperty.addListener(ev ->
        {            
            ButtonType btnType = resultProperty.getValue();
            boolean quitGame = false;
            if (btnType.getText().equals("Yes"))
            {
                if (topScoresIO.topScorer())
                {
                    topScoresIO.addPlayerShowList(quitGame);
                }
                Game game = new Game();
                game.show();                               
            }
            else
            {
                quitGame = true;
                if (topScoresIO.topScorer())
                {
                    topScoresIO.addPlayerShowList(quitGame);
                }
                else
                {
                    topScoresIO.showList(quitGame);                 
                }                   
            }
        });                           
    }
    
    private void resetGame(boolean clearPoints)
    {
        alBall.clear();
        alBrick.clear();
        getChildren().clear();
        numBalls = 0;
        if (clearPoints){
            scoreBoard.reset();
        }
        else{
            scoreBoard.continueGame();
        }
        setUpBricks();
        addPaddle(scene.getWidth()/2, scene.getHeight());        
    }

    public boolean[][] getBrickGrid() {
        return brickGrid;
    }
     
    public static int getNUM_ROWS() {
        return NUM_ROWS;
    }

    public static int getNUM_COLUMNS() {
        return NUM_COLUMNS;
    }
    
    public Paddle getPaddle()
    {
        return paddle;
    }
}
