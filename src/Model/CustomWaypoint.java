package Model;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;

public class CustomWaypoint extends DefaultWaypoint
{
    //private final JButton icona;
    public boolean selezionato = false;

    private final String nome;
    private final double longitudine;
    private final double latitudine;
    private Image icona;

    public CustomWaypoint(String nome, GeoPosition coords)
    {
        super(coords);
        this.nome = nome;
        this.longitudine = coords.getLongitude();
        this.latitudine = coords.getLatitude();
        ImageIcon img_icon = new ImageIcon("assets/bus-solid.png");
        this.icona = img_icon.getImage();
    }

    public String getNome()
    {
        return nome;
    }

    public double getLongitudine()
    {
        return longitudine;
    }

    public double getLatitudine()
    {
        return latitudine;
    }

    public Image getIcona()
    {
        return icona;
    }

    public void seleziona()
    {
        if (selezionato) return; //superfluo?

        selezionato = true;
        ImageIcon img_icon = new ImageIcon("assets/bus-solid_selezionato.png");
        icona = img_icon.getImage();
    }

    public void deseleziona()
    {
        if (!selezionato) return;

        selezionato = false;
        ImageIcon img_icon = new ImageIcon("assets/bus-solid.png");
        icona = img_icon.getImage();
    }
}
