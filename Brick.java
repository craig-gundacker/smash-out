package smash;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Brick extends Rectangle
{
    private final double lly;
    private final int gridRow;
    private final int gridCol;

    public Brick(int gridRow, int gridCol, double x, double y, double width, double height, Color color)
    {
        super(x, y, width, height);
        this.gridRow = gridRow;
        this.gridCol = gridCol;
        setFill(color);
        setStroke(Color.BLACK);
        lly = y + height;
    }

    public int getGridRow() {
        return gridRow;
    }

    public int getGridCol() {
        return gridCol;
    }
}