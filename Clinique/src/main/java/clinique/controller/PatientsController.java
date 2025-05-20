package main.java.clinique.controller;

import java.io.IOException; 
import java.sql.Date;

import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import main.java.clinique.dao.PatientDAO;
import main.java.clinique.dao.UtilisateurDAO;
import main.java.clinique.model.Patient;
import main.java.clinique.model.Utilisateur;
import main.java.clinique.util.SessionManager;


public class PatientsController {

    @FXML private TableView<Patient> tablePatients;
    @FXML private TableColumn<Patient, String> colNom;
    @FXML private TableColumn<Patient, String> colPrenom;
    @FXML private TableColumn<Patient, String> colEmail;
    @FXML private TableColumn<Patient, String> colSexe;
    @FXML private TableColumn<Patient, String> colTelephone;
    @FXML private TableColumn<Patient, Void> colActions;

    private final PatientDAO patientDAO = new PatientDAO();

    @FXML private TextField tfNom;
    @FXML private TextField tfPrenom;
    @FXML private DatePicker dpNaissance;
    @FXML private TextField tfSexe;
    @FXML private TextField tfTelephone;
    @FXML private TextField tfEmail;
    @FXML private TextField tfAdresse;
    @FXML private Label lblErreur;
    
    private String userRole ;


    @FXML private BorderPane root;


    @FXML private ChoiceBox<String> cbSexe;

  
 
   

    private Patient patient;
    public void setPatient(Patient patient) {
        this.patient = patient;
        // Pré-remplir le formulaire
        tfNom.setText(patient.getNom());
        tfPrenom.setText(patient.getPrenom());
        dpNaissance.setValue(patient.getDateNaissance().toLocalDate());
        cbSexe.setValue(String.valueOf(patient.getSexe()));
        tfTelephone.setText(patient.getTelephone());
        tfEmail.setText(patient.getEmail());
        tfAdresse.setText(patient.getAdresse());
    }
    
    @FXML
    private void handleEnregistrer() {
        try {
            // Vérifie que les champs existent (ajout/modification)
            if (tfNom == null || tfPrenom == null || dpNaissance == null) {
                showAlert("Formulaire incomplet ou mal connecté.");
                return;
            }

            String nom = tfNom.getText().trim();
            String prenom = tfPrenom.getText().trim();
            String telephone = tfTelephone != null ? tfTelephone.getText().trim() : "";
            String email = tfEmail != null ? tfEmail.getText().trim() : "";
            String adresse = tfAdresse != null ? tfAdresse.getText().trim() : "";

            // Sexe : récupérer depuis cbSexe OU tfSexe
            String sexeStr = (cbSexe != null && cbSexe.getValue() != null)
                    ? cbSexe.getValue()
                    : (tfSexe != null ? tfSexe.getText().trim().toUpperCase() : "");

            if (nom.isEmpty() || prenom.isEmpty() || dpNaissance.getValue() == null || sexeStr.isEmpty()) {
                if (lblErreur != null) lblErreur.setText("Veuillez remplir tous les champs obligatoires.");
                return;
            }

            if (patient == null) patient = new Patient(); // Si c'est un ajout

            patient.setNom(nom);
            patient.setPrenom(prenom);
            patient.setDateNaissance(Date.valueOf(dpNaissance.getValue()));
            patient.setSexe(sexeStr.charAt(0));
            patient.setTelephone(telephone);
            patient.setEmail(email);
            patient.setAdresse(adresse);

            if (patient.getId() > 0) {
                patientDAO.modifierPatient(patient);
            } else {
                patientDAO.ajouterPatient(patient);
            }

            // Fermer la fenêtre
            Stage stage = (Stage) tfNom.getScene().getWindow();
            if (stage != null) stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            if (lblErreur != null) lblErreur.setText("Erreur lors de l'enregistrement.");
        }
    }


    @FXML
    private void handleAnnuler() {
        ((Stage) tfNom.getScene().getWindow()).close();
    }

    private void ouvrirModifierPatient(Patient patient) {
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

            chargerPatients();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ouverture du formulaire de modification.");
        }
    }
    
    
    @FXML
    public void initialize() {
        // Initialisation pour la table (liste des patients)
        if (tablePatients != null) {
            colNom.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
            colPrenom.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPrenom()));
            colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
            colSexe.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getSexe())));
            colTelephone.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTelephone()));
            
            colActions.setPrefWidth(250);
            tablePatients.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            ajouterColonneActions();
            chargerPatients();
        }

        // Initialisation pour ajout/modification (sexe choix)
        if (cbSexe != null) {
            cbSexe.setItems(FXCollections.observableArrayList("M", "F"));
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
                    Patient pat = getTableView().getItems().get(getIndex());
                    afficherPopupDetailsPatient(pat);
                });

                btnModifier.setOnAction(e -> {
                    Patient pat = getTableView().getItems().get(getIndex());
                    ouvrirModifierPatient(pat);
                });

                btnSupprimer.setOnAction(e -> {
                    Patient pat = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer ce patient ?", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            try {
                                new UtilisateurDAO().supprimerUtilisateur(pat.getId()); // si tu as lié utilisateur et patient par id
                                patientDAO.supprimerPatient(pat.getId());
                                tablePatients.getItems().remove(pat);
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

    private void afficherPopupDetailsPatient(Patient patient) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails du Patient");
        alert.setHeaderText(patient.getNom() + " " + patient.getPrenom());
        alert.setContentText(
            "Sexe : " + patient.getSexe() + "\n" +
            "Téléphone : " + patient.getTelephone() + "\n" +
            "Email : " + patient.getEmail()
        );
        alert.showAndWait();
    }

    

    private void chargerPatients() {
        try {
            ObservableList<Patient> list = FXCollections.observableArrayList(patientDAO.listerPatients());
            tablePatients.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur de chargement des patients.");
        }
    }

    @FXML
    private void handleAjouterPatient() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/patient/ajouter.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un patient");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
            chargerPatients();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void ouvrirListePatients() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/patient/liste_patients.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Liste des Patients");
            stage.setScene(new Scene(root, 1200, 700));
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
 

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    private TextField searchField;

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        try {
            ObservableList<Patient> all = FXCollections.observableArrayList(patientDAO.listerPatients());
            ObservableList<Patient> filtered = all.filtered(p ->
                p.getNom().toLowerCase().contains(keyword) ||
                p.getPrenom().toLowerCase().contains(keyword) ||
                p.getEmail().toLowerCase().contains(keyword)
            );
            tablePatients.setItems(filtered);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        chargerPatients();
    }
    
    
    @FXML
    private void handleHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
            Parent root = loader.load();

            // Passer le rôle à DashboardController
            DashboardController controller = loader.getController();
            if (controller != null) {
                controller.setUserRole(SessionManager.getCurrentUserRole());
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

    public static String getCurrentUserRole() {
        Utilisateur utilisateur = SessionManager.getUtilisateur();
        return utilisateur != null ? utilisateur.getRole() : "admin"; // valeur par défaut
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
        


        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        HBox header = loader.load();

        // Si besoin, tu peux récupérer le controller du header et lui passer des infos
        // Exemple:
        // HeaderController controller = loader.getController();
        // controller.setParentController(this);

        root.setTop(header);
    }}
}
