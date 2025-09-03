package Model;

import Controller.StaticGTFS;
import View.CustomWaypointPainter;
import View.Mappa;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashSet;

// Questa classe gestisce le fermate - tranne nel caso siano cercate tramite comboBox; in quel
// caso vengono gestite dal frame stesso

public class GestoreWaypoint {
    private final static WaypointPainter<CustomWaypoint> waypointPainter = new CustomWaypointPainter();
    private static CustomWaypoint ultimaFermata;

    // Questo metodo, oltre a posizionare le fermate sulla mappa (richiamando il CustomWaypointPainter),
    // si occupa anche di associare un MouseListener per rendere interattivi i vari waypoint
    public static void posizionaFermate() {
        waypointPainter.setWaypoints(new HashSet<>(StaticGTFS.stops));
        Mappa.setPainter(waypointPainter);

        CustomMouseListener(Mappa.getMapViewer());
    }

    //Collega alla mappa un mouse listener per poter interagire con i singoli waypoint (che sono immagini)
    private static void CustomMouseListener(JXMapViewer mappa) {
        mappa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Mappa.getMapViewer().getZoom() > 3)
                    return;

                Point puntoClick = e.getPoint();
                Rectangle viewport = mappa.getViewportBounds();

                for (CustomWaypoint wp : StaticGTFS.stops) {
                    Point2D punto = mappa.getTileFactory().geoToPixel(wp.getPosition(), mappa.getZoom());
                    int x = (int) (punto.getX() - viewport.getX());
                    int y = (int) (punto.getY() - viewport.getY());
                    Rectangle bordi = new Rectangle(x - 8, y - 8, 17, 17); //TODO: rivedere i bordi
                    //8 sopra è un po' un numero magico - in questo caso la metà (-.5) di 17, ovvero
                    //la metà della grandezza dell'icona delle fermate
                    if (bordi.contains(puntoClick)) {
                        if (wp.selezionato)
                        {
                            wp.deseleziona();
                            ultimaFermata = null;
                            return;
                        }

                        if (ultimaFermata != null)
                            ultimaFermata.deseleziona();

                        wp.seleziona();
                        ultimaFermata = wp;
                        mappa.setOverlayPainter(waypointPainter);
                        break;
                    }
                }
            }
        });
    }

    public static WaypointPainter<CustomWaypoint> getWaypointPainter()
    {
        return waypointPainter;
    }

    public static CustomWaypoint getUltimaFermata()
    {
        return ultimaFermata;
    }

    public static void setUltimaFermata(CustomWaypoint fermata)
    {
        ultimaFermata = fermata;
    }
}