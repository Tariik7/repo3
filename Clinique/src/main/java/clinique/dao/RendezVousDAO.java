package main.java.clinique.dao;

import main.java.clinique.model.RendezVous;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe d'accès aux données pour les rendez-vous
 */
public class RendezVousDAO {
    
    /**
     * Ajoute un nouveau rendez-vous dans la base de données
     * @param rendezVous Le rendez-vous à ajouter
     * @return L'ID du rendez-vous créé
     * @throws SQLException En cas d'erreur SQL
     */
    public int ajouterRendezVous(RendezVous rendezVous) throws SQLException {
        String query = "INSERT INTO rendezvous (date_rdv, heure_debut, heure_fin, etat, motif, id_patient, id_medecin) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setDate(1, rendezVous.getDate_rdv());
            ps.setTime(2, rendezVous.getHeure_debut());
            ps.setTime(3, rendezVous.getHeure_fin());
            ps.setString(4, rendezVous.getEtat());
            ps.setString(5, rendezVous.getMotif());
            ps.setInt(6, rendezVous.getId_patient());
            ps.setInt(7, rendezVous.getId_medecin());
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    rendezVous.setId_rdv(id);
                    return id;
                } else {
                    throw new SQLException("Erreur lors de la création du rendez-vous, aucun ID généré");
                }
            }
        }
    }
    
    /**
     * Modifie un rendez-vous existant dans la base de données
     * @param rendezVous Le rendez-vous à modifier
     * @throws SQLException En cas d'erreur SQL
     */
    public void modifierRendezVous(RendezVous rendezVous) throws SQLException {
        String query = "UPDATE rendezvous SET date_rdv = ?, heure_debut = ?, heure_fin = ?, etat = ?, motif = ?, id_patient = ?, id_medecin = ? WHERE id_rdv = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setDate(1, rendezVous.getDate_rdv());
            ps.setTime(2, rendezVous.getHeure_debut());
            ps.setTime(3, rendezVous.getHeure_fin());
            ps.setString(4, rendezVous.getEtat());
            ps.setString(5, rendezVous.getMotif());
            ps.setInt(6, rendezVous.getId_patient());
            ps.setInt(7, rendezVous.getId_medecin());
            ps.setInt(8, rendezVous.getId_rdv());
            
            ps.executeUpdate();
        }
    }
    
    /**
     * Change le statut d'un rendez-vous
     * @param idRendezVous L'ID du rendez-vous
     * @param nouveauStatut Le nouveau statut
     * @throws SQLException En cas d'erreur SQL
     */
    public void changerStatut(int idRendezVous, String nouveauStatut) throws SQLException {
        String query = "UPDATE rendezvous SET etat = ? WHERE id_rdv = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, nouveauStatut);
            ps.setInt(2, idRendezVous);
            
            ps.executeUpdate();
        }
    }
    
    /**
     * Supprime un rendez-vous de la base de données
     * @param id L'ID du rendez-vous à supprimer
     * @throws SQLException En cas d'erreur SQL
     */
    public void supprimerRendezVous(int id) throws SQLException {
        String query = "DELETE FROM rendezvous WHERE id_rdv = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
    /**
     * Récupère un rendez-vous par son ID
     * @param id L'ID du rendez-vous
     * @return Le rendez-vous trouvé, ou null s'il n'existe pas
     * @throws SQLException En cas d'erreur SQL
     */
    public RendezVous trouverParId(int id) throws SQLException {
        String query = "SELECT r.*, p.nom AS nom_patient, p.prenom AS prenom_patient, " +
                      "m.nom AS nom_medecin, m.prenom AS prenom_medecin, m.specialite " +
                      "FROM rendezvous r " +
                      "JOIN patient p ON r.id_patient = p.id_patient " +
                      "JOIN medecin m ON r.id_medecin = m.id_medecin " +
                      "WHERE r.id_rdv = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extraireRendezVousResultSet(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Liste tous les rendez-vous
     * @return La liste des rendez-vous
     * @throws SQLException En cas d'erreur SQL
     */
    public List<RendezVous> listerRendezVous() throws SQLException {
        List<RendezVous> rendezVousList = new ArrayList<>();
        String query = "SELECT r.*, p.nom AS nom_patient, p.prenom AS prenom_patient, " +
                      "m.nom AS nom_medecin, m.prenom AS prenom_medecin, m.specialite " +
                      "FROM rendezvous r " +
                      "JOIN patient p ON r.id_patient = p.id_patient " +
                      "JOIN medecin m ON r.id_medecin = m.id_medecin " +
                      "ORDER BY r.date_rdv DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                rendezVousList.add(extraireRendezVousResultSet(rs));
            }
        }
        
        return rendezVousList;
    }
    
    /**
     * Liste les rendez-vous d'un médecin
     * @param idMedecin L'ID du médecin
     * @return La liste des rendez-vous du médecin
     * @throws SQLException En cas d'erreur SQL
     */
    public List<RendezVous> listerRendezVousParMedecin(int idMedecin) throws SQLException {
        List<RendezVous> rendezVousList = new ArrayList<>();
        String query = "SELECT r.*, p.nom AS nom_patient, p.prenom AS prenom_patient, " +
                      "m.nom AS nom_medecin, m.prenom AS prenom_medecin, m.specialite " +
                      "FROM rendezvous r " +
                      "JOIN patient p ON r.id_patient = p.id_patient " +
                      "JOIN medecin m ON r.id_medecin = m.id_medecin " +
                      "WHERE r.id_medecin = ? " +
                      "ORDER BY r.date_rdv DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, idMedecin);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rendezVousList.add(extraireRendezVousResultSet(rs));
                }
            }
        }
        
        return rendezVousList;
    }
    
    /**
     * Liste les rendez-vous d'un patient
     * @param idPatient L'ID du patient
     * @return La liste des rendez-vous du patient
     * @throws SQLException En cas d'erreur SQL
     */
    public List<RendezVous> listerRendezVousParPatient(int idPatient) throws SQLException {
        List<RendezVous> rendezVousList = new ArrayList<>();
        String query = "SELECT r.*, p.nom AS nom_patient, p.prenom AS prenom_patient, " +
                      "m.nom AS nom_medecin, m.prenom AS prenom_medecin, m.specialite " +
                      "FROM rendezvous r " +
                      "JOIN patient p ON r.id_patient = p.id_patient " +
                      "JOIN medecin m ON r.id_medecin = m.id_medecin " +
                      "WHERE r.id_patient = ? " +
                      "ORDER BY r.date_rdv DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, idPatient);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rendezVousList.add(extraireRendezVousResultSet(rs));
                }
            }
        }
        
        return rendezVousList;
    }
    
    /**
     * Liste les rendez-vous pour une date donnée
     * @param date La date pour laquelle rechercher les rendez-vous
     * @return La liste des rendez-vous pour cette date
     * @throws SQLException En cas d'erreur SQL
     */
    public List<RendezVous> listerRendezVousParDate(java.sql.Date date) throws SQLException {
        List<RendezVous> rendezVousList = new ArrayList<>();
        
        String query = "SELECT r.*, p.nom AS nom_patient, p.prenom AS prenom_patient, " +
                      "m.nom AS nom_medecin, m.prenom AS prenom_medecin, m.specialite " +
                      "FROM rendezvous r " +
                      "JOIN patient p ON r.id_patient = p.id_patient " +
                      "JOIN medecin m ON r.id_medecin = m.id_medecin " +
                      "WHERE r.date_rdv = ? " +
                      "ORDER BY r.heure_debut ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setDate(1, date);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rendezVousList.add(extraireRendezVousResultSet(rs));
                }
            }
        }
        
        return rendezVousList;
    }
    
    /**
     * Vérifie si un créneau horaire est disponible pour un médecin
     * @param idMedecin L'ID du médecin
     * @param date La date du rendez-vous
     * @param heureDebut L'heure de début du créneau à vérifier
     * @param dureeMinutes La durée du rendez-vous en minutes
     * @return true si le créneau est disponible, false sinon
     * @throws SQLException En cas d'erreur SQL
     */
    public boolean estCreneauDisponible(int idMedecin, Date date, Time heureDebut, int dureeMinutes) throws SQLException {
        Time heureFin = Time.valueOf(heureDebut.toLocalTime().plusMinutes(dureeMinutes));
        
        String query = "SELECT COUNT(*) FROM rendezvous " +
                      "WHERE id_medecin = ? " +
                      "AND date_rdv = ? " +
                      "AND ((heure_debut <= ? AND heure_fin > ?) " +
                      "OR (heure_debut < ? AND heure_fin >= ?) " +
                      "OR (heure_debut >= ? AND heure_fin <= ?)) " +
                      "AND etat != 'Annulé'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, idMedecin);
            ps.setDate(2, date);
            ps.setTime(3, heureDebut);
            ps.setTime(4, heureDebut);
            ps.setTime(5, heureFin);
            ps.setTime(6, heureFin);
            ps.setTime(7, heureDebut);
            ps.setTime(8, heureFin);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0; // Retourne vrai si aucun rendez-vous trouvé
                }
            }
        }
        
        return false;
    }
    
    /**
     * Extrait un objet RendezVous à partir d'un ResultSet
     * @param rs Le ResultSet contenant les données du rendez-vous
     * @return Un objet RendezVous rempli avec les données du ResultSet
     * @throws SQLException En cas d'erreur SQL
     */
    private RendezVous extraireRendezVousResultSet(ResultSet rs) throws SQLException {
        RendezVous rv = new RendezVous();
        rv.setId_rdv(rs.getInt("id_rdv"));
        rv.setDate_rdv(rs.getDate("date_rdv"));
        rv.setHeure_debut(rs.getTime("heure_debut"));
        rv.setHeure_fin(rs.getTime("heure_fin"));
        rv.setEtat(rs.getString("etat"));
        rv.setMotif(rs.getString("motif"));
        rv.setId_patient(rs.getInt("id_patient"));
        rv.setId_medecin(rs.getInt("id_medecin"));
        
        // Informations supplémentaires
        rv.setNomPatient(rs.getString("nom_patient"));
        rv.setPrenomPatient(rs.getString("prenom_patient"));
        rv.setNomMedecin(rs.getString("nom_medecin"));
        rv.setPrenomMedecin(rs.getString("prenom_medecin"));
        rv.setSpecialiteMedecin(rs.getString("specialite"));
        
        return rv;
    }
}
