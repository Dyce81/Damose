package Model;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

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

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public double getLongitudine() {
        return longitudine;
    }

    public double getLatitudine() {
        return latitudine;
    }

    public Image getIcona() {
        return icona;
    }

    public void seleziona() {
        if (selezionato) return;
        selezionato = true;
        icona = iconaSelezionata;
    }

    public void deseleziona() {
        if (!selezionato) return;
        selezionato = false;

        if (id.startsWith("ITO")) {
            icona = iconaMetro;
        } else {
            icona = iconaAutobus;
        }
    }

    @Override
    public String toString() {
        return nome;
    }
}