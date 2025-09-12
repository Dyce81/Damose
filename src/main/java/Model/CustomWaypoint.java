package Model;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import javax.swing.*;
import java.awt.*;

/**
 * Questa classe rappresenta l'oggetto Waypoint personalizzato.
 */
public class CustomWaypoint extends DefaultWaypoint {
    public boolean selezionato = false;

    private final String id;
    private final String nome;
    private final double longitudine;
    private final double latitudine;
    private Image icona;

    private static final Image iconaAutobus = new ImageIcon("assets/bus-solid_grande.png").getImage().getScaledInstance(17, 17, Image.SCALE_SMOOTH);
    private static final Image iconaMetro = new ImageIcon("assets/metro_icona.png").getImage().getScaledInstance(17, 17, Image.SCALE_SMOOTH);
    private static final Image iconaSelezionata = new ImageIcon("assets/fermata-selezionata.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);

    /**
     * Costruttore.
     * @param id l'Id della fermata.
     * @param nome il nome della fermata.
     * @param coords le coordinate della fermata.
     */
    public CustomWaypoint(String id, String nome, GeoPosition coords) {
        super(coords);
        this.id = id;
        this.nome = nome;
        this.longitudine = coords.getLongitude();
        this.latitudine = coords.getLatitude();

        if (id.startsWith("ITO")) {
            icona = iconaMetro;
        } else {
            icona = iconaAutobus;
        }
    }

    /**
     * Ritorna l'Id della fermata.
     */
    public String getId() {
        return id;
    }

    /**
     * Ritorna il nome della fermata.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Ritorna la longitudine della fermata.
     */
    public double getLongitudine() {
        return longitudine;
    }

    /**
     * Ritorna la latitudine della fermata.
     */
    public double getLatitudine() {
        return latitudine;
    }

    /**
     * Ritorna l'icona della fermata.
     */
    public Image getIcona() {
        return icona;
    }

    /**
     * Seleziona la fermata e ne cambia l'icona.
     */
    public void seleziona() {
        if (selezionato) return;
        selezionato = true;
        icona = iconaSelezionata;
    }

    /**
     * Deseleziona la fermata e ne cambia l'icona.
     */
    public void deseleziona() {
        if (!selezionato) return;
        selezionato = false;

        if (id.startsWith("ITO")) {
            icona = iconaMetro;
        } else {
            icona = iconaAutobus;
        }
    }

    /**
     * Sovrascrive il metodo toString di DefaultWaypoint.
     */
    @Override
    public String toString() {
        return nome;
    }
}