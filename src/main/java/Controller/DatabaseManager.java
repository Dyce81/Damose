package Controller;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:./data/mydb";
    private static final String USER = "sa";
    private static final String PASS = "password";

    public static boolean logged = false;

    public DatabaseManager() {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("H2 JDBC Driver not found!");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // Crea la tabella per gli utenti
    public static void createUsersTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(255) NOT NULL UNIQUE," +
                "password_hash VARCHAR(255) NOT NULL)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Tabella 'users' creata o già esistente.");
        } catch (SQLException e) {
            System.err.println("Errore durante la creazione della tabella: " + e.getMessage());
        }
    }

    // Metodo per hashare la password
    public static String hashPassword(String plainTextPassword)
    {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    // Metodo per controllare una password e l'hash esistente
    public static boolean checkPassword(String plainTextPassword, String hashedPassword)
    {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }

    // Aggiunge un utente al database (ora riceve la password in chiaro)
    public static void addUser(String username, String plainTextPassword)
    {
        if (userExists(username))
        {
            System.out.println("Errore: L'utente '" + username + "' esiste già. Non verrà aggiunto.");
            return;
        }

        String hashedPassword = hashPassword(plainTextPassword);

        String insertSQL = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.executeUpdate();
            System.out.println("Utente '" + username + "' aggiunto con successo.");
        } catch (SQLException e) {
            System.err.println("Errore durante l'aggiunta dell'utente: " + e.getMessage());
        }
    }

    // Controlla se un utente esiste già
    public static boolean userExists(String username)
    {
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

    // Rimuove un utente
    public static boolean removeUser(String username)
    {
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

    // Recupera l'hash della password di un utente
    public static String getUserPasswordHash(String username)
    {
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

    public static boolean changePassword(String username, String oldPassword, String newPassword) {
        String hashedPassword = getUserPasswordHash(username);

        // 1. Controlla se l'utente esiste e se la vecchia password è corretta
        if (hashedPassword == null) {
            System.err.println("Errore: Utente non trovato.");
            return false;
        }

        if (!checkPassword(oldPassword, hashedPassword)) {
            System.err.println("Errore: Vecchia password non corretta.");
            return false;
        }

        // 2. Hasha la nuova password
        String newHashedPassword = hashPassword(newPassword);

        // 3. Aggiorna la password nel database
        String updateSQL = "UPDATE users SET password_hash = ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, newHashedPassword);
            pstmt.setString(2, username);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Password dell'utente '" + username + "' cambiata con successo.");
                return true;
            } else {
                System.err.println("Errore: Impossibile aggiornare la password per l'utente '" + username + "'.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Errore durante l'aggiornamento della password: " + e.getMessage());
            return false;
        }
    }

    // Svuota completamente la tabella degli utenti
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

    // Recupera e stampa tutti gli utenti
    public void printAllUsers()
    {
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
