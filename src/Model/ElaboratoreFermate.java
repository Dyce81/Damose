package Model;

import Controller.ReaderStaticGTFS;
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

//TODO: (forse) cambiare il nome di questo file in "ManagerWaypoint" o "GestoreFermate" o qualcosa così?
//comunque questa classe gestisce le fermate - tranne nel caso siano cercate tramite comboBox; in quel
//caso vengono gestite dal frame stesso

public class ElaboratoreFermate {
    //Come suggerisce il nome, contiene tutti gli oggetti (derivati dalla classe "Model.Fermata") delle
    //fermate
    public final static ArrayList<CustomWaypoint> listaFermate = new ArrayList<>();
    public final static ArrayList<String> nomi_fermate = new ArrayList<>();
    Set<CustomWaypoint> waypoints = new HashSet<>();
    //questo era originariamente locale all'interno del metodo "elabora_fermate"; probabilmente
    //converebbe in locale, magari passandolo per riferimento come parametro quando si
    //richiama la funzione
    public static WaypointPainter<CustomWaypoint> waypoint_painter = new CustomWaypointPainter();

    private CustomWaypoint ultimaFermata;

    //DATI GTFS Statici

    public void posizionaFermate(Mappa mappa) {
        ArrayList<String[]> listaValoriFermate;
        listaValoriFermate = ReaderStaticGTFS.leggi_csv("data/rome_static_gtfs/stops.txt");

        nomi_fermate.add("-- Seleziona una fermata --");

        for (String[] valori : listaValoriFermate) {
            double longit = Double.parseDouble(valori[4]);
            double latit = Double.parseDouble(valori[5]);
            GeoPosition coords = new GeoPosition(longit, latit);
            CustomWaypoint cwp = new CustomWaypoint(valori[2], coords);
            waypoints.add(cwp);

            nomi_fermate.add(valori[2].toUpperCase());
            listaFermate.add(cwp);
        }

        waypoint_painter.setWaypoints(waypoints);

        //forse
        mappa.set_painter(waypoint_painter);
    }

    //Collega alla mappa un mouse listener per poter interagire con i singoli waypoint (che sono immagini)
    public void CustomMouseListener(JXMapViewer mappa) {
        mappa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point puntoClick = e.getPoint();
                Rectangle viewport = mappa.getViewportBounds();

                if (ultimaFermata != null)
                    ultimaFermata.deseleziona();

                for (CustomWaypoint wp : waypoints) {
                    Point2D punto = mappa.getTileFactory().geoToPixel(wp.getPosition(), mappa.getZoom());
                    int x = (int) (punto.getX() - viewport.getX());
                    int y = (int) (punto.getY() - viewport.getY());
                    Rectangle bordi = new Rectangle(x, y, 15, 15); //TODO: rivedere i bordi
                    if (bordi.contains(puntoClick)) {
                        wp.seleziona();
                        ultimaFermata = wp;
                        mappa.setOverlayPainter(waypoint_painter);
                    }
                }
            }
        });

    }
}