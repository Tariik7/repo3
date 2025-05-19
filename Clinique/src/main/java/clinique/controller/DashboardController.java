package main.java.clinique.controller;

import main.java.clinique.util.SessionManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import main.java.clinique.dao.MedecinDAO;
import main.java.clinique.dao.PatientDAO;
import main.java.clinique.dao.UtilisateurDAO;
import main.java.clinique.model.Medecin;
import main.java.clinique.model.Patient;

import java.io.IOException;

public class DashboardController {

    @FXML private TableView<Object> table; // peut contenir Medecin ou Patient
    @FXML private TableColumn<Object, String> colNom;
    @FXML private TableColumn<Object, String> colPrenom;
    @FXML private TableColumn<Object, String> colEmail;
    @FXML private TableColumn<Object, String> colTelephone;
    @FXML private TableColumn<Object, String> colAutre;
    
    @FXML private Button btnMedecins;
    @FXML private Button btnPatients;
    @FXML private Button btnSecretaires;
    
    @FXML private ImageView homeImage;

    @FXML private BorderPane root;
    
    private String userRole ;

    @FXML private TextField searchField;

    private final MedecinDAO medecinDAO = new MedecinDAO();
    private final PatientDAO patientDAO = new PatientDAO();

    private enum ModeAffichage { MEDECIN, PATIENT }
    private ModeAffichage modeActuel = ModeAffichage.MEDECIN;

    @FXML
    public void initialize() {
        try {
            // Vérifier si homeImage est null avant de l'utiliser
            if (homeImage != null) {
                try {
                    java.io.InputStream resourceStream = getClass().getResourceAsStream("/images/home.jpg");
                    if (resourceStream != null) {
                        Image image = new Image(resourceStream);
                        homeImage.setImage(image);
                    } else {
                        System.out.println("Ressource image not found: /images/home.jpg");
                    }
                } catch (Exception ex) {
                    System.out.println("Erreur lors du chargement de l'image: " + ex.getMessage());
                }
            }
            
            // Charger header dynamique seulement si root n'est pas null
            if (root != null) {
                loadHeaderByRole();
            }
            
        } catch (Exception e) {
            System.out.println("Erreur de chargement de l'image ou header : " + e);
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
        String fxmlFile;
        if (userRole != null && !userRole.isEmpty()) {
            switch (userRole.toLowerCase()) {
                case "admin": fxmlFile = "/views/HeaderAdmin.fxml"; break;
                case "medecin": fxmlFile = "/views/HeaderMedecin.fxml"; break;
                case "secretaire": fxmlFile = "/views/HeaderSecretaire.fxml"; break;
                default: fxmlFile = "/views/HeaderAdmin.fxml"; break;
            }
        } else {
            // Par défaut, utiliser le header Admin si aucun rôle n'est défini
            fxmlFile = "/views/HeaderAdmin.fxml";
        }
        
        try {
            // Vérifier que le chemin FXML et la racine ne sont pas nulls
            if (fxmlFile != null && root != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                HBox header = loader.load();
                
                // Si besoin, tu peux récupérer le controller du header et lui passer des infos
                // Exemple:
                // HeaderController controller = loader.getController();
                // controller.setParentController(this);
                
                root.setTop(header);
                System.out.println("En-tête chargé avec succès: " + fxmlFile);
            } else {
                System.out.println("Impossible de charger l'en-tête: fichier FXML ou root null");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement de l'en-tête: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Ajoute un setter pour changer le rôle (à appeler après login)
   

    
    
    @FXML
    private void handleAfficherMedecins() {
        try {
            ObservableList<Medecin> medecins = FXCollections.observableArrayList(medecinDAO.listerMedecins());
            table.setItems(FXCollections.observableArrayList(medecins));
            modeActuel = ModeAffichage.MEDECIN;

            // Reconfigurer les colonnes
            colNom.setCellValueFactory(data -> new SimpleStringProperty(((Medecin) data.getValue()).getNom()));
            colPrenom.setCellValueFactory(data -> new SimpleStringProperty(((Medecin) data.getValue()).getPrenom()));
            colEmail.setCellValueFactory(data -> new SimpleStringProperty(((Medecin) data.getValue()).getEmail()));
            colTelephone.setCellValueFactory(data -> new SimpleStringProperty(((Medecin) data.getValue()).getTelephone()));
            colAutre.setText("Spécialité");
            colAutre.setCellValueFactory(data -> new SimpleStringProperty(((Medecin) data.getValue()).getSpecialite()));

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur de chargement des médecins.");
        }
    }

    @FXML
    private void logout(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
        SessionManager.clearSession();
    }
    
    @FXML
    private void handleMedecins() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/medecin/lister.fxml"));
            Parent root = loader.load();
            
            // Récupérer la fenêtre actuelle et la réutiliser
            Stage currentStage = (Stage) btnMedecins.getScene().getWindow();
            currentStage.setTitle("Liste des Médecins");
            currentStage.setScene(new Scene(root, 1200, 700));
            
            // Passer les informations au controller
            MedecinsController controller = loader.getController();
            if (controller != null) {
                controller.setUserRole(userRole);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ouverture de la fenêtre des médecins.");
        }
    }


    @FXML
    private void handlePatients() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/patient/lister.fxml"));
            Parent root = loader.load();
            
            // Récupérer la fenêtre actuelle et la réutiliser
            Stage currentStage = (Stage) btnPatients.getScene().getWindow();
            currentStage.setTitle("Liste des Patients");
            currentStage.setScene(new Scene(root, 1200, 700));
            
            // Si vous voulez passer des informations au controller
            PatientsController controller = loader.getController();
            controller.setUserRole(userRole);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSecretaires() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/secretaire/lister.fxml"));
            Parent root = loader.load();
            
            // Récupérer la fenêtre actuelle et la réutiliser
            Stage currentStage = (Stage) btnSecretaires.getScene().getWindow();
            currentStage.setTitle("Liste des Secrétaires");
            currentStage.setScene(new Scene(root, 1200, 700));
            
            // Passer les informations au controller
            SecretaireController controller = loader.getController();
            if (controller != null) {
                controller.setUserRole(userRole);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ouverture de la fenêtre des secrétaires.");
        }
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();

        try {
            if (modeActuel == ModeAffichage.MEDECIN) {
                ObservableList<Medecin> all = FXCollections.observableArrayList(medecinDAO.listerMedecins());
                ObservableList<Medecin> filtered = all.filtered(m ->
                        m.getNom().toLowerCase().contains(searchTerm) ||
                        m.getPrenom().toLowerCase().contains(searchTerm) ||
                        m.getEmail().toLowerCase().contains(searchTerm));
                table.setItems(FXCollections.observableArrayList(filtered));
            } else {
                ObservableList<Patient> all = FXCollections.observableArrayList(patientDAO.listerPatients());
                ObservableList<Patient> filtered = all.filtered(p ->
                        p.getNom().toLowerCase().contains(searchTerm) ||
                        p.getPrenom().toLowerCase().contains(searchTerm) ||
                        p.getEmail().toLowerCase().contains(searchTerm));
                table.setItems(FXCollections.observableArrayList(filtered));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur pendant la recherche.");
        }
    }

    @FXML
    private void handleRefresh() {
        if (modeActuel == ModeAffichage.MEDECIN) {
            handleAfficherMedecins();
        } else {
            handlePatients();
        }
    }

    @FXML
    private void handleAddMedecin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/medecin/ajouter.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un médecin");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
            handleAfficherMedecins(); // rafraîchir
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddPatient() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/patient/ajouter.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un patient");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
            handlePatients(); // rafraîchir
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) throws Exception {
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(loginRoot));
        stage.show();
        SessionManager.clearSession();

    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    
    @FXML private TableColumn<Object, Void> colActions;

    private void ajouterColonneActions() {
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnVoir = new Button("Voir");
            private final Button btnModifier = new Button("Modifier");
            private final Button btnSupprimer = new Button("Supprimer");

            {
                btnVoir.getStyleClass().add("button-green");
                btnModifier.getStyleClass().add("button-blue");
                btnSupprimer.getStyleClass().add("button-red");

                btnVoir.setOnAction(e -> {
                    Object item = getTableView().getItems().get(getIndex());
                    if (item instanceof Medecin med) {
                        System.out.println("Voir médecin: " + med.getNom());
                        // TODO: Afficher détails médecin
                    } else if (item instanceof Patient pat) {
                        System.out.println("Voir patient: " + pat.getNom());
                        // TODO: Afficher détails patient
                    }
                });

                btnModifier.setOnAction(e -> {
                    Object item = getTableView().getItems().get(getIndex());
                    if (item instanceof Medecin med) {
                        ouvrirFenetreModificationMedecin(med);
                    } else if (item instanceof Patient pat) {
                        ouvrirFenetreModificationPatient(pat);
                    }
                });

                btnSupprimer.setOnAction(e -> {
                    Object item = getTableView().getItems().get(getIndex());
                    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmation.setTitle("Confirmation de suppression");
                    confirmation.setHeaderText(null);
                    confirmation.setContentText("Voulez-vous vraiment supprimer cet élément ?");

                    confirmation.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                if (item instanceof Medecin med) {
                                    new UtilisateurDAO().supprimerUtilisateur(med.getId());
                                    handleAfficherMedecins();
                                } else if (item instanceof Patient pat) {
                                    new PatientDAO().supprimerPatient(pat.getId());
                                    handlePatients();
                                }
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

    private void ouvrirFenetreModificationMedecin(Medecin medecin) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/medecin/modifier.fxml"));
            Parent root = loader.load();

            MedecinsController controller = loader.getController();
            controller.setMedecin(medecin);

            Stage stage = new Stage();
            stage.setTitle("Modifier un médecin");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            handleAfficherMedecins(); // rafraîchir la table
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ouverture du formulaire de modification.");
        }
    }

    
    private void ouvrirFenetreModificationPatient(Patient patient) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/patient/modifier.fxml"));
            Parent root = loader.load();

            PatientsController controller = loader.getController();
            controller.setPatient(patient);

            Stage stage = new Stage();
            stage.setTitle("Modifier un patient");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            handlePatients(); // rafraîchir table
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ouverture du formulaire de modification.");
        }
    }}



