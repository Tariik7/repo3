package main.java.clinique.model;

import java.sql.Date;
import java.sql.Time;

public class RendezVous {
    private int id;
    private Date date;
    private Time heureDebut;
    private Time heureFin;
    private String etat;
    private String motif;
    private int idPatient;
    private int idMedecin;

    public RendezVous() {}

    public RendezVous(int id, Date date, Time heureDebut, Time heureFin, String etat, String motif, int idPatient, int idMedecin) {
        this.id = id;
        this.date = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.etat = etat;
        this.motif = motif;
        this.idPatient = idPatient;
        this.idMedecin = idMedecin;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Time getHeureDebut() { return heureDebut; }
    public void setHeureDebut(Time heureDebut) { this.heureDebut = heureDebut; }

    public Time getHeureFin() { return heureFin; }
    public void setHeureFin(Time heureFin) { this.heureFin = heureFin; }

    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public int getIdPatient() { return idPatient; }
    public void setIdPatient(int idPatient) { this.idPatient = idPatient; }

    public int getIdMedecin() { return idMedecin; }
    public void setIdMedecin(int idMedecin) { this.idMedecin = idMedecin; }

	
}
