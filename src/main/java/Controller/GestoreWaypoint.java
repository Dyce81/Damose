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

public class GestoreWaypoint {
    private final static WaypointPainter<CustomWaypoint> waypointPainter = new CustomWaypointPainter();
    private final GestoreInformazioni gestoreInformazioni;

    // Aggiungi un costruttore che riceve l'istanza di GestoreInformazioni
    public GestoreWaypoint(GestoreInformazioni gestoreInformazioni) {
        this.gestoreInformazioni = gestoreInformazioni;
    }

    public static WaypointPainter<CustomWaypoint> getWaypointPainter() {
        return waypointPainter;
    }

    public void posizionaFermate() {
        waypointPainter.setWaypoints(new HashSet<>(StaticGTFS.stops));
        Mappa.setPainter(waypointPainter);
        CustomMouseListener(Mappa.getMapViewer());
    }

    private void CustomMouseListener(JXMapViewer mappa) {
        mappa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Mappa.getMapViewer().getZoom() > 3) return;
                Point puntoClick = e.getPoint();
                Rectangle viewport = mappa.getViewportBounds();

                for (CustomWaypoint wp : StaticGTFS.stops) {
                    Point2D punto = mappa.getTileFactory().geoToPixel(wp.getPosition(), mappa.getZoom());
                    int x = (int) (punto.getX() - viewport.getX());
                    int y = (int) (punto.getY() - viewport.getY());
                    Rectangle bordi = new Rectangle(x - 8, y - 8, 17, 17);

                    if (bordi.contains(puntoClick)) {
                        if (wp.selezionato) {
                            // Delega la deselezione al GestoreInformazioni
                            gestoreInformazioni.deselezionaFermata();
                        } else {
                            // Delega la selezione al GestoreInformazioni
                            gestoreInformazioni.selezionaFermata(wp);
                        }
                        break;
                    }
                }
            }
        });
    }
}