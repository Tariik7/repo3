package main.java.clinique.dao;

import main.java.clinique.model.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    public void ajouterPatient(Patient p) throws SQLException {
        String sql = "INSERT INTO Patient (nom, prenom, date_naissance, sexe, telephone, email, adresse) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getNom());
            stmt.setString(2, p.getPrenom());
            stmt.setDate(3, p.getDateNaissance());
            stmt.setString(4, String.valueOf(p.getSexe()));
            stmt.setString(5, p.getTelephone());
            stmt.setString(6, p.getEmail());
            stmt.setString(7, p.getAdresse());
            stmt.executeUpdate();
        }
    }

    public List<Patient> listerPatients() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM Patient";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Patient p = new Patient(
                    rs.getInt("id_patient"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getDate("date_naissance"),
                    rs.getString("sexe").charAt(0),
                    rs.getString("telephone"),
                    rs.getString("email"),
                    rs.getString("adresse")
                );
                patients.add(p);
            }
        }
        return patients;
    }

    public void supprimerPatient(int id) throws SQLException {
        String sql = "DELETE FROM Patient WHERE id_patient = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void modifierPatient(Patient p) throws SQLException {
        String sql = "UPDATE Patient SET nom=?, prenom=?, date_naissance=?, sexe=?, telephone=?, email=?, adresse=? WHERE id_patient=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getNom());
            stmt.setString(2, p.getPrenom());
            stmt.setDate(3, p.getDateNaissance());
            stmt.setString(4, String.valueOf(p.getSexe()));
            stmt.setString(5, p.getTelephone());
            stmt.setString(6, p.getEmail());
            stmt.setString(7, p.getAdresse());
            stmt.setInt(8, p.getId());
            stmt.executeUpdate();
        }
    }
}
