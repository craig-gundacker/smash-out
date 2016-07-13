
package smash;

import java.io.IOException;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
Entry class for Game.  Lists hyperlinks for starting a new game or restoring a saved
game and an event handler associated with each link
*/
public class LoaderStage extends Application 
{    
    private Stage primaryStage;
    @Override
    public void start(Stage primaryStage) 
    {
        try
        {
            //GameIO.deleteOldGames();
            GameIO.loadPackagedGames();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        
        BorderPane root = new BorderPane();
        root.setCenter(createSelectGamePane());
        root.setBottom(createButtonPane());
        Scene scene = new Scene(root, 300, 300);
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("SmashOut Loader");
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
    }
    
    private Node createSelectGamePane()
    {
        TreeItem rootItem = new TreeItem("Select new game or saved game"); //root of TreeView
        rootItem.setExpanded(true);
        
        Hyperlink hlNewGame = new Hyperlink();
        hlNewGame.setText("New game");
        hlNewGame.setOnAction(e ->
        {
            Game game = new Game();
            game.show();
            primaryStage.close();
        });
        
        VBox vbGames = new VBox(2.5);
        vbGames.setAlignment(Pos.CENTER_LEFT);
        vbGames.getChildren().add(hlNewGame);
        try
        {
            List<String> gameNames = GameIO.retrieveSavedGames();
            for (String gameName : gameNames)
            {
                Hyperlink hlSavedGame = new Hyperlink();
                hlSavedGame.setText(gameName);
                hlSavedGame.setOnAction(e ->
                {
                    primaryStage.close();
                    SavedGame saved = GameIO.buildSavedGame(gameName);
                    Game game = new Game(saved);
                    game.showAndWait();
                });
                vbGames.getChildren().add(hlSavedGame);
            }
            TreeItem file = new TreeItem(vbGames);
            rootItem.getChildren().add(file); //adds node to root
            TreeView treeView = new TreeView(rootItem);

            HBox hbDisplay = new HBox(10);
            hbDisplay.setAlignment(Pos.CENTER);
            hbDisplay.setPadding(new Insets(10, 10, 10, 10));
            hbDisplay.getChildren().addAll(treeView);
            
            ScrollPane scrollPane = new ScrollPane(hbDisplay);
            scrollPane.setPadding(new Insets(10, 10, 10, 10));
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            return scrollPane;             
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
    
    private Node createButtonPane()
    {
        Button btnQuit = new Button("Quit");
        btnQuit.setOnAction(e ->
        {
            primaryStage.close();
        });
        HBox hb = new HBox(10);
        hb.setAlignment(Pos.CENTER);
        hb.setPadding(new Insets(10, 10, 10, 10));
        hb.getChildren().add(btnQuit);
        return hb;
    }

    public static void main(String[] args) 
    {
        launch(args);
    }
    
}
