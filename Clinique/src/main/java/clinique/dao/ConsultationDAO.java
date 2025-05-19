package main.java.clinique.dao;

import main.java.clinique.model.Consultation;

import java.sql.*;

public class ConsultationDAO {

    public void ajouterConsultation(Consultation consultation) throws SQLException {
        String sql = "INSERT INTO Consultation(diagnostic, traitement, notes, id_rdv) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, consultation.getDiagnostic());
            stmt.setString(2, consultation.getTraitement());
            stmt.setString(3, consultation.getNotes());
            stmt.setInt(4, consultation.getIdRdv());
            stmt.executeUpdate();
        }
    }

    public Consultation getByRdv(int idRdv) throws SQLException {
        String sql = "SELECT * FROM Consultation WHERE id_rdv = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRdv);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Consultation(
                        rs.getInt("id_consultation"),
                        rs.getString("diagnostic"),
                        rs.getString("traitement"),
                        rs.getString("notes"),
                        rs.getInt("id_rdv")
                    );
                }
            }
        }
        return null;
    }

    public void supprimerConsultation(int idConsultation) throws SQLException {
        String sql = "DELETE FROM Consultation WHERE id_consultation = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idConsultation);
            stmt.executeUpdate();
        }
    }
}
