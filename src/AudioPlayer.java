/* setPadding(new Insets(a, b, c, d)) means:
    a: Padding from the top (a pixels)
    b: Padding from the right (b pixels)
    c: Padding from the bottom (c pixels)
    d: Padding from the left (d pixels) */

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
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
    private Image themeImage;
    private Image gifImage;
    private ImageView imageView;
    private  Slider volumeSlider;

    /* start method
     - is the entry point for the JavaFX application
     - is called automatically after the application is launched */
    @Override
    public void start(Stage primaryStage) {

        // Create objects
        Button playButton = createButton("file:./../resources/pics/Start_Button.png", 20, 20, "Play");
        Button nextButton = createButton("file:./../resources/pics/Next_Button.png", 15, 15, "Next");
        Button previousButton = createButton("file:./../resources/pics/Previous_Button.png", 15, 15, "Previous");
        Button addButton = createButton("file:./../resources/pics/Add_Button.png", 20, 20, "Upload");
        volumeSlider = new Slider(0, 1, 0.25); // Min=0, Max=1, Initial=0.5
        songNamelabel = new Label("Upload a song to be played");
        songNamelabel.getStyleClass().add("label-bg"); // Apply the CSS class to the label

        themeImage = new Image("file:./../resources/pics/tiger.png");
        gifImage = new Image("file:./../resources/pics/SoundWave.gif");

        // Create an ImageView to display the GIF
        imageView = new ImageView(themeImage);

        // Optional: Set the size of the ImageView
        imageView.setFitWidth(250);
        imageView.setFitHeight(250);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        // Add event handler for buttons
        addButton.setOnAction(e -> selectSongsToBePlayed(primaryStage));
        playButton.setOnAction(e -> playSong());
        nextButton.setOnAction(e -> progressToNextSong());
        previousButton.setOnAction(e ->progressToPreviousSong());

        volumeSlider.setOnMouseClicked(e -> changePlayerVolume());

        // Align elements in a HBox: 70 is the spacing between the elements
        HBox hboxTop = new HBox(30);
        hboxTop.getChildren().addAll(previousButton, playButton, nextButton);
        hboxTop.setAlignment(Pos.CENTER);
        hboxTop.setPadding(new Insets(30, 0, 0, 0));

        HBox hBoxMiddle = new HBox();
        hBoxMiddle.getChildren().addAll(imageView);
        hBoxMiddle.setAlignment(Pos.CENTER);
        hBoxMiddle.setMinSize(250, 300);
        hBoxMiddle.setPadding(new Insets(20, 0, 0, 0));

        // Align elements in a HBox: 70 is the spacing between the elements
        HBox hboxBottom = new HBox(100);
        hboxBottom.getChildren().addAll(addButton, volumeSlider);
        hboxBottom.setAlignment(Pos.CENTER);
        hboxBottom.setPadding(new Insets(30, 0, 0, 0));

        VBox vbox = new VBox(songNamelabel, hboxTop,hBoxMiddle, hboxBottom);
        vbox.setAlignment(Pos.CENTER);
        vbox.getStyleClass().add("root");
        vbox.setOnDragOver((DragEvent event) -> {
            if (event.getGestureSource() != vbox && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        vbox.setOnDragDropped(this::dragInSongsToBePlayed);

        // Create a scene with the box
        Scene scene = new Scene(vbox, 370, 550);
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

    private void selectSongsToBePlayed(Stage stage){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac") );
        fileList = fileChooser.showOpenMultipleDialog(stage);
        if (fileList != null) {
            File song = fileList.getFirst();
            setUpMediaPlayer(song);
        }
    }

    private void dragInSongsToBePlayed(DragEvent event){
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            fileList = db.getFiles();  // Get files from dragboard
            if (!fileList.isEmpty()) {
                File song = fileList.getFirst();
                setUpMediaPlayer(song);
            }
        }
        event.setDropCompleted(true);
        event.consume();
    }
    
    private void playSong(){
        if (player != null) {
            if(player.getStatus() == MediaPlayer.Status.PLAYING){
                System.out.println("pause");
                player.pause();
                imageView.setImage(themeImage);
            } else {
                System.out.println("Player is playing");
                player.play();
                imageView.setImage(gifImage);
            }
        }
    }

    private void progressToNextSong() {
        if (fileList == null || fileList.isEmpty()) {
            System.out.println("No songs in the playlist.");
            return; // Exit the method if fileList is null or empty
        }
        if(songIndex < fileList.size() - 1){
            songIndex++;
        } else {
            songIndex = 0;
        }
        File song = fileList.get(songIndex);
        setUpMediaPlayer(song);
    }

    private void progressToPreviousSong(){
        if (fileList == null || fileList.isEmpty()) {
            System.out.println("No songs in the playlist.");
            return; // Exit the method if fileList is null or empty
        }
        if(songIndex >= 1){
            songIndex--;
        } else {
            songIndex = fileList.size()-1;
        }
        File song = fileList.get(songIndex);
        setUpMediaPlayer(song);
    }

    private void changePlayerVolume(){
        if (player != null) {
            player.setVolume(volumeSlider.getValue());
        }
    }

    private void setUpMediaPlayer(File song) {
        if(player != null) {
            player.stop();
        }
        player = new MediaPlayer(new Media(song.toURI().toString()));
        changePlayerVolume();
        player.setOnEndOfMedia(this::progressToNextSong);
        player.play();
        imageView.setImage(gifImage);
        songNamelabel.setText(song.getName());
    }

    private Button createButton(String imagePath, int imageWidth, int imageHeight, String text){
        Button button = new Button();
        ImageView buttonImage = new ImageView((new Image(imagePath)));
        buttonImage.setFitHeight(imageHeight);
        buttonImage.setFitWidth(imageWidth);
        button.setGraphic(buttonImage);
        button.setTooltip(new Tooltip(text));
        return button;
    }
}

