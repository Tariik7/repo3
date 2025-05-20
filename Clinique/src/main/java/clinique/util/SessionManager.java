package main.java.clinique.util;

import main.java.clinique.model.Utilisateur;

public class SessionManager {
    private static Utilisateur utilisateurConnecte;
    private static String lastUserRole = null; // Pour conserver le dernier rôle connu

    public static void setUtilisateur(Utilisateur utilisateur) {
        utilisateurConnecte = utilisateur;
        if (utilisateur != null) {
            lastUserRole = utilisateur.getRole(); // Conserver le rôle lors de la connexion
        }
    }

    public static Utilisateur getUtilisateur() {
        return utilisateurConnecte;
    }

    public static void clearSession() {
        utilisateurConnecte = null;
        // On ne réinitialise pas lastUserRole pour pouvoir y accéder même après déconnexion
    }
    
    public static String getCurrentUserRole() {
        if (utilisateurConnecte != null) {
            return utilisateurConnecte.getRole();
        } else {
            // Retourner le dernier rôle connu au lieu de hardcoder "admin"
            return lastUserRole != null ? lastUserRole : "";
        }
    }
}
