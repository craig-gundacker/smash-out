# Smash Out

Smash Out is a Java application loosely based on the classic arcade game Break Out. Players control a paddle to bounce a ball and earn points by breaking bricks.

## Project Structure

The project consists of:

- Java files in `src/main/java/smash/`:
  1. Ball.java
  2. BallPane.java
  3. Bolt.java
  4. Brick.java
  5. Game.java
  6. GameIO.java
  7. Paddle.java
  8. SavedGame.java
  9. ScoreBoard.java
  10. ScoresIO.java

- Resource files in `src/main/resources/`
  1. Lucille.zip (saved game data)
  2. Roger.zip (saved game data)
  3. Teddy.zip (saved game data)
  4. high_scores_resource.txt (contains top 10 high scores)
  5. style_sheet.css (styles the game)

## Prerequisites

You'll need to have installed:

- Java Development Kit (JDK) 17 (tested version)
- Git (latest version recommended)

>Gradle installation is not required as this project uses the Gradle Wrapper, which automatically downloads the correct Gradle version.

## Cloning the Repository

To get started with this project, clone the repository to your local machine. To do that:

1. Open your terminal or command prompt.
2. Navigate to the directory where you want to clone the repository.
3. Run the following command:

   ```powershell
   git clone https://github.com/craig-gundacker/smash-out.git
   ```

4. Once the cloning process is complete, navigate to the project directory:

   ```powershell
   cd smash-out
   ```

You now have a local copy of the project and can start working with it.

## Configuration

This project uses JavaFX. If you encounter any issues related to JavaFX, you may need to adjust the JavaFX version in the `build.gradle` file to match your Java version.

## Building and running the Project

To build the project, open a terminal in the project root directory and run:

| UNIX    | Microsoft |
| -------- | ------- |
| <code>./gradlew build</code> | <code> gradlew.bat build</code>                 |

This command compiles your Java files, includes the resource files, and creates a runnable JAR file.

## Running the Application

After building the project, run the application using:

| UNIX    | Microsoft |
| -------- | ------- |
| <code>./gradlew run</code> | <code> gradlew.bat run</code>                 |

This starts the Smash Out graphical user interface.

## Accessing Resource Files

In your Java code, you can access the resource files using the following method:

```java
// For ZIP files
InputStream inputStream = getClass().getResourceAsStream("/Lucille.zip");

// For the high scores file
InputStream highScoresStream = getClass().getResourceAsStream("/high_scores_resource");
```

Replace "Lucille.zip" with the appropriate filename as needed.

## Troubleshooting

If you encounter any issues:

1. Check that your Java installations are up to date.
2. Make sure you're using a compatible version of JavaFX. You may need to adjust the JavaFX version in the `build.gradle` file.

## Contributing

To contribute to this project, please fork the repository and submit a pull request with your changes.
