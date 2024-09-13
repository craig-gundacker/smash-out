package smash;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Ball extends Circle
{
    protected double dx; //x axis direction of ball
    protected double dy; //y axis direciton of ball

    /*
    Constructor for creating a ball
    */
    protected Ball(double x, double y, double radius, Color color)
    {
        super(x, y, radius);
        setInitPath();           
        setFill(color);
    }
    
    /*
    Constructor for restoring a ball
    */
    protected Ball(double dx, double dy, double x, double y, double radius)
    {
        super(x, y, radius);
        this.dx = dx;
        this.dy = dy;
        setFill(Color.color(Math.random(), Math.random(), Math.random()));
    }

    /*
    Determines initial ball direction
    */
    private void setInitPath()
    {
        double coinFlip = Math.random();
        if (coinFlip < .5)
        {
            dx = .5 + Math.random();
        }
        else
        {
            dx = -(.5 + Math.random());
        }
        dy = -(.5 + Math.random());         
    }
}
