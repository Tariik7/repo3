package main.java.clinique.dao;

import main.java.clinique.model.RendezVous;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RendezVousDAO {

    public void ajouterRendezVous(RendezVous rdv) throws SQLException {
        String sql = "INSERT INTO RendezVous(date_rdv, heure_debut, heure_fin, etat, motif, id_patient, id_medecin) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            //stmt.setDate(1, rdv.getDateRdv());
            stmt.setDate(1, rdv.getDate());
            stmt.setTime(2, rdv.getHeureDebut());
            stmt.setTime(3, rdv.getHeureFin());
            stmt.setString(4, rdv.getEtat());
            stmt.setString(5, rdv.getMotif());
            stmt.setInt(6, rdv.getIdPatient());
            stmt.setInt(7, rdv.getIdMedecin());
            stmt.executeUpdate();
        }
    }

    public List<RendezVous> listerRendezVous() throws SQLException {
        List<RendezVous> liste = new ArrayList<>();
        String sql = "SELECT * FROM RendezVous";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                RendezVous rdv = new RendezVous(
                    rs.getInt("id_rdv"),
                    rs.getDate("date_rdv"),
                    rs.getTime("heure_debut"),
                    rs.getTime("heure_fin"),
                    rs.getString("etat"),
                    rs.getString("motif"),
                    rs.getInt("id_patient"),
                    rs.getInt("id_medecin")
                );
                liste.add(rdv);
            }
        }

        return liste;
    }

    public void modifierEtat(int idRdv, String nouvelEtat) throws SQLException {
        String sql = "UPDATE RendezVous SET etat = ? WHERE id_rdv = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nouvelEtat);
            stmt.setInt(2, idRdv);
            stmt.executeUpdate();
        }
    }

    public void supprimerRendezVous(int idRdv) throws SQLException {
        String sql = "DELETE FROM RendezVous WHERE id_rdv = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRdv);
            stmt.executeUpdate();
        }
    }
}
