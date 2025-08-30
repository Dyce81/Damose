package Model;

public class Trip
{
    private final String id;
    private final String routeId;
    private final String shapeId;

    public Trip(String id, String routeId, String shapeId)
    {
        this.id = id;
        this.routeId = routeId;
        this.shapeId = shapeId;
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

    @Override
    public String toString()
    {
        return this.id + " " + this.routeId;
    }
}
