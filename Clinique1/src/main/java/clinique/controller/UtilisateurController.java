package main.java.clinique.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import main.java.clinique.dao.UtilisateurDAO;
import main.java.clinique.model.Utilisateur;

public class UtilisateurController {

    @FXML private TableView<Utilisateur> tableUtilisateurs;
    @FXML private TableColumn<Utilisateur, String> colLogin;
    @FXML private TableColumn<Utilisateur, String> colRole;
    @FXML private TableColumn<Utilisateur, Void> colActions;
    @FXML private TextField searchField;

    // Champs du formulaire (ajout/modif)
    @FXML private TextField tfLogin;
    @FXML private PasswordField tfMotDePasse;
    @FXML private ChoiceBox<String> cbRole;
    @FXML private Label lblErreur;

    private Utilisateur utilisateur;
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    @FXML
    public void initialize() {
        if (tableUtilisateurs != null) {
            colLogin.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLogin()));
            colRole.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRole()));
            ajouterColonneActions();
            chargerUtilisateurs();
        }

        if (cbRole != null) {
            cbRole.setItems(FXCollections.observableArrayList("admin", "medecin", "secretaire"));
        }
    }

    private void ajouterColonneActions() {
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnModifier = new Button("Modifier");

            {
                btnModifier.getStyleClass().add("button-edit");
                btnModifier.setOnAction(e -> {
                    Utilisateur u = getTableView().getItems().get(getIndex());
                    handleModifierUtilisateur(u);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5, btnModifier));
                }
            }
        });
    }

    public void chargerUtilisateurs() {
        try {
            ObservableList<Utilisateur> list = FXCollections.observableArrayList(utilisateurDAO.listerUtilisateurs());
            tableUtilisateurs.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAjouterUtilisateur() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/utilisateur/ajouter.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un utilisateur");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
            chargerUtilisateurs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleModifierUtilisateur(Utilisateur utilisateur) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/utilisateur/modifier.fxml"));
            Parent root = loader.load();
            UtilisateurController controller = loader.getController();
            controller.setUtilisateur(utilisateur);

            Stage stage = new Stage();
            stage.setTitle("Modifier utilisateur");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            chargerUtilisateurs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        if (tfLogin != null) tfLogin.setText(utilisateur.getLogin());
        if (cbRole != null) cbRole.setValue(utilisateur.getRole());
    }

    @FXML
    private void handleEnregistrer() {
        try {
            if (tfLogin == null || cbRole == null) return;

            String login = tfLogin.getText().trim();
            String role = cbRole.getValue();
            String motDePasse = tfMotDePasse != null ? tfMotDePasse.getText().trim() : "";

            if (login.isEmpty() || role == null || role.isEmpty()) {
                if (lblErreur != null) lblErreur.setText("Tous les champs sont obligatoires.");
                return;
            }

            if (utilisateur == null) utilisateur = new Utilisateur();

            utilisateur.setLogin(login);
            utilisateur.setRole(role);

            if (!motDePasse.isEmpty()) {
                utilisateur.setMotDePasse(motDePasse);
            }

            if (utilisateur.getId_utilisateur() > 0) {
                utilisateurDAO.modifierUtilisateur(utilisateur);
            } else {
                utilisateurDAO.ajouterUtilisateur(utilisateur);
            }

            ((Stage) tfLogin.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            if (lblErreur != null) lblErreur.setText("Erreur lors de l'enregistrement.");
        }
    }

    @FXML
    private void handleAnnuler() {
        ((Stage) tfLogin.getScene().getWindow()).close();
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        try {
            ObservableList<Utilisateur> all = FXCollections.observableArrayList(utilisateurDAO.listerUtilisateurs());
            ObservableList<Utilisateur> filtered = all.filtered(u ->
                u.getLogin().toLowerCase().contains(keyword) || u.getRole().toLowerCase().contains(keyword)
            );
            tableUtilisateurs.setItems(filtered);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
