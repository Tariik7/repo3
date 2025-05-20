package main.java.clinique.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.java.clinique.dao.MedecinDAO;
import main.java.clinique.dao.PatientDAO;
import main.java.clinique.dao.RendezVousDAO;
import main.java.clinique.model.Medecin;
import main.java.clinique.model.Patient;
import main.java.clinique.model.RendezVous;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

/**
 * Contrôleur pour la gestion des rendez-vous
 */
public class RendezVousController {

    // Composants de la vue liste
    @FXML private TableView<RendezVous> tableRendezVous;
    @FXML private TableColumn<RendezVous, String> colPatient;
    @FXML private TableColumn<RendezVous, String> colMedecin;
    @FXML private TableColumn<RendezVous, String> colDate;
    @FXML private TableColumn<RendezVous, String> colHeure;
    @FXML private TableColumn<RendezVous, String> colMotif;
    @FXML private TableColumn<RendezVous, String> colStatut;
    @FXML private TableColumn<RendezVous, Void> colActions;
    
    // Composants pour la recherche et le filtrage
    @FXML private DatePicker dpDateRecherche;
    @FXML private TextField tfRechercheNom;
    @FXML private ComboBox<String> cbFiltreStatut;
    
    // Composants du formulaire d'ajout/modification
    @FXML private ComboBox<Patient> cbPatient;
    @FXML private ComboBox<Medecin> cbMedecin;
    @FXML private DatePicker dpDate;
    @FXML private ComboBox<String> cbHeure;
    @FXML private TextArea taMotif;
    @FXML private ComboBox<String> cbStatut;
    @FXML private Label lblErreur;
    
    // Autres attributs du contrôleur
    private String userRole;
    private RendezVous rendezVous;
    private final RendezVousDAO rendezVousDAO = new RendezVousDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final MedecinDAO medecinDAO = new MedecinDAO();
    
    // Formateurs pour l'affichage des dates et heures
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    
    /**
     * Initialise le contrôleur
     */
    @FXML
    public void initialize() {
        try {
            // Initialisation de la vue liste
            if (tableRendezVous != null) {
                initializeTableView();
            }
            
            // Initialisation des filtres
            if (cbFiltreStatut != null) {
                cbFiltreStatut.setItems(FXCollections.observableArrayList(
                    "Tous", "Planifié", "Terminé", "Annulé"
                ));
                cbFiltreStatut.getSelectionModel().selectFirst();
                
                cbFiltreStatut.setOnAction(e -> filtrerRendezVous());
            }
            
            if (dpDateRecherche != null) {
                dpDateRecherche.setValue(LocalDate.now());
                dpDateRecherche.setOnAction(e -> filtrerRendezVous());
            }
            
            if (tfRechercheNom != null) {
                tfRechercheNom.textProperty().addListener((obs, oldVal, newVal) -> filtrerRendezVous());
            }
            
            // Initialisation du formulaire
            if (cbPatient != null) {
                initializeFormulaire();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur d'initialisation", 
                    "Une erreur est survenue lors de l'initialisation: " + e.getMessage());
        }
    }
    
    /**
     * Initialise la TableView et ses colonnes
     */
    private void initializeTableView() {
        // Configuration des cellules de chaque colonne
        colPatient.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPatientComplet()));
        colMedecin.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMedecinComplet()));
        colDate.setCellValueFactory(data -> {
            if (data.getValue().getDate_rdv() != null) {
                return new SimpleStringProperty(DATE_FORMAT.format(data.getValue().getDate_rdv()));
            }
            return new SimpleStringProperty("");
        });
        colHeure.setCellValueFactory(data -> {
            if (data.getValue().getHeure_debut() != null) {
                return new SimpleStringProperty(TIME_FORMAT.format(data.getValue().getHeure_debut()));
            }
            return new SimpleStringProperty("");
        });
        colMotif.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMotif()));
        colStatut.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEtat()));
        
        // Configuration de la colonne d'actions
        configureActionsColumn();
        
        // Définir la politique de redimensionnement pour que toutes les colonnes s'adaptent à la largeur de la table
        tableRendezVous.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Charger les données initiales
        chargerRendezVous();
        
        // Ajuster les largeurs des colonnes après le chargement des données
        ajusterLargeurColonnes();
    }
    
    /**
     * Configure la colonne d'actions avec les boutons Voir, Modifier, Annuler
     */
    private void configureActionsColumn() {
        colActions.setCellFactory(new Callback<TableColumn<RendezVous, Void>, TableCell<RendezVous, Void>>() {
            @Override
            public TableCell<RendezVous, Void> call(TableColumn<RendezVous, Void> param) {
                return new TableCell<>() {
                    private final Button btnVoir = new Button("Voir");
                    private final Button btnModifier = new Button("Modifier");
                    private final Button btnAnnuler = new Button("Annuler");
                    
                    {
                        btnVoir.getStyleClass().add("button-view");
                        btnVoir.setOnAction(e -> {
                            RendezVous rv = getTableView().getItems().get(getIndex());
                            afficherDetailsRendezVous(rv);
                        });
                        
                        btnModifier.getStyleClass().add("button-edit");
                        btnModifier.setOnAction(e -> {
                            RendezVous rv = getTableView().getItems().get(getIndex());
                            modifierRendezVous(rv);
                        });
                        
                        btnAnnuler.getStyleClass().add("button-delete");
                        btnAnnuler.setOnAction(e -> {
                            RendezVous rv = getTableView().getItems().get(getIndex());
                            confirmerAnnulation(rv);
                        });
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            RendezVous rv = getTableView().getItems().get(getIndex());
                            // N'afficher le bouton d'annulation que pour les rendez-vous planifiés
                            if ("Planifié".equals(rv.getEtat())) {
                                setGraphic(new javafx.scene.layout.HBox(5, btnVoir, btnModifier, btnAnnuler));
                            } else {
                                setGraphic(new javafx.scene.layout.HBox(5, btnVoir, btnModifier));
                            }
                        }
                    }
                };
            }
        });
    }
    
    /**
     * Initialise le formulaire d'ajout/modification
     */
    private void initializeFormulaire() throws SQLException {
        // Remplir la liste des patients
        List<Patient> patients = patientDAO.listerPatients();
        cbPatient.setItems(FXCollections.observableArrayList(patients));
        cbPatient.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Patient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getPrenom() + " " + item.getNom());
                }
            }
        });
        cbPatient.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Patient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getPrenom() + " " + item.getNom());
                }
            }
        });
        
        // Remplir la liste des médecins
        List<Medecin> medecins = medecinDAO.listerMedecins();
        cbMedecin.setItems(FXCollections.observableArrayList(medecins));
        cbMedecin.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Medecin item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getPrenom() + " " + item.getNom() + " (" + item.getSpecialite() + ")");
                }
            }
        });
        cbMedecin.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Medecin item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getPrenom() + " " + item.getNom() + " (" + item.getSpecialite() + ")");
                }
            }
        });
        
        // Remplir la liste des heures disponibles (plages de 30 minutes)
        ObservableList<String> heures = FXCollections.observableArrayList();
        for (int heure = 8; heure < 18; heure++) {
            heures.add(String.format("%02d:00", heure));
            heures.add(String.format("%02d:30", heure));
        }
        cbHeure.setItems(heures);
        
        // Remplir la liste des statuts
        cbStatut.setItems(FXCollections.observableArrayList("Planifié", "Terminé", "Annulé"));
        cbStatut.getSelectionModel().selectFirst();
        
        // Initialiser la date à aujourd'hui
        dpDate.setValue(LocalDate.now());
    }
    
    /**
     * Charge la liste de tous les rendez-vous
     */
    public void chargerRendezVous() {
        try {
            List<RendezVous> rendezVousList = rendezVousDAO.listerRendezVous();
            tableRendezVous.setItems(FXCollections.observableArrayList(rendezVousList));
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", 
                    "Impossible de charger les rendez-vous: " + e.getMessage());
        }
    }
    
    /**
     * Filtre les rendez-vous selon les critères sélectionnés
     */
    private void filtrerRendezVous() {
        try {
            List<RendezVous> tousRendezVous = rendezVousDAO.listerRendezVous();
            ObservableList<RendezVous> filteredList = FXCollections.observableArrayList(tousRendezVous);
            
            // Filtre par date
            if (dpDateRecherche.getValue() != null) {
                LocalDate dateRecherche = dpDateRecherche.getValue();
                Date sqlDate = Date.valueOf(dateRecherche);
                
                filteredList = filteredList.filtered(rv -> {
                    if (rv.getDate_rdv() == null) return false;
                    return rv.getDate_rdv().equals(sqlDate);
                });
            }
            
            // Filtre par nom
            if (tfRechercheNom.getText() != null && !tfRechercheNom.getText().isEmpty()) {
                String recherche = tfRechercheNom.getText().toLowerCase();
                filteredList = filteredList.filtered(rv -> 
                    rv.getNomPatient().toLowerCase().contains(recherche) || 
                    rv.getPrenomPatient().toLowerCase().contains(recherche) ||
                    rv.getNomMedecin().toLowerCase().contains(recherche) || 
                    rv.getPrenomMedecin().toLowerCase().contains(recherche)
                );
            }
            
            // Filtre par statut
            if (cbFiltreStatut.getValue() != null && !"Tous".equals(cbFiltreStatut.getValue())) {
                String statut = cbFiltreStatut.getValue();
                filteredList = filteredList.filtered(rv -> rv.getEtat().equals(statut));
            }
            
            tableRendezVous.setItems(filteredList);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de filtrage", 
                    "Impossible de filtrer les rendez-vous: " + e.getMessage());
        }
    }
    
    /**
     * Affiche les détails d'un rendez-vous dans une boîte de dialogue
     */
    private void afficherDetailsRendezVous(RendezVous rendezVous) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Détails du rendez-vous");
        dialog.setHeaderText("Rendez-vous #" + rendezVous.getId_rdv());
        
        // Créer le contenu
        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        content.setPadding(new javafx.geometry.Insets(20));
        
        content.getChildren().add(new Label("Patient: " + rendezVous.getPatientComplet()));
        content.getChildren().add(new Label("Médecin: " + rendezVous.getMedecinComplet()));
        
        if (rendezVous.getDate_rdv() != null) {
            content.getChildren().add(new Label("Date: " + DATE_FORMAT.format(rendezVous.getDate_rdv())));
        }
        
        if (rendezVous.getHeure_debut() != null) {
            content.getChildren().add(new Label("Heure début: " + TIME_FORMAT.format(rendezVous.getHeure_debut())));
        }
        
        if (rendezVous.getHeure_fin() != null) {
            content.getChildren().add(new Label("Heure fin: " + TIME_FORMAT.format(rendezVous.getHeure_fin())));
        }
        
        content.getChildren().add(new Label("État: " + rendezVous.getEtat()));
        
        content.getChildren().add(new Separator());
        content.getChildren().add(new Label("Motif:"));
        TextArea motifArea = new TextArea(rendezVous.getMotif());
        motifArea.setEditable(false);
        motifArea.setPrefHeight(100);
        content.getChildren().add(motifArea);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        dialog.showAndWait();
    }
    
    /**
     * Ouvre la fenêtre de modification d'un rendez-vous
     */
    private void modifierRendezVous(RendezVous rendezVous) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/rendezvous/modifier.fxml"));
            Parent root = loader.load();
            
            RendezVousController controller = loader.getController();
            controller.setRendezVous(rendezVous);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier un rendez-vous");
            stage.setScene(new Scene(root, 600, 650));
            stage.setResizable(false);
            stage.showAndWait();
            
            // Rafraîchir la liste après modification
            chargerRendezVous();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Erreur lors de l'ouverture de la fenêtre de modification: " + e.getMessage());
        }
    }
    
    /**
     * Demande confirmation avant d'annuler un rendez-vous
     */
    private void confirmerAnnulation(RendezVous rendezVous) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation d'annulation");
        alert.setHeaderText("Annulation du rendez-vous #" + rendezVous.getId_rdv());
        alert.setContentText("Êtes-vous sûr de vouloir annuler ce rendez-vous ?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    rendezVousDAO.changerStatut(rendezVous.getId_rdv(), "Annulé");
                    chargerRendezVous();
                    showAlert(Alert.AlertType.INFORMATION, "Succès", 
                            "Le rendez-vous a été annulé avec succès.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", 
                            "Impossible d'annuler le rendez-vous: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Définit le rendez-vous à modifier et remplit le formulaire
     */
    public void setRendezVous(RendezVous rendezVous) {
        this.rendezVous = rendezVous;
        
        if (cbPatient == null || cbMedecin == null || dpDate == null || 
            cbHeure == null || taMotif == null || cbStatut == null) {
            return;
        }
        
        try {
            // Sélectionner le patient
            for (Patient p : cbPatient.getItems()) {
                if (p.getId() == rendezVous.getId_patient()) {
                    cbPatient.getSelectionModel().select(p);
                    break;
                }
            }
            
            // Sélectionner le médecin
            for (Medecin m : cbMedecin.getItems()) {
                if (m.getId() == rendezVous.getId_medecin()) {
                    cbMedecin.getSelectionModel().select(m);
                    break;
                }
            }
            
            // Définir la date
            if (rendezVous.getDate_rdv() != null) {
                LocalDate date = rendezVous.getDate_rdv().toLocalDate();
                dpDate.setValue(date);
            }
            
            // Définir l'heure
            if (rendezVous.getHeure_debut() != null) {
                String heureString = TIME_FORMAT.format(rendezVous.getHeure_debut());
                cbHeure.setValue(heureString);
            }
            
            // Définir le motif et le statut
            taMotif.setText(rendezVous.getMotif());
            cbStatut.setValue(rendezVous.getEtat());
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Erreur lors du chargement des données du rendez-vous: " + e.getMessage());
        }
    }
    
    /**
     * Initialise le formulaire pour la création d'un nouveau rendez-vous
     */
    public void initializeForCreation() {
        this.rendezVous = null;
        
        if (cbPatient == null || cbMedecin == null || dpDate == null || 
            cbHeure == null || taMotif == null || cbStatut == null) {
            return;
        }
        
        // Réinitialiser les champs du formulaire
        cbPatient.getSelectionModel().clearSelection();
        cbMedecin.getSelectionModel().clearSelection();
        dpDate.setValue(LocalDate.now());
        cbHeure.getSelectionModel().clearSelection();
        taMotif.clear();
        cbStatut.setValue("Planifié");
    }
    
    /**
     * Définit le rôle de l'utilisateur connecté
     */
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
    
    /**
     * Gère l'action du bouton Enregistrer
     */
    @FXML
    private void handleEnregistrer() {
        try {
            // Vérifier que tous les champs sont remplis
            if (cbPatient.getValue() == null || cbMedecin.getValue() == null || 
                dpDate.getValue() == null || cbHeure.getValue() == null || 
                taMotif.getText().isEmpty() || cbStatut.getValue() == null) {
                
                lblErreur.setText("Veuillez remplir tous les champs.");
                return;
            }
            
            // Récupérer les valeurs du formulaire
            Patient patient = cbPatient.getValue();
            Medecin medecin = cbMedecin.getValue();
            LocalDate localDate = dpDate.getValue();
            String heureStr = cbHeure.getValue();
            String motif = taMotif.getText();
            String etat = cbStatut.getValue();
            
            // Convertir la date locale en SQL Date
            Date date_rdv = Date.valueOf(localDate);
            
            // Parser l'heure sélectionnée (format HH:mm)
            String[] heureParts = heureStr.split(":");
            int heure = Integer.parseInt(heureParts[0]);
            int minute = Integer.parseInt(heureParts[1]);
            
            // Créer un Time pour l'heure de début
            Time heure_debut = Time.valueOf(String.format("%02d:%02d:00", heure, minute));
            
            // Calculer l'heure de fin (30 minutes après l'heure de début)
            Calendar cal = Calendar.getInstance();
            cal.setTime(heure_debut);
            cal.add(Calendar.MINUTE, 30);
            Time heure_fin = new Time(cal.getTimeInMillis());
            
            // Créer ou mettre à jour l'objet RendezVous
            if (this.rendezVous == null) {
                // Création d'un nouveau rendez-vous
                this.rendezVous = new RendezVous();
            }
            
            // Définir les propriétés du rendez-vous
            rendezVous.setId_patient(patient.getId());
            rendezVous.setId_medecin(medecin.getId());
            rendezVous.setDate_rdv(date_rdv);
            rendezVous.setHeure_debut(heure_debut);
            rendezVous.setHeure_fin(heure_fin);
            rendezVous.setEtat(etat);
            rendezVous.setMotif(motif);
            
            // Vérifier la disponibilité du créneau
            if (!"Annulé".equals(etat) && !rendezVousDAO.estCreneauDisponible(
                    rendezVous.getId_medecin(), date_rdv, heure_debut, 30)) {
                
                if (this.rendezVous.getId_rdv() == 0) {
                    // Pour un nouveau rendez-vous, empêcher la création
                    lblErreur.setText("Ce créneau n'est pas disponible pour ce médecin.");
                    return;
                } else {
                    // Pour une modification, vérifier si c'est le même créneau
                    RendezVous original = rendezVousDAO.trouverParId(this.rendezVous.getId_rdv());
                    if (!original.getHeure_debut().equals(heure_debut) || 
                        original.getId_medecin() != rendezVous.getId_medecin() ||
                        !original.getDate_rdv().equals(date_rdv)) {
                        
                        lblErreur.setText("Ce créneau n'est pas disponible pour ce médecin.");
                        return;
                    }
                }
            }
            
            // Enregistrer dans la base de données
            if (rendezVous.getId_rdv() == 0) {
                rendezVousDAO.ajouterRendezVous(rendezVous);
                showAlert(Alert.AlertType.INFORMATION, "Succès", 
                        "Le rendez-vous a été créé avec succès.");
            } else {
                rendezVousDAO.modifierRendezVous(rendezVous);
                showAlert(Alert.AlertType.INFORMATION, "Succès", 
                        "Le rendez-vous a été modifié avec succès.");
            }
            
            // Fermer la fenêtre
            ((Stage) cbPatient.getScene().getWindow()).close();
            
        } catch (Exception e) {
            e.printStackTrace();
            lblErreur.setText("Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Gère l'action du bouton Annuler
     */
    @FXML
    private void handleAnnuler() {
        // Simplement fermer la fenêtre actuelle sans rediriger
        // (ce comportement est approprié pour les fenêtres modales comme la création/modification de RDV)
        if (cbPatient != null && cbPatient.getScene() != null) {
            Stage stage = (Stage) cbPatient.getScene().getWindow();
            stage.close();
        } else {
            // Si c'est la fenêtre principale des rendez-vous (liste), alors rediriger vers le dashboard
            try {
                // Charger le dashboard avec le bon rôle utilisateur
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
                Parent root = loader.load();
                
                // Définir le rôle utilisateur dans le contrôleur du dashboard
                DashboardController controller = loader.getController();
                controller.setUserRole(main.java.clinique.util.SessionManager.getCurrentUserRole());
                
                // Obtenir la fenêtre actuelle et afficher le dashboard
                Stage stage = (Stage) tableRendezVous.getScene().getWindow();
                stage.setTitle("Tableau de bord");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                        "Erreur lors du retour au tableau de bord: " + e.getMessage());
            }
        }
    }
    
    /**
     * Gère le retour au dashboard
     */
    @FXML
    private void handleHome() {
        try {
            // Utiliser le SessionManager pour récupérer le rôle actuel de l'utilisateur
            String currentRole = main.java.clinique.util.SessionManager.getCurrentUserRole();
            
            // Il n'y a qu'un seul dashboard, mais on doit s'assurer que le rôle est bien transmis
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
            Parent root = loader.load();
            
            // Récupérer le contrôleur et lui transmettre explicitement le rôle utilisateur
            DashboardController controller = loader.getController();
            if (controller != null) {
                System.out.println("Définition du rôle utilisateur depuis SessionManager: " + currentRole);
                controller.setUserRole(currentRole);
            } else {
                System.out.println("Controller est null, impossible de définir le rôle utilisateur");
            }
            
            // Déterminer quelle fenêtre est active (liste ou formulaire)
            Stage stage;
            if (tableRendezVous != null && tableRendezVous.getScene() != null) {
                stage = (Stage) tableRendezVous.getScene().getWindow();
            } else if (cbPatient != null && cbPatient.getScene() != null) {
                stage = (Stage) cbPatient.getScene().getWindow();
            } else {
                throw new IllegalStateException("Aucun élément d'interface disponible pour obtenir la fenêtre");
            }
            
            stage.setTitle("Tableau de bord");
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Erreur lors du retour au tableau de bord: " + e.getMessage());
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
    
    /**
     * Gère l'action du bouton Créer un rendez-vous
     */
    @FXML
    private void handleCreerRendezVous() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/rendezvous/ajouter.fxml"));
            Parent root = loader.load();
            
            RendezVousController controller = loader.getController();
            controller.setUserRole(userRole);
            controller.initializeForCreation();
            
            Stage stage = new Stage();
            stage.setTitle("Créer un rendez-vous");
            stage.setScene(new Scene(root, 600, 650));
            stage.setResizable(false);
            stage.showAndWait();
            
            // Rafraîchir la liste après création
            chargerRendezVous();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Erreur lors de l'ouverture de la fenêtre de création: " + e.getMessage());
        }
    }
    
    /**
     * Ajuste automatiquement la largeur des colonnes en fonction de leur contenu
     */
    private void ajusterLargeurColonnes() {
        // Largeurs spécifiques pour chaque colonne
        colPatient.setPrefWidth(145.0);
        colMedecin.setPrefWidth(145.0);
        colDate.setPrefWidth(85.0);
        colHeure.setPrefWidth(70.0);
        colMotif.setPrefWidth(260.0);
        colStatut.setPrefWidth(95.0);
        colActions.setPrefWidth(170.0);
        
        // Assurer que la colonne Actions a une largeur minimale pour accueillir les boutons
        colActions.setMinWidth(150.0);
    }
}