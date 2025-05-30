package Model;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

public class CustomWaypoint extends DefaultWaypoint
{
    //private final JButton icona;
    public boolean selezionato = false;

    private Image icona;
    private final String testo;

    public CustomWaypoint(String testo, GeoPosition coords)
    {
        super(coords);
        this.testo = testo;
        ImageIcon img_icon = new ImageIcon("assets/bus-solid.png");
        this.icona = img_icon.getImage();
    }

    public Image getIcona()
    {
        return icona;
    }

    public String getTesto()
    {
        return testo;
    }

    public void cliccato()
    {
        System.out.println("cliccato");
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
