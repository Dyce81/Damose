package Controller;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.transit.realtime.GtfsRealtime.*;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.InputStream;
import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * La classe DynamicGTFS contiene tutti i metodi atti ad ottenere dati in tempo reale sul traffico
 * dei mezzi pubblici della città di Roma (dati specificati da RomaMobilità).
 */
public class DynamicGTFS
{
    private final static String tripUpdateUrl = "https://romamobilita.it/sites/default/files/rome_rtgtfs_trip_updates_feed.pb";
    private final static String vehicleUrl = "https://romamobilita.it/sites/default/files/rome_rtgtfs_vehicle_positions_feed.pb";
    private final static String serviceAlertsUrl = "https://romamobilita.it/sites/default/files/rome_rtgtfs_service_alerts_feed.pb";

    private static TripUpdate ultimoTripUpdate;
    private static TripDescriptor ultimoTripDescriptor;

    /**
     * Ottiene le coordinate di ogni singolo mezzo presente in questo momento sulla linea
     * specifica tramite routeId, le mette in un arraylist e lo restituisce
     * alla funzione chiamante, che si occuperà poi di disegnare i mezzi.
     * @param routeId codice identificativo della linea.
     * @return lista di coordinate di ciascun mezzo tracciato.
     */
    public static ArrayList<GeoPosition> getVehiclePosition(String routeId)
    {
        try (InputStream input = new URI(vehicleUrl).toURL().openStream())
        {
            FeedMessage feed = FeedMessage.parseFrom(input);
            ArrayList<GeoPosition> lista = new ArrayList<>();

            for (FeedEntity entita : feed.getEntityList())
            {
                if (!entita.hasVehicle()) continue;

                String idTrovato = entita.getVehicle().getTrip().getRouteId();
                if (!idTrovato.equals(routeId)) continue;

                VehiclePosition posizione = entita.getVehicle();
                double latitudine = posizione.getPosition().getLatitude();
                double longitudine = posizione.getPosition().getLongitude();

                lista.add(new GeoPosition(latitudine, longitudine));
            }
            return lista;
        }
        catch (Exception e)
        {
            System.out.println("DEBUG: Errore nell'ottenimento delle informazioni sui veicoli.");
        }

        return new ArrayList<>();
    }

    /**
     * Ottiene un TripUpdate della linea specificata e della fermata, per poi calcolare
     * il tempo di arrivo del mezzo più vicino alla fermata passata come parametro.
     * @param routeId codice identificativo della linea.
     * @param stopId codice identificativo della fermata.
     * @return stringa rappresentante l'orario di arrivo del prossimo mezzo.
     */
    public static String getTripUpdate(String routeId, String stopId)
    {
        if (!WiFi.connesso()) return "";

        try (InputStream input = new URI(tripUpdateUrl).toURL().openStream()) {
            FeedMessage feed = FeedMessage.parseFrom(input);

            long adesso = Instant.now().getEpochSecond();
            long prossimoArrivo = Long.MAX_VALUE;
            String tripIdCercato = null;

            for (FeedEntity entita : feed.getEntityList())
            {
                if (!entita.hasTripUpdate()) continue;

                TripUpdate aggiornamento = entita.getTripUpdate();
                TripDescriptor viaggio = aggiornamento.getTrip();

                if (!viaggio.getRouteId().equals(routeId)) continue;

                for (TripUpdate.StopTimeUpdate stu : aggiornamento.getStopTimeUpdateList())
                {
                    if (!stu.getStopId().equals(stopId)) continue;
                    if (!stu.hasArrival() || !stu.getArrival().hasTime()) continue;

                    long tempoArrivo = stu.getArrival().getTime();

                    if (tempoArrivo > adesso && tempoArrivo < prossimoArrivo)
                    {
                        prossimoArrivo = tempoArrivo;
                        tripIdCercato = viaggio.getTripId();
                        ultimoTripDescriptor = viaggio;
                        ultimoTripUpdate = aggiornamento;
                        break;
                    }
                }
            }

            if (tripIdCercato != null)
            {
                return Instant.ofEpochSecond(prossimoArrivo)
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("HH:mm"));
            }
            else
                return "";
        }
        catch (InvalidProtocolBufferException e)
        {
            System.out.println("DEBUG: Il messaggio del protocollo non è valido.");
        }
        catch (Exception e)
        {
            System.out.println("DEBUG: Errore nella ricezione del messaggio (Dati GTFS dinamici)");
        }

        return "";
    }

    /**
     * Controlla se c'è qualche avviso diffuso da Roma Mobilità per quanto riguarda la linea
     * selezionata.
     * @param routeId codice identificativo della linea selezionata.
     * @return una stringa con l'eventuale avviso diffuso da Roma Mobilità.
     */
    public static String getServiceAlert(String routeId)
    {
        if (!WiFi.connesso()) return "";

        try (InputStream input = new URI(serviceAlertsUrl).toURL().openStream())
        {
            FeedMessage feed = FeedMessage.parseFrom(input);

            for (FeedEntity entita : feed.getEntityList())
            {
                Alert allerta = entita.getAlert();
                for (EntitySelector entitySelector : entita.getAlert().getInformedEntityList())
                {
                    if (entitySelector.getRouteId().equals(routeId))
                    {
                        long adesso = Instant.now().getEpochSecond();
                        long tempoInizio = allerta.getActivePeriod(0).getStart();
                        long tempoFine = allerta.getActivePeriod(0).getEnd();

                        String orarioValidita = "Valido tutto il giorno.";

                        if (tempoInizio < adesso && adesso < tempoFine)
                        {
                            String stringaInizioTempo = Instant.ofEpochSecond(tempoInizio)
                                    .atZone(ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("HH:mm"));

                            String stringaFineTempo = Instant.ofEpochSecond(tempoFine)
                                    .atZone(ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("HH:mm"));

                            orarioValidita = "Valido dalle " + stringaInizioTempo + " alle " + stringaFineTempo;
                        }

                        String causa = "<i>Messaggio da RomaMobilità:</i><br>" +
                                allerta.getDescriptionText().getTranslation(0).getText();

                        return "<html><u>Problema sulla linea</u><br>" + causa + "<br><i>" + orarioValidita + "</i></html>";
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("DEBUG: Problema con URL dei serviceAlerts");
        }

        return "";
    }

    /**
     * Restituisce l'ultimo TripUpdate calcolato (dinamicamente tramite getTripUpdate).
     * @return l'ultimo trip update calcolato.
     */
    public static TripUpdate getUltimoTripUpdate() { return ultimoTripUpdate; }

    /**
     * Restituisce l'ultimo TripDescriptor calcolato (dinamicamente tramite getTripUpdate).
     * @return l'ultimo trip descriptor calcolato.
     */
    public static TripDescriptor getUltimoTripDescriptor() { return ultimoTripDescriptor; }

    /**
     * Calcola il ritardo totale sulla corsa (passata nel TripUpdate) e lo restituisce.
     * @param trip il TripUpdate di cui si vuole conoscere il ritardo.
     * @return ritardo sulla corsa interessata (in secondi).
     */
    public static int getRitardo(TripUpdate trip)
    {
        if (!WiFi.connesso() || !trip.isInitialized()) return 0;

        // Ottiene il ritardo della corsa tramite trip.getDelay() e lo converte in secondi
        return (trip.getDelay() % 3600) / 60;
    }

    /**
     * Ottiene lo stato della corsa passata come parametro (tramite TripDescriptor).
     * @param trip il TripDescriptor del viaggio di cui si vuole conoscere lo stato.
     * @return una stringa che specifica se la corsa è in orario, cancellata, ecc.
     */
    public static String getStato(TripDescriptor trip)
    {
        if (!WiFi.connesso() || !trip.isInitialized()) return "";

        String stato = trip.getScheduleRelationship().toString();

        return switch (stato)
        {
            case "SCHEDULED" -> "IN ORARIO";
            case "CANCELED" -> "CANCELLATA";
            case "DUPLICATED" -> "DUPLICATA";
            case "NEW" -> "AGGIUNTIVA";
            default -> "PROGRAMMATA"; //forse?
        };
    }
}
