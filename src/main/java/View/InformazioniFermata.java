package View;

import Controller.DynamicGTFS;
import Controller.StaticGTFS;
import Controller.WiFi;
import Model.*;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class InformazioniFermata
{
    private final Frame padre;

    private final JPanel pannello;
    private final JLabel indicazioneFermata;
    private final JLabel nome;
    //private final JLabel tipoMezzo;
    private final JLabel lineeServite;
    private final JPanel pulsantiLinee;
    private final JPanel infoLinea;
    private final JLabel avvisoPrevisione;
    private final JButton mostraMezzi;
    private final JLabel avvisoTracciamento;
    private final JLabel statoCorsa;
    private final JLabel ritardoCorsa;

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> task;

    private boolean tracciamentoAttivo = false;
    private String tipoMezzoSelezionato = "";
    private boolean avvisoMostrato = false;

    private static final Color rossoScuro = new Color(143, 51, 51);
    private static final Color rosso = new Color(175, 62, 62);

    public InformazioniFermata(Frame padre)
    {
        this.padre = padre;

        pannello = new JPanel();
        pannello.setBackground(rossoScuro);
        //pannello.setPreferredSize(new Dimension(200, pannello.getPreferredSize().height)); //forse? (non proprio responsive)

        indicazioneFermata = new JLabel("Fermata selezionata:");
        nome = new JLabel("Seleziona una fermata.");
        //tipoMezzo = new JLabel("");
        lineeServite = new JLabel("");
        lineeServite.setBackground(rossoScuro);

        pulsantiLinee = new JPanel();
        pulsantiLinee.setBackground(rossoScuro);
        pulsantiLinee.setLayout(new BoxLayout(pulsantiLinee, BoxLayout.Y_AXIS));

        infoLinea = new JPanel();
        infoLinea.setLayout(new BoxLayout(infoLinea, BoxLayout.Y_AXIS));
        infoLinea.setBackground(rosso);

        avvisoPrevisione = new JLabel("");

        mostraMezzi = new JButton("  Mostra mezzi sulla linea  ");
        mostraMezzi.addActionListener(e -> tracciaMezzi());

        statoCorsa = new JLabel("");
        ritardoCorsa = new JLabel("");

        avvisoTracciamento = new JLabel("");

        pannello.setLayout(new BoxLayout(pannello, BoxLayout.Y_AXIS));
        pannello.add(indicazioneFermata);
        pannello.add(nome);
        pannello.add(lineeServite);
        pannello.add(pulsantiLinee);
        pannello.add(infoLinea);
        //pannello.add(tipoMezzo);
    }

    public JPanel getPannello()
    {
        return this.pannello;
    }

    public void impostaInfo(CustomWaypoint fermata)
    {
        nome.setText(fermata.getNome());

        /*if (fermata.getId().startsWith("ITO"))
            tipoMezzoSelezionato = "Metropolitana";
        else
            tipoMezzoSelezionato = "Autobus";*/
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

            //pulsanteLinea.addActionListener(e -> mostraInfoLinea(pulsanteLinea.getText(), false));
            pulsanteLinea.addActionListener(e -> mostraInfoLinea(r.getId(), false));

            //TODO: vabbe qui potrebbe essere gestito meglio:
            // l'unico motivo per cui non viene creato un pulsante vuoto con testo messo poi (in queste righe)
            // è perché altrimenti, per alcuni pulsanti, il testo non viene mostrato correttamente
            if (StaticGTFS.lineaDellaMetro(r.getId()))
                pulsanteLinea.setText(StaticGTFS.getNomeRealeMetro(r.getId()));

            pulsantiLinee.add(pulsanteLinea);
        }

        lineeServite.setText("Linee servite:");
        pannello.revalidate();
        pannello.repaint();
    }

    // Il parametro "f" serve solo per capire se inserire o meno il label "prossimo arrivo";
    // infatti questo label non è necessario se il metodo viene richiamato dal frame (tramite combo
    // box per cercare una linea - quindi nessuna fermata è selezionata direttamente, e non si può
    // prevedere un prossimo arrivo)
    public void mostraInfoLinea(String id, boolean f)
    {
        infoLinea.removeAll();
        CustomWaypoint fermata = GestoreWaypoint.ultimaFermata;
        Route linea = StaticGTFS.getLinea(id);

        //assert linea != null;
        if (linea != null)
        {
            if (linea.getTipo() == 0) tipoMezzoSelezionato = "Tram";
            else if (linea.getTipo() == 1) tipoMezzoSelezionato = "Metropolitana";
            else if (linea.getTipo() == 3) tipoMezzoSelezionato = "Autobus";
        }

        JLabel testoLinea = new JLabel("Linea selezionata: " + id);
        JLabel tipoMezzo = new JLabel("Tipo mezzo: " + tipoMezzoSelezionato);
        JLabel prossimoArrivo = new JLabel("<html>Calcolo del prossimo arrivo<br> in corso...</html>"); //new JLabel("Prossimo arrivo: ");

        infoLinea.add(testoLinea);
        infoLinea.add(tipoMezzo);
        if (!f) infoLinea.add(prossimoArrivo);

        pannello.scrollRectToVisible(new Rectangle(infoLinea.getBounds()));
        pannello.add(mostraMezzi);

        if (task != null) task.cancel(true);

        if (StaticGTFS.lineaDellaMetro(id)) prossimoArrivo.setText("");

        if (WiFi.WiFi || !WiFi.WiFi)
        {

            task = scheduler.scheduleAtFixedRate(() ->
            {
                if (!WiFi.wifi_connesso())
                {
                    /*task.cancel(true);*/
                    /*mostraInfoLinea(id);*/

                    System.out.println("DEBUG: Offline [orario calcolato staticamente]");

                    if (!StaticGTFS.lineaDellaMetro(id))
                    {
                        //TODO: racchiudere queste righe in un metodo? (visto che anche sotto viene
                        // usata la stessa sequenza di istruzioni [per lap parte connessa al wifi])
                        prossimoArrivo.setText("Prossimo arrivo previsto: " +
                                StaticGTFS.getTripUpdate(fermata.getId(), id));
                        avvisoPrevisione.setText("<html><u><i>Attenzione: questo orario non<br>è basato su dati in tempo reale,<br>ma è l'orario di arrivo<br>programmato.</i></u></html>");
                        infoLinea.add(avvisoPrevisione);
                    }

                    if (tracciamentoAttivo)
                    {
                        System.out.println("DEBUG: Offline [tracciamento statico]");
                        ArrayList<GeoPosition> lista = StaticGTFS.getPosizioneVeicolo(id);
                        CustomWaypointPainter.setPosizioniMezzi(lista);

                        CustomWaypointPainter.setTracciamentoAttivo(true);

                        List<GeoPosition> percorso = StaticGTFS.getPercorso(id);
                        Mappa.disegnaLinea(percorso);
                        Mappa.getMapViewer().repaint();
                    }
                }
                else
                {
                    if (!StaticGTFS.lineaDellaMetro(id))
                    {
                        String tempo = DynamicGTFS.getTripUpdate(id, fermata.getId());
                        if (tempo.isEmpty()) {
                            System.out.println("DEBUG: Connesso a internet ma orario vuoto - orario previsto staticamente");
                            tempo = StaticGTFS.getTripUpdate(fermata.getId(), id);
                            avvisoPrevisione.setText("<html><u><i>Attenzione: questo orario non<br>è basato su dati in tempo reale,<br>ma è l'orario di arrivo<br>programmato.</i></u></html>");
                            infoLinea.add(avvisoPrevisione);
                        }

                        statoCorsa.setText("Stato corsa: " + DynamicGTFS.getStato(DynamicGTFS.getUltimoTripDescriptor()));

                        int ritardo = DynamicGTFS.getRitardo(DynamicGTFS.getUltimoTripUpdate());
                        if (ritardo < 0) // In anticipo
                            ritardoCorsa.setText("Anticipo stimato: " + ritardo * -1 + " minuti.");
                        else             // In ritardo
                            ritardoCorsa.setText("Ritardo stimato: " + ritardo + " minuti.");

                        infoLinea.add(statoCorsa);
                        infoLinea.add(ritardoCorsa);

                        prossimoArrivo.setText("Prossimo arrivo previsto: " + tempo);
                    }

                    if (tracciamentoAttivo) {
                        ArrayList<GeoPosition> lista = DynamicGTFS.getVehiclePosition(id);
                        if (lista.isEmpty())
                        {
                            avvisoTracciamento.setText("<html><u><i>Attenzione: non è stato<br> possibile tracciare alcun mezzo.</i></u></html>");
                            infoLinea.add(avvisoTracciamento);
                        }

                        //Mappa.getMapViewer().zoomToBestFit(new HashSet<>(), 0.7);
                        CustomWaypointPainter.setPosizioniMezzi(lista);

                        CustomWaypointPainter.setTracciamentoAttivo(true);

                        List<GeoPosition> percorso = StaticGTFS.getPercorso(id);
                        Mappa.disegnaLinea(percorso);

                        Mappa.getMapViewer().repaint();
                    }

                    String problema = DynamicGTFS.getServiceAlert(id);
                    if (!problema.isBlank() && !avvisoMostrato)
                    {
                        avvisoMostrato = true;
                        padre.mostraAvviso(problema);
                        //TODO: forse al posto di mostrare una finestra andrebbe proprio lasciato
                        // scritto da qualche parte nel pannello/sulla mappa?
                    }

                    //prossimoArrivo.setText("Prossimo arrivo previsto: " + tempo);
                }
            }, 0, 5, TimeUnit.SECONDS);
        }
        /*else {
            // TODO: questa parte di codice qui sotto deve essere messa in un metodo
            // per calcolare i dati statici... - stesso metodo usato nell'if qui sopra
            List<Trip> viaggi = StaticGTFS.trips.stream()
                    .filter(trip -> trip.getRouteId().equals(id))
                    //.filter(trip -> trip.isServiceActiveToday
                    .toList();

            List<String> viaggiValidi = viaggi.stream()
                    .map(Trip::getId).toList();

            LocalTime adesso = LocalTime.now();

            Optional<StopTime> prossimoStopTime = StaticGTFS.stopTimes.stream()
                    .filter(st -> viaggiValidi.contains(st.getTripId()))
                    .filter(st -> st.getStopId().equals(fermata.getId()))
                    .filter(st -> st.getOrarioArrivo().isAfter(adesso))
                    .min(Comparator.comparing(StopTime::getOrarioArrivo));

            if (prossimoStopTime.isPresent()) {
                StopTime st = prossimoStopTime.get();
                System.out.print("OFFLINE!!!: ");
                System.out.println(st);
            }
        }*/
    }

    public void tracciaMezzi()
    {
        if (tracciamentoAttivo)
        {
            tracciamentoAttivo = false;
            mostraMezzi.setText("  Mostra mezzi sulla linea  ");
            CustomWaypointPainter.setTracciamentoAttivo(false);
            Mappa.getMapViewer().setOverlayPainter(GestoreWaypoint.getWaypointPainter());
            Mappa.getMapViewer().repaint();
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
        CustomWaypointPainter.setTracciamentoAttivo(false);
        Mappa.getMapViewer().setOverlayPainter(GestoreWaypoint.getWaypointPainter());
        Mappa.getMapViewer().repaint();
        tracciamentoAttivo = false;
        mostraMezzi.setText("  Mostra mezzi sulla linea  ");
        nome.setText("Seleziona una fermata.");
        //tipoMezzo.setText("");
        lineeServite.setText("");
        pulsantiLinee.removeAll();
        pulsantiLinee.repaint();
        infoLinea.removeAll();
        infoLinea.repaint();
        pannello.remove(mostraMezzi);
    }
}
