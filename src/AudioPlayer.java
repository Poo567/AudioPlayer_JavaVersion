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
        Button playButton = new Button();
        ImageView playButtonImage = new ImageView(new Image("file:/E:/java_workspace/JavaPractice/AudioPlayer_JavaVersion/resources/pics/Start_Button.png"));
        playButtonImage.setFitWidth(20); // Set image width
        playButtonImage.setFitHeight(20); // Set image height
        playButton.setGraphic(playButtonImage);

        Button nextButton = new Button();
        ImageView nextImageButton = new ImageView(new Image("file:/E:/java_workspace/JavaPractice/AudioPlayer_JavaVersion/resources/pics/Next_Button.png"));
        nextImageButton.setFitWidth(15); // Set image width
        nextImageButton.setFitHeight(15); // Set image height
        nextButton.setGraphic(nextImageButton);

        Button previousButton = new Button();
        ImageView previousImageButton = new ImageView(new Image("file:/E:/java_workspace/JavaPractice/AudioPlayer_JavaVersion/resources/pics/Previous_Button.png"));
        previousImageButton.setFitWidth(15); // Set image width
        previousImageButton.setFitHeight(15); // Set image height
        previousButton.setGraphic(previousImageButton);

        Button addButton = new Button();
        ImageView addImageButton = new ImageView(new Image("file:/E:/java_workspace/JavaPractice/AudioPlayer_JavaVersion/resources/pics/Add_Button.png"));
        addImageButton.setFitWidth(20); // Set image width
        addImageButton.setFitHeight(20); // Set image height
        addButton.setGraphic(addImageButton);

        volumeSlider = new Slider(0, 1, 0.25); // Min=0, Max=1, Initial=0.5
        songNamelabel = new Label("Upload a song to be played");
        songNamelabel.getStyleClass().add("label-bg"); // Apply the CSS class to the label

        themeImage = new Image("file:/E:/java_workspace/JavaPractice/AudioPlayer_JavaVersion/resources/pics/tiger.png");
        gifImage = new Image("file:/E:/java_workspace/JavaPractice/AudioPlayer_JavaVersion/resources/pics/SoundWave.gif");

        // Create an ImageView to display the GIF
        imageView = new ImageView(themeImage);

        // Optional: Set the size of the ImageView
        imageView.setFitWidth(250);
        imageView.setFitHeight(250);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

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
                setUpPlayer(media);
            }
        });

        playButton.setOnAction(e -> {
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
                setUpPlayer(media);
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
                setUpPlayer(media);
            }
        });

        volumeSlider.setOnMouseClicked(e -> {
            if (player != null) {
                player.setVolume(volumeSlider.getValue());
            }
        });

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

        vbox.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                fileList = db.getFiles();  // Get files from dragboard
                if (!fileList.isEmpty()) {
                    media = new Media(fileList.getFirst().toURI().toString());
                    songNamelabel.setText(fileList.getFirst().getName());
                    if (player != null) {
                        player.stop();
                    }
                    player = new MediaPlayer(media);
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

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

    private void progressToNextSong() {
        if(songIndex < fileList.size() - 1){
            songIndex++;
        } else {
            songIndex = 0;
        }
        media = new Media(fileList.get(songIndex).toURI().toString());
        songNamelabel.setText(fileList.get(songIndex).getName());
        setUpPlayer(media);
    }

    private void setUpPlayer(Media media) {
        player = new MediaPlayer(media);
        player.setVolume(volumeSlider.getValue());
        player.setOnEndOfMedia(this::progressToNextSong);
        player.play();
        imageView.setImage(gifImage);
    }
}

