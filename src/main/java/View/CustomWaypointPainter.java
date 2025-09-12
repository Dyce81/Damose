package View;

import Model.CustomWaypoint;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.WaypointPainter;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Questa classe si occupa di disegnare sia le fermate che gli autobus.
 */
public class CustomWaypointPainter extends WaypointPainter<CustomWaypoint>
{
    /**
     * Costruttore.
     */
    public CustomWaypointPainter()
    {
        setCacheable(false);
    }

    private static boolean tracciamentoAttivo = false;
    private static ArrayList<GeoPosition> posizioniMezzi = new ArrayList<>();

    private static final Image iconaAutobus = new ImageIcon("assets/autobus_icona.png").getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);

    @Override
    protected void doPaint(Graphics2D g, JXMapViewer map, int larghezza, int altezza)
    {
        if (tracciamentoAttivo && !posizioniMezzi.isEmpty())
        {
            for (GeoPosition posizione : posizioniMezzi)
            {
                Point2D punto = map.convertGeoPositionToPoint(posizione);
                int x = (int) (punto.getX()) - 12; //12 = la metà (circa) della dimensione dell'icona
                int y = (int) (punto.getY()) - 12; //idem

                g.drawImage(iconaAutobus, x, y, null);
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
            //Se l'icona si trova fuori dai confini della mappa oltre questo offset, non sarà disegnata
            int offsetVisibilita = 30;
            if (x < -offsetVisibilita || x > larghezza + offsetVisibilita || y < -offsetVisibilita
                    || y > altezza + offsetVisibilita) continue;

            Image icona = wp.getIcona();
            g.drawImage(icona, x - icona.getWidth(null) / 2, y - icona.getHeight(null) / 2, null);
        }
    }

    /**
     * Imposta il tracciamentoAttivo.
     */
    public static void setTracciamentoAttivo(boolean attivo)
    {
        tracciamentoAttivo = attivo;
    }

    /**
     * Imposta la lista delle posizioni dei mezzi.
     */
    public static void setPosizioniMezzi(ArrayList<GeoPosition> pos)
    {
        posizioniMezzi = pos;
    }
}
