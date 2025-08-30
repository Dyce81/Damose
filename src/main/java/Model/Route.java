package Model;

public class Route
{
    private final String id;
    private final String nome;
    private final int tipo;

    public Route(String id, String nome, int tipo)
    {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
    }

    public String getId() { return this.id; }
    public String getNome() { return this.nome; }
    public int getTipo() {
        return tipo;
    }

    @Override
    public String toString()
    {
        return nome;
    }
}
