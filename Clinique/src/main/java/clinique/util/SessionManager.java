package main.java.clinique.util;

import main.java.clinique.model.Utilisateur;

public class SessionManager {
    private static Utilisateur utilisateurConnecte;

    public static void setUtilisateur(Utilisateur utilisateur) {
        utilisateurConnecte = utilisateur;
    }

    public static Utilisateur getUtilisateur() {
        return utilisateurConnecte;
    }

    public static void clearSession() {
        utilisateurConnecte = null;
    }
    public static String getCurrentUserRole() {
        Utilisateur utilisateur = SessionManager.getUtilisateur();
        return utilisateur != null ? utilisateur.getRole() : "admin"; // valeur par défaut
    }
}
