package main.java.clinique.model;

import java.sql.Date;
import java.time.LocalDate;

public class Patient {
    private int id;
    private String nom;
    private String prenom;
    private Date dateNaissance;
    private char sexe;
    private String telephone;
    private String email;
    private String adresse;

    public Patient() {}

    // Constructeur principal avec java.sql.Date
    public Patient(int id, String nom, String prenom, Date dateNaissance, char sexe, String telephone, String email, String adresse) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.sexe = sexe;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
    }

    // Constructeur pratique avec java.time.LocalDate
    public Patient(int id, String nom, String prenom, LocalDate localDateNaissance, char sexe, String telephone, String email, String adresse) {
        this(id, nom, prenom, Date.valueOf(localDateNaissance), sexe, telephone, email, adresse);
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public Date getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(Date dateNaissance) { this.dateNaissance = dateNaissance; }

    public char getSexe() { return sexe; }
    public void setSexe(char sexe) { this.sexe = sexe; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
}
