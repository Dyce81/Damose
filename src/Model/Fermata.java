package Model;

public class Fermata
{
    //Per adesso sono inclusi solo 5 campi in quanto nel file stops.txt vengono utilizzati
    //solo questi campi
    private String id_fermata = "0";
    private String codice_fermata = "0";
    private String nome_fermata = "";
    private String descrizione = "";
    private double latitudine = 0.0;
    private double longitudine = 0.0;
    private String url_fermata = "";
    private String wheelchair = "";
    private String timezone = "";
    private String loc_type = "";
    private String stazione_padre = "";

    //Costruttore
    public Fermata(String id, String codice, String desc, String nome, double latit, double longit, String url, String wc, String tz, String loc, String ps)
    {
        this.id_fermata = id;
        this.codice_fermata = codice;
        this.nome_fermata = nome;
        this.descrizione = desc;
        this.latitudine = latit;
        this.longitudine = longit;
        this.url_fermata = url;
        this.wheelchair = wc;
        this.timezone = tz;
        this.loc_type = loc;
        this.stazione_padre = ps;
    }

    public String get_nome() { return this.nome_fermata; }
    public double get_latitudine() { return this.latitudine; }
    public double get_longitudine() { return this.longitudine; }
}
