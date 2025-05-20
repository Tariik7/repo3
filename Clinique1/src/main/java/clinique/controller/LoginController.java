package main.java.clinique.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import main.java.clinique.dao.UtilisateurDAO;
import main.java.clinique.model.Utilisateur;
import main.java.clinique.util.SessionManager;

public class LoginController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    @FXML
    private void handleLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        try {
            Utilisateur user = utilisateurDAO.trouverParLoginEtMotDePasse(login, password);
            if (user != null) {
                SessionManager.setUtilisateur(user); // 🔐 On enregistre l'utilisateur connecté
                errorLabel.setText("");
                redirigerVersDashboard(user.getRole());
            } else {
                errorLabel.setText("Login ou mot de passe incorrect !");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Erreur : " + e.getMessage());
        }
    }

    private void redirigerVersDashboard(String role) throws Exception {
        String fxml = switch (role) {
            case "admin", "medecin", "secretaire" -> "/views/dashboard.fxml";
            default -> throw new IllegalArgumentException("Rôle inconnu : " + role);
        };

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();

        // Récupérer le controller du Dashboard
        DashboardController dashboardController = loader.getController();
        // Passer le rôle à DashboardController pour charger le header adapté
        dashboardController.setUserRole(role);

        Stage stage = (Stage) loginField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Tableau de bord - Clinique");
        stage.setWidth(1000);
        stage.setHeight(700);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

}
