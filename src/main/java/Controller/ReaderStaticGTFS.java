package Controller;

import Model.PuntoShape;
import Model.Route;
import Model.Trip;
import Model.StopTime;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/*                                  IMPORTANTE
    Sarebbe meglio usare una libreria come OpenCSV per leggere i file .csv; per il momento
    è stato implementato un metodo (dividi_stringa).
 */

//SOLO PER ADESSO questa classe contiene anche il riferimento (statico) alle liste di routes, trips,
//(shapes) e stopTimes

public class ReaderStaticGTFS
{
    public static ArrayList<Route> routes = new ArrayList<>();
    public static ArrayList<Trip> trips = new ArrayList<>();
    public static ArrayList<StopTime> stopTimes = new ArrayList<>();
    public static ArrayList<PuntoShape> shapes = new ArrayList<>();

    public static void iniziaPROVVISORIO()
    {
        ArrayList<String[]> provvisorio = leggi_csv("data/rome_static_gtfs/routes.txt");

        for (String[] lista : provvisorio)
        {
            Route aggiungi = new Route("", "", 0, "");

            aggiungi.setId(lista[0]);
            aggiungi.setNome(lista[2]);
            aggiungi.setTipo(Integer.parseInt(lista[4]));
            aggiungi.setUrl(lista[5]);
            //System.out.println(aggiungi);

            routes.add(aggiungi);
        }

        provvisorio = leggi_csv("data/rome_static_gtfs/trips.txt");

        for (String[] lista : provvisorio)
        {
            Trip aggiungi = new Trip("", "", "", lista[7]);

            aggiungi.setId(lista[2]);
            aggiungi.setRouteId(lista[0]);
            aggiungi.setHeadsign(lista[3]);
            //System.out.println(aggiungi);

            trips.add(aggiungi);
        }

        provvisorio = leggi_csv("data/rome_static_gtfs/stop_times.txt");

        for (String[] lista : provvisorio)
        {
            StopTime aggiungi = new StopTime("", "", "", "");

            aggiungi.setTripId(lista[0]);
            aggiungi.setStopId(lista[3]);
            aggiungi.setOrarioArrivo(lista[1]);
            aggiungi.setOrarioPartenza(lista[2]);
            //System.out.println(aggiungi);

            stopTimes.add(aggiungi);
        }

        provvisorio = leggi_csv("data/rome_static_gtfs/shapes.txt");

        for (String[] lista : provvisorio)
        {
            double latitudine = Double.parseDouble(lista[1]);
            double longitudine = Double.parseDouble(lista[2]);
            int sequenza = Integer.parseInt(lista[3]);

            shapes.add(new PuntoShape(lista[0], latitudine, longitudine, sequenza));
        }
    }

    //Questo metodo restituisce un'arraylist di array, dove ciascuna lista interna
    //indica i valori di una singola fermata; quindi l'array esterno racchiude tutte le fermate.
    //Da lì si può poi generare ogni singola fermata (oggetto) sulla mappa

    //più generalmente restituisce una lista di lista (quest'ultima contiene i valori di ogni riga)
    public static ArrayList<String[]> leggi_csv(String path)
    {
        //Senza try... catch non è possibile usare FileReader
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));

            ArrayList<String[]> lista_fermate = new ArrayList<String[]>();

            String linea;
            //linea = reader.readLine(); //Ignora la prima riga (contiene i nomi dei campi)
            reader.readLine();
            while ((linea = reader.readLine()) != null)
            {
                String[] valori = dividi_stringa(linea, ',').toArray(new String[0]);
                lista_fermate.add(valori);
            }

            return lista_fermate;
        } catch (Exception e) {
            System.out.println("Impossibile leggere il file indicato. \n " + path);
            return new ArrayList<String[]>();
        }
    }

    //Metodo sostitutivo di String.split() - questo metodo divide una stringa in un Array in base
    //al carattere separatore scelto; a differenze di String.split(), in un caso come ",," il metodo
    //comunque restituisce un valore vuoto
    public static ArrayList<String> dividi_stringa(String valore, char separatore)
    {
        String buffer = "";
        ArrayList<String> lista = new ArrayList<String>();
        for (int c = 0; c < valore.length(); c++)
        {
            if (valore.charAt(c) == separatore)
            {
                lista.add(buffer);
                buffer = "";
                continue;
            }

            buffer += valore.charAt(c);
        }

        lista.add(buffer);
        //System.out.println(lista); // da rimuovere
        return lista;
    }
}
