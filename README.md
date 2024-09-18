# Smash Out

Smash Out is a Java application that is sort of based on the classic arcade game Break Out. This project uses Gradle for build automation and dependency management.

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

To build and run this project, you need to have the following installed on your system:

- Java Development Kit (JDK) 11 or later
- Gradle 6.0 or later

## Setting up the Project

1. Create a new directory for your project and navigate to it in the terminal.

2. Create a `build.gradle` file in the project root directory with the following content:

   ```groovy
   plugins {
        id 'application'
        id 'org.openjfx.javafxplugin' version '0.0.10'
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation 'commons-io:commons-io:2.11.0'
    }

    javafx {
        version = "16"
        modules = ['javafx.controls', 'javafx.fxml']
    }

    application {
        mainClass = 'smash.LoaderStage'
    }

    sourceSets {
        main {
            java {
                srcDirs = ['src/main/java']
            }
            resources {
                srcDirs = ['src/main']
                include '**/*.css'
                include '**/*.zip'
                include '**/*.txt'
            }
        }
    }

    jar {
        from {
            configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) }
        }
        manifest {
            attributes 'Main-Class': 'smash.LoaderStage'
        }
    }

    tasks.withType(Jar) {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    compileJava {
        options.compilerArgs << '-Xlint:-module'
    }
   ```

3. Create the following directory structure:
   ```
   src/main/java/smash/
   src/main/resources/
   ```

4. Place all Java files in the `src/main/java/smash/` directory.

5. Place the ZIP, the high scores, and style sheet files in in the `src/main/resources/` directory.

## Building the Project

To build the project, open a terminal in the project root directory and run:

```
gradle build
```

This command will compile your Java files, include the resource files, and create a runnable JAR file.

## Running the Application

After building the project, you can run the application using:

```
gradle run
```

This will start the Smash Out game with its graphical user interface.

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

1. Ensure all Java files are in the correct package directory (`src/main/java/smash/`).
2. Verify that the ZIP, high scores, and style sheet files are in the `src/main/resources/` directory.
3. Check that your Gradle and Java installations are up to date.
4. If you have GUI issues, make sure you're using a compatible version of JavaFX. You may need to adjust the JavaFX version in the `build.gradle` file.

## Contributing

To contribute to this project, please fork the repository and submit a pull request with your changes.

