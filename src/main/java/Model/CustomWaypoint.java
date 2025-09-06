package Model;

import Controller.StaticGTFS;
import View.InformazioniFermata;
import View.Mappa;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomWaypoint extends DefaultWaypoint
{
    public boolean selezionato = false;

    private final String id;
    private final String nome;
    private final double longitudine;
    private final double latitudine;
    private Image icona;

    private static final Image iconaAutobus = new ImageIcon("assets/bus-solid_grande.png").getImage().getScaledInstance(17, 17, Image.SCALE_SMOOTH);
    private static final Image iconaMetro = new ImageIcon("assets/metro_icona.png").getImage().getScaledInstance(17, 17, Image.SCALE_SMOOTH);
    private static final Image iconaSelezionata = new ImageIcon("assets/fermata-selezionata.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);

    private static InformazioniFermata pannelloInformazioni;

    public CustomWaypoint(String id, String nome, GeoPosition coords)
    {
        super(coords);
        this.id = id;
        this.nome = nome;
        this.longitudine = coords.getLongitude();
        this.latitudine = coords.getLatitude();

        if (id.startsWith("ITO")) {
            icona = iconaMetro;
        } else {
            icona = iconaAutobus;
        }
    }

    public String getId()
    {
        return id;
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
        icona = iconaSelezionata;
        trovaLinee();

        pannelloInformazioni.impostaNome(this);
        Mappa.getMapViewer().repaint();
    }

    public void deseleziona()
    {
        if (!selezionato) return;

        selezionato = false;

        if (id.startsWith("ITO")) {
            icona = iconaMetro;
        } else {
            icona = iconaAutobus;
        }

        pannelloInformazioni.resetPannello();
        Mappa.getMapViewer().repaint();
    }

    //Questo metodo trova le linee che passano per questa fermata
    //per adesso restituisce un array di routes che passano per quella fermata
    public void trovaLinee()
    {
        ArrayList<Route> lineeTrovate = new ArrayList<>();
        //TODO: (forse) cambiare questo in List<Route> ? (andrebbe cambiato anche nel pannello informazioni)

        if (id.startsWith("ITO"))
        {
            List<CollegamentoMetro> collegamentiTrovati = StaticGTFS.collegamentiMetro.stream()
                    .filter(c -> c.stopId().equals(id))
                    .toList();

            for (Route r : StaticGTFS.routes)
                if (r.tipo() == 1)
                    for (CollegamentoMetro c : collegamentiTrovati)
                        if (c.routeId().equals(r.id()))
                            lineeTrovate.add(r);

            //TODO: attualmente c'è un file fatto da noi per verificare quali linee della metro
            // corrispondono ad una certa fermata;
            // nel file stoptimes.txt mancano gli orari di arrivo delle metropolitane, quindi non
            // è possibile mettere in correlazione le routes e le stops correttamente.
            // forse c'è un modo, ma meglio verificarlo alla fine
        }
        else {
            Set<String> tripIds = new HashSet<>();
            for (StopTime st : StaticGTFS.stopTimes)
                if (st.getStopId().equals(this.id))
                    tripIds.add(st.getTripId());

            Set<String> routeIds = new HashSet<>();
            for (Trip t : StaticGTFS.trips)
                if (tripIds.contains(t.id()))
                    routeIds.add(t.routeId());

            for (Route r : StaticGTFS.routes)
                if (routeIds.contains(r.id()))
                    lineeTrovate.add(r);
        }

        pannelloInformazioni.setLineeServite(lineeTrovate);
        //return lineeTrovate;
    }

    @Override
    public String toString()
    {
        return nome;
    }
}