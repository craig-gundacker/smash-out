package smash;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import org.apache.commons.io.FileUtils;

/*
Handles input and output of saved games
*/
public class GameIO
{    
    protected static final String PROJECT_ROOT_DIR = "smash_out";
    private static final String PROJECT_ROOT_DIR_PATH = System.getProperty("user.home") 
            + File.separator + PROJECT_ROOT_DIR;
    private static final String SAVED_GAMES_DIR = "saved_games";
    private static final String SAVED_GAMES_DIR_PATH = PROJECT_ROOT_DIR_PATH 
            + File.separator + SAVED_GAMES_DIR;
    private static final String DELETED_GAMES_FILE = "games_to_delete.txt";
    private static final String[] PRELOADED_NAMES = {"Lucille", "Roger", "Teddy"};
    private static final String[] ZIP_FILES = {"resources/Lucille.zip", "resources/Roger.zip",
                                                "resources/Teddy.zip"};
    private static PrintWriter output;
    private static Scanner input;
    
    private static final String NAME_FILE = "name.txt";
    private static final String SCOREBOARD_FILE = "scoreboard_data.txt";
    private static final String BRICK_FILE = "brick_data.txt";
    private static final String PADDLE_FILE = "paddle_data.txt";
    private static final String BALL_FILE = "ball_data.txt";
    private static final String BOLT_FILE = "bolt_data.txt";
    private static final String SPEED_FILE = "speed_data.txt";
    private static final String ATTACK_FILE = "attack_data.txt";
    private static final String DELIMITER = ":";
    protected static final String[] PADDLE_PARTS = {"x", "y", "width", "height", "red", "green", "blue", "opacity"};
    
    /*
    If not already loaded, method loads in progress games that are bundled in jar file
    */
    public static void loadPackagedGames() throws IOException
    {
        Path pathSavedGames = Paths.get(SAVED_GAMES_DIR_PATH);
        if (Files.notExists(pathSavedGames))
        {     
            File dirSavedGames = new File(SAVED_GAMES_DIR_PATH);
            if(dirSavedGames.mkdirs())
            {
                for (int i = 0; i < PRELOADED_NAMES.length; i++)
                {
                    File dirPreLoaded = new File(dirSavedGames, PRELOADED_NAMES[i]);
                    if(dirPreLoaded.mkdirs())
                    {
                        unZipPreLoadedGame(dirPreLoaded, i);
                    }
                }
            }
        }
    }
    
    /*
    Streams game data text files to directories associated with each game
    */
    private static void unZipPreLoadedGame(File dirUser, int i)
    {
        final int BUFFER = 2048;
        try 
        {
           BufferedOutputStream dest = null;
           ZipInputStream zis;
            try (InputStream fis = GameIO.class.getResourceAsStream(ZIP_FILES[i])) 
            {
                zis = new ZipInputStream(new BufferedInputStream(fis));
                ZipEntry entry;
                while((entry = zis.getNextEntry()) != null)
                {
                    int count;
                    byte data[] = new byte[BUFFER];
                    // write the files to the disk
                    int index = entry.getName().indexOf("/");
                    String txt = entry.getName().substring(index + 1, entry.getName().length());
                    File file = new File(dirUser, txt);
                    try (FileOutputStream fos = new FileOutputStream(file)) 
                    {
                        dest = new BufferedOutputStream(fos);
                        while ((count = zis.read(data, 0, BUFFER)) != -1)
                        {
                            dest.write(data, 0, count);
                        }
                        dest.flush();
                        dest.close();
                        fos.flush();
                        fos.close();
                    }
                }}
           zis.close();
        } 
        catch(Exception e) 
        {
           e.printStackTrace();
        }        
    }
    
    /*
    Built as a workaround.  The program couldn't delete a game directory and its
    sub files once a game was completed because of files being locked.  I thought
    that I could delete the files when the program reloaded.  It didn't work.  I found
    a more straightforward solution
    */
    public static void deleteOldGames() throws IOException
    {
        List<String> gamesToDelete = new ArrayList<>();
        
        Path pathGamesToDelete = Paths.get(PROJECT_ROOT_DIR_PATH + File.separator + DELETED_GAMES_FILE);
        if (Files.exists(pathGamesToDelete))
        {
            File file = pathGamesToDelete.toFile();
            try (Scanner in = new Scanner(file)) 
            {
                while (in.hasNext())
                {
                    gamesToDelete.add(in.nextLine());
                }
                in.close();
            }
            file.delete();
            
            for (String game: gamesToDelete)
            {
                deleteGameDir(game);
            }
        }
    }
    
    public static void deleteGameDir(String name) throws IOException
    {                      
        Path pathDir = Paths.get(SAVED_GAMES_DIR_PATH + File.separator + name);
        if (Files.exists(pathDir))
        {
            File fileDir = pathDir.toFile();
            File[] files = fileDir.listFiles();
            for (File file: files)
            {
                FileUtils.deleteQuietly(file);
            }
            FileUtils.deleteDirectory(fileDir);
        }
    }
    
    /*
    See comments above deleteOldGame() method
    */
    public static List<String> addGameToDeletionList(String newDeleteGame)
    {
        List<String> gamesToDelete = new ArrayList<>();
        gamesToDelete.add(newDeleteGame);
        
        try
        {
            Path path = Paths.get(PROJECT_ROOT_DIR_PATH + File.separator + DELETED_GAMES_FILE);
            if (Files.exists(path))
            {
                File file = path.toFile();
                try (Scanner in = new Scanner(file)) 
                {
                    while (in.hasNext())
                    {
                        gamesToDelete.add(in.nextLine());
                    }
                    in.close();
                }
            }

            if (Files.notExists(path))
            {
                Files.createFile(path);
            }
            
            File newFile = path.toFile();
            try (PrintWriter writer = new PrintWriter(newFile)) 
            {
                for (String game: gamesToDelete)
                {
                    writer.println(game);
                }
                writer.close();
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return gamesToDelete;
    }
    
    /*
    Takes user through different options and results during quit game process
    */
    public static void processQuit(Game game, boolean quitGame) throws IOException, NoSuchElementException
    {
        Path pathGames = Paths.get(SAVED_GAMES_DIR_PATH);
        if (Files.notExists(pathGames))
        {
           Files.createDirectory(pathGames);
        }        

        Alert alertSave = new Alert(AlertType.CONFIRMATION);
        alertSave.setTitle("Save game?");
        alertSave.setHeaderText(null);
        alertSave.setContentText("Do you want to save your game?");

        ButtonType btnYes = new ButtonType("Yes");
        ButtonType btnNo = new ButtonType("No");
        alertSave.getButtonTypes().setAll(btnYes, btnNo);

        Optional<ButtonType> result = alertSave.showAndWait();
        String userName = null;
        if (result.get() == btnYes)
        {
            userName = game.getUserName();
            Path pathGame = null;
            if (userName != null)
            {
                pathGame = Paths.get(SAVED_GAMES_DIR_PATH + File.separator + userName);
                saveGame(game, userName, pathGame);
            }
            else
            {
                boolean cont = true;
                do
                {
                    Optional<String> in = showEnterNameDialog();
                    userName = in.get();
                    if (userName != null && !userName.isEmpty())
                    {
                        pathGame = Paths.get(SAVED_GAMES_DIR_PATH + File.separator + userName);
                        if (Files.exists(pathGame))
                        {
                            showGameExistsDialog();
                        }
                        else
                        {
                            Files.createDirectory(pathGame);
                            cont = false;
                        }                       
                    }
                }
                while (cont);
                saveGame(game, userName, pathGame);
            }
        }
                   
        if (quitGame)
        {
            game.quitGame();         
        }
        else
        {
            game.startNewGame();
        }
    }
    
    private static Optional<String> showEnterNameDialog()
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save Game");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter your name: ");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setVisible(false);
        return dialog.showAndWait();          
    }

    private static void showGameExistsDialog()
    {
        ButtonType btnOK = new ButtonType("OK");
        Alert alertExists = new Alert(AlertType.WARNING);
        alertExists.getButtonTypes().setAll(btnOK);
        alertExists.setTitle("Name exists");
        alertExists.setHeaderText(null);
        alertExists.setContentText("That name exists.  Enter another name.");
        alertExists.showAndWait();        
    }
    
    /*
    Saves relevant data to files for a game in progress
    */
    private static void saveGame(Game game, String userName, Path pathGame)
    {
        try
        {
            File fileDir = pathGame.toFile();
            saveNameData(userName, fileDir);
            saveScoreboardData(game, fileDir);
            saveBallData(game, fileDir);
            saveBoltData(game, fileDir);
            saveBrickData(game, fileDir);
            savePaddleData(game, fileDir);
            saveSpeedData(game, fileDir);
            saveAttackData(fileDir);
            showGameSavedDialog(userName);
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
    }
    
    private static void saveNameData(String name, File fileDir) throws FileNotFoundException
    {
        File file = new File(fileDir, NAME_FILE);
        output = new PrintWriter(file);
        output.println("name" + DELIMITER + name);
        closeOut(output);
    }
    
    private static void saveScoreboardData(Game game, File fileDir) throws FileNotFoundException
    {
        File file = new File(fileDir, SCOREBOARD_FILE);
        output = new PrintWriter(file);
        output.println("points per brick" + DELIMITER + game.getPointsPerBrick());
        output.println("total points" + DELIMITER + game.getTotalPoints());
        output.println("number of lives remaining" + DELIMITER + game.getNumLives());
        closeOut(output);
    }
    
    private static void saveBallData(Game game, File fileDir) throws FileNotFoundException
    {
        List<Ball> alBall = game.getListBall();
        
        File file = new File(fileDir, BALL_FILE);
        output = new PrintWriter(file);
        
        for (Ball ball: alBall)
        {
            String line = ball.dx + DELIMITER + ball.dy + DELIMITER + ball.getCenterX() +
                    DELIMITER + ball.getCenterY() + DELIMITER + ball.getRadius();
            output.println(line);
        }
        closeOut(output);
    }
    
    private static void saveBoltData(Game game, File fileDir) throws FileNotFoundException
    {
        List<Bolt> bolts = game.getListBolt();
        
        File file = new File(fileDir, BOLT_FILE);
        output = new PrintWriter(file);
        
        for (Bolt bolt: bolts)
        {
            String line = bolt.getCenterX() + DELIMITER + bolt.getCenterY();
            output.println(line);
        }
        closeOut(output);
    }
    
    private static void saveBrickData(Game game, File fileDir) throws FileNotFoundException
    {
        boolean[][] brickGrid = game.getGridBrick();
        int numRows = BallPane.getNUM_ROWS();
        int numCols = BallPane.getNUM_COLUMNS();
        
        File file = new File(fileDir, BRICK_FILE);
        output = new PrintWriter(file);
        
        for (int i = 0; i < numRows; i++)
        {
            for (int j = 0; j < numCols; j++)
            {
                Boolean isFilled = brickGrid[i][j];
                output.write(isFilled.toString());
                if (j != numCols - 1)
                {
                    output.write(DELIMITER);
                }
            }
            output.println();
        }
        closeOut(output);
    }
    
    private static void savePaddleData(Game game, File fileDir) throws FileNotFoundException
    {
        File file = new File(fileDir, PADDLE_FILE);
        output = new PrintWriter(file);
        
        Paddle paddle = game.getPaddle();
        output.println(PADDLE_PARTS[0] + DELIMITER + paddle.getX());
        output.println(PADDLE_PARTS[1] + DELIMITER + paddle.getY());
        output.println(PADDLE_PARTS[2] + DELIMITER + paddle.getWidth());
        output.println(PADDLE_PARTS[3] + DELIMITER + paddle.getHeight());
        output.println(PADDLE_PARTS[4] + DELIMITER + paddle.getColor().getRed());
        output.println(PADDLE_PARTS[5] + DELIMITER + paddle.getColor().getGreen());
        output.println(PADDLE_PARTS[6] + DELIMITER + paddle.getColor().getBlue());
        output.println(PADDLE_PARTS[7] + DELIMITER + paddle.getOpacity());
        closeOut(output);
    }
    
    private static void saveSpeedData(Game game, File fileDir) throws FileNotFoundException
    {
        File file = new File(fileDir, SPEED_FILE);
        output = new PrintWriter(file);
        output.println("ball speed" + DELIMITER + game.getBallSpeed());
        closeOut(output);
    }
    
    private static void saveAttackData(File fileDir) throws FileNotFoundException
    {
        File file = new File(fileDir, ATTACK_FILE);
        output = new PrintWriter(file);
        output.println(Boolean.toString(Game.isAttackSelected()));
        closeOut(output);
    }
    
    private static void showGameSavedDialog(String userName)
    {
        ButtonType btnOK = new ButtonType("OK");
        Alert alertSaved = new Alert(AlertType.CONFIRMATION);
        alertSaved.getButtonTypes().setAll(btnOK);
        alertSaved.setTitle("Game saved");
        alertSaved.setHeaderText(null);
        alertSaved.setContentText("Game saved as " + userName);
        alertSaved.showAndWait();         
    }
    
    /*
    Retrieves games saved for display in the game loader window
    */
    public static List<String> retrieveSavedGames() throws IOException
    {
        List<String> filePaths = new ArrayList<>();
        Path path = Paths.get(SAVED_GAMES_DIR_PATH);
        if (Files.exists(path))
        {
            if (Files.isDirectory(path))
            {
                Files.walk(path).forEach(filePath -> 
                {
                    if (Files.isDirectory(filePath)) 
                    {
                        filePaths.add(filePath.getFileName().toString());
                    }
                });
            }            
        }
        if (!filePaths.isEmpty())
        {
            filePaths.remove(0);
        }
        return filePaths;
    }
    
    /*
    Called when the user selects a saved game in the game loader window.  Inputs
    game data from files and passes values to SavedGame constructor.
    */
    public static SavedGame buildSavedGame(String dir)
    {
        SavedGame savedGame = null;
        try
        {            
            String name = retrieveName(dir);
            List<Integer> scoreBoardData = retrieveScoreboardData(dir);
            int ptsBrick = scoreBoardData.get(0);
            int totalPts = scoreBoardData.get(1);
            int numLives = scoreBoardData.get(2);
            List<Ball> alBall = retrieveBallData(dir);
            List<Bolt> alBolt = retrieveBoltData(dir);
            boolean[][] brickGrid = retrieveBrickData(dir);
            Map<String, Double> hmPaddle = retrievePaddleData(dir);
            double ballSpeed = retrieveBallSpeedData(dir);
            boolean isAttackModeOn = retrieveAttackModeData(dir);
            savedGame = new SavedGame(name, ptsBrick, totalPts, numLives, alBall, alBolt, brickGrid, 
                            hmPaddle, ballSpeed, isAttackModeOn);
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (input != null)
            {
                input.close();
            }
        }
        return savedGame;
    }
    
    private static String retrieveName(String dir) throws FileNotFoundException
    {
        File file = new File(SAVED_GAMES_DIR_PATH + File.separator + dir + File.separator + 
                NAME_FILE);
        input = new Scanner(file);
        String name = null;
        while (input.hasNext())
        {
            String line = input.nextLine();
            String[] parts = line.split(DELIMITER);
            name = parts[1];
        }
        closeIn(input);
        return name;        
    }
    
    private static List<Integer> retrieveScoreboardData(String directory) throws FileNotFoundException
    {
        File file = new File(SAVED_GAMES_DIR_PATH + File.separator + directory + File.separator + 
                SCOREBOARD_FILE);
        input = new Scanner(file);
        List<Integer> data = new ArrayList<>();
        while (input.hasNext())
        {
            String line = input.nextLine();
            String[] parts = line.split(DELIMITER);
            data.add(Integer.parseInt(parts[1]));
        }
        closeIn(input);
        return data;
    }
    
    private static List<Ball> retrieveBallData(String directory) throws FileNotFoundException
    {
        File file = new File(SAVED_GAMES_DIR_PATH + File.separator + directory + File.separator + 
                BALL_FILE);
        input = new Scanner(file);
        List<Ball> alRestoredBall = new ArrayList<>();
        while (input.hasNext())
        {
            String line = input.nextLine();
            String[] parts = line.split(DELIMITER);
            Double[] ballParts = new Double[parts.length];
            for (int i = 0; i < parts.length; i++)
            {
                ballParts[i] = Double.parseDouble(parts[i]);
            }
            Ball restoredBall = new Ball(ballParts[0], ballParts[1], ballParts[2],
                ballParts[3], ballParts[4]);
            alRestoredBall.add(restoredBall);
        }
        closeIn(input);
        return alRestoredBall;       
    }
    
    private static List<Bolt> retrieveBoltData(String directory) throws FileNotFoundException
    {
        File file = new File(SAVED_GAMES_DIR_PATH + File.separator + directory + File.separator + 
                BOLT_FILE);
        input = new Scanner(file);
        List<Bolt> alRestoredBolt = new ArrayList<>();
        while (input.hasNext())
        {
            String line = input.nextLine();
            String[] parts = line.split(DELIMITER);
            Double[] boltParts = new Double[parts.length];
            for (int i = 0; i < parts.length; i++)
            {
                boltParts[i] = Double.parseDouble(parts[i]);
            }
            Bolt restoredBolt = new Bolt(boltParts[0], boltParts[1]);
            alRestoredBolt.add(restoredBolt);
        }
        closeIn(input);
        return alRestoredBolt;       
    }
    
    private static boolean[][] retrieveBrickData(String directory) throws FileNotFoundException
    {
        boolean brickGrid[][] = new boolean[BallPane.getNUM_ROWS()][BallPane.getNUM_COLUMNS()];
        File file = new File(SAVED_GAMES_DIR_PATH + File.separator + directory + File.separator +
                BRICK_FILE);
        input = new Scanner(file);
        
        int row = 0;
        while (input.hasNext())
        {
            String line = input.nextLine();
            String[] parts = line.split(DELIMITER);
            for (int col = 0; col < parts.length; col++)
            {
                brickGrid[row][col] = Boolean.parseBoolean(parts[col]);
            }
            row++;
        }
        closeIn(input);        
        return brickGrid;
    }
    
    private static Map<String, Double> retrievePaddleData(String directory) throws FileNotFoundException
    {
        Map<String, Double> hmPaddleData = new HashMap<>();
        File file = new File(SAVED_GAMES_DIR_PATH + File.separator + directory + File.separator +
                PADDLE_FILE);
        input = new Scanner(file);
        while (input.hasNext())
        {
            String line = input.nextLine();
            String parts[] = line.split(DELIMITER);
            hmPaddleData.put(parts[0], Double.parseDouble(parts[1]));
        }
        closeIn(input);
        return hmPaddleData;
    }
    
    private static double retrieveBallSpeedData(String directory) throws FileNotFoundException
    {
        File file = new File(SAVED_GAMES_DIR_PATH + File.separator + directory + File.separator +
        SPEED_FILE);
        input = new Scanner(file);
        double speed = Game.INIT_SPEED;
        while (input.hasNext())
        {
            String line = input.nextLine();
            String parts[] = line.split(DELIMITER);
            speed = Double.parseDouble(parts[1]);
        }
        closeIn(input);
        return speed;
    }
    
    private static boolean retrieveAttackModeData(String directory) throws FileNotFoundException
    {
        File file = new File(SAVED_GAMES_DIR_PATH + File.separator + directory + File.separator +
        ATTACK_FILE);
        input = new Scanner(file);
        boolean attackOn = false;
        while (input.hasNext())
        {
            String value = input.nextLine();
            attackOn = Boolean.parseBoolean(value);
        }
        closeIn(input);
        return attackOn;
    }
    
    private static void closeOut(PrintWriter output)
    {
        if (output != null)
        {
            output.flush();
            output.close();
        }
    }
    
    private static void closeIn(Scanner input)
    {
        if (input != null)
        {
            input.close();
        }
    }
}
