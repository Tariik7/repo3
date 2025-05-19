package main.java.clinique.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.java.clinique.dao.SecretaireDAO;
import main.java.clinique.dao.UtilisateurDAO;
import main.java.clinique.model.Secretaire;

import java.sql.SQLException;

public class SecretaireEditController {

    @FXML private TextField tfNom;
    @FXML private TextField tfPrenom;
    @FXML private TextField tfEmail;
    @FXML private TextField tfTelephone;
    @FXML private Label lblErreur;

    private Secretaire secretaire;
    private final SecretaireDAO secretaireDAO = new SecretaireDAO();
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private boolean isNewSecretaire = false;
    
    @FXML
    public void initialize() {
        // Initialisation si nécessaire
    }

    public void setSecretaire(Secretaire secretaire) {
        this.secretaire = secretaire;
        if (secretaire.getId() == 0) {
            isNewSecretaire = true;
        } else {
            tfNom.setText(secretaire.getNom());
            tfPrenom.setText(secretaire.getPrenom());
            tfEmail.setText(secretaire.getEmail());
            tfTelephone.setText(secretaire.getTelephone());
            lblErreur.setText("");
        }
    }

    @FXML
    private void handleEnregistrer() {
        try {
            String nom = tfNom.getText().trim();
            String prenom = tfPrenom.getText().trim();
            String email = tfEmail.getText().trim();
            String telephone = tfTelephone.getText().trim();

            if (nom.isEmpty() || prenom.isEmpty()) {
                lblErreur.setText("Nom et prénom sont obligatoires.");
                return;
            }

            secretaire.setNom(nom);
            secretaire.setPrenom(prenom);
            secretaire.setEmail(email);
            secretaire.setTelephone(telephone);

            if (isNewSecretaire) {
                secretaireDAO.ajouterSecretaire(secretaire);
                // Éventuellement, créer ou lier un utilisateur système
                // utilisateurDAO.creerUtilisateurPourSecretaire(secretaire);
            } else {
                secretaireDAO.modifierSecretaire(secretaire);
                // Mettre à jour l'utilisateur système si nécessaire
                // utilisateurDAO.modifierUtilisateurDepuisSecretaire(secretaire);
            }

            fermerFenetre();

        } catch (SQLException e) {
            e.printStackTrace();
            lblErreur.setText("Erreur lors de l'enregistrement: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            lblErreur.setText("Erreur lors de l'enregistrement.");
        }
    }

    @FXML
    private void handleAnnuler() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) tfNom.getScene().getWindow();
        stage.close();
    }
}
