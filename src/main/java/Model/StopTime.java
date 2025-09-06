package Model;

import Controller.StaticGTFS;

import java.time.LocalTime;

public class StopTime
{
    private final String tripId;
    private final String stopId;
    private final LocalTime orarioArrivo;

    public StopTime(String tripId, String stopId, String orarioArrivo)
    {
        this.tripId = tripId;
        this.stopId = stopId;
        this.orarioArrivo = StaticGTFS.parseTimeCorretto(orarioArrivo);
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
}
