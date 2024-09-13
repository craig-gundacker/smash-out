package smash;

import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;

public class Bolt extends Arc
{
    private static final double RADIUS_X = 10;
    private static final double RADIUS_Y = 30;
    private static final double START_ANGLE = 75;
    private static final double LENGTH = 30;
    public Bolt(double centerX, double centerY)
    {
        super(centerX, centerY, RADIUS_X, RADIUS_Y, START_ANGLE, LENGTH);
        setFill(Color.GOLD);
        setType(ArcType.ROUND);
    }
}