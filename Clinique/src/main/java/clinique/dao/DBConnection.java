package main.java.clinique.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/clinique_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    static {
        try {
            // Charge le driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement du driver JDBC");
            e.printStackTrace();
        }
    }
    

    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion à la base réussie !"); // Debug
            return connection;
        } catch (SQLException e) {
            System.err.println("Échec de la connexion à la base:");
            System.err.println("URL: " + URL);
            System.err.println("User: " + USER);
            e.printStackTrace();
            throw e;
        }
    }
}