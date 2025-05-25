package Model;

import Controller.ReaderStaticGTFS;
import View.Mappa;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.*;
import org.jxmapviewer.painter.Painter;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class ElaboratoreFermate
{
    //Come suggerisce il nome, contiene tutti gli oggetti (derivati dalla classe "Model.Fermata") delle
    //fermate
    public final static ArrayList<Fermata> lista_fermate = new ArrayList<Fermata>();
    public final static ArrayList<String> nomi_fermate = new ArrayList<String>();
    Set<CustomWaypoint> waypoints = new HashSet<CustomWaypoint>();
    //questo era originariamente locale all'interno del metodo "elabora_fermate"; probabilmente
    //converebbe in locale, magari passandolo per riferimento come parametro quando si
    //richiama la funzione
    public static WaypointPainter<CustomWaypoint> waypoint_painter = new CustomWaypointPainter();

    //DATI GTFS Statici

    //probabilmente questa intera parte deve essere fatta da zero - attualmente, avendo le fermate
    //(oggetti) in un array, è impossibile distinguerle dall'esterno, rendendo probabilmente
    //impossibile tracciare le linee - FORSE

    //elabora e posiziona tutte le fermate e restituisce un painter per farle disegnare dalla mappa
    public void posiziona_fermate(Mappa mappa)
    {
        elabora_fermate();

        //Set<CustomWaypoint> waypoints = new HashSet<CustomWaypoint>();

        //Posizione le fermate
        for (Fermata fermata : lista_fermate)
        {
            GeoPosition coords = new GeoPosition(fermata.get_latitudine(), fermata.get_longitudine());
            //DefaultWaypoint wp = new DefaultWaypoint(coords);
            //waypoints.add(wp);
            CustomWaypoint cwp = new CustomWaypoint(fermata.get_nome(), coords);
            waypoints.add(cwp);
        }

        //WaypointPainter<CustomWaypoint> waypoint_painter = new CustomWaypointPainter();
        waypoint_painter.setWaypoints(waypoints);

        /*for (CustomWaypoint w : waypoints)
        {
            mappa.mappa.add(w.getIcona()); //brutto
        }*/

        //forse
        mappa.set_painter(waypoint_painter);
    }

    //Utilizza Controller.ReaderStaticGTFS per leggere il file delle fermate ed elaborare una lista di fermate
    public static void elabora_fermate()
    {
        ArrayList<String[]> lista_valori_fermate = new ArrayList<String[]>();
        lista_valori_fermate = ReaderStaticGTFS.leggi_csv("data/rome_static_gtfs/stops.txt");

        ArrayList<Fermata> lista = new ArrayList<Fermata>();
        nomi_fermate.add("-- Seleziona una fermata --");

        for (String[] valori : lista_valori_fermate)
        {
            //TODO: trovare un modo carino per non scrivere tutto questo costruttore (forse i parametri si possono passare più velocemente?)
            double longit = Double.parseDouble(valori[4]);
            double latit = Double.parseDouble(valori[5]);
            lista_fermate.add(new Fermata(valori[0], valori[1], valori[2], valori[3], longit, latit, valori[6], valori[7], valori[8], valori[9], valori[10]));
            nomi_fermate.add(valori[2].toUpperCase());
        }
    }

    //Collega alla mappa un mouse listener per poter interagire con i singoli waypoint (che sono immagini)
    public void CustomMouseListener(JXMapViewer mappa)
    {
        mappa.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                Point puntoClick = e.getPoint();
                Rectangle viewport = mappa.getViewportBounds();

                for (CustomWaypoint wp : waypoints)
                {
                    Point2D punto = mappa.getTileFactory().geoToPixel(wp.getPosition(), mappa.getZoom());
                    int x = (int)(punto.getX() - viewport.getX());
                    int y = (int)(punto.getY() - viewport.getY());
                    Rectangle bordi = new Rectangle(x - 15, y - 15, 30, 30); //TODO: rivedere i bordi
                    boolean trovato = false; //serve per cercare la fermata già selezionata
                    if (bordi.contains(puntoClick))
                    {
                        // TODO: prima di cambiare la selezione, dovrebbe assicurarsi che tutti gli
                        // altri waypoint non siano selezionati
                        wp.seleziona();
                        mappa.setOverlayPainter(waypoint_painter);
                        if (trovato) break;
                        //break;
                    }
                    else if (wp.selezionato) //brutto?
                    {
                        trovato = true;
                        wp.deseleziona();
                    }
                }
            }
        });
    }
}
