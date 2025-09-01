package Model;

public class Trip
{
    private final String id;
    private final String routeId;
    private final String shapeId;
    private final String headsign;

    public Trip(String id, String routeId, String shapeId, String headsign)
    {
        this.id = id;
        this.routeId = routeId;
        this.shapeId = shapeId;
        this.headsign = headsign;
    }

    public String getId() {
        return id;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getShapeId() {
        return shapeId;
    }

    public String getHeadsign()
    {
        return headsign;
    }

    @Override
    public String toString()
    {
        return this.id + " " + this.routeId;
    }
}
