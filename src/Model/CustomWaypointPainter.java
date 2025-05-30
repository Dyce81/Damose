package Model;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointPainter;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class CustomWaypointPainter extends WaypointPainter<CustomWaypoint>
{
    public CustomWaypointPainter()
    {
        setCacheable(false);
    }

    //Se l'icona si trova fuori dai confini della mappa oltre questo offset, non sarà disegnata
    private final int offsetVisibilita = 30;

    @Override
    protected void doPaint(Graphics2D g, JXMapViewer map, int larghezza, int altezza)
    {
        if (map.getZoom() > 3)
            return;

        for (CustomWaypoint wp : getWaypoints())
        {
            Point2D punto = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());
            Rectangle viewport = map.getViewportBounds();
            int x = (int)(punto.getX() - viewport.getX());
            int y = (int)(punto.getY() - viewport.getY());

            //Se l'icona è fuori dallo schermo, non la disegnare
            if (x < -offsetVisibilita || x > larghezza + offsetVisibilita || y < -offsetVisibilita
            || y > altezza + offsetVisibilita) continue;

            Image icona = wp.getIcona();
            g.drawImage(icona, x, y, null);
            //JButton icona = wp.getIcona();
            //icona.setLocation(iconaX - icona.getWidth() / 2, iconaY - icona.getHeight() / 2);
        }
    }
}
