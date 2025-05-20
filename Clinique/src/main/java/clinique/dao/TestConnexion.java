package main.java.clinique.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnexion {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/clinique_db", "root", ""
            );
            System.out.println("Connexion réussie !");
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
