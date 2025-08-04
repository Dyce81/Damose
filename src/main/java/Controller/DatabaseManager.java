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
    // private static final String DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";

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
            e.printStackTrace();
            System.err.println("H2 JDBC Driver not found!");
        }
    }

    public Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public void createUsersTable()
    {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(255) NOT NULL UNIQUE," +
                "password_hash VARCHAR(255) NOT NULL)"; // Per le password hashatte

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Tabella 'users' creata o già esistente.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Errore durante la creazione della tabella: " + e.getMessage());
        }
    }

    public void addUser(String username, String passwordHash) {
        String insertSQL = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            pstmt.executeUpdate();
            System.out.println("Utente '" + username + "' aggiunto con successo.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Errore durante l'aggiunta dell'utente: " + e.getMessage());
        }
    }

    public String getUserPasswordHash(String username) {
        String selectSQL = "SELECT password_hash FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("password_hash");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Errore durante il recupero della password per l'utente '" + username + "': " + e.getMessage());
        }
        return null; // Utente non trovato o errore
    }

    /** esempio utilizzo
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();

        // 1. Crea la tabella
        dbManager.createUsersTable();

        // 2. Aggiungi un utente (simulando una password hashatta)
        String username1 = "alice";
        String hashedPassword1 = "hashed_password_for_alice_123"; // In un'applicazione reale useresti BCrypt qui
        dbManager.addUser(username1, hashedPassword1);

        String username2 = "bob";
        String hashedPassword2 = "hashed_password_for_bob_456";
        dbManager.addUser(username2, hashedPassword2);

        // 3. Recupera la password hashata per un utente
        System.out.println("\nRecupero password hashate:");
        String retrievedHashAlice = dbManager.getUserPasswordHash("alice");
        if (retrievedHashAlice != null) {
            System.out.println("Hash password per Alice: " + retrievedHashAlice);
        } else {
            System.out.println("Utente Alice non trovato.");
        }

        String retrievedHashCharlie = dbManager.getUserPasswordHash("charlie");
        if (retrievedHashCharlie == null) {
            System.out.println("Utente Charlie non trovato (come previsto).");
        }
    }
     **/
}


