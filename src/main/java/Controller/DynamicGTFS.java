package Controller;

import View.Frame;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.transit.realtime.GtfsRealtime.*;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DynamicGTFS
{
    private final static String tripUpdateUrl = "https://romamobilita.it/sites/default/files/rome_rtgtfs_trip_updates_feed.pb";
    private final static String vehicleUrl = "https://romamobilita.it/sites/default/files/rome_rtgtfs_vehicle_positions_feed.pb";
    private final static String serviceAlertsUrl = "https://romamobilita.it/sites/default/files/rome_rtgtfs_service_alerts_feed.pb";

    private static TripUpdate ultimoTripUpdate;
    private static TripDescriptor ultimoTripDescriptor;

    //Ottiene le coordinate dei mezzi in movimento, le mette in un arraylist e lo restituisce
    //alla funzione chiamante, che si occuperà poi di disegnare i mezzi
    public static ArrayList<GeoPosition> getVehiclePosition(String routeId)
    {
        try (InputStream input = new URL(vehicleUrl).openStream())
        {
            FeedMessage feed = FeedMessage.parseFrom(input);
            ArrayList<GeoPosition> lista = new ArrayList<>();

            for (FeedEntity entita : feed.getEntityList())
            {
                if (!entita.hasVehicle()) continue;

                String idTrovato = entita.getVehicle().getTrip().getRouteId();
                if (!idTrovato.equals(routeId)) continue;

                VehiclePosition posizione = entita.getVehicle();
                //String tripId = posizione.getTrip().getTripId();
                //String vehicleId = posizione.getVehicle().getId();
                double latitudine = posizione.getPosition().getLatitude();
                double longitudine = posizione.getPosition().getLongitude();
                //long timestamp = posizione.getTimestamp();*/

                lista.add(new GeoPosition(latitudine, longitudine));
                /*lista.add(posizione.toString());
                lista.add(tripId);
                lista.add(vehicleId);
                lista.add(Double.toString(latitudine));
                lista.add(Double.toString(longitudine));
                lista.add(Long.toString(timestamp));*/
            }
            return lista;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public static String getTripUpdate(String routeId, String stopId)
    {
        if (!WiFi.wifi_connesso()) return "";

        try (InputStream input = new URL(tripUpdateUrl).openStream()) {
            FeedMessage feed = FeedMessage.parseFrom(input);

            long adesso = Instant.now().getEpochSecond();
            long prossimoArrivo = Long.MAX_VALUE;
            String tripIdCercato = null;

            for (FeedEntity entita : feed.getEntityList()) {
                if (!entita.hasTripUpdate()) continue;

                TripUpdate aggiornamento = entita.getTripUpdate();
                TripDescriptor viaggio = aggiornamento.getTrip();

                if (!viaggio.getRouteId().equals(routeId)) continue;

                for (TripUpdate.StopTimeUpdate stu : aggiornamento.getStopTimeUpdateList()) {
                    if (!stu.getStopId().equals(stopId)) continue;
                    if (!stu.hasArrival() || !stu.getArrival().hasTime()) continue;

                    long tempoArrivo = stu.getArrival().getTime();

                    if (tempoArrivo > adesso && tempoArrivo < prossimoArrivo) {
                        prossimoArrivo = tempoArrivo;
                        tripIdCercato = viaggio.getTripId();
                        ultimoTripDescriptor = viaggio;
                        ultimoTripUpdate = aggiornamento;
                        break; //TODO: non so se questa cosa va bene, se rompe in qualche modo il codice, se cambia qualcosa...? pare di no?
                    }
                }
            }

            if (tripIdCercato != null)
            {
                String tempo = Instant.ofEpochSecond(prossimoArrivo)
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("HH:mm"));

                return tempo;
            } else return "";
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

    // Controlla se c'è qualche avviso relativo alla linea selezionata (passata come parametro qui)
    public static String getServiceAlert(String routeId)
    {
        if (!WiFi.wifi_connesso()) return "";

        try (InputStream input = new URL(serviceAlertsUrl).openStream())
        {
            FeedMessage feed = FeedMessage.parseFrom(input);

            for (FeedEntity entita : feed.getEntityList())
            {
                Alert allerta = entita.getAlert();
                for (EntitySelector entitySelector : entita.getAlert().getInformedEntityList())
                {
                    if (entitySelector.getRouteId().equals(routeId))
                    {
                        //System.out.println(entita);
                        //TODO: cambiare i nomi dei campi qui sotto

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

                        String causa = "<html><i>Causa indicata:</i><br>" +
                                allerta.getDescriptionText().getTranslation(0).getText() +
                                "</html>";

                        return "<html>Problema sulla linea:<br><br>" + causa + "<br><br><i>" + orarioValidita + "</i></html>";
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return "";
    }

    public static TripUpdate getUltimoTripUpdate() { return ultimoTripUpdate; }

    public static TripDescriptor getUltimoTripDescriptor() { return ultimoTripDescriptor; }

    public static int getRitardo(TripUpdate trip)
    {
        if (!WiFi.wifi_connesso() || !trip.isInitialized()) return 0;

        // Ottiene il ritardo della corsa tramite trip.getDelay() e lo converte in secondi
        return (trip.getDelay() % 3600) / 60;
    }

    public static String getStato(TripDescriptor trip)
    {
        if (!WiFi.wifi_connesso() || !trip.isInitialized()) return "";

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
