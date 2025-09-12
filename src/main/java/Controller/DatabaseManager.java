package Controller;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Questa classe è stata realizzata con l'aiuto di Google Gemini e si occupa della gestione del Database in cui
 * vengono memorizzate le credenziali dei vari utenti.
 */
public class DatabaseManager {
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:./data/mydb";
    private static final String USER = "sa";
    private static final String PASS = "password";

    /**
     * Costruttore.
     */
    public DatabaseManager() {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("H2 JDBC Driver not found!");
        }
    }

    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    /**
     * Crea la tabella per gli utenti.
     */
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

    /**
     * Crea la tabella per i preferiti.
     */
    public static void createPreferencesTables() {
        String createStopsTableSQL = "CREATE TABLE IF NOT EXISTS favorite_stops (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "stop_name VARCHAR(255) NOT NULL," +
                "UNIQUE(user_id, stop_name)," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)";

        String createLinesTableSQL = "CREATE TABLE IF NOT EXISTS favorite_lines (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "line_name VARCHAR(255) NOT NULL," +
                "UNIQUE(user_id, line_name)," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createStopsTableSQL);
            stmt.execute(createLinesTableSQL);
            System.out.println("Tabelle preferiti create o già esistenti.");
        } catch (SQLException e) {
            System.err.println("Errore durante la creazione delle tabelle preferiti: " + e.getMessage());
        }
    }

    // Metodo per hashare la password
    static String hashPassword(String plainTextPassword)
    {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    // Metodo per controllare una password e l'hash esistente
    static boolean checkPassword(String plainTextPassword, String hashedPassword)
    {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }

    // Aggiunge un utente al database (ora riceve la password in chiaro)
    static void addUser(String username, String plainTextPassword)
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
    static boolean userExists(String username)
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
    static boolean removeUser(String username)
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
    static String getUserPasswordHash(String username)
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

    /**
     * Metodo per recuperare l'ID di un utente tramite lo username.
     * @param username il nome dell'utente.
     * @return ritorna l'Id.
     */
    public static Integer getUserId(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Errore nel recuperare l'ID utente: " + e.getMessage());
        }
        return null;
    }

    /**
     * Cambia password all'utente.
     * @param username il nome dell'utente.
     * @param oldPassword la vecchia password.
     * @param newPassword la nuova password.
     * @return ritorna un boolean per controllare se il cambio password ha avuto successo o meno.
     */
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

        //METODI USATI PER DEBUG

        /*
        // Metodo per svuotare le tabelle e risolvere l'errore di integrità referenziale
        public static void deleteUsersTable() {
            String truncateFavoriteLinesSQL = "TRUNCATE TABLE favorite_lines";
            String truncateFavoriteStopsSQL = "TRUNCATE TABLE favorite_stops";
            String truncateUsersSQL = "TRUNCATE TABLE users";

            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {

                // Disattiva l'integrità referenziale temporaneamente per garantire che il TRUNCATE funzioni
                // Questa è una soluzione potente ma va usata con cautela
                stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");

                // Svuota prima le tabelle che hanno le chiavi esterne
                stmt.executeUpdate(truncateFavoriteLinesSQL);
                System.out.println("Tabella favorite_lines svuotata con successo.");

                stmt.executeUpdate(truncateFavoriteStopsSQL);
                System.out.println("Tabella favorite_stops svuotata con successo.");

                // Ora svuota la tabella 'users'
                stmt.executeUpdate(truncateUsersSQL);
                System.out.println("Tabella users svuotata con successo.");

                // Riaattiva l'integrità referenziale
                stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");

            } catch (SQLException e) {
                System.err.println("Errore durante lo svuotamento delle tabelle: " + e.getMessage());
            }
        }

    // Recupera e stampa tutti gli utenti
    public static void printAllUsers()
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
    }*/
}
