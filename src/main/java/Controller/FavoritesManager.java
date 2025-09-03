package Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FavoritesManager
{
    // Aggiungi una fermata preferita
    public static void addFavoriteStop(String username, String stopName) {
        Integer userId = DatabaseManager.getUserId(username);
        if (userId == null) {
            System.err.println("Errore: utente non trovato.");
            return;
        }

        String sql = "INSERT INTO favorite_stops (user_id, stop_name) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, stopName);
            pstmt.executeUpdate();
            System.out.println("Fermata '" + stopName + "' aggiunta ai preferiti di " + username + ".");
        } catch (SQLException e) {
            System.err.println("Errore nell'aggiungere la fermata preferita: " + e.getMessage());
        }
    }

    // Rimuovi una fermata preferita
    public static void removeFavoriteStop(String username, String stopName) {
        Integer userId = DatabaseManager.getUserId(username);
        if (userId == null) {
            System.err.println("Errore: utente non trovato.");
            return;
        }

        String sql = "DELETE FROM favorite_stops WHERE user_id = ? AND stop_name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, stopName);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Fermata '" + stopName + "' rimossa dai preferiti di " + username + ".");
            } else {
                System.out.println("Fermata '" + stopName + "' non trovata nei preferiti.");
            }
        } catch (SQLException e) {
            System.err.println("Errore nel rimuovere la fermata preferita: " + e.getMessage());
        }
    }

    // Ottieni tutte le fermate preferite di un utente
    public static List<String> getFavoriteStops(String username) {
        List<String> stops = new ArrayList<>();
        Integer userId = DatabaseManager.getUserId(username);
        if (userId == null) {
            return stops;
        }

        String sql = "SELECT stop_name FROM favorite_stops WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                stops.add(rs.getString("stop_name"));
            }
        } catch (SQLException e) {
            System.err.println("Errore nel recuperare le fermate preferite: " + e.getMessage());
        }
        return stops;
    }

    // Aggiungi una linea preferita
    public static void addFavoriteLine(String username, String lineName) {
        Integer userId = DatabaseManager.getUserId(username);
        if (userId == null) {
            System.err.println("Errore: utente non trovato.");
            return;
        }

        String sql = "INSERT INTO favorite_lines (user_id, line_name) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, lineName);
            pstmt.executeUpdate();
            System.out.println("Linea '" + lineName + "' aggiunta ai preferiti di " + username + ".");
        } catch (SQLException e) {
            System.err.println("Errore nell'aggiungere la linea preferita: " + e.getMessage());
        }
    }

    // Rimuovi una linea preferita
    public static void removeFavoriteLine(String username, String lineName) {
        Integer userId = DatabaseManager.getUserId(username);
        if (userId == null) {
            System.err.println("Errore: utente non trovato.");
            return;
        }

        String sql = "DELETE FROM favorite_lines WHERE user_id = ? AND line_name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, lineName);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Linea '" + lineName + "' rimossa dai preferiti di " + username + ".");
            } else {
                System.out.println("Linea '" + lineName + "' non trovata nei preferiti.");
            }
        } catch (SQLException e) {
            System.err.println("Errore nel rimuovere la linea preferita: " + e.getMessage());
        }
    }

    // Ottieni tutte le linee preferite di un utente
    public static List<String> getFavoriteLines(String username) {
        List<String> lines = new ArrayList<>();
        Integer userId = DatabaseManager.getUserId(username);
        if (userId == null) {
            return lines;
        }

        String sql = "SELECT line_name FROM favorite_lines WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lines.add(rs.getString("line_name"));
            }
        } catch (SQLException e) {
            System.err.println("Errore nel recuperare le linee preferite: " + e.getMessage());
        }
        return lines;
    }
}