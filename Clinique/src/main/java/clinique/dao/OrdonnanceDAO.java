package main.java.clinique.dao;

import main.java.clinique.model.Ordonnance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceDAO {

    public void ajouterOrdonnance(Ordonnance ordonnance) throws SQLException {
        String sql = "INSERT INTO Ordonnance(medicament, posologie, duree, id_consultation) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ordonnance.getMedicament());
            stmt.setString(2, ordonnance.getPosologie());
            stmt.setString(3, ordonnance.getDuree());
            stmt.setInt(4, ordonnance.getIdConsultation());
            stmt.executeUpdate();
        }
    }

    public List<Ordonnance> getByConsultation(int idConsultation) throws SQLException {
        List<Ordonnance> liste = new ArrayList<>();
        String sql = "SELECT * FROM Ordonnance WHERE id_consultation = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idConsultation);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Ordonnance o = new Ordonnance(
                        rs.getInt("id_ordonnance"),
                        rs.getString("medicament"),
                        rs.getString("posologie"),
                        rs.getString("duree"),
                        rs.getInt("id_consultation")
                    );
                    liste.add(o);
                }
            }
        }

        return liste;
    }

    public void supprimerOrdonnance(int id) throws SQLException {
        String sql = "DELETE FROM Ordonnance WHERE id_ordonnance = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
