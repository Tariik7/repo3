package main.java.clinique.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.java.clinique.dao.MedecinDAO;
import main.java.clinique.dao.SecretaireDAO;
import main.java.clinique.dao.UtilisateurDAO;
import main.java.clinique.model.Medecin;
import main.java.clinique.model.Secretaire;
import main.java.clinique.model.Utilisateur;
import java.sql.SQLException;
import java.util.List;

public class UtilisateurController {

    // Composants de la vue liste
    @FXML private TableView<Utilisateur> tableUtilisateurs;
    @FXML private TableColumn<Utilisateur, String> colLogin;
    @FXML private TableColumn<Utilisateur, String> colRole;
    @FXML private TableColumn<Utilisateur, Void> colActions;
    @FXML private TextField searchField;

    // Composants du formulaire
    @FXML private TextField tfLogin;
    @FXML private PasswordField tfMotDePasse;
    @FXML private ChoiceBox<String> cbRole;
    @FXML private TextField tfNom;
    @FXML private TextField tfPrenom;
    @FXML private TextField tfTelephone;
    @FXML private TextField tfSpecialite;
    @FXML private VBox vbChampsSpecifiques;
    @FXML private Label lblErreur;

    private Utilisateur utilisateur;
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    @FXML
    public void initialize() {
        // Initialisation de la table (vue liste)
        if (tableUtilisateurs != null) {
            colLogin.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLogin()));
            colRole.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRole()));
            ajouterColonneActions();
            chargerUtilisateurs();
        }

        // Initialisation du formulaire (vues ajout/modification)
        if (cbRole != null) {
            // Remplir la liste des rôles
            cbRole.setItems(FXCollections.observableArrayList("admin", "medecin", "secretaire"));
            
            // Sélectionner le premier rôle par défaut si aucun n'est sélectionné
            if (cbRole.getValue() == null) {
                cbRole.getSelectionModel().selectFirst();
            }
            
            // Ajouter un écouteur pour le changement de rôle
            cbRole.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && vbChampsSpecifiques != null) {
                    // Afficher/masquer les champs spécifiques
                    boolean estPersonnel = "medecin".equals(newVal) || "secretaire".equals(newVal);
                    vbChampsSpecifiques.setVisible(estPersonnel);
                    vbChampsSpecifiques.setManaged(estPersonnel);
                    
                    // Afficher/masquer le champ spécialité pour les médecins
                    if (tfSpecialite != null) {
                        boolean estMedecin = "medecin".equals(newVal);
                        tfSpecialite.setVisible(estMedecin);
                        tfSpecialite.setManaged(estMedecin);
                    }
                }
            });
            
            // Déclencher l'affichage initial
            String roleInitial = cbRole.getValue();
            if (roleInitial != null && vbChampsSpecifiques != null) {
                boolean estPersonnel = "medecin".equals(roleInitial) || "secretaire".equals(roleInitial);
                vbChampsSpecifiques.setVisible(estPersonnel);
                vbChampsSpecifiques.setManaged(estPersonnel);
                
                if (tfSpecialite != null) {
                    tfSpecialite.setVisible("medecin".equals(roleInitial));
                    tfSpecialite.setManaged("medecin".equals(roleInitial));
                }
            }
        }
    }

    private void ajouterColonneActions() {
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnVoir = new Button("Voir");
            private final Button btnModifier = new Button("Modifier");
            private final Button btnSupprimer = new Button("Supprimer");

            {
                // Style des boutons
                btnVoir.getStyleClass().add("button-view");
                btnModifier.getStyleClass().add("button-edit");
                btnSupprimer.getStyleClass().add("button-delete");
                
                // Définir une petite taille pour les boutons
                btnVoir.setPrefWidth(60);
                btnModifier.setPrefWidth(70);
                btnSupprimer.setPrefWidth(80);
                
                // Action pour le bouton Voir
                btnVoir.setOnAction(e -> {
                    Utilisateur u = getTableView().getItems().get(getIndex());
                    afficherDetailsUtilisateur(u);
                });
                
                // Action pour le bouton Modifier
                btnModifier.setOnAction(e -> {
                    Utilisateur u = getTableView().getItems().get(getIndex());
                    handleModifierUtilisateur(u);
                });
                
                // Action pour le bouton Supprimer
                btnSupprimer.setOnAction(e -> {
                    Utilisateur u = getTableView().getItems().get(getIndex());
                    confirmerSuppression(u);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(5, btnVoir, btnModifier, btnSupprimer);
                    setGraphic(hbox);
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
        
        // Remplir les champs avec les données de l'utilisateur
        if (tfLogin != null) tfLogin.setText(utilisateur.getLogin());
        if (cbRole != null) cbRole.setValue(utilisateur.getRole());
        
        try {
            // Charger les données spécifiques selon le rôle
            String role = utilisateur.getRole();
            int id = utilisateur.getId_utilisateur();
            
            if ("medecin".equals(role)) {
                chargerDonneesMedecin(id);
            } else if ("secretaire".equals(role)) {
                chargerDonneesSecretaire(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void chargerDonneesMedecin(int id) {
        try {
            MedecinDAO medecinDAO = new MedecinDAO();
            List<Medecin> medecins = medecinDAO.listerMedecins();
            
            for (Medecin m : medecins) {
                if (m.getId() == id) {
                    if (tfNom != null) tfNom.setText(m.getNom());
                    if (tfPrenom != null) tfPrenom.setText(m.getPrenom());
                    if (tfTelephone != null) tfTelephone.setText(m.getTelephone());
                    if (tfSpecialite != null) tfSpecialite.setText(m.getSpecialite());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void chargerDonneesSecretaire(int id) {
        try {
            SecretaireDAO secretaireDAO = new SecretaireDAO();
            Secretaire s = secretaireDAO.trouverParId(id);
            
            if (s != null) {
                if (tfNom != null) tfNom.setText(s.getNom());
                if (tfPrenom != null) tfPrenom.setText(s.getPrenom());
                if (tfTelephone != null) tfTelephone.setText(s.getTelephone());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEnregistrer() {
        try {
            if (tfLogin == null || cbRole == null) {
                return;
            }

            String login = tfLogin.getText().trim();
            String role = cbRole.getValue();
            String motDePasse = tfMotDePasse != null ? tfMotDePasse.getText().trim() : "";

            // Validation des champs communs
            if (login.isEmpty() || role == null || role.isEmpty()) {
                if (lblErreur != null) lblErreur.setText("Login et rôle sont obligatoires.");
                return;
            }
            
            // Validation des champs spécifiques
            if ("medecin".equals(role) || "secretaire".equals(role)) {
                if (tfNom == null || tfPrenom == null || tfNom.getText().trim().isEmpty() || tfPrenom.getText().trim().isEmpty()) {
                    if (lblErreur != null) lblErreur.setText("Nom et prénom sont obligatoires pour ce rôle.");
                    return;
                }
                
                if ("medecin".equals(role) && tfSpecialite != null && tfSpecialite.getText().trim().isEmpty()) {
                    if (lblErreur != null) lblErreur.setText("La spécialité est obligatoire pour un médecin.");
                    return;
                }
            }

            // Création ou mise à jour
            boolean estCreation = (utilisateur == null || utilisateur.getId_utilisateur() <= 0);
            if (estCreation) {
                utilisateur = new Utilisateur();
            }
            
            utilisateur.setLogin(login);
            utilisateur.setRole(role);
            
            if (!motDePasse.isEmpty()) {
                utilisateur.setMotDePasse(motDePasse);
            } else if (estCreation) {
                utilisateur.setMotDePasse("1234"); // Mot de passe par défaut pour les nouveaux utilisateurs
            }
            
            int userId;
            
            if (estCreation) {
                userId = utilisateurDAO.ajouterUtilisateur(utilisateur);
            } else {
                utilisateurDAO.modifierUtilisateur(utilisateur);
                userId = utilisateur.getId_utilisateur();
            }
            
            // Traitement spécifique pour médecins et secrétaires
            if ("medecin".equals(role)) {
                enregistrerMedecin(userId, login, estCreation);
            } else if ("secretaire".equals(role)) {
                enregistrerSecretaire(userId, login, estCreation);
            }
            
            // Fermer la fenêtre
            ((Stage) tfLogin.getScene().getWindow()).close();
            
        } catch (Exception e) {
            e.printStackTrace();
            if (lblErreur != null) lblErreur.setText("Erreur: " + e.getMessage());
        }
    }
    
    private void enregistrerMedecin(int userId, String login, boolean estCreation) throws SQLException {
        Medecin medecin = new Medecin();
        medecin.setId(userId);
        medecin.setNom(tfNom.getText().trim());
        medecin.setPrenom(tfPrenom.getText().trim());
        medecin.setEmail(login);
        medecin.setTelephone(tfTelephone != null ? tfTelephone.getText().trim() : "");
        medecin.setSpecialite(tfSpecialite != null ? tfSpecialite.getText().trim() : "");
        
        MedecinDAO medecinDAO = new MedecinDAO();
        if (estCreation) {
            medecinDAO.ajouterMedecinAvecUtilisateur(medecin, userId);
        } else {
            medecinDAO.modifierMedecin(medecin);
        }
    }
    
    private void enregistrerSecretaire(int userId, String login, boolean estCreation) throws SQLException {
        Secretaire secretaire = new Secretaire();
        secretaire.setId(userId);
        secretaire.setNom(tfNom.getText().trim());
        secretaire.setPrenom(tfPrenom.getText().trim());
        secretaire.setEmail(login);
        secretaire.setTelephone(tfTelephone != null ? tfTelephone.getText().trim() : "");
        
        SecretaireDAO secretaireDAO = new SecretaireDAO();
        if (estCreation) {
            secretaireDAO.ajouterSecretaireAvecUtilisateur(secretaire, userId);
        } else {
            secretaireDAO.modifierSecretaire(secretaire);
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

    @FXML
    private void handleHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et définir le rôle utilisateur
            DashboardController controller = loader.getController();
            controller.setUserRole("admin"); // Par défaut, utilisez "admin" ou récupérez le rôle de SessionManager

            Stage stage = (Stage) tableUtilisateurs.getScene().getWindow();
            stage.setTitle("Tableau de bord");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Impossible de charger le tableau de bord: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    /**
     * Affiche les détails d'un utilisateur dans une fenêtre modale
     */
    private void afficherDetailsUtilisateur(Utilisateur utilisateur) {
        try {
            // Créer une boîte de dialogue pour afficher les détails
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Détails de l'utilisateur");
            dialog.setHeaderText("Informations sur " + utilisateur.getLogin());
            
            // Créer le contenu
            VBox content = new VBox(10);
            content.setPadding(new javafx.geometry.Insets(20));
            
            // Ajouter les informations de base de l'utilisateur
            content.getChildren().add(new Label("ID: " + utilisateur.getId_utilisateur()));
            content.getChildren().add(new Label("Login: " + utilisateur.getLogin()));
            content.getChildren().add(new Label("Rôle: " + utilisateur.getRole()));
            
            // Ajouter les informations spécifiques selon le rôle
            if ("medecin".equals(utilisateur.getRole())) {
                ajouterDetailsMedecin(content, utilisateur.getId_utilisateur());
            } else if ("secretaire".equals(utilisateur.getRole())) {
                ajouterDetailsSecretaire(content, utilisateur.getId_utilisateur());
            }
            
            // Configurer la boîte de dialogue
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            
            // Afficher la boîte de dialogue
            dialog.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'afficher les détails: " + e.getMessage());
        }
    }
    
    /**
     * Ajoute les détails d'un médecin au contenu de la boîte de dialogue
     */
    private void ajouterDetailsMedecin(VBox content, int id) {
        try {
            MedecinDAO medecinDAO = new MedecinDAO();
            List<Medecin> medecins = medecinDAO.listerMedecins();
            
            for (Medecin m : medecins) {
                if (m.getId() == id) {
                    content.getChildren().add(new Separator());
                    content.getChildren().add(new Label("Détails du médecin:"));
                    content.getChildren().add(new Label("Nom: " + m.getNom()));
                    content.getChildren().add(new Label("Prénom: " + m.getPrenom()));
                    content.getChildren().add(new Label("Email: " + m.getEmail()));
                    content.getChildren().add(new Label("Téléphone: " + m.getTelephone()));
                    content.getChildren().add(new Label("Spécialité: " + m.getSpecialite()));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            content.getChildren().add(new Label("Erreur lors du chargement des détails du médecin: " + e.getMessage()));
        }
    }
    
    /**
     * Ajoute les détails d'un secrétaire au contenu de la boîte de dialogue
     */
    private void ajouterDetailsSecretaire(VBox content, int id) {
        try {
            SecretaireDAO secretaireDAO = new SecretaireDAO();
            Secretaire s = secretaireDAO.trouverParId(id);
            
            if (s != null) {
                content.getChildren().add(new Separator());
                content.getChildren().add(new Label("Détails du secrétaire:"));
                content.getChildren().add(new Label("Nom: " + s.getNom()));
                content.getChildren().add(new Label("Prénom: " + s.getPrenom()));
                content.getChildren().add(new Label("Email: " + s.getEmail()));
                content.getChildren().add(new Label("Téléphone: " + s.getTelephone()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            content.getChildren().add(new Label("Erreur lors du chargement des détails du secrétaire: " + e.getMessage()));
        }
    }
    
    /**
     * Demande confirmation avant de supprimer un utilisateur
     */
    private void confirmerSuppression(Utilisateur utilisateur) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Suppression de l'utilisateur " + utilisateur.getLogin());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet utilisateur ? Cette action est irréversible.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                supprimerUtilisateur(utilisateur);
            }
        });
    }
    
    /**
     * Supprime un utilisateur et ses données associées (médecin ou secrétaire)
     */
    private void supprimerUtilisateur(Utilisateur utilisateur) {
        try {
            int id = utilisateur.getId_utilisateur();
            String role = utilisateur.getRole();
            
            // Supprimer dans la table associée selon le rôle
            if ("medecin".equals(role)) {
                // Supprimer de la table médecin
                MedecinDAO medecinDAO = new MedecinDAO();
                medecinDAO.supprimerMedecin(id);
            } else if ("secretaire".equals(role)) {
                // Supprimer de la table secrétaire
                SecretaireDAO secretaireDAO = new SecretaireDAO();
                secretaireDAO.supprimerSecretaire(id);
            }
            
            // Supprimer de la table utilisateur
            utilisateurDAO.supprimerUtilisateur(id);
            
            // Rafraîchir la liste
            chargerUtilisateurs();
            
            // Afficher un message de confirmation
            showAlert(Alert.AlertType.INFORMATION, "Suppression réussie", 
                    "L'utilisateur " + utilisateur.getLogin() + " a été supprimé avec succès.");
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de suppression", 
                    "Impossible de supprimer l'utilisateur: " + e.getMessage());
        }
    }
    
    /**
     * Affiche une alerte avec le type, titre et message spécifiés
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
