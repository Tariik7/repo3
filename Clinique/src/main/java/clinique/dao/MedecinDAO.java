package main.java.clinique.dao;

import main.java.clinique.model.Medecin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedecinDAO {

	public void ajouterMedecinAvecUtilisateur(Medecin medecin, int idUtilisateur) throws SQLException {
	    String sql = "INSERT INTO Medecin (id_medecin, nom, prenom, specialite, telephone, email) VALUES (?, ?, ?, ?, ?, ?)";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, idUtilisateur);
	        stmt.setString(2, medecin.getNom());
	        stmt.setString(3, medecin.getPrenom());
	        stmt.setString(4, medecin.getSpecialite());
	        stmt.setString(5, medecin.getTelephone());
	        stmt.setString(6, medecin.getEmail());
	        stmt.executeUpdate();
	    }
	}


    public List<Medecin> listerMedecins() throws SQLException {
        List<Medecin> liste = new ArrayList<>();
        String sql = "SELECT * FROM Medecin";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Medecin m = new Medecin(
                    rs.getInt("id_medecin"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("specialite"),
                    rs.getString("telephone"),
                    rs.getString("email")
                );
                liste.add(m);
            }
        }

        return liste;
    }

    public void supprimerMedecin(int id) throws SQLException {
        String sql = "DELETE FROM Medecin WHERE id_medecin = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void modifierMedecin(Medecin medecin) throws SQLException {
        String sql = "UPDATE medecin SET nom = ?, prenom = ?, specialite = ?, telephone = ?, email = ? WHERE id_medecin = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, medecin.getNom());
            stmt.setString(2, medecin.getPrenom());
            stmt.setString(3, medecin.getSpecialite());
            stmt.setString(4, medecin.getTelephone());
            stmt.setString(5, medecin.getEmail());
            stmt.setInt(6, medecin.getId());
            stmt.executeUpdate();
        }
    }

}
