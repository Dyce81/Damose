package Model;

import Controller.ReaderStaticGTFS;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class ElaboratoreFermate
{
    //Come suggerisce il nome, contiene tutti gli oggetti (derivati dalla classe "Model.Fermata") delle
    //fermate
    private static ArrayList<Fermata> lista_fermate = new ArrayList<Fermata>();

    //DATI GTFS Statici

    //probabilmente questa intera parte deve essere fatta da zero - attualmente, avendo le fermate
    //(oggetti) in un array, è impossibile distinguerle dall'esterno, rendendo probabilmente
    //impossibile tracciare le linee - FORSE

    //elabora e posiziona tutte le fermate e restituisce un painter per farle disegnare dalla mappa
    public Painter posiziona_fermate()
    {
        elabora_fermate();

        Set<Waypoint> waypoints = new HashSet<Waypoint>();

        //Posizione le fermate
        for (Fermata fermata : lista_fermate)
        {
            DefaultWaypoint wp = new DefaultWaypoint(fermata.get_latitudine(), fermata.get_longitudine());
            waypoints.add(wp);
        }

        WaypointPainter<Waypoint> waypoint_painter = new WaypointPainter<Waypoint>();
        waypoint_painter.setWaypoints(waypoints);

        return waypoint_painter;
    }

    //Utilizza Controller.ReaderStaticGTFS per leggere il file delle fermate ed elaborare una lista di fermate
    public static void elabora_fermate()
    {
        ArrayList<String[]> lista_valori_fermate = new ArrayList<String[]>();
        lista_valori_fermate = ReaderStaticGTFS.leggi_csv("data/rome_static_gtfs/stops.txt");

        ArrayList<Fermata> lista = new ArrayList<Fermata>();

        for (String[] valori : lista_valori_fermate)
        {
            //TODO: trovare un modo carino per non scrivere tutto questo costruttore (forse i parametri si possono passare più velocemente?)
            double longit = Double.parseDouble(valori[4]);
            double latit = Double.parseDouble(valori[5]);
            lista_fermate.add(new Fermata(valori[0], valori[1], valori[2], valori[3], longit, latit, valori[6], valori[7], valori[8], valori[9], valori[10]));
        }
    }
}
