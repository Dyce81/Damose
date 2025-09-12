package Model;

import Controller.StaticGTFS;

import java.time.LocalTime;

/**
 * Questa classe rappresenta l'oggetto StopTime e i suoi campi.
 */
public class StopTime
{
    private final String tripId;
    private final String stopId;
    private final LocalTime orarioArrivo;


    /**
     * Costruttore.
     */
    public StopTime(String tripId, String stopId, String orarioArrivo)
    {
        this.tripId = tripId;
        this.stopId = stopId;
        this.orarioArrivo = StaticGTFS.parseTimeCorretto(orarioArrivo);
    }

    /**
     * Ritorna l'Id della corsa.
     */
    public String getTripId() {
        return tripId;
    }

    /**
     * Ritorna l'Id della fermata.
     */
    public String getStopId() {
        return stopId;
    }

    /**
     * Ritorna l'orario di arrivo.
     */
    public LocalTime getOrarioArrivo() {
        return orarioArrivo;
    }
}
