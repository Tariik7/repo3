package main.java.clinique.model;

import java.sql.Date;
import java.sql.Time;

/**
 * Modèle représentant un rendez-vous dans la clinique
 */
public class RendezVous {
    
    private int id_rdv;
    private Date date_rdv;
    private Time heure_debut;
    private Time heure_fin;
    private String etat;
    private String motif;
    private int id_patient;
    private int id_medecin;
    
    // Variables temporaires pour l'affichage
    private String nomPatient;
    private String prenomPatient;
    private String nomMedecin;
    private String prenomMedecin;
    private String specialiteMedecin;
    
    // Constructeur par défaut
    public RendezVous() {
    }
    
    // Constructeur complet
    public RendezVous(int id_rdv, Date date_rdv, Time heure_debut, Time heure_fin, 
                      String etat, String motif, int id_patient, int id_medecin) {
        this.id_rdv = id_rdv;
        this.date_rdv = date_rdv;
        this.heure_debut = heure_debut;
        this.heure_fin = heure_fin;
        this.etat = etat;
        this.motif = motif;
        this.id_patient = id_patient;
        this.id_medecin = id_medecin;
    }
    
    // Getters et Setters
    public int getId_rdv() {
        return id_rdv;
    }
    
    public void setId_rdv(int id_rdv) {
        this.id_rdv = id_rdv;
    }
    
    public Date getDate_rdv() {
        return date_rdv;
    }
    
    public void setDate_rdv(Date date_rdv) {
        this.date_rdv = date_rdv;
    }
    
    public Time getHeure_debut() {
        return heure_debut;
    }
    
    public void setHeure_debut(Time heure_debut) {
        this.heure_debut = heure_debut;
    }
    
    public Time getHeure_fin() {
        return heure_fin;
    }
    
    public void setHeure_fin(Time heure_fin) {
        this.heure_fin = heure_fin;
    }
    
    public String getEtat() {
        return etat;
    }
    
    public void setEtat(String etat) {
        this.etat = etat;
    }
    
    public String getMotif() {
        return motif;
    }
    
    public void setMotif(String motif) {
        this.motif = motif;
    }
    
    public int getId_patient() {
        return id_patient;
    }
    
    public void setId_patient(int id_patient) {
        this.id_patient = id_patient;
    }
    
    public int getId_medecin() {
        return id_medecin;
    }
    
    public void setId_medecin(int id_medecin) {
        this.id_medecin = id_medecin;
    }
    
    // Getters et Setters pour les variables d'affichage
    public String getNomPatient() {
        return nomPatient;
    }
    
    public void setNomPatient(String nomPatient) {
        this.nomPatient = nomPatient;
    }
    
    public String getPrenomPatient() {
        return prenomPatient;
    }
    
    public void setPrenomPatient(String prenomPatient) {
        this.prenomPatient = prenomPatient;
    }
    
    public String getNomMedecin() {
        return nomMedecin;
    }
    
    public void setNomMedecin(String nomMedecin) {
        this.nomMedecin = nomMedecin;
    }
    
    public String getPrenomMedecin() {
        return prenomMedecin;
    }
    
    public void setPrenomMedecin(String prenomMedecin) {
        this.prenomMedecin = prenomMedecin;
    }
    
    public String getSpecialiteMedecin() {
        return specialiteMedecin;
    }
    
    public void setSpecialiteMedecin(String specialiteMedecin) {
        this.specialiteMedecin = specialiteMedecin;
    }
    
    // Méthodes utiles
    public String getPatientComplet() {
        return prenomPatient + " " + nomPatient;
    }
    
    public String getMedecinComplet() {
        return prenomMedecin + " " + nomMedecin + " (" + specialiteMedecin + ")";
    }
    
    @Override
    public String toString() {
        return "RendezVous{" +
                "id_rdv=" + id_rdv +
                ", patient=" + getPatientComplet() +
                ", médecin=" + getMedecinComplet() +
                ", date_rdv=" + date_rdv +
                ", heure_debut=" + heure_debut +
                ", heure_fin=" + heure_fin +
                ", etat='" + etat + '\'' +
                ", motif='" + motif + '\'' +
                '}';
    }
}
