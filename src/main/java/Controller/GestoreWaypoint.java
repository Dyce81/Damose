package Controller;

import Model.CustomWaypoint;
import View.CustomWaypointPainter;
import View.Mappa;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashSet;

/**
 * GestoreWaypoint si occupa della gestione dei CustomWaypoint (le fermate/stazioni). Si occupa
 * di posizionare le fermate sulla mappa e aggiunge a ciascuna icona (waypoint) un MouseListener
 * per rendere possibile l'interazione con l'utente.
 */
public class GestoreWaypoint
{
    private final static WaypointPainter<CustomWaypoint> waypointPainter = new CustomWaypointPainter();
    private final GestoreInformazioni gestoreInformazioni;

    /**
     * Il costruttore della classe.
     * @param gestoreInformazioni il riferimento al gestoreInformazioni istanziato.
     */
    public GestoreWaypoint(GestoreInformazioni gestoreInformazioni) {
        this.gestoreInformazioni = gestoreInformazioni;
    }

    /**
     * Restituisce il CustomWaypointPainter usato per posizionare (e quindi disegnare) le fermate
     * sulla mappa.
     * @return il CustomWaypointPainter usato.
     */
    public static WaypointPainter<CustomWaypoint> getWaypointPainter() {
        return waypointPainter;
    }

    /**
     * Disegna le fermate (i waypoint memorizzati in StaticGTFS.stops) sulla mappa, passando alla mappa
     * stessa il CustomWaypointPainter di questa classe.
     */
    public void posizionaFermate()
    {
        waypointPainter.setWaypoints(new HashSet<>(StaticGTFS.stops));
        Mappa.setPainter(waypointPainter);
        CustomMouseListener(Mappa.getMapViewer());
    }

    private void CustomMouseListener(JXMapViewer mappa)
    {
        mappa.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (Mappa.getMapViewer().getZoom() > 3) return;
                Point puntoClick = e.getPoint();
                Rectangle viewport = mappa.getViewportBounds();

                for (CustomWaypoint wp : StaticGTFS.stops)
                {
                    Point2D punto = mappa.getTileFactory().geoToPixel(wp.getPosition(), mappa.getZoom());
                    int x = (int) (punto.getX() - viewport.getX());
                    int y = (int) (punto.getY() - viewport.getY());
                    Rectangle bordi = new Rectangle(x - 8, y - 8, 17, 17);

                    if (bordi.contains(puntoClick))
                    {
                        if (wp.selezionato) {
                            // Delega la deselezione al GestoreInformazioni
                            gestoreInformazioni.deselezionaFermata();
                        } else {
                            gestoreInformazioni.selezionaFermata(wp);
                        }
                        break;
                    }
                }
            }
        });
    }
}