package main.java.clinique.controller;


import javafx.event.ActionEvent; 
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import main.java.clinique.util.SessionManager;

public class HeaderController {


    @FXML
    private void goHome(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/dashboard.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
