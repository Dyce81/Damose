package Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestisce linee e fermate preferite inserite dall'utente.
 */
public class FavoritesManager
{
    /**
     * Aggiunge una fermata ai preferiti dell'utente.
     * @param username il nome utente dell'utente attualmente loggato.
     * @param stopName il nome della fermata da aggiungere ai preferiti.
     */
    public static void addFavoriteStop(String username, String stopName)
    {
        // Ottieni l'ID utente una sola volta e gestisci il caso in cui sia nullo
        Integer userId = DatabaseManager.getUserId(username);
        if (userId == null)
        {
            System.err.println("Errore: Utente non trovato nel database per il nome: " + username);
            return;
        }

        // Controlla se la fermata è già presente prima di aggiungerla
        if (isFavoriteStopPresent(userId, stopName))
        {
            System.out.println("La fermata '" + stopName + "' è già nei preferiti.");
            return;
        }

        String sql = "INSERT INTO favorite_stops (user_id, stop_name) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, userId);
            pstmt.setString(2, stopName);
            pstmt.executeUpdate();
            System.out.println("Fermata '" + stopName + "' aggiunta ai preferiti di " + username + ".");
        }
        catch (SQLException e)
        {
            System.err.println("Errore nell'aggiungere la fermata preferita: " + e.getMessage());
        }
    }

    /**
     * Aggiunge una linea ai preferiti dell'utente.
     * @param username il nome utente dell'utente attualmente loggato.
     * @param lineName il nome della fermata da aggiungere ai preferiti.
     */
    public static void addFavoriteLine(String username, String lineName)
    {
        // Ottieni l'ID utente una sola volta e gestisci il caso in cui sia nullo
        Integer userId = DatabaseManager.getUserId(username);
        if (userId == null)
        {
            System.err.println("Errore: Utente non trovato nel database per il nome: " + username);
            return;
        }

        // Controlla se la linea è già presente prima di aggiungerla
        if (isFavoriteLinePresent(userId, lineName))
        {
            System.out.println("La linea '" + lineName + "' è già nei preferiti.");
            return;
        }

        String sql = "INSERT INTO favorite_lines (user_id, line_name) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, userId);
            pstmt.setString(2, lineName);
            pstmt.executeUpdate();
            System.out.println("Linea '" + lineName + "' aggiunta ai preferiti di " + username + ".");
        }
        catch (SQLException e)
        {
            System.err.println("Errore nell'aggiungere la linea preferita: " + e.getMessage());
        }
    }

    // ---

    /**
     * Questo metodo controlla se la fermata interessata è già nel database (quindi già tra i preferiti).
     * @param userId il nome utente dell'utente attualmente loggato.
     * @param stopName il nome della fermata da controllare.
     * @return restituisce true se la fermata è nel database, false altrimenti.
     */
    public static boolean isFavoriteStopPresent(Integer userId, String stopName) {
        String sql = "SELECT COUNT(*) FROM favorite_stops WHERE user_id = ? AND stop_name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, userId);
            pstmt.setString(2, stopName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                return rs.getInt(1) > 0;
            }
        }
        catch (SQLException e)
        {
            System.err.println("Errore nel controllo della fermata preferita: " + e.getMessage());
        }
        return false;
    }

    /**
     * Questo metodo controlla se la linea interessata è già nel database (quindi già tra i preferiti).
     * @param userId il nome utente dell'utente attualmente loggato.
     * @param lineName il nome della linea da controllare.
     * @return restituisce true se la linea è nel database, false altrimenti.
     */
    public static boolean isFavoriteLinePresent(Integer userId, String lineName)
    {
        String sql = "SELECT COUNT(*) FROM favorite_lines WHERE user_id = ? AND line_name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, userId);
            pstmt.setString(2, lineName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
            {
                return rs.getInt(1) > 0;
            }
        }
        catch (SQLException e)
        {
            System.err.println("Errore nel controllo della linea preferita: " + e.getMessage());
        }
        return false;
    }

    /**
     * Rimuove una fermata dai preferiti.
     * @param username il nome utente dell'utente attualmente loggato.
     * @param stopName il nome della fermata da rimuovere dai preferiti.
     */
    public static void removeFavoriteStop(String username, String stopName)
    {
        Integer userId = DatabaseManager.getUserId(username);
        if (userId == null)
        {
            System.err.println("Errore: utente non trovato.");
            return;
        }

        String sql = "DELETE FROM favorite_stops WHERE user_id = ? AND stop_name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, userId);
            pstmt.setString(2, stopName);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0)
            {
                System.out.println("Fermata '" + stopName + "' rimossa dai preferiti di " + username + ".");
            }
            else
            {
                System.out.println("Fermata '" + stopName + "' non trovata nei preferiti.");
            }
        }
        catch (SQLException e)
        {
            System.err.println("Errore nel rimuovere la fermata preferita: " + e.getMessage());
        }
    }

    /**
     * Ottiene le fermate preferite di un utente.
     * @param username il nome utente dell'utente attualmente loggato.
     * @return una lista dei nomi delle fermate preferite dell'utente specificato.
     */
    public static List<String> getFavoriteStops(String username)
    {
        List<String> stops = new ArrayList<>();
        Integer userId = DatabaseManager.getUserId(username);
        if (userId == null)
        {
            return stops;
        }

        String sql = "SELECT stop_name FROM favorite_stops WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                stops.add(rs.getString("stop_name"));
            }
        }
        catch (SQLException e)
        {
            System.err.println("Errore nel recuperare le fermate preferite: " + e.getMessage());
        }
        return stops;
    }

    /**
     * Rimuove una linea dai preferiti.
     * @param username il nome utente dell'utente attualmente loggato.
     * @param lineName il nome della linea da rimuovere dai preferiti.
     */
    public static void removeFavoriteLine(String username, String lineName)
    {
        Integer userId = DatabaseManager.getUserId(username);
        if (userId == null)
        {
            System.err.println("Errore: utente non trovato.");
            return;
        }

        String sql = "DELETE FROM favorite_lines WHERE user_id = ? AND line_name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, userId);
            pstmt.setString(2, lineName);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0)
            {
                System.out.println("Linea '" + lineName + "' rimossa dai preferiti di " + username + ".");
            }
            else
            {
                System.out.println("Linea '" + lineName + "' non trovata nei preferiti.");
            }
        }
        catch (SQLException e)
        {
            System.err.println("Errore nel rimuovere la linea preferita: " + e.getMessage());
        }
    }

    /**
     * Ottiene le linee preferite di un utente.
     * @param username il nome utente dell'utente attualmente loggato.
     * @return una lista dei nomi delle linee preferite dell'utente specificato.
     */
    public static List<String> getFavoriteLines(String username)
    {
        List<String> lines = new ArrayList<>();
        Integer userId = DatabaseManager.getUserId(username);
        if (userId == null)
        {
            return lines;
        }

        String sql = "SELECT line_name FROM favorite_lines WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
                lines.add(rs.getString("line_name"));
            }
        }
        catch (SQLException e)
        {
            System.err.println("Errore nel recuperare le linee preferite: " + e.getMessage());
        }
        return lines;
    }
}