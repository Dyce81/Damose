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

    private static InformazioniFermata pannelloInformazioni; // E' l'observer in questo caso

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

        //TODO: commento inutile da cancellare più tardi perché ho paura che in realtà serva \/

        // qui praticamente si deve ridefinire da zero il corpo di un metodo già presente in "Frame"
        // (mostraInformazioni()); se possibile, vedere se ci si può riferire direttamente a quello
        // (forse rendendo il pannello dentro frame statico?)
        ////pannelloInformazioni.setNome(this.nome);
        //pannelloInformazioni.setTipoMezzo();

        pannelloInformazioni.impostaInfo(this);
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
        /*if (id.startsWith("ITO"))
        {
            //se è una metropolitana fai queste cose

            /*ArrayList<Route> lineeMetro = StaticGTFS.routes.stream()
                    .filter(route -> route.getTipo() == 1)
                    .collect(Collectors.toCollection(ArrayList::new));*/

            /*List<Route> lineeMetro = StaticGTFS.routes.stream()
                    .filter(route -> route.getTipo() == 1)
                    .toList();



            //System.out.println(lineeMetro);
            pannelloInformazioni.setLineeServite(lineeMetro);
            return;
        }*/

        ArrayList<Route> lineeTrovate = new ArrayList<>();

        Set<String> tripIds = new HashSet<>();
        for (StopTime st : StaticGTFS.stopTimes)
            if (st.getStopId().equals(this.id))
                tripIds.add(st.getTripId());

        Set<String> routeIds = new HashSet<>();
        for (Trip t : StaticGTFS.trips)
            if (tripIds.contains(t.getId()))
                routeIds.add(t.getRouteId());

        if (id.startsWith("ITO"))
        {
            //TODO: ogni fermata della metro imposta come linee servite tutte quelle della metro
            //(MA, MB, MB1, MC). bisogna restituire SOLO le linee che passano per la fermata voluta.
            for (Route r : StaticGTFS.routes)
                if (r.getTipo() == 1)
                    lineeTrovate.add(r);

            System.out.println(lineeTrovate);

            //TODO: attualmente c'è un file fatto da noi per verificare quali linee della metro
            //corrispondono ad una certa fermata;
            //nel file stoptimes.txt mancano gli orari di arrivo delle metropolitane, quindi non
            //è possibile mettere in correlazione le routes e le stops correttamente.
            //forse c'è un modo, ma meglio verificarlo alla fine

            //TODO: adesso non mi va di fare quel file quindi lo farò dopo

            /*List<Trip> viaggi = StaticGTFS.trips.stream()
                    .filter(t -> lineeTrovate.stream().anyMatch(l -> l.getId().equals(t.getRouteId())))
                    .toList();

            List<StopTime> orari = StaticGTFS.stopTimes.stream()
                    .filter(st -> st.getStopId().equals(id))
                    //.filter(st -> viaggi.stream().anyMatch(t -> t.getId().equals(st.getTripId())))
                    .toList();*/

            /*for (Route r : lineeTrovate)
            {

            }*/
            //System.out.println(orari);
        }
        else {
            for (Route r : StaticGTFS.routes)
                if (routeIds.contains(r.getId()))
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

