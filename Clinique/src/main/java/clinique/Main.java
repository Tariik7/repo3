package main.java.clinique;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader splashLoader = new FXMLLoader(getClass().getResource("/views/splash.fxml"));
        Parent splashRoot = splashLoader.load();

        Stage splashStage = new Stage(StageStyle.UNDECORATED);
        splashStage.setScene(new Scene(splashRoot));
        splashStage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(3)); // Durée du splash
        delay.setOnFinished(event -> {
            splashStage.close();

            try {
                Parent loginRoot = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
                Stage loginStage = new Stage();
                loginStage.setTitle("Connexion");
                loginStage.setScene(new Scene(loginRoot));
                loginStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        delay.play();
    }
   
    public static void main(String[] args) {
        launch(args);                         
    }
}
