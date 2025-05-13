
import java.util.ArrayList;

import Controller.Wifi;
import View.Frame;
import Controller.ReaderStaticGTFS;
import Model.Fermata;
import org.jxmapviewer.viewer.*;

public class Main
{
    //Come suggerisce il nome, contiene tutti gli oggetti (derivati dalla classe "Model.Fermata") delle
    //fermate
    private static ArrayList<Fermata> lista_fermate = new ArrayList<Fermata>();

    public static void main(String[] args) {

        Wifi wifi = new Wifi();
        wifi.wifi_controller();

        Frame frame = new Frame(600, 800, "Damose");

        //DATI GTFS Statici

        //probabilmente questa intera parte deve essere fatta da zero - attualmente, avendo le fermate
        //(oggetti) in un array, è impossibile distinguerle dall'esterno, rendendo probabilmente
        //impossibile tracciare le linee - FORSE

        //Genera tutte le fermate
        lista_fermate = elabora_fermate();

        DefaultWaypointRenderer wp_render = new DefaultWaypointRenderer();

        //WaypointPainter

        //Posizione le fermate
        for (Fermata fermata : lista_fermate)
        {
            DefaultWaypoint wp = new DefaultWaypoint(fermata.get_latitudine(), fermata.get_longitudine());

            //Graphics2D cerchio = new Graphics2D();
            //cerchio.setColor(Color.BLUE);

            //wp_render.paintWaypoint(new Graphics2D(), );
        }
    }

    //Utilizza Controller.ReaderStaticGTFS per leggere il file delle fermate ed elaborare una lista di fermate
    public static ArrayList<Fermata> elabora_fermate()
    {
        ArrayList<String[]> lista_valori_fermate = new ArrayList<String[]>();
        ReaderStaticGTFS reader = new ReaderStaticGTFS();
        lista_valori_fermate = reader.leggi_csv("data/rome_static_gtfs/stops.txt");

        ArrayList<Fermata> lista = new ArrayList<Fermata>();

        for (String[] valori : lista_valori_fermate)
        {
            //TODO: trovare un modo carino per non scrivere tutto questo costruttore (forse i parametri si possono passare più velocemente?)
            double longit = Double.parseDouble(valori[4]);
            double latit = Double.parseDouble(valori[5]);
            lista.add(new Fermata(valori[0], valori[1], valori[2], valori[3], longit, latit, valori[6], valori[7], valori[8], valori[9], valori[10]));
        }

        return lista;
    }
}