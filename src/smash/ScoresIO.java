package smash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Scanner;
import static javafx.application.Application.STYLESHEET_CASPIAN;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

/*
Provides input and output functionality for top scores list
*/
public class ScoresIO
{
    private final ScoreBoard currentScoreBoard;
    private final ArrayList<String> alLines = new ArrayList<>();
    private final ArrayList<Integer> alScores = new ArrayList<>();
    private final String RESOURCE_HIGH_SCORES = "resources/high_scores_resource.txt";
    private final String FILE_PATH_HIGH_SCORES = System.getProperty("user.home") + File.separator + 
            GameIO.PROJECT_ROOT_DIR + File.separator + "high_scores_file.txt";
    private static final int NUM_ENTRIES = 10;
    
    public ScoresIO(ScoreBoard currentScoreBoard)
    {
        this.currentScoreBoard = currentScoreBoard;
        initCheckTopScores();      
    }
    
    private void initCheckTopScores()
    {
        Path path = Paths.get(FILE_PATH_HIGH_SCORES);
        try
        {
            if (Files.exists(path)) 
            {
                Scanner input = new Scanner(path);
                inputScores(input);
                System.out.println("Loaded scores from machine");
            }

            if (Files.notExists(path)) //Input from jar file
            {
                InputStream inStream = this.getClass().getResourceAsStream(RESOURCE_HIGH_SCORES);
                Scanner input = new Scanner(new BufferedReader(new InputStreamReader(inStream)));
                inputScores(input);
                inStream.close();
                System.out.println("Loaded scores from Jar file");
                writeScoresToMachine();
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();;
        }
    }
    
    private void inputScores(Scanner input)
    {
        int counter = 0;
        while (input.hasNext() && counter < NUM_ENTRIES)
        {
            String line = input.nextLine();
            alLines.add(line);
            String[] tokens = line.split(" ");
            Integer intScore = Integer.parseInt(tokens[tokens.length - 1]);
            alScores.add(intScore);
            counter++;
        }
        input.close();        
    }
        
    public boolean topScorer()
    {
        return currentScoreBoard.getTotalPts() > alScores.get(alScores.size() - 1);
    }
    
    public void addPlayerShowList(boolean quitGame)
    {
        Integer userPoints = currentScoreBoard.getTotalPts();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Top Scorer");
        dialog.setHeaderText("You made the top scorer list");
        dialog.setContentText("Enter your name: ");
        dialog.initOwner(Game.stage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.show();
        dialog.setOnCloseRequest(e ->
        {
            String userName = dialog.getResult();
            if (userName == null || userName.isEmpty())
            {
                addPlayerShowList(quitGame);
            }
            else
            {
                boolean inserted = false;

                int counter = 0;
                while (counter < alScores.size() && inserted == false)
                {
                    if (userPoints >= alScores.get(counter))
                    {
                        alScores.add(counter, userPoints);
                        String line = userName + "   " + stampDate() + "   " + userPoints.toString();
                        alLines.add(counter, line);
                        inserted = true;
                    }
                    counter++;
                }
                writeScoresToMachine();
                showList(quitGame);                
            }
        });              
    }
    
    private void writeScoresToMachine()
    {
        try
        (PrintWriter writer = new PrintWriter(new File(FILE_PATH_HIGH_SCORES))) 
        {
            for (String line: alLines)
            {
                writer.println(line);
            }
            writer.flush();
            writer.close();
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
    }
    
    private String stampDate()
    {
        LocalDate gameDate = LocalDate.now();
        Integer day = gameDate.getDayOfMonth();
        Month month = gameDate.getMonth();
        Integer year = gameDate.getYear();
        return month.toString() + " " + day.toString() + ", " + year.toString();        
    }
    
    public void showList(boolean quitGame)
    {
        Stage stage = new Stage();
        VBox vbScores = new VBox(10);
        vbScores.setAlignment(Pos.CENTER);
        vbScores.setPadding(new Insets(20, 20, 20, 20));

        for (int i = 0; i < NUM_ENTRIES; i++)
        {
            String line = alLines.get(i);
            Label lblScores = new Label(line);
            lblScores.setAlignment(Pos.CENTER_LEFT);
            lblScores.setFont(Font.font(STYLESHEET_CASPIAN, FontWeight.BOLD, 12));
            vbScores.getChildren().add(lblScores);
        }
        
        Button btnClose = new Button("Close");
        btnClose.setOnAction(e ->
        {
            stage.close();
            if (quitGame)
            {
                Game.stage.close();
                LoaderStage loader = new LoaderStage();
                loader.start(new Stage());
            }
        });
        
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.setPadding(new Insets(10, 0, 10, 0));
        hbBtn.getChildren().add(btnClose);
        
        BorderPane root = new BorderPane();
        root.setCenter(vbScores);
        root.setBottom(hbBtn);
        vbScores.toBack();                   
        Scene scene = new Scene(root, 300, 400);

        stage.setScene(scene);
        stage.initOwner(Game.stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Top Scores");
        stage.show();
    }
}
