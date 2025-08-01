package Model;

import Controller.ReaderStaticGTFS;
import View.InformazioniFermata;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CustomWaypoint extends DefaultWaypoint
{
    //private final JButton icona;
    public boolean selezionato = false;

    private final String id;
    private final String nome;
    private final double longitudine;
    private final double latitudine;
    private Image icona;

    private static InformazioniFermata pannelloInformazioni; // E' l'observer in questo caso

    public CustomWaypoint(String id, String nome, GeoPosition coords)
    {
        super(coords);
        this.id = id;
        this.nome = nome;
        this.longitudine = coords.getLongitude();
        this.latitudine = coords.getLatitude();
        ImageIcon img_icon = new ImageIcon("assets/bus-solid.png");
        this.icona = img_icon.getImage();
    }

    public String getNome()
    {
        return nome;
    }

    public double getLongitudine()
    {
        return longitudine;
    }

    public double getLatitudine()
    {
        return latitudine;
    }

    public Image getIcona()
    {
        return icona;
    }

    public static void setPannello(InformazioniFermata pannello)
    {
        pannelloInformazioni = pannello;
    }

    public void seleziona()
    {
        if (selezionato) return; //superfluo?

        selezionato = true;
        ImageIcon img_icon = new ImageIcon("assets/bus-solid_selezionato.png");
        icona = img_icon.getImage();
        trovaLinee();

        // qui praticamente si deve ridefinire da zero il corpo di un metodo già presente in "Frame"
        // (mostraInformazioni()); se possibile, vedere se ci si può riferire direttamente a quello
        // (forse rendendo il pannello dentro frame statico?)
        pannelloInformazioni.setNome(this.nome);
    }

    public void deseleziona()
    {
        if (!selezionato) return;

        selezionato = false;
        ImageIcon img_icon = new ImageIcon("assets/bus-solid.png");
        icona = img_icon.getImage();
    }

    //Questo metodo trova le linee che passano per questa fermata
    //per adesso restituisce un array di routes che passano per quella fermata
    public ArrayList<Route> trovaLinee()
    {
        ArrayList<Route> lineeTrovate = new ArrayList<>();

        Set<String> tripIds = new HashSet<>();
        for (StopTime st : ReaderStaticGTFS.stopTimes)
            if (st.getStopId().equals(this.id))
                tripIds.add(st.getTripId());

        Set<String> routeIds = new HashSet<>();
        for (Trip t : ReaderStaticGTFS.trips)
            if (tripIds.contains(t.getId()))
                routeIds.add(t.getRouteId());

        for (Route r : ReaderStaticGTFS.routes)
            if (routeIds.contains(r.getId()))
                lineeTrovate.add(r);
                //System.out.println("- " + r.getNome() + " (" + r.getUrl() +")");

        pannelloInformazioni.setLineeServite(lineeTrovate);
        return lineeTrovate;
    }
}
