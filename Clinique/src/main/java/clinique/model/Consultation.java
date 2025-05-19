package main.java.clinique.model;

public class Consultation {
    private int id;
    private String diagnostic;
    private String traitement;
    private String notes;
    private int idRdv;

    public Consultation() {}

    public Consultation(int id, String diagnostic, String traitement, String notes, int idRdv) {
        this.id = id;
        this.diagnostic = diagnostic;
        this.traitement = traitement;
        this.notes = notes;
        this.idRdv = idRdv;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDiagnostic() { return diagnostic; }
    public void setDiagnostic(String diagnostic) { this.diagnostic = diagnostic; }

    public String getTraitement() { return traitement; }
    public void setTraitement(String traitement) { this.traitement = traitement; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getIdRdv() { return idRdv; }
    public void setIdRdv(int idRdv) { this.idRdv = idRdv; }
}
