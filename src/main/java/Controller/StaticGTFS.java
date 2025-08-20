package Controller;

import Model.*;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/*                                  IMPORTANTE
    Sarebbe meglio usare una libreria come OpenCSV per leggere i file .csv; per il momento
    è stato implementato un metodo (dividi_stringa).
 */

//SOLO PER ADESSO questa classe contiene anche il riferimento (statico) alle liste di routes, trips,
//(shapes) e stopTimes
// - no forse conviene che rimangano qui (magari più tardi insieme a stops)

public class StaticGTFS
{
    public static ArrayList<Route> routes = new ArrayList<>();
    public static ArrayList<Trip> trips = new ArrayList<>();
    public static ArrayList<StopTime> stopTimes = new ArrayList<>();
    public static ArrayList<PuntoShape> shapes = new ArrayList<>();

    //public static ArrayList<Calendar> calendars = new ArrayList<>();

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
            aggiungi.setServiceId(lista[1]);
            //System.out.println(aggiungi);

            trips.add(aggiungi);
        }

        provvisorio = leggi_csv("data/rome_static_gtfs/stop_times.txt");

        for (String[] lista : provvisorio)
        {
            StopTime aggiungi = new StopTime("", "", lista[1], lista[2]);

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

    //Tracciamento statico dei mezzi
    //Non so se questo metodo va bene: è molto rigido, mostra molti mezzi in circolazione (ignora
    //eventuali disservizi, corse cancellate, festività eccetera) e mostra i mezzi solo in fermata
    //ignorando il percorso tra le fermate. quindi probabilmente questo metodo è da rifare completamente
    public static ArrayList<GeoPosition> getPosizioneVeicolo(String routeId)
    {
        LocalTime adesso = LocalTime.now();
        System.out.println(adesso.truncatedTo(ChronoUnit.MINUTES));

        List<Trip> viaggiTrovati = trips.stream()
                .filter(t -> t.getRouteId().equals(routeId))
                .toList();

        List<StopTime> orariFermate = stopTimes.stream()
                .filter(st -> st.getOrarioArrivo().truncatedTo(ChronoUnit.MINUTES).equals(adesso.truncatedTo(ChronoUnit.MINUTES)))
                .filter(st -> viaggiTrovati.stream().anyMatch(t -> t.getId().equals(st.getTripId())))
                .toList();

        List<CustomWaypoint> fermate = GestoreWaypoint.listaFermate.stream()
                .filter(f -> orariFermate.stream().anyMatch(st -> st.getStopId().equals(f.getId())))
                .toList();

        ArrayList<GeoPosition> coordinateMezzi = new ArrayList<>();

        for (CustomWaypoint f : fermate)
            coordinateMezzi.add(new GeoPosition(f.getLatitudine(), f.getLongitudine()));

        return coordinateMezzi;
    }

    public static String getTripUpdate(String stopId, String routeId)
    {
        //long adesso = Instant.now().getEpochSecond();
        //System.out.println(adesso);

        List<Trip> viaggiTrovati = trips.stream()
                .filter(t -> t.getRouteId().equals(routeId))
                .toList();

        //questo qui sotto è temporaneo
        List<StopTime> orariFermate = stopTimes.stream()
                .filter(st -> st.getStopId().equals(stopId))
                .filter(st -> viaggiTrovati.stream().anyMatch(t -> t.getId().equals(st.getTripId())))
                .toList();

        LocalTime adesso = LocalTime.now();

        Optional<StopTime> prossimoArrivo = orariFermate.stream()
                .filter(st -> parseTimeCorretto(st.getOrarioArrivo().toString()).isAfter(adesso))
                .min(Comparator.comparing(st -> parseTimeCorretto(st.getOrarioArrivo().toString())));

        if (prossimoArrivo.isPresent())
            return prossimoArrivo.get().getOrarioArrivo().format(DateTimeFormatter.ofPattern("HH:mm"));
        else
            return null;
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

    public static LocalTime parseTimeCorretto(String tempo)
    {
        String[] parti = tempo.split(":");
        int ore = Integer.parseInt(parti[0]);
        int minuti = Integer.parseInt(parti[1]);
        int secondi = Integer.parseInt(parti[2]);

        ore %= 24;

        return LocalTime.of(ore, minuti, secondi);
    }

    public static Route getLinea(String routeId)
    {
        for (Route r : routes)
            if (r.getId().equals(routeId))
                return r;

        return null; //alquanto improbabile che venga restituito null
    }

    public static List<GeoPosition> getPercorso(String routeId)
    {
        ArrayList<Trip> viaggi = trips.stream()
                .filter(trip -> trip.getRouteId().equals(routeId)).collect(Collectors.toCollection(ArrayList::new));

        if (viaggi.isEmpty())
        {
            return new ArrayList<>();
        }

        Trip viaggioSelezionato = viaggi.getFirst();

        return shapes.stream()
                .filter(sp -> sp.getId().equals(viaggioSelezionato.getShapeId()))
                .sorted(Comparator.comparingInt(PuntoShape::getSequenza))
                .map(sp -> new GeoPosition(sp.getLatitudine(), sp.getLongitudine()))
                .toList();
    }
}
