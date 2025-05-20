package main.java.clinique.dao;

import main.java.clinique.model.Secretaire;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SecretaireDAO {

    private Connection getConnection() throws SQLException {
        // Implémente ta méthode de connexion à la base
        return DBConnection.getConnection(); // Exemple, adapte selon ton code
    }

    public List<Secretaire> listerSecretaires() throws SQLException {
        List<Secretaire> liste = new ArrayList<>();
        String sql = "SELECT id_secretaire, nom, prenom, email, telephone FROM secretaire";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Secretaire s = new Secretaire();
                s.setId(rs.getInt("id_secretaire"));
                s.setNom(rs.getString("nom"));
                s.setPrenom(rs.getString("prenom"));
                s.setEmail(rs.getString("email"));
                s.setTelephone(rs.getString("telephone"));
                liste.add(s);
            }
        }
        return liste;
    }

    public void ajouterSecretaire(Secretaire s) throws SQLException {
        String sql = "INSERT INTO secretaire (nom, prenom, email, telephone) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getNom());
            stmt.setString(2, s.getPrenom());
            stmt.setString(3, s.getEmail());
            stmt.setString(4, s.getTelephone());
            stmt.executeUpdate();
        }
    }

    public void modifierSecretaire(Secretaire s) throws SQLException {
        String sql = "UPDATE secretaire SET nom=?, prenom=?, email=?, telephone=? WHERE id_secretaire=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s.getNom());
            stmt.setString(2, s.getPrenom());
            stmt.setString(3, s.getEmail());
            stmt.setString(4, s.getTelephone());
            stmt.setInt(5, s.getId());
            stmt.executeUpdate();
        }
    }

    public void supprimerSecretaire(int id) throws SQLException {
        String sql = "DELETE FROM secretaire WHERE id_secretaire=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Secretaire trouverParId(int id) throws SQLException {
        Secretaire s = null;
        String sql = "SELECT id_secretaire, nom, prenom, email, telephone FROM secretaire WHERE id_secretaire=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    s = new Secretaire();
                    s.setId(rs.getInt("id_secretaire"));
                    s.setNom(rs.getString("nom"));
                    s.setPrenom(rs.getString("prenom"));
                    s.setEmail(rs.getString("email"));
                    s.setTelephone(rs.getString("telephone"));
                }
            }
        }
        return s;
    }
    public void ajouterSecretaireAvecUtilisateur(Secretaire secretaire, int idUtilisateur) throws SQLException {
        String sql = "INSERT INTO secretaire (id_secretaire, nom, prenom, email, telephone) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUtilisateur); // Utiliser l'ID utilisateur comme ID secrétaire
            stmt.setString(2, secretaire.getNom());
            stmt.setString(3, secretaire.getPrenom());
            stmt.setString(4, secretaire.getEmail());
            stmt.setString(5, secretaire.getTelephone());
            stmt.executeUpdate();
        }
    }

    /**
     * Met à jour les informations du secrétaire depuis un utilisateur modifié
     * @param userId ID de l'utilisateur (qui est aussi l'ID du secrétaire)
     * @param login Le login de l'utilisateur (sera utilisé comme email)
     * @throws SQLException En cas d'erreur SQL
     */
    public void modifierSecretaireDepuisUtilisateur(int userId, String login) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String query = "UPDATE secretaire SET email = ? WHERE id_secretaire = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, login); // Utiliser le login comme email
        ps.setInt(2, userId);
        ps.executeUpdate();
    }
}
