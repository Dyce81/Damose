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
import java.util.ArrayList;
import java.util.HashSet;

// Questa classe gestisce le fermate - tranne nel caso siano cercate tramite comboBox; in quel
// caso vengono gestite dal frame stesso

public class GestoreWaypoint {
    public final static ArrayList<CustomWaypoint> listaFermate = new ArrayList<>();
    public static WaypointPainter<CustomWaypoint> waypoint_painter = new CustomWaypointPainter();

    public static CustomWaypoint ultimaFermata;

    //DATI GTFS Statici

    // Questo metodo, oltre a posizionare le fermate sulla mappa (richiamando il CustomWaypointPainter),
    // si occupa anche di associare un MouseListener per rendere interattivi i vari waypoint
    public static void posizionaFermate() {
        waypoint_painter.setWaypoints(new HashSet<>(StaticGTFS.stops));
        Mappa.setPainter(waypoint_painter);

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

                //if (ultimaFermata != null)
                //    ultimaFermata.deseleziona();

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
                        mappa.setOverlayPainter(waypoint_painter);
                        break;
                    }
                }
            }
        });
    }

    public static WaypointPainter<CustomWaypoint> getWaypointPainter()
    {
        return waypoint_painter;
    }
}