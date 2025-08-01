package Model;

public class Trip
{
    private String id;
    private String routeId;
    private String headsign;

    public Trip(String id, String routeId, String headsign)
    {
        this.id = id;
        this.routeId = routeId;
        this.headsign = headsign;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getHeadsign() {
        return headsign;
    }

    public void setHeadsign(String headsign) {
        this.headsign = headsign;
    }

    @Override
    public String toString()
    {
        return this.id + " " + this.routeId + " " + headsign;
    }
}
