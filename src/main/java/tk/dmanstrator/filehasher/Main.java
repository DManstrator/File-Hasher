package tk.dmanstrator.filehasher;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * Main File with GUI Support to execute the File Hasher.
 * 
 * @author DManstrator
 *
 */
public class Main extends Application {
    
    /**
     * Starts the GUI.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        configuringDirectoryChooser(directoryChooser);
        
        // Creating a GridPane container
        GridPane grid = new GridPane();
        grid.setVgap(5);
        grid.setHgap(5);
        
        final Label pathLabel = new Label();
        final Label pathNameLabel = new Label("Path:");
        pathNameLabel.setMinWidth(30);
        
        GridPane.setConstraints(pathNameLabel, 0, 0);
        GridPane.setConstraints(pathLabel, 1, 0);
        
        grid.getChildren().addAll(pathNameLabel, pathLabel);
        
        Button chooseButton = new Button("Choose A Path");
        Button okayButton = new Button("Okay");
        Button closeButton = new Button("Exit");

        chooseButton.setOnAction(action -> {
            File dir = directoryChooser.showDialog(primaryStage);
            if (dir != null) {
                pathLabel.setText(dir.getAbsolutePath());
                okayButton.setDisable(false);
            } else {
                pathLabel.setText(null);
                okayButton.setDisable(true);
            }
        });
        
        okayButton.setDisable(true);
        
        closeButton.setOnAction(action ->  {
            primaryStage.close();
        });
        
        HBox buttons = new HBox();
        buttons.setSpacing(5);
        buttons.getChildren().addAll(chooseButton, okayButton, closeButton);
        
        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(5);
 
        root.getChildren().addAll(grid, buttons);
 
        Scene scene = new Scene(root);
        
        Image icon = new Image(getClass().getClassLoader().getResourceAsStream("img/logo.png"));
        primaryStage.setTitle("File-Hasher");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(icon);
        primaryStage.setMinWidth(270);
        primaryStage.setMinHeight(115);
        primaryStage.show();
        
        okayButton.setOnAction(action ->  {
            Hasher hasher = new Hasher();
            
            Button gotoButton = new Button("Open Folder");
            Button returnButton = new Button("Return");
            
            String pathToOutputFile = null;
            String returnValue;
            try {
                pathToOutputFile = hasher.hash(pathLabel.getText());
                returnValue = String.format("Successfully created %s as the Output File!", pathToOutputFile);
            } catch (IllegalArgumentException | FileNotFoundException | UnsupportedEncodingException e) {
                returnValue = e.getMessage();
                gotoButton.setDisable(true);
            }
            
            Label userInformation = new Label(returnValue);
            userInformation.setWrapText(true);
            
            final String outputPath = pathToOutputFile;
            gotoButton.setOnAction(gotoAction ->  {
                String error = openFolderWithFile(outputPath);
                if (error != null)  {
                    userInformation.setText(userInformation.getText() + System.lineSeparator() + error);
                    gotoButton.setDisable(true);
                }
            });
            
            returnButton.setOnAction(returnAction ->  {
                buttons.getChildren().add(closeButton);  // Disappears if not re-added 
                pathLabel.setText(null);
                okayButton.setDisable(true);
                primaryStage.setScene(scene);
            });
            
            HBox newButtons = new HBox();
            newButtons.setSpacing(5);
            newButtons.getChildren().addAll(gotoButton, returnButton, closeButton);
            
            VBox out = new VBox();
            out.setPadding(new Insets(10));
            out.setSpacing(5);
            out.getChildren().addAll(userInformation, newButtons);
            out.setMinSize(500, 250);

            Scene infoScreen = new Scene(out);
            primaryStage.setScene(infoScreen);
            primaryStage.setHeight(100);  // trick to use min width

            System.out.println(returnValue);
        });
    }
    
    /**
     * Opens the Folder to the given File.
     * 
     * @param outputFile
     *            File to get Folder from
     * @return <code>null</code> if the call was successfully, else an Error Response
     */
    private static String openFolderWithFile(String outputFile) {
        File testFile = new File(outputFile);
        if (!testFile.isFile())  {
            return "The file isn't avaliable (anymore)!";
        }
        try {
            // new ProcessBuilder("explorer.exe", "/select,\"" + outputPath + "\"").start();  // doesn't work
            Runtime.getRuntime().exec("explorer.exe /select," + outputFile);
        } catch (IOException e) {
            String currentDir = System.getProperty("user.dir");
            try {
                Desktop.getDesktop().open(new File(currentDir));
            } catch (IOException e1) {
                return "An Error occured: Cannot open the Folder for you!";
            }
        }
          
        return null;
    }

    /**
     * Configures a Title and an initial Directory for a given Directory Chooser.
     * 
     * @param directoryChooser
     *            Directory Chooser to change things on
     */
    private void configuringDirectoryChooser(DirectoryChooser directoryChooser) {
        directoryChooser.setTitle("Choose a Directory to Scan");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }

    /**
     * Main Method. Checks for Program Arguments, if none given the GUI will be started.
     * 
     * @param args
     *            Program Arguments
     */
    public static void main(String[] args) {
        if (args.length != 0)  {
            Hasher hasher = new Hasher();
            try {
                String pathToOutputFile = hasher.hash(args[0]);
                String output = String.format("Successfully created %s as the Output File!", pathToOutputFile);
                System.out.println(output);
                String error = openFolderWithFile(pathToOutputFile);
                if (error != null)  {
                    System.err.println(error);
                }
                System.exit(0);
            } catch (IllegalArgumentException | FileNotFoundException | UnsupportedEncodingException e) {
                System.err.println(e.getMessage());
                System.exit(-1);
            }
        }
        
        Application.launch(args);
    }
 
}