package Model;

public class StopTime
{
    private String tripId;
    private String stopId;
    private String orarioArrivo;
    private String orarioPartenza;
    private int stopSequenza;

    public StopTime(String tripId, String stopId, String orarioArrivo, String orarioPartenza)
    {
        this.tripId = tripId;
        this.stopId = stopId;
        this.orarioArrivo = orarioArrivo;
        this.orarioPartenza = orarioPartenza;
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

    public String getOrarioArrivo() {
        return orarioArrivo;
    }

    public void setOrarioArrivo(String orarioArrivo) {
        this.orarioArrivo = orarioArrivo;
    }

    public String getOrarioPartenza() {
        return orarioPartenza;
    }

    public void setOrarioPartenza(String orarioPartenza) {
        this.orarioPartenza = orarioPartenza;
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
