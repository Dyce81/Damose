package Controller;

import Model.*;
import View.LoadingScreen;
import com.opencsv.CSVReader;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.FileReader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class StaticGTFS
{
    public static ArrayList<CustomWaypoint> stops = new ArrayList<>();
    public static ArrayList<Route> routes = new ArrayList<>();
    public static ArrayList<Trip> trips = new ArrayList<>();
    public static ArrayList<StopTime> stopTimes = new ArrayList<>();
    public static ArrayList<PuntoShape> shapes = new ArrayList<>();
    public static ArrayList<CollegamentoMetro> collegamentiMetro = new ArrayList<>();

    private static final List<String> lineeMetro = new ArrayList<>();

    public static void inizializzaDati()
    {
        ArrayList<String[]> provvisorio = leggiCSV("data/rome_static_gtfs/stops.txt");

        for (String[] lista : provvisorio)
        {
            double longit = Double.parseDouble(lista[4]);
            double latit = Double.parseDouble(lista[5]);

            // Se la stazione/fermata analizzata è della metro, ignorare quelle con campo
            // location_type != 1 (1 è la stazione fisica, altri valori rappresentano
            // "sottocomponenti" della stazione stessa)
            if (lista[0].startsWith("ITO"))
            {
                if (!lista[9].equals("1")) continue;
                else {
                    // Sposta leggermente la fermata, perché quei geni di Roma Capitale hanno messo
                    // (per OGNI stazione della metro) una fermata dell'autobus ESATTAMENTE alle
                    // stesse identiche coordinate, rendendo di fatto impossibile cliccare
                    // una delle due (solitamente la fermata dell'autobus, perché in stops.txt
                    // le fermate della metro sono le ultime ad essere specificate, ergo le ultime
                    // ad essere piazzate sulla mappa)
                    longit += 0.0002;
                }
            }

            GeoPosition coords = new GeoPosition(longit, latit);
            CustomWaypoint stop = new CustomWaypoint(lista[0], lista[2], coords);

            stops.add(stop);
        }

        provvisorio = leggiCSV("data/rome_static_gtfs/routes.txt");
        //questi pezzi di codice aggiornano la progressBar della LoadingScreen
        LoadingScreen.updateProgress(0, 5, LoadingScreen.progressBar);

        for (String[] lista : provvisorio)
        {
            Route aggiungi = new Route(lista[0], lista[2], Integer.parseInt(lista[4]));
            routes.add(aggiungi);
        }
        LoadingScreen.updateProgress(6, 25, LoadingScreen.progressBar);


        provvisorio = leggiCSV("data/rome_static_gtfs/trips.txt");
        LoadingScreen.updateProgress(26, 30, LoadingScreen.progressBar);

        for (String[] lista : provvisorio)
        {
            Trip aggiungi = new Trip(lista[2], lista[0], lista[7], lista[3]);
            trips.add(aggiungi);
        }
        LoadingScreen.updateProgress(31, 44, LoadingScreen.progressBar);

        provvisorio = leggiCSV("data/rome_static_gtfs/stop_times.txt");
        LoadingScreen.updateProgress(45, 55, LoadingScreen.progressBar);

        for (String[] lista : provvisorio)
        {
            StopTime aggiungi = new StopTime(lista[0], lista[3], lista[1]);
            stopTimes.add(aggiungi);
        }
        LoadingScreen.updateProgress(56, 75, LoadingScreen.progressBar);

        provvisorio = leggiCSV("data/rome_static_gtfs/shapes.txt");
        LoadingScreen.updateProgress(76, 80, LoadingScreen.progressBar);

        for (String[] lista : provvisorio)
        {
            double latitudine = Double.parseDouble(lista[1]);
            double longitudine = Double.parseDouble(lista[2]);
            int sequenza = Integer.parseInt(lista[3]);

            shapes.add(new PuntoShape(lista[0], latitudine, longitudine, sequenza));
        }
        LoadingScreen.updateProgress(81, 95, LoadingScreen.progressBar);

        provvisorio = leggiCSV("data/collegamenti_metro.txt");
        for (String[] lista : provvisorio)
        {
            CollegamentoMetro aggiungi = new CollegamentoMetro(lista[0], lista[2]);
            collegamentiMetro.add(aggiungi);

            boolean lineaTrovata = false;
            for (String linea : lineeMetro)
            {
                if (aggiungi.routeId().equals(linea))
                {
                    lineaTrovata = true;
                    break;
                }
            }

            if (!lineaTrovata) lineeMetro.add(aggiungi.routeId());
        }
        LoadingScreen.updateProgress(96, 100, LoadingScreen.progressBar);
    }

    //Tracciamento statico dei mezzi
    //Non so se questo metodo va bene: è molto rigido, mostra molti mezzi in circolazione (ignora
    //eventuali disservizi, corse cancellate, festività eccetera) e mostra i mezzi solo in fermata
    //ignorando il percorso tra le fermate. quindi probabilmente questo metodo è da rifare completamente
    public static ArrayList<GeoPosition> getPosizioneVeicolo(String routeId)
    {
        LocalTime adesso = LocalTime.now();

        List<Trip> viaggiTrovati = trips.stream()
                .filter(t -> t.routeId().equals(routeId))
                .toList();

        List<StopTime> orariFermate = stopTimes.stream()
                .filter(st -> st.getOrarioArrivo().truncatedTo(ChronoUnit.MINUTES).equals(adesso.truncatedTo(ChronoUnit.MINUTES)))
                .filter(st -> viaggiTrovati.stream().anyMatch(t -> t.id().equals(st.getTripId())))
                .toList();

        List<CustomWaypoint> fermate = stops.stream()
                .filter(f -> orariFermate.stream().anyMatch(st -> st.getStopId().equals(f.getId())))
                .toList();

        ArrayList<GeoPosition> coordinateMezzi = new ArrayList<>();

        for (CustomWaypoint f : fermate)
            coordinateMezzi.add(new GeoPosition(f.getLatitudine(), f.getLongitudine()));

        return coordinateMezzi;
    }

    public static String getTripUpdate(String stopId, String routeId)
    {
        List<Trip> viaggiTrovati = trips.stream()
                .filter(t -> t.routeId().equals(routeId))
                .toList();

        List<StopTime> orariFermate = stopTimes.stream()
                .filter(st -> st.getStopId().equals(stopId))
                .filter(st -> viaggiTrovati.stream().anyMatch(t -> t.id().equals(st.getTripId())))
                .toList();

        LocalTime adesso = LocalTime.now();

        Optional<StopTime> prossimoArrivo = orariFermate.stream()
                .filter(st -> parseTimeCorretto(st.getOrarioArrivo().toString()).isAfter(adesso))
                .min(Comparator.comparing(st -> parseTimeCorretto(st.getOrarioArrivo().toString())));

        return prossimoArrivo.map(stopTime -> stopTime.getOrarioArrivo().format(DateTimeFormatter.ofPattern("HH:mm"))).orElse("");
    }

    // Controlla se la linea passata come parametro è una linea della metropolitana.
    // La lista delle linee viene popolata in automatico in "inizializzaDati()"
    public static boolean lineaDellaMetro(String routeId)
    {
        for (String id : lineeMetro)
            if (id.equals(routeId))
                return true;

        return false;
    }

    // Questo metodo serve per le linee delle metropolitane: queste infatti hanno id come le altre
    // linee, ma è più comodo usare il loro nome commerciale (linea A, B...). Questo
    // controllo deve essere fatto "manualmente" perché nei file GTFS non è incluso il nome
    // commerciale delle linee (o comunque non per le metropolitane)
    public static String getNomeRealeMetro(String routeId)
    {
        return switch (routeId)
        {
            case "248" -> "Linea A";
            case "249" -> "Linea B (Laurentina-Rebibbia)";
            case "305" -> "Linea B1 (Laurentina-Jonio)";
            case "342" -> "Linea C";
            default -> routeId;
        };
    }

    public static ArrayList<String[]> leggiCSV(String path)
    {
        ArrayList<String[]> valori = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(path)))
        {
            reader.readNext();
            String[] linea; //Ignora la prima linea
            while ((linea = reader.readNext()) != null)
            {
                valori.add(linea);
            }
        }
        catch (Exception e)
        {
            System.out.println("DEBUG: Impossibile leggere il file indicato: " + path);
        }

        return valori;
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
            if (r.id().equals(routeId))
                return r;

        return null; //alquanto improbabile che venga restituito null
    }

    public static CustomWaypoint getFermata(String stopId)
    {
        for (CustomWaypoint f : stops)
            if (f.getId().equals(stopId))
                return f;

        return null;
    }

    public static String getIdFermata(String nomeFermata) {
        for (CustomWaypoint stop : stops) {
            if (stop.getNome().equals(nomeFermata)) {
                return stop.getId();
            }
        }
        return null;
    }

    public static List<GeoPosition> getPercorso(String routeId)
    {
        ArrayList<Trip> viaggi = trips.stream()
                .filter(trip -> trip.routeId().equals(routeId)).collect(Collectors.toCollection(ArrayList::new));

        if (viaggi.isEmpty())
        {
            return new ArrayList<>();
        }

        Trip viaggioSelezionato = viaggi.getFirst();

        return shapes.stream()
                .filter(sp -> sp.getId().equals(viaggioSelezionato.shapeId()))
                .sorted(Comparator.comparingInt(PuntoShape::getSequenza))
                .map(sp -> new GeoPosition(sp.getLatitudine(), sp.getLongitudine()))
                .toList();
    }
}
