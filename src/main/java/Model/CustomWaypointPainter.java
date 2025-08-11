package Model;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.WaypointPainter;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class CustomWaypointPainter extends WaypointPainter<CustomWaypoint>
{
    public CustomWaypointPainter()
    {
        setCacheable(false);
    }

    //Se l'icona si trova fuori dai confini della mappa oltre questo offset, non sarà disegnata
    private final int offsetVisibilita = 30;
    private boolean tracciamentoAttivo = false;
    private ArrayList<GeoPosition> posizioniMezzi = new ArrayList<>();

    @Override
    protected void doPaint(Graphics2D g, JXMapViewer map, int larghezza, int altezza)
    {
        if (map.getZoom() < 6 && tracciamentoAttivo)
        {
            for (GeoPosition posizione : posizioniMezzi)
            {

            }
        }

        if (map.getZoom() > 3)
        {
            for (CustomWaypoint wp : getWaypoints())
            {
                if (wp.selezionato)
                {
                    Point2D punto = map.getTileFactory().geoToPixel(wp.getPosition(), map.getZoom());
                    Rectangle viewport = map.getViewportBounds();
                    int x = (int) (punto.getX() - viewport.getX());
                    int y = (int) (punto.getY() - viewport.getY());

                    Image icona = wp.getIcona();
                    g.drawImage(icona, x - icona.getWidth(null) / 2, y - icona.getHeight(null) / 2, null);
                    return;
                }
            }
            return;
        }

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
            //g.drawImage(icona, x, y, null);
            g.drawImage(icona, x - icona.getWidth(null) / 2, y - icona.getHeight(null) / 2, null);
            //JButton icona = wp.getIcona();
            //icona.setLocation(iconaX - icona.getWidth() / 2, iconaY - icona.getHeight() / 2);
        }
    }

    public void setTracciamentoAttivo(boolean tracciamentoAttivo)
    {
        this.tracciamentoAttivo = tracciamentoAttivo;
    }

    public void setPosizioniMezzi(ArrayList<GeoPosition> posizioniMezzi)
    {
        this.posizioniMezzi = posizioniMezzi;
    }
}
