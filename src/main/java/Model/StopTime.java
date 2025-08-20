package Model;

import Controller.StaticGTFS;

import java.time.LocalTime;

public class StopTime
{
    private String tripId;
    private String stopId;
    private LocalTime orarioArrivo;
    private LocalTime orarioPartenza;
    private int stopSequenza;

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

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public LocalTime getOrarioArrivo() {
        return orarioArrivo;
    }

    public void setOrarioArrivo(String orarioArrivo) {
        this.orarioArrivo = StaticGTFS.parseTimeCorretto(orarioArrivo);
    }

    public LocalTime getOrarioPartenza() {
        return orarioPartenza;
    }

    public void setOrarioPartenza(String orarioPartenza) {
        this.orarioPartenza = StaticGTFS.parseTimeCorretto(orarioPartenza);
    }

    public int getStopSequenza()
    {
        return stopSequenza;
    }

    @Override
    public String toString()
    {
        return this.tripId + " " + this.stopId + " " + this.orarioPartenza + " " + this.orarioArrivo;
    }
}
