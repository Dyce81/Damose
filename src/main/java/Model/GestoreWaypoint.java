package Model;

import Controller.StaticGTFS;
import View.Mappa;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

// Questa classe gestisce le fermate - tranne nel caso siano cercate tramite comboBox; in quel
// caso vengono gestite dal frame stesso

public class GestoreWaypoint {
    //Come suggerisce il nome, contiene tutti gli oggetti delle fermate
    public final static ArrayList<CustomWaypoint> listaFermate = new ArrayList<>();
    //public final static ArrayList<String> nomi_fermate = new ArrayList<>();
    private final static Set<CustomWaypoint> waypoints = new HashSet<>();
    //questo era originariamente locale all'interno del metodo "elabora_fermate"; probabilmente
    //converebbe in locale, magari passandolo per riferimento come parametro quando si
    //richiama la funzione
    public static WaypointPainter<CustomWaypoint> waypoint_painter = new CustomWaypointPainter();

    public static CustomWaypoint ultimaFermata;

    //DATI GTFS Statici
    //TODO: spostare questo metodo qui sotto in StaticGTFS (in realtà è un po' da ripensare tutto,
    // visto che adesso ci sono due liste (waypoints e listaFermate), mentre se ne potrebbe fare solo
    // una (stops) (forse?)

    public void posizionaFermate(Mappa mappa) {
        ArrayList<String[]> listaValoriFermate;
        listaValoriFermate = StaticGTFS.leggi_csv("data/rome_static_gtfs/stops.txt");

        //nomi_fermate.add("-- Seleziona una fermata --");

        for (String[] valori : listaValoriFermate) {
            double longit = Double.parseDouble(valori[4]);
            double latit = Double.parseDouble(valori[5]);

            // Se la stazione/fermata analizzata è della metro, ignorare quelle con campo
            // location_type != 1 (1 è la stazione fisica, altri valori rappresentano
            // "sottocomponenti" della stazione stessa)
            if (valori[0].startsWith("ITO"))
                if (!valori[9].equals("1")) continue;
            else
            {
                // Sposta leggermente la fermata, perché quei geni di Roma Capitale hanno messo
                // (per OGNI stazione della metro) una fermata dell'autobus ESATTAMENTE alle
                // stesse identiche coordinate, rendendo di fatto impossibile cliccare
                // una delle due (solitamente la fermata dell'autobus, perché in stops.txt
                // le fermate della metro sono le ultime ad essere specificate, ergo le ultime
                // ad essere piazzate sulla mappa)
                longit += 0.0002;
            }

            //double longit = Double.parseDouble(valori[4]);
            //double latit = Double.parseDouble(valori[5]);
            GeoPosition coords = new GeoPosition(longit, latit);
            CustomWaypoint cwp = new CustomWaypoint(valori[0], valori[2], coords);
            waypoints.add(cwp);

            //nomi_fermate.add(valori[2].toUpperCase());
            listaFermate.add(cwp);
        }

        waypoint_painter.setWaypoints(waypoints);

        //forse
        //mappa.set_painter(waypoint_painter);
        Mappa.set_painter(waypoint_painter);
    }

    //Collega alla mappa un mouse listener per poter interagire con i singoli waypoint (che sono immagini)
    public void CustomMouseListener(JXMapViewer mappa) {
        mappa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Mappa.getMapViewer().getZoom() > 3)
                    return;

                Point puntoClick = e.getPoint();
                Rectangle viewport = mappa.getViewportBounds();

                //if (ultimaFermata != null)
                //    ultimaFermata.deseleziona();

                for (CustomWaypoint wp : waypoints) {
                    Point2D punto = mappa.getTileFactory().geoToPixel(wp.getPosition(), mappa.getZoom());
                    int x = (int) (punto.getX() - viewport.getX());
                    int y = (int) (punto.getY() - viewport.getY());
                    Rectangle bordi = new Rectangle(x - 8, y - 8, 17, 17); //TODO: rivedere i bordi
                    //8 sopra è un po' un numero magico - in questo caso la metà (-1) di 17, ovvero
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