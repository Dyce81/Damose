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
    private final Image icona;
    private final String testo;

    public CustomWaypoint(String testo, GeoPosition coords)
    {
        super(coords);
        this.testo = testo;
        ImageIcon img_icon = new ImageIcon("assets/bus-solid.png");
        this.icona = img_icon.getImage();
        /*icona = new JButton(img_icon);
        icona.setSize(48, 24);
        icona.setBorder(null);
        icona.setMargin(new Insets(0, 0, 0, 0));
        icona.setContentAreaFilled(false);
        icona.addMouseListener(new CustomWaypointMouseListener());
        icona.setVisible(true);*/
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

    /*private static class CustomWaypointMouseListener implements MouseListener
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            System.out.println("cliccato");
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
        }

        public void mouseEntered(MouseEvent e)
        {
        }

        public void mouseExited(MouseEvent e)
        {
        }
    }*/
}
