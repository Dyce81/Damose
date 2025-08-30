package Model;

import Controller.StaticGTFS;

import java.time.LocalTime;

public class StopTime
{
    private final String tripId;
    private final String stopId;
    private final LocalTime orarioArrivo;
    private final LocalTime orarioPartenza;

    public StopTime(String tripId, String stopId, String orarioArrivo, String orarioPartenza)
    {
        this.tripId = tripId;
        this.stopId = stopId;
        this.orarioArrivo = StaticGTFS.parseTimeCorretto(orarioArrivo);
        this.orarioPartenza = StaticGTFS.parseTimeCorretto(orarioPartenza);
    }

    public String getTripId() {
        return tripId;
    }

    public String getStopId() {
        return stopId;
    }

    public LocalTime getOrarioArrivo() {
        return orarioArrivo;
    }

    public LocalTime getOrarioPartenza() {
        return orarioPartenza;
    }

    @Override
    public String toString()
    {
        return this.tripId + " " + this.stopId + " " + this.orarioPartenza + " " + this.orarioArrivo;
    }
}
