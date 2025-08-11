package View;

import Controller.DynamicGTFS;
import Controller.ReaderStaticGTFS;
import Controller.Wifi;
import Model.*;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class InformazioniFermata
{
    private final JPanel pannello;
    private final JLabel indicazioneFermata;
    private final JLabel nome;
    private final JLabel tipoMezzo;
    private final JLabel lineeServite;
    private final JPanel pulsantiLinee;
    private final JPanel infoLinea;
    private final JButton mostraMezzi;

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> task;

    private boolean tracciamentoAttivo = false;

    private static final Color rossoScuro = new Color(143, 51, 51);
    private static final Color rosso = new Color(175, 62, 62);

    public InformazioniFermata()
    {
        pannello = new JPanel();
        pannello.setBackground(rossoScuro);
        //pannello.setPreferredSize(new Dimension(200, pannello.getPreferredSize().height)); //forse? (non proprio responsive)

        indicazioneFermata = new JLabel("Fermata selezionata:");
        nome = new JLabel("Seleziona una fermata.");
        tipoMezzo = new JLabel("");
        lineeServite = new JLabel("");
        lineeServite.setBackground(rossoScuro);

        pulsantiLinee = new JPanel();
        pulsantiLinee.setBackground(rossoScuro);
        pulsantiLinee.setLayout(new BoxLayout(pulsantiLinee, BoxLayout.Y_AXIS));

        infoLinea = new JPanel();
        infoLinea.setLayout(new BoxLayout(infoLinea, BoxLayout.Y_AXIS));
        infoLinea.setBackground(rosso);

        mostraMezzi = new JButton("  Mostra mezzi sulla linea  ");
        mostraMezzi.addActionListener(e -> tracciaMezzi());

        pannello.setLayout(new BoxLayout(pannello, BoxLayout.Y_AXIS));
        pannello.add(indicazioneFermata);
        pannello.add(nome);
        pannello.add(tipoMezzo);
        pannello.add(lineeServite);
        pannello.add(pulsantiLinee);
        pannello.add(infoLinea);
    }

    public JPanel getPannello()
    {
        return this.pannello;
    }

    public void impostaInfo(CustomWaypoint fermata)
    {
        nome.setText(fermata.getNome());

        if (fermata.getId().startsWith("ITO"))
            tipoMezzo.setText("Tipo mezzo: Metropolitana");
        else
            tipoMezzo.setText("Tipo mezzo: Autobus");
    }

    public void setLineeServite(ArrayList<Route> linee)
    {
        pulsantiLinee.removeAll();

        for (Route r : linee) {
            //infoLinee.append("- ").append(r.getId()).append(", ").append(r.getUrl()).append('\n');
            //infoLinee.append("- ").append(r.getId()).append('\n');

            JButton pulsanteLinea = new JButton(r.getId());
            pulsanteLinea.setBorderPainted(false);
            pulsanteLinea.setBackground(rossoScuro);
            pulsanteLinea.setMaximumSize(new Dimension(Integer.MAX_VALUE, pulsanteLinea.getPreferredSize().height));
            //pulsanteLinea.setAlignmentX(Component.CENTER_ALIGNMENT);

            pulsanteLinea.addActionListener(e -> mostraInfoLinea(pulsanteLinea.getText()));
            pulsantiLinee.add(pulsanteLinea);
        }

        lineeServite.setText("Linee servite:");
        pannello.revalidate();
        pannello.repaint();
    }

    public void mostraInfoLinea(String id)
    {
        infoLinea.removeAll();
        CustomWaypoint fermata = ElaboratoreFermate.ultimaFermata;

        JLabel testoLinea = new JLabel("Linea selezionata: " + id);
        JLabel prossimoArrivo = new JLabel("Prossimo arrivo: ");

        infoLinea.add(testoLinea);
        infoLinea.add(prossimoArrivo);

        pannello.scrollRectToVisible(new Rectangle(infoLinea.getBounds()));
        pannello.add(mostraMezzi);

        if (task != null) task.cancel(true);

        if (Wifi.WiFi)
        {
            //DynamicGTFS.getVehiclePosition();

            task = scheduler.scheduleAtFixedRate(() ->
            {
                if (!Wifi.WiFi) //non so se funziona
                {
                    /*task.cancel(true);*/
                    /*mostraInfoLinea(id);*/
                    // TODO: chiamare un metodo che calcola staticamente il prossimo arrivo
                    // (e se è attivo, anche il tracciamento mezzi (statico) (prova a indovinare
                    // in quale fermata si trova il mezzo))
                }

                String tempo = DynamicGTFS.getTripUpdate(id, fermata.getId());
                if (tempo.isEmpty()) tempo = "(Nessun orario previsto)";
                // TODO: qui sopra magari predirlo staticamente - avvisando l'utente

                if (tracciamentoAttivo)
                {
                    System.out.println(id);
                    ArrayList<GeoPosition> lista = DynamicGTFS.getVehiclePosition(id);
                    //Mappa.getMapViewer().zoomToBestFit(new HashSet<>(), 0.7);
                    CustomWaypointPainter.setPosizioniMezzi(lista);
                    /*for (GeoPosition coords : lista)
                    {
                        System.out.println(coords);
                        //Mappa.aggiungiMezzo(coords);
                    }*/

                    CustomWaypointPainter.setTracciamentoAttivo(true);

                    List<GeoPosition> percorso = ReaderStaticGTFS.getPercorso(id);
                    Mappa.disegnaLinea(percorso);

                    Mappa.getMapViewer().repaint();
                    //System.out.println("aaa");
                }

                prossimoArrivo.setText("Prossimo arrivo previsto: " + tempo);
            }, 0, 5, TimeUnit.SECONDS);
        }
        else {

            // TODO: questa parte di codice qui sotto deve essere messa in un metodo
            // per calcolare i dati statici... - stesso metodo usato nell'if qui sopra
            List<Trip> viaggi = ReaderStaticGTFS.trips.stream()
                    .filter(trip -> trip.getRouteId().equals(id))
                    //.filter(trip -> trip.isServiceActiveToday
                    .toList();

            List<String> viaggiValidi = viaggi.stream()
                    .map(Trip::getId).toList();

            LocalTime adesso = LocalTime.now();

            Optional<StopTime> prossimoStopTime = ReaderStaticGTFS.stopTimes.stream()
                    .filter(st -> viaggiValidi.contains(st.getTripId()))
                    .filter(st -> st.getStopId().equals(fermata.getId()))
                    .filter(st -> st.getOrarioArrivo().isAfter(adesso))
                    .min(Comparator.comparing(StopTime::getOrarioArrivo));

            if (prossimoStopTime.isPresent()) {
                StopTime st = prossimoStopTime.get();
                System.out.print("OFFLINE!!!: ");
                System.out.println(st);
            }
        }
    }

    public void tracciaMezzi()
    {
        if (tracciamentoAttivo)
        {
            tracciamentoAttivo = false;
            mostraMezzi.setText("  Mostra mezzi sulla linea  ");
        }
        else
        {
            tracciamentoAttivo = true;
            mostraMezzi.setText("Nascondi mezzi sulla linea");

        }
    }

    public void resetPannello()
    {
        if (task != null) task.cancel(true);
        nome.setText("Seleziona una fermata.");
        tipoMezzo.setText("");
        lineeServite.setText("");
        pulsantiLinee.removeAll();
        pulsantiLinee.repaint();
        infoLinea.removeAll();
        infoLinea.repaint();
        pannello.remove(mostraMezzi);
        //System.out.println("reset pannello");
    }
}
