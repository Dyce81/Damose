package Model;

public class PuntoShape
{
    private final String id;
    private final double latitudine;
    private final double longitudine;
    private final int sequenza;

    public PuntoShape(String id, double latitudine, double longitudine, int sequenza) {
        this.id = id;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.sequenza = sequenza;
    }

    public String getId() {
        return id;
    }

    public double getLatitudine() {
        return latitudine;
    }

    public double getLongitudine() {
        return longitudine;
    }

    public int getSequenza() {
        return sequenza;
    }
}
