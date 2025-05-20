package main.java.clinique.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SplashController {

    @FXML private ImageView logo;

    @FXML
    public void initialize() {
        logo.setImage(new Image("/images/logo.png")); // ajoute un logo dans resources/images
    }
}
