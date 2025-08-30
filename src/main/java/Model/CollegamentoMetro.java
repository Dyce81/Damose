package Model;

// Questa classe è necessaria per capire quali linee della metropolitana passano per una fermata della
// metro.
// In stop_times.txt mancano gli orari delle fermate della metropolitana, quindi non è possibile mettere
// in relazione trips con routes e routes con stops. Questa classe mantiene le informazioni di un file
// creato da noi per vedere a quale fermata corrispondono quali linee della metro.

//TODO: (forse) convertire questa classe in un record? --- un sacco di altre classi potrebbero
// diventare dei record
public class CollegamentoMetro
{
    private final String stopId;
    private final String routeId;

    public CollegamentoMetro(String stopId, String routeId)
    {
        this.stopId = stopId;
        this.routeId = routeId;
    }

    public String getStopId()
    {
        return this.stopId;
    }

    public String getRouteId()
    {
        return this.routeId;
    }

    @Override
    public String toString()
    {
        return this.stopId + " " + this.routeId;
    }
}
