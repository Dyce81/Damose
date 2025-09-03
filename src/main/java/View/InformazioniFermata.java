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
    private final JLabel avvisoPrevisione;
    private final JLabel direzione;

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> task;

    private boolean tracciamentoAttivo = false;
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
        avvisoPrevisione = new JLabel("");
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

    // Il parametro "daComboBox" serve a specificare se il metodo è stato chiamato dal pannello
    // stesso o meno, ed è utile in due situazioni particolari: in primis perché se la il metodo
    // viene chiamato dalla combo box delle linee, allora non serve mostrare il "prossimo arrivo",
    // in quanto è utile solo se è selezionata una fermata (non possibile se la linea è stata
    // selezionata dalla combo box); in secondo luogo, questo parametro viene assegnato al campo
    // "chiamatoDaComboBox", che serve a capire se, una volta disegnata, la linea debba essere
    // cancellata o meno dalla mappa (il tracciamento, una volta terminato, cerca di cancellare
    // in automatico la linea sulla mappa; questa cosa viene impedita proprio dal
    // campo "chiamatoDaComboBox").
    public void mostraInfoLinea(String routeId, boolean daComboBox)
    {
        Route linea = StaticGTFS.getLinea(routeId);
        CustomWaypoint fermata = GestoreWaypoint.getUltimaFermata();

        if (linea == null) return;
        infoLinea.removeAll();

        JLabel tipoMezzo = new JLabel("");
        switch (linea.getTipo())
        {
            case 0:
                tipoMezzo.setText("Tipo mezzo: Tram");
                break;
            case 1:
                tipoMezzo.setText("Tipo mezzo: Metropolitana");
                break;
            case 2:
                tipoMezzo.setText("Tipo mezzo: Treno");
                break;
            case 3:
                tipoMezzo.setText("Tipo mezzo: Autobus");
                break;
            default:
                tipoMezzo.setText("");
        }

        JLabel testoLinea = new JLabel("Linea selezionata: " + routeId);
        JLabel prossimoArrivo = new JLabel("");

        infoLinea.add(testoLinea);
        infoLinea.add(tipoMezzo);

        chiamataDaComboBox = daComboBox; //forse?

        if (!StaticGTFS.lineaDellaMetro(routeId) && !daComboBox)
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
                        System.out.println("DEBUG: Errore nel calcolo statico");
                        prossimoArrivo.setText("<html>Impossibile calcolare l'orario<br>di arrivo.</html>");
                    }
                    else
                    {
                        System.out.println("DEBUG: Calcolato");
                        avvisoPrevisione.setText("<html><u><i>Attenzione: questo orario non<br>è basato su dati in tempo reale,<br>ma è l'orario di arrivo<br>programmato.</i></u></html>");
                        infoLinea.add(avvisoPrevisione);
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

            // Controlla (in tempo reale) se ci sono problemi segnalati da Roma Mobilità sulla
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

            // Se il metodo mostraInfoLinea è stato chiamato dalla combo box delle linee, allora
            // la linea deve rimanere visibile anche quando il tracciamento non è attivo. La linea
            // viene disegnata quando si chiama il metodo "cercaLinea()" [in Frame.java]
            if (!chiamataDaComboBox)
            {
                Mappa.getMapViewer().setOverlayPainter(GestoreWaypoint.getWaypointPainter());
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
        lineeServite.setText("");
        pulsantiLinee.removeAll();
        pulsantiLinee.repaint();
        infoLinea.removeAll();
        infoLinea.repaint();
        pannello.remove(mostraMezzi);
    }
}