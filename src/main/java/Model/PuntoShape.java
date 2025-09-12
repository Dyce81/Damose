package Model;

/**
 * Questa classe crea dei semplici punti da piazzare sulla mappa per disegnare correttamente i
 * percorsi. I dati dei punti vengono presi dal file shapes.txt
 */
public class PuntoShape
{
    private final String id;
    private final double latitudine;
    private final double longitudine;
    private final int sequenza;

    /**
     * Costruttore.
     */
    public PuntoShape(String id, double latitudine, double longitudine, int sequenza)
    {
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
