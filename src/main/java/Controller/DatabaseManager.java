package Controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager
{
    private static final String JDBC_DRIVER = "org.h2.Driver";
    // URL per database H2 in-memory (per test, si resetta ad ogni avvio)
    // private static final String DB_URL = "jdbc:h2:mem:testdb;

    // URL per database H2 persistente (salvato su file)
    private static final String DB_URL = "jdbc:h2:./data/mydb"; // Il database sarà in ./data/mydb.mv.db
    private static final String USER = "sa";
    private static final String PASS = "password"; // Password per H2, da NON usare per utenti reali

    public DatabaseManager()
    {
        // Carica il driver JDBC (non sempre strettamente necessario con i driver moderni, ma buona pratica)
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("H2 JDBC Driver not found!");
        }
    }

    public static Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    //crea il database per gli utenti
    public void createUsersTable()
    {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(255) NOT NULL UNIQUE," +
                "password_hash VARCHAR(255) NOT NULL)"; // Per le password hashate

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Tabella 'users' creata o già esistente.");
        } catch (SQLException e) {
            System.err.println("Errore durante la creazione della tabella: " + e.getMessage());
        }
    }

    //aggiunge un utente al database
    public static void addUser(String username, String passwordHash) {
        if (userExists(username)) {
            System.out.println("Errore: L'utente '" + username + "' esiste già. Non verrà aggiunto.");
            return;
        }
        String insertSQL = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            pstmt.executeUpdate();
            System.out.println("Utente '" + username + "' aggiunto con successo.");
        } catch (SQLException e) {
            System.err.println("Errore durante l'aggiunta dell'utente: " + e.getMessage());
        }
    }

    //controlla se un utente esiste già nel database
    public static boolean userExists(String username) {
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Errore durante il controllo dell'esistenza utente");
        }
        return false;
    }

    //rimuove un utente dal database
    public boolean removeUser(String username) {
        String deleteSQL = "DELETE FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {

            pstmt.setString(1, username);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Utente '" + username + "' rimosso con successo.");
                return true;
            } else {
                System.out.println("Nessun utente trovato con lo username '" + username + "'.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la rimozione dell'utente: " + e.getMessage());
            return false;
        }
    }

    //recupera la password hashata prendendo in input l'username
    public static String getUserPasswordHash(String username) {
        String selectSQL = "SELECT password_hash FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("password_hash");
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero della password per l'utente '" + username + "': " + e.getMessage());
        }
        return null; // Utente non trovato o errore
    }

    //svuota completamente il database degli utenti
    public void deleteUsersTable() {
        String truncateSQL = "TRUNCATE TABLE users";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(truncateSQL);
            System.out.println("La tabella 'users' è stata svuotata con successo.");

        } catch (SQLException e) {
            System.err.println("Errore durante lo svuotamento della tabella: " + e.getMessage());
        }
    }

    //Recupera e stampa nella console i dati di tutti gli utenti presenti nella tabella.
    public void printAllUsers() {
        String selectSQL = "SELECT id, username, password_hash FROM users";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            System.out.println("------------------------------------------");
            System.out.println("Elenco di tutti gli utenti nel database:");
            System.out.println("------------------------------------------");

            boolean foundUsers = false;
            while (rs.next()) {
                foundUsers = true;
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String passwordHash = rs.getString("password_hash");

                System.out.println("ID: " + id + ", Username: " + username + ", Password Hash: " + passwordHash);
            }

            if (!foundUsers) {
                System.out.println("Nessun utente trovato nel database.");
            }

            System.out.println("------------------------------------------");

        } catch (SQLException e) {
            System.err.println("Errore durante il recupero degli utenti: " + e.getMessage());
        }
    }
}


