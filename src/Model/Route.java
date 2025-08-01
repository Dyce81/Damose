package Model;

public class Route
{
    private String id;
    private String nome;
    private int tipo;
    private String url;

    public Route(String id, String nome, int tipo, String url)
    {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.url = url;
    }

    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return this.nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) { this.tipo = tipo; }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) { this.url = url; }

    @Override
    public String toString()
    {
        return this.id + " " + this.nome + " " + this.tipo + " " + this.url;
    }
}
