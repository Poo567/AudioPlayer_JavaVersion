/* setPadding(new Insets(a, b, c, d)) means:
    a: Padding from the top (a pixels)
    b: Padding from the right (b pixels)
    c: Padding from the bottom (c pixels)
    d: Padding from the left (d pixels) */
//        vboxTop.setPadding(new Insets(50, 0, 0, 0));

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.List;

public class AudioPlayer extends Application {
    private MediaPlayer player;
    private Media media;
    private List<File> fileList;
    private int songIndex = 0;
    private Label songNamelabel;
    /* start method
     - is the entry point for the JavaFX application
     - is called automatically after the application is launched */
    @Override
    public void start(Stage primaryStage) {

        // Create objects
        Button playButton = new Button("Play");
        Button nextButton = new Button("Next");
        Button previousButton = new Button("Prev");
        Button addButton = new Button("Add");
        Slider volumeSlider = new Slider(0, 1, 0.25); // Min=0, Max=1, Initial=0.5
        songNamelabel = new Label("Upload a song to be played");
        songNamelabel.getStyleClass().add("label-bg"); // Apply the CSS class to the label

        // Add event handler for buttons
        addButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac") );
            fileList = fileChooser.showOpenMultipleDialog(primaryStage);
            if (fileList != null) {
                media = new Media(fileList.getFirst().toURI().toString());
                songNamelabel.setText(fileList.get(songIndex).getName());
                if(player != null){
                    player.stop();
                }
                player = new MediaPlayer(media);
            }
        });

        playButton.setOnAction(e -> {
            if (player != null) {
                if(player.getStatus() == MediaPlayer.Status.PLAYING){
                    System.out.println("pause");
                    player.pause();
                } else {
                    System.out.println("Player is playing");
                    player.play();
                }
            }
        });

        nextButton.setOnAction(e -> {
            if(player != null){
                player.stop();

                if(songIndex <= fileList.size()-2){
                     songIndex++;
                } else {
                    songIndex = 0;
                }
                media = new Media(fileList.get(songIndex).toURI().toString());
                songNamelabel.setText(fileList.get(songIndex).getName());
                player = new MediaPlayer(media);
                player.play();
            }
        });

        previousButton.setOnAction(e -> {
            if(player != null){
                player.stop();

                if(songIndex >= 1){
                    songIndex--;
                } else {
                    songIndex = fileList.size()-1;
                }
                media = new Media(fileList.get(songIndex).toURI().toString());
                songNamelabel.setText(fileList.get(songIndex).getName());
                player = new MediaPlayer(media);
                player.play();
            }
        });

        volumeSlider.setOnMouseClicked(e -> player.setVolume(volumeSlider.getValue()));

        // Align elements in a HBox: 70 is the spacing between the elements
        HBox hboxTop = new HBox(30);
        hboxTop.getChildren().addAll(previousButton, playButton, nextButton);
        hboxTop.setAlignment(Pos.CENTER);
        hboxTop.setPadding(new Insets(75, 0, 0, 0));

        // Align elements in a HBox: 70 is the spacing between the elements
        HBox hboxBottom = new HBox(100);
        hboxBottom.getChildren().addAll(addButton, volumeSlider);
        hboxBottom.setAlignment(Pos.CENTER);
        hboxBottom.setPadding(new Insets(300, 0, 0, 0));

        VBox vbox = new VBox(songNamelabel, hboxTop, hboxBottom);
        vbox.setAlignment(Pos.CENTER);

        // Create a scene with the box
        Scene scene = new Scene(vbox, 450, 700);
        scene.getStylesheets().add("style.css");

        // Set up the stage
        primaryStage.setTitle("Audio Player");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
