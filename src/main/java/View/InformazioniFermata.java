package View;

import Controller.DynamicGTFS;
import Controller.StaticGTFS;
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
    private final JLabel nome;
    private final JLabel lineeServite;
    private final JPanel pulsantiLinee;
    private final JPanel infoLinea;
    private final JButton mostraMezzi;
    private final JLabel avvisoTracciamento;
    private final JLabel statoCorsa;
    private final JLabel ritardoCorsa;
    private final JLabel direzione;

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> task;

    private boolean tracciamentoAttivo = false;
    private String tipoMezzoSelezionato = "";
    private boolean avvisoMostrato = false;

    private static final Color rossoScuro = new Color(143, 51, 51);
    private static final Color rosso = new Color(175, 62, 62);

    private boolean chiamataDaComboBox = false;

    public InformazioniFermata(Frame padre)
    {
        this.padre = padre;

        pannello = new JPanel();
        pannello.setBackground(rossoScuro);
        //pannello.setPreferredSize(new Dimension(200, pannello.getPreferredSize().height)); //forse? (non proprio responsive)

        nome = new JLabel("Seleziona una fermata.");
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

        statoCorsa = new JLabel("");
        ritardoCorsa = new JLabel("");
        avvisoTracciamento = new JLabel("");
        direzione = new JLabel("");

        pannello.setLayout(new BoxLayout(pannello, BoxLayout.Y_AXIS));
        pannello.add(new JLabel("Fermata selezionata:"));
        pannello.add(nome);
        pannello.add(lineeServite);
        pannello.add(pulsantiLinee);
        pannello.add(infoLinea);
    }

    public JPanel getPannello()
    {
        return this.pannello;
    }

    public void impostaNome(CustomWaypoint fermata)
    {
        nome.setText(fermata.getNome());
    }

    public void setLineeServite(ArrayList<Route> linee)
    {
        pulsantiLinee.removeAll();

        for (Route r : linee) {
            JButton pulsanteLinea = new JButton(r.getId());
            pulsanteLinea.setBorderPainted(false);
            pulsanteLinea.setBackground(rossoScuro);
            pulsanteLinea.setMaximumSize(new Dimension(Integer.MAX_VALUE, pulsanteLinea.getPreferredSize().height));

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

    // Il parametro "daComboBox" serve solo per capire se inserire o meno il label "prossimo arrivo"
    // (e per evitare che le linee vengano cancellate quando NON necessario);
    // infatti questo label non è necessario se il metodo viene richiamato dal frame (tramite combo
    // box per cercare una linea - quindi nessuna fermata è selezionata direttamente, e non si può
    // prevedere un prossimo arrivo)
    /*public void mostraInfoLinea(String id, boolean daComboBox)
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
        JLabel prossimoArrivo = new JLabel("<html>Calcolo del prossimo arrivo<br> in corso...</html>");

        infoLinea.add(testoLinea);
        infoLinea.add(tipoMezzo);
        if (!daComboBox)
        {
            chiamataDaComboBox = false;
            infoLinea.add(prossimoArrivo);
        }
        else
            chiamataDaComboBox = true;
        //infoLinea.add(direzione);

        pannello.scrollRectToVisible(new Rectangle(infoLinea.getBounds()));
        pannello.add(mostraMezzi);

        if (task != null) task.cancel(true);

        if (StaticGTFS.lineaDellaMetro(id)) prossimoArrivo.setText("");

        task = scheduler.scheduleAtFixedRate(() ->
        {
            if (!WiFi.connesso())
            {
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
                boolean orarioStatico = false; // Specifica se l'orario attuale è calcolata staticamente
                //String headsign = "";
                if (!StaticGTFS.lineaDellaMetro(id))
                {
                    String tempo = DynamicGTFS.getTripUpdate(id, fermata.getId());
                    if (tempo.isEmpty())
                    {
                        orarioStatico = true;
                        System.out.println("DEBUG: Connesso a internet ma orario vuoto - orario previsto staticamente");
                        // TODO: a volte questo metodo (StaticGTFS.getTripUpdate) sembra non trovare mai un risultato, andando avanti all'infinito. questa cosa è da risolvere
                        tempo = StaticGTFS.getTripUpdate(fermata.getId(), id);
                        avvisoPrevisione.setText("<html><u><i>Attenzione: questo orario non<br>è basato su dati in tempo reale,<br>ma è l'orario di arrivo<br>programmato.</i></u></html>");
                        infoLinea.add(avvisoPrevisione);
                    }

                    // Se l'orario risulta nullo anche tramite dati statici, allora non è proprio
                    // possibile calcolare l'orario
                    if (tempo.isBlank())
                    {
                        infoLinea.remove(avvisoPrevisione); //Forse?
                        prossimoArrivo.setText("<html>Impossibile calcolare l'orario<br>di arrivo.</html>");
                    }
                    else
                    {
                        prossimoArrivo.setText("Prossimo arrivo previsto: " + tempo);
                        //String tripId = DynamicGTFS.getUltimoTripUpdate().getTrip().getTripId();
                        //headsign = StaticGTFS.getTrip(tripId).getHeadsign();
                    }

                    //direzione.setText("Direzione: " + headsign);

                    // Se orarioStatico è vero, allora il trip update è stato calcolato
                    // staticamente non è possibile ottenere in tempo reale lo stato della
                    // corsa e il ritardo/anticipo stimato, quindi è inutile calcolarli
                    if(!orarioStatico)
                    {
                        statoCorsa.setText("Stato corsa: " + DynamicGTFS.getStato(DynamicGTFS.getUltimoTripDescriptor()));

                        int ritardo = DynamicGTFS.getRitardo(DynamicGTFS.getUltimoTripUpdate());
                        if (ritardo < 0) // In anticipo
                            ritardoCorsa.setText("Anticipo stimato: " + ritardo * -1 + " minuti.");
                        else             // In ritardo
                            ritardoCorsa.setText("Ritardo stimato: " + ritardo + " minuti.");

                        infoLinea.add(statoCorsa);
                        infoLinea.add(ritardoCorsa);
                    }
                }

                if (tracciamentoAttivo)
                {
                    ArrayList<GeoPosition> lista = DynamicGTFS.getVehiclePosition(id);
                    if (lista.isEmpty())
                    {
                        avvisoTracciamento.setText("<html><u><i>Attenzione: non è stato<br> possibile tracciare alcun mezzo.</i></u></html>");
                        infoLinea.add(avvisoTracciamento);
                    }

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
            }

            infoLinea.revalidate();
            infoLinea.repaint();
        }, 0, 5, TimeUnit.SECONDS);
    }*/

    public void mostraInfoLinea(String routeId, boolean daComboBox)
    {
        Route linea = StaticGTFS.getLinea(routeId);
        CustomWaypoint fermata = GestoreWaypoint.getUltimaFermata();

        if (linea == null) return;
        infoLinea.removeAll();

        //Forse da sostituire con switch?
        if (linea.getTipo() == 0) tipoMezzoSelezionato = "Tram";
        else if (linea.getTipo() == 1) tipoMezzoSelezionato = "Metropolitana";
        else if (linea.getTipo() == 3) tipoMezzoSelezionato = "Autobus";

        JLabel testoLinea = new JLabel("Linea selezionata: " + routeId);
        JLabel tipoMezzo = new JLabel("Tipo mezzo: " + tipoMezzoSelezionato);
        JLabel prossimoArrivo = new JLabel("");

        infoLinea.add(testoLinea);
        infoLinea.add(tipoMezzo);

        chiamataDaComboBox = daComboBox; //forse?

        if (!StaticGTFS.lineaDellaMetro(routeId) && !daComboBox) // && non proviene da combobox
        {
            prossimoArrivo.setText("<html>Calcolo del prossimo arrivo<br> in corso...</html>");
            infoLinea.add(prossimoArrivo);
        }

        pannello.add(mostraMezzi);
        pannello.scrollRectToVisible(new Rectangle(infoLinea.getBounds()));

        if (task != null) task.cancel(true);

        task = scheduler.scheduleAtFixedRate(() ->
        {
            // Se la linea selezionata è una linea metropolitana, allora non serve calcolare
            // orari di arrivo/ritardi.
            // (in realtà non è proprio possibile, vista la struttura di stop_times.txt)
            //TODO: controllare anche che il metodo non sia stato chiamato dal frame
            if (!StaticGTFS.lineaDellaMetro(routeId) && !daComboBox)
            {
                String tempo = DynamicGTFS.getTripUpdate(routeId, fermata.getId());

                // Se non è stato possibile calcolare il tempo dinamicamente (stringa vuota),
                // allora si prova a calcolarlo staticamente (tramite gli orari previsti su
                // stop_times.txt)
                if (tempo.isBlank())
                {
                    System.out.println("DEBUG: Connesso a internet, ma orario calcolato staticamente");
                    tempo = StaticGTFS.getTripUpdate(fermata.getId(), routeId);

                    // Se per una seconda volta non è stato possibile calcolare il tempo, allora
                    // per il momento non è possibile calcolare l'orario
                    if (tempo.isBlank())
                    {
                        prossimoArrivo.setText("<html>Impossibile calcolare l'orario<br>di arrivo.</html>");
                    }
                    else
                    {
                        infoLinea.add(new JLabel("<html><u><i>Attenzione: questo orario non<br>è basato su dati in tempo reale,<br>ma è l'orario di arrivo<br>programmato.</i></u></html>"));
                    }
                }
                else
                {
                    // Se la stringa del tempo non è vuota, allora il tempo è stato calcolato
                    // dinamicamente; questo vuol dire che è anche possibile ottenere lo stato della
                    // corsa (programmata, in ritardo, cancellata...) e l'eventuale ritardo/anticipo
                    statoCorsa.setText("Stato corsa: " + DynamicGTFS.getStato(DynamicGTFS.getUltimoTripDescriptor()));

                    int ritardo = DynamicGTFS.getRitardo(DynamicGTFS.getUltimoTripUpdate());
                    if (ritardo < 0) // In anticipo
                        ritardoCorsa.setText("Anticipo stimato: " + ritardo * -1 + " minuti.");
                    else             // In ritardo
                        ritardoCorsa.setText("Ritardo stimato: " + ritardo + " minuti.");

                    infoLinea.add(statoCorsa);
                    infoLinea.add(ritardoCorsa);
                }

                prossimoArrivo.setText("Prossimo arrivo previsto: " + tempo);
            }

            if (tracciamentoAttivo) {
                // Ottiene una lista delle coordinate di tutti i veicoli in circolazione
                // sulla linea selezionata
                ArrayList<GeoPosition> lista = DynamicGTFS.getVehiclePosition(routeId);

                // Se la lista è vuota prova a fare una stima della posizione dei mezzi (tramite
                // dati statici). Il controllo viene saltato se la linea selezionata è una linea
                // della metro, in quanto non è mai possibile stimare dinamicamente la posizione
                // dei mezzi. Si potrebbe tracciarli staticamente, ma per semplicità dell'interfaccia
                // è stato scelto di non mostrarli comunque, disegnando solo la linea
                //TODO: problema -> non viene mostrato alcun messaggio per far capire all'utente
                // che non siamo riusciti a stimare la posizione dei mezzi. se metti la seconda
                // condizione di questo if nell'if annidato dentro, comunque i mezzi vengono tracciati
                // quindi forse si deve mettere un ulteriore if qui dentro, per evitare che la
                // lista venga calcolata (?)
                if (lista.isEmpty() && !StaticGTFS.lineaDellaMetro(routeId))
                {
                    System.out.println("DEBUG: Tentativo di tracciamento statico");
                    lista = StaticGTFS.getPosizioneVeicolo(routeId);

                    // Se la lista dei veicoli è ancora vuota, allora non è stato possibile tracciare
                    // alcun veicolo; la linea viene comunque disegnata sulla mappa (sotto)
                    if (lista.isEmpty())
                    {
                        avvisoTracciamento.setText("<html><u><i>Attenzione: non è stato<br> possibile tracciare alcun mezzo.</i></u></html>");
                        infoLinea.add(avvisoTracciamento);
                    }
                }

                CustomWaypointPainter.setPosizioniMezzi(lista);
                CustomWaypointPainter.setTracciamentoAttivo(true);

                List<GeoPosition> percorso = StaticGTFS.getPercorso(routeId);
                Mappa.disegnaLinea(percorso);

                Mappa.getMapViewer().repaint();
            }

            // Controla (in tempo reale) se ci sono problemi segnalati da Roma Mobilità sulla
            // linea selezionata
            String problema = DynamicGTFS.getServiceAlert(routeId);
            if (!problema.isBlank() && !avvisoMostrato)
            {
                avvisoMostrato = true;
                padre.mostraAvviso(problema);
                //TODO: forse al posto di mostrare una finestra andrebbe proprio lasciato
                // scritto da qualche parte nel pannello/sulla mappa?
            }

            infoLinea.revalidate();
            infoLinea.repaint();
        }, 0, 5, TimeUnit.SECONDS);
    }

    public void tracciaMezzi()
    {
        if (tracciamentoAttivo)
        {
            tracciamentoAttivo = false;
            mostraMezzi.setText("  Mostra mezzi sulla linea  ");
            CustomWaypointPainter.setTracciamentoAttivo(false);

            //forse?
            if (!chiamataDaComboBox)
            {
                Mappa.getMapViewer().setOverlayPainter(GestoreWaypoint.getWaypointPainter());
                //Mappa.getMapViewer().repaint();
            }
        }
        else
        {
            tracciamentoAttivo = true;
            mostraMezzi.setText("Nascondi mezzi sulla linea");
        }

        Mappa.getMapViewer().repaint();
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
