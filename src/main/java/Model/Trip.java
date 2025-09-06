package Model;

public record Trip(String id, String routeId, String shapeId, String headsign) {

    @Override
    public String toString() {
        return this.id + " " + this.routeId;
    }
}
