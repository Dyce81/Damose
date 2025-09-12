package Model;

/**
 * Questo record rappresenta l'oggetto Route.
 */
public record Route(String id, String nome, int tipo)
{
    @Override
    public String toString() {
        return nome;
    }
}
