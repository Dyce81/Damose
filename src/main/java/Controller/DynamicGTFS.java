package Controller;

import com.google.transit.realtime.GtfsRealtime.*;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DynamicGTFS
{
    private static String tripUpdateUrl = "https://romamobilita.it/sites/default/files/rome_rtgtfs_trip_updates_feed.pb";
    private static String vehicleUrl = "https://romamobilita.it/sites/default/files/rome_rtgtfs_vehicle_positions_feed.pb";

    /*public static ArrayList<String> getVehiclePosition()
    {
        try (InputStream input = new URL(vehicleUrl).openStream())
        {
            FeedMessage feed = FeedMessage.parseFrom(input);

            for (FeedEntity entita : feed.getEntityList())
            {
                if (!entita.hasVehicle()) continue;

                VehiclePosition posizione = entita.getVehicle();
                String tripId = posizione.getTrip().getTripId();
                String vehicleId = posizione.getVehicle().getId();
                double latitudine = posizione.getPosition().getLatitude();
                double longitudine = posizione.getPosition().getLongitude();
                long timestamp = posizione.getTimestamp();

                ArrayList<String> lista = new ArrayList<>();
                lista.add(posizione.toString());
                lista.add(tripId);
                lista.add(vehicleId);
                lista.add(Double.toString(latitudine));
                lista.add(Double.toString(longitudine));
                lista.add(Long.toString(timestamp));

                return lista;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }*/

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
            if (lista.isEmpty())
                return null;

            return lista;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static String getTripUpdate(String routeId, String stopId)
    {
        if (!Wifi.wifi_connesso()) return "";

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
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return "";
    }
}
