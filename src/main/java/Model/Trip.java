package Model;

public class Trip
{
    private String id;
    private String routeId;
    private String headsign;
    private String shapeId;
    private String serviceId;

    public Trip(String id, String routeId, String headsign, String shapeId)
    {
        this.id = id;
        this.routeId = routeId;
        this.headsign = headsign;
        this.shapeId = shapeId;
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

    public String getShapeId() {
        return shapeId;
    }

    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
    }

    public String getServiceId() { return serviceId; }

    public void setServiceId(String serviceId) { this.serviceId = serviceId; }

    @Override
    public String toString()
    {
        return this.id + " " + this.routeId + " " + headsign;
    }
}
