package main.java.clinique.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import main.java.clinique.dao.SecretaireDAO;
import main.java.clinique.dao.UtilisateurDAO;
import main.java.clinique.model.Medecin;
// import main.java.clinique.dao.UtilisateurDAO; // Non utilisé
import main.java.clinique.model.Secretaire;
// import main.java.clinique.util.SessionManager; // Non utilisé

import java.io.IOException;
import java.sql.SQLException;

public class SecretaireController {

    // Elements UI pour la liste des secrétaires
    @FXML private TableView<Secretaire> tableSecretaires;
    @FXML private TableColumn<Secretaire, String> colNom;
    @FXML private TableColumn<Secretaire, String> colPrenom;
    @FXML private TableColumn<Secretaire, String> colEmail;
    @FXML private TableColumn<Secretaire, String> colTelephone;
    @FXML private TableColumn<Secretaire, Void> colActions;
    @FXML private TextField searchField;
    @FXML private BorderPane root;
    @FXML private ImageView homeImage;
    
    // Elements UI pour le formulaire d'ajout/modification
    @FXML private TextField tfNom;
    @FXML private TextField tfPrenom;
    @FXML private TextField tfEmail;
    @FXML private TextField tfTelephone;
    @FXML private Label lblErreur;
    
    private String userRole;
    private Secretaire secretaire;
    private boolean isNewSecretaire = false;

    private final SecretaireDAO secretaireDAO = new SecretaireDAO();

    @FXML
    public void initialize() {
        try {
            // Initialiser l'image d'accueil si elle existe dans cette vue
            if (homeImage != null) {
                homeImage.setImage(new Image(getClass().getResourceAsStream("/images/home.jpg")));
            }
            
            // Initialisation pour la vue liste
            if (tableSecretaires != null) {
                colNom.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
                colPrenom.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPrenom()));
                colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
                colTelephone.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTelephone()));

                ajouterColonneActions();
                chargerSecretaires();
            }
            
            // Si c'est le formulaire d'édition et que le secrétaire est défini
            if (tfNom != null && secretaire != null) {
                tfNom.setText(secretaire.getNom());
                tfPrenom.setText(secretaire.getPrenom());
                tfEmail.setText(secretaire.getEmail());
                tfTelephone.setText(secretaire.getTelephone());
                if (lblErreur != null) {
                    lblErreur.setText("");
                }
            }
            
            // Note: Ne pas charger automatiquement l'en-tête ici, car il est déjà chargé par le DashboardController
            // La méthode loadHeaderByRole() ne sera utilisée que lorsque setUserRole est appelée
            
        } catch (Exception e) {
            System.out.println("Erreur d'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setUserRole(String role) {
        this.userRole = role;
        try {
            loadHeaderByRole();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadHeaderByRole() throws IOException {
        // Si root est null, aucun besoin de charger un header
        if (root == null) {
            return;
        }
        
        String fxmlFile;
        if (userRole != null && !userRole.isEmpty()) {
            switch (userRole.toLowerCase()) {
                case "admin": fxmlFile = "/views/HeaderAdmin.fxml"; break;
                case "medecin": fxmlFile = "/views/HeaderMedecin.fxml"; break;
                case "secretaire": fxmlFile = "/views/HeaderSecretaire.fxml"; break;
                default: fxmlFile = "/views/HeaderAdmin.fxml"; break;
            }
        } else {
            fxmlFile = "/views/HeaderAdmin.fxml"; // Default
        }

        try {
            // Créer un nouveau ClassLoader pour éviter les conflits
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            // Éviter les conflits avec le controller actuel
            loader.setControllerFactory(c -> {
                // Si c'est DashboardController, on utilise celui qui a été fourni
                if (c.equals(DashboardController.class)) {
                    return new DashboardController(); 
                }
                return null;
            });
            
            HBox header = loader.load();
            root.setTop(header);
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement du header: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
            Parent root = loader.load();

            // Passer le rôle à DashboardController
            DashboardController controller = loader.getController();
            if (controller != null) {
                // Définir le rôle utilisateur avant toute initialisation
                controller.setUserRole(userRole);
                // S'assurer que le controller est bien initialisé
                controller.initialize();
            }

            // Réutiliser la même fenêtre
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de bord");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du retour à l'accueil.");
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        try {
            ObservableList<Secretaire> all = FXCollections.observableArrayList(secretaireDAO.listerSecretaires());
            ObservableList<Secretaire> filtered = all.filtered(s ->
                s.getNom().toLowerCase().contains(keyword) ||
                s.getPrenom().toLowerCase().contains(keyword) ||
                (s.getEmail() != null && s.getEmail().toLowerCase().contains(keyword))
            );
            tableSecretaires.setItems(filtered);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur pendant la recherche.");
        }
    }

    @FXML
    private void handleRefresh() {
        chargerSecretaires();
    }

    @FXML
    private void handleAjouterSecretaire() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/secretaire/ajouter.fxml"));
            Parent root = loader.load();
            
            SecretaireController controller = loader.getController();
            
            // Créer un nouveau secrétaire et définir explicitement qu'il s'agit d'un nouveau secrétaire
            controller.secretaire = null; // Forcer à null pour déclencher la création dans handleEnregistrer
            controller.isNewSecretaire = true;
            
            Stage stage = new Stage();
            stage.setTitle("Ajouter un secrétaire");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
            
            chargerSecretaires();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ouverture du formulaire d'ajout.");
        }
    }

    public void setSecretaire(Secretaire secretaire) {
        this.secretaire = secretaire;
        
        // Si les champs du formulaire sont déjà initialisés, les remplir
        if (tfNom != null) {
            tfNom.setText(secretaire.getNom());
            tfPrenom.setText(secretaire.getPrenom());
            tfEmail.setText(secretaire.getEmail());
            tfTelephone.setText(secretaire.getTelephone());
            if (lblErreur != null) {
                lblErreur.setText("");
            }
        }
    }
    
    @FXML
    private void handleEnregistrer() {
        try {
            String nom = tfNom.getText().trim();
            String prenom = tfPrenom.getText().trim();
            String email = tfEmail.getText().trim();
            String telephone = tfTelephone.getText().trim();

            if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty() || email.isEmpty()) {
                lblErreur.setText("Veuillez remplir tous les champs.");
                return;
            }

            if (secretaire == null) {
                // Ajout
                String login = email;
                String motDePasse = "1234";
                int idUtilisateur = new UtilisateurDAO().creerUtilisateur(login, motDePasse, "secretaire");
                secretaire = new Secretaire(idUtilisateur, nom, prenom, email, telephone);
                secretaireDAO.ajouterSecretaireAvecUtilisateur(secretaire, idUtilisateur);
            } else {
                // Modification
                secretaire.setNom(nom);
                secretaire.setPrenom(prenom);
                secretaire.setTelephone(telephone);
                secretaire.setEmail(email);

                secretaireDAO.modifierSecretaire(secretaire);
                new UtilisateurDAO().modifierUtilisateurDepuisSecretaire(secretaire);
            }

            ((Stage) tfNom.getScene().getWindow()).close();

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
        if (tfNom != null) {
            Stage stage = (Stage) tfNom.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        }
    }

    private void chargerSecretaires() {
        try {
            ObservableList<Secretaire> liste = FXCollections.observableArrayList(secretaireDAO.listerSecretaires());
            tableSecretaires.setItems(liste);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors du chargement des secrétaires.");
        }
    }

    private void ajouterColonneActions() {
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnVoir = new Button("Voir");
            private final Button btnModifier = new Button("Modifier");
            private final Button btnSupprimer = new Button("Supprimer");

            {
                btnVoir.getStyleClass().add("button-view");
                btnModifier.getStyleClass().add("button-edit");
                btnSupprimer.getStyleClass().add("button-delete");

                btnVoir.setOnAction(e -> {
                    Secretaire sec = getTableView().getItems().get(getIndex());
                    afficherDetails(sec);
                });

                btnModifier.setOnAction(e -> {
                    Secretaire sec = getTableView().getItems().get(getIndex());
                    ouvrirModifierSecretaire(sec);
                });

                btnSupprimer.setOnAction(e -> {
                    Secretaire sec = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer ce secrétaire ?", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            try {
                                secretaireDAO.supprimerSecretaire(sec.getId());
                                chargerSecretaires();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                showAlert("Erreur lors de la suppression.");
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(5, btnVoir, btnModifier, btnSupprimer);
                    setGraphic(box);
                }
            }
        });
    }

    private void afficherDetails(Secretaire sec) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails du Secrétaire");
        alert.setHeaderText(sec.getNom() + " " + sec.getPrenom());
        alert.setContentText(
            "Email : " + sec.getEmail() + "\n" +
            "Téléphone : " + sec.getTelephone()
        );
        alert.showAndWait();
    }

    private void ouvrirModifierSecretaire(Secretaire sec) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/secretaire/modifier.fxml"));
            Parent root = loader.load();

            SecretaireController controller = loader.getController();
            controller.setSecretaire(sec);
            controller.isNewSecretaire = false;

            Stage stage = new Stage();
            stage.setTitle("Modifier un secrétaire");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            chargerSecretaires();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ouverture du formulaire de modification.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
