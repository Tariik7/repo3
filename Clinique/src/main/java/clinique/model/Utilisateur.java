package main.java.clinique.model;

public class Utilisateur {
    private int id_utilisateur;
    private String login;
    private String motDePasse;
    private String role;

    public Utilisateur() {}

    public Utilisateur(int id_utilisateur, String login, String motDePasse, String role) {
        this.id_utilisateur = id_utilisateur;
        this.login = login;
        this.motDePasse = motDePasse;
        this.role = role;
    }

    public int getId_utilisateur() {
        return id_utilisateur;
    }

    public void setId_utilisateur(int id_utilisateur) {
        this.id_utilisateur = id_utilisateur;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
