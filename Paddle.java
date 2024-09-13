package smash;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Paddle extends Rectangle
{
    private final double initWidth;
    private Color color;

    /*
    Builds new paddle
    */
    public Paddle(double screenX, double screenY, Color color)
    {
        super(screenX, screenY, BallPane.PADDLE_WIDTH, BallPane.PADDLE_HEIGHT);
        initWidth = BallPane.PADDLE_WIDTH;
        setFill(color);
        this.color = color;
        setStroke(Color.BLACK);
        setOnMouseDragged(e ->
        {
            if (e.getX() > 0 && e.getX() < 875)
            {
                setX(e.getX());
            }              
        });
    }
    
    /*
    Restores paddle
    */
    public Paddle(double screenX, double screenY, double width, double height, Color color)
    {
        super(screenX, screenY, width, height);
        setFill(color);
        this.color = color;
        setStroke(Color.BLACK);
        initWidth = BallPane.PADDLE_WIDTH;
        setOnMouseDragged(e ->
        {
            if (e.getX() > 0 && e.getX() < 875)
            {
                setX(e.getX());
            }              
        });
    }

    public double getInitWidth()
    {
        return initWidth;
    }
    
    public Color getColor()
    {
        return color;
    }
    
    public void setColor(Color color)
    {
        this.color = color;
        setFill(color);
    }
}