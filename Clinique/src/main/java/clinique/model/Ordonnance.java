package main.java.clinique.model;

public class Ordonnance {
    private int id;
    private String medicament;
    private String posologie;
    private String duree;
    private int idConsultation;

    public Ordonnance() {}

    public Ordonnance(int id, String medicament, String posologie, String duree, int idConsultation) {
        this.id = id;
        this.medicament = medicament;
        this.posologie = posologie;
        this.duree = duree;
        this.idConsultation = idConsultation;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMedicament() { return medicament; }
    public void setMedicament(String medicament) { this.medicament = medicament; }

    public String getPosologie() { return posologie; }
    public void setPosologie(String posologie) { this.posologie = posologie; }

    public String getDuree() { return duree; }
    public void setDuree(String duree) { this.duree = duree; }

    public int getIdConsultation() { return idConsultation; }
    public void setIdConsultation(int idConsultation) { this.idConsultation = idConsultation; }
}
