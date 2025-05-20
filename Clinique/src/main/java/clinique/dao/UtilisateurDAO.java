package main.java.clinique.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import main.java.clinique.model.Secretaire;
import main.java.clinique.model.Utilisateur;


public class UtilisateurDAO {

    public Utilisateur trouverParLoginEtMotDePasse(String login, String motDePasse) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String query = "SELECT * FROM utilisateur WHERE login = ? AND mot_de_passe = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, login);
        ps.setString(2, motDePasse);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Utilisateur(
                rs.getInt("id_utilisateur"),
                rs.getString("login"),
                rs.getString("mot_de_passe"),
                rs.getString("role")
            );
        }
        return null;
    }

    public int creerUtilisateur(String login, String motDePasse, String role) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String query = "INSERT INTO utilisateur (login, mot_de_passe, role) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, login);
        ps.setString(2, motDePasse);
        ps.setString(3, role);
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1);
        }
        throw new SQLException("Échec lors de la création de l'utilisateur.");
    }

    public void supprimerUtilisateur(int id_utilisateur) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String query = "DELETE FROM utilisateur WHERE id_utilisateur = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, id_utilisateur);
        ps.executeUpdate();
    }

    public void modifierUtilisateurDepuisMedecin(main.java.clinique.model.Medecin medecin) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String query = "UPDATE utilisateur SET login = ? WHERE id_utilisateur = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, medecin.getEmail()); // Par exemple, login = email
        ps.setInt(2, medecin.getId());
        ps.executeUpdate();
 
    }
    public List<Utilisateur> listerUtilisateurs() throws SQLException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String query = "SELECT * FROM utilisateur";
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Utilisateur u = new Utilisateur(
                rs.getInt("id_utilisateur"),
                rs.getString("login"),
                rs.getString("mot_de_passe"),
                rs.getString("role")
            );
            utilisateurs.add(u);
        }

        return utilisateurs;
    }
    
    public void modifierUtilisateurDepuisSecretaire(Secretaire secretaire) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String query = "UPDATE utilisateur SET login = ? WHERE id_utilisateur = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, secretaire.getEmail()); // Login = email
        ps.setInt(2, secretaire.getId());
        ps.executeUpdate();
    }

    // ✏️ Ajouter un utilisateur
    public int ajouterUtilisateur(Utilisateur utilisateur) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String query = "INSERT INTO utilisateur (login, mot_de_passe, role) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, utilisateur.getLogin());
        ps.setString(2, utilisateur.getMotDePasse());
        ps.setString(3, utilisateur.getRole());
        ps.executeUpdate();
        
        // Récupérer l'ID généré
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            int id = rs.getInt(1);
            utilisateur.setId_utilisateur(id); // Mettre à jour l'objet avec l'ID généré
            return id;
        }
        throw new SQLException("Échec lors de la création de l'utilisateur, aucun ID généré.");
    }

    // 🛠️ Modifier un utilisateur
    public void modifierUtilisateur(Utilisateur utilisateur) throws SQLException {
        Connection conn = DBConnection.getConnection();
        StringBuilder query = new StringBuilder("UPDATE utilisateur SET login = ?, role = ?");
        if (utilisateur.getMotDePasse() != null && !utilisateur.getMotDePasse().isEmpty()) {
            query.append(", mot_de_passe = ?");
        }
        query.append(" WHERE id_utilisateur = ?");

        PreparedStatement ps = conn.prepareStatement(query.toString());
        ps.setString(1, utilisateur.getLogin());
        ps.setString(2, utilisateur.getRole());

        int index = 3;
        if (utilisateur.getMotDePasse() != null && !utilisateur.getMotDePasse().isEmpty()) {
            ps.setString(index++, utilisateur.getMotDePasse());
        }
        ps.setInt(index, utilisateur.getId_utilisateur());

        ps.executeUpdate();
    }

}
