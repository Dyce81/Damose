package Controller;

import Model.*;
import View.CustomWaypointPainter;
import View.Mappa;
import View.Frame;
import View.PannelloInformazioni;
import org.jxmapviewer.viewer.GeoPosition;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * GestoreInformazioni si occupa di gestire tutte le informazioni richieste sulle linee e le fermate.
 * Le informazioni reperite qui vengono poi mostrate da PannelloInformazioni.
 */
public class GestoreInformazioni
{
    private final Frame padre;
    private final PannelloInformazioni pannelloInformazioni;
    private final JButton mostraMezzi;
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> task;
    private boolean tracciamentoAttivo = false;
    private boolean avvisoMostrato = false;
    private boolean chiamataDaComboBox = false;
    private CustomWaypoint ultimaFermataSelezionata;

    /**
     * Il costruttore di GestoreInformazioni.
     * @param padre il Frame dentro cui è si trova la mappa e il pannello informazioni. Questo campo viene usato per mostrare l'avvisi su eventuali problemi sulla linea.
     * @param pannelloInformazioni il pannello che mostra le informazioni ottenute in questa classe.
     */
    public GestoreInformazioni(Frame padre, PannelloInformazioni pannelloInformazioni)
    {
        this.padre = padre;
        this.pannelloInformazioni = pannelloInformazioni;
        mostraMezzi = pannelloInformazioni.getMostraMezziButton();
        mostraMezzi.addActionListener(e -> tracciaMezzi());
    }

    /**
     * Ottiene le informazioni relative alla fermata passata come parametro. In questo metodo vengono anche calcolate le linee che passano per questa fermata.
     * @param fermata la fermata di cui si vogliono conoscere le informazioni.
     */
    public void selezionaFermata(CustomWaypoint fermata)
    {
        // Deseleziona la fermata precedente se esiste
        if (ultimaFermataSelezionata != null)
        {
            ultimaFermataSelezionata.deseleziona();
            // Resetta il gestore precedente, inclusa la UI
            resetGestore();
        }

        // Seleziona la nuova fermata
        fermata.seleziona();
        ultimaFermataSelezionata = fermata;

        // Aggiorna il pannello
        pannelloInformazioni.impostaFermata(fermata);

        ArrayList<Route> lineeTrovate = trovaLineePerFermata(fermata.getId());
        pannelloInformazioni.setLineeServite(lineeTrovate, this);

        Mappa.getMapViewer().repaint();
    }

    /**
     * Deseleziona la fermata attualmente selezionata.
     */
    public void deselezionaFermata()
    {
        if (ultimaFermataSelezionata != null)
        {
            ultimaFermataSelezionata.deseleziona();
            resetGestore();
            ultimaFermataSelezionata = null;
        }
    }

    /**
     * Deseleziona la linea attualmente selezionata.
     */
    public void deselezionaLinea()
    {
        Mappa.disegnaLinea(new ArrayList<>());
        Mappa.getMapViewer().setOverlayPainter(GestoreWaypoint.getWaypointPainter());
        Mappa.getMapViewer().repaint();
    }

    private ArrayList<Route> trovaLineePerFermata(String stopId)
    {
        ArrayList<Route> lineeTrovate = new ArrayList<>();

        if (stopId.startsWith("ITO")) {
            List<CollegamentoMetro> collegamentiTrovati = StaticGTFS.collegamentiMetro.stream()
                    .filter(c -> c.stopId().equals(stopId))
                    .toList();

            for (Route r : StaticGTFS.routes) {
                // Controlla se la linea è una metropolitana (tipo 1)
                if (r.tipo() == 1) {
                    for (CollegamentoMetro c : collegamentiTrovati) {
                        // Controlla se il routeId del collegamento corrisponde a quello della linea
                        if (c.routeId().equals(r.id())) {
                            lineeTrovate.add(r);
                        }
                    }
                }
            }
        }
        else
        {
            // Logica per autobus e altri mezzi

            //Trova tutti gli ID dei "trip" (corse) che si fermano a questo "stopId"
            Set<String> tripIds = new HashSet<>();
            for (StopTime st : StaticGTFS.stopTimes) {
                if (st.getStopId().equals(stopId)) {
                    tripIds.add(st.getTripId());
                }
            }

            //Usa gli ID dei "trip" per trovare gli ID delle "route" (linee) corrispondenti
            Set<String> routeIds = new HashSet<>();
            for (Trip t : StaticGTFS.trips) {
                if (tripIds.contains(t.id())) {
                    routeIds.add(t.routeId());
                }
            }

            //usa gli ID delle "route" per trovare gli oggetti "Route" completi
            for (Route r : StaticGTFS.routes) {
                if (routeIds.contains(r.id())) {
                    lineeTrovate.add(r);
                }
            }
        }

        return lineeTrovate;
    }

    /**
     * Calcola il tipo di linea e avvia un timer che ogni 30 secondi cerca di ottenere un aggiornamento sulla linea (prossimo arrivo, eventuale ritardo ecc.).
     * @param routeId il codice identificativo della linea per la quale si desidera ottenere un aggiornamento.
     * @param daComboBox boolean che specifica se il metodo è stato invocato da una ComboBox o dal GestoreInformazioni stesso.
     */
    public void mostraInfoLinea(String routeId, boolean daComboBox)
    {
        Route linea = StaticGTFS.getLinea(routeId);
        CustomWaypoint fermata = this.getUltimaFermata();
        if (linea == null) return;

        pannelloInformazioni.updateLineaInfo(routeId, getTipoMezzoString(linea.tipo()), "<html>Calcolo del prossimo arrivo in corso...</html>");
        pannelloInformazioni.mostraInfoLineaUI(routeId, daComboBox);
        avvisoMostrato = false;
        chiamataDaComboBox = daComboBox;

        if (task != null) task.cancel(true);
        task = scheduler.scheduleAtFixedRate(() -> gestisciAggiornamento(routeId, fermata, daComboBox), 0, 5, TimeUnit.SECONDS);
    }

    private void gestisciAggiornamento(String routeId, CustomWaypoint fermata, boolean daComboBox)
    {
        if (!StaticGTFS.lineaDellaMetro(routeId) && !daComboBox)
            gestisciProssimoArrivo(routeId, fermata);

        if (tracciamentoAttivo)
            gestisciTracciamento(routeId);

        gestisciAvvisi(routeId);
    }

    private void gestisciProssimoArrivo(String routeId, CustomWaypoint fermata)
    {
        String tempo = DynamicGTFS.getTripUpdate(routeId, fermata.getId());
        if (tempo.isBlank())
        {
            tempo = StaticGTFS.getTripUpdate(fermata.getId(), routeId);
            if (tempo.isBlank())
            {
                pannelloInformazioni.setProssimoArrivo("<html>Impossibile calcolare l'orario di arrivo.</html>");
            }
            else
            {
                pannelloInformazioni.setAvvisoPrevisione("<html><u><i>Attenzione: questo orario non<br>è basato su dati in tempo reale,<br>ma è l'orario di arrivo<br>programmato.</i></u></html>");
                pannelloInformazioni.setProssimoArrivo("<html>Prossimo arrivo previsto: " + tempo + "</html>");
            }
        }
        else
        {
            pannelloInformazioni.setAvvisoPrevisione("");
            String stato = DynamicGTFS.getStato(DynamicGTFS.getUltimoTripDescriptor());
            int ritardo = DynamicGTFS.getRitardo(DynamicGTFS.getUltimoTripUpdate());
            String ritardoTesto = (ritardo < 0) ? "Anticipo stimato: " + (ritardo * -1) + " minuti." : "Ritardo stimato: " + ritardo + " minuti.";
            pannelloInformazioni.setStatoCorsa(stato, ritardoTesto);
            pannelloInformazioni.setProssimoArrivo("Prossimo arrivo previsto: " + tempo);
        }
    }

    // Questo metodo si occupa del tracciamento dei mezzi (statico e dinamico)
    private void gestisciTracciamento(String routeId)
    {
        // Ottiene una lista delle coordinate di tutti i veicoli in circolazione
        // sulla linea selezionata. Se la linea selezionata è una linea della metropolitana
        // è inutile provare a tracciare i mezzi: non sono disponibili
        ArrayList<GeoPosition> lista = DynamicGTFS.getVehiclePosition(routeId);

        // Se la lista è vuota prova a fare una stima della posizione dei mezzi (tramite
        // dati statici). Il controllo viene saltato se la linea selezionata è una linea
        // della metro, in quanto non è mai possibile stimare dinamicamente la posizione
        // dei mezzi. Si potrebbe tracciarli staticamente, ma per semplicità dell'interfaccia
        // è stato scelto di non mostrarli comunque, disegnando solo la linea
        if (lista.isEmpty())
        {
            System.out.println("DEBUG: Tentativo di tracciamento statico");
            if (!StaticGTFS.lineaDellaMetro(routeId))
                lista = StaticGTFS.getPosizioneVeicolo(routeId);

            // Se la lista dei veicoli è ancora vuota, allora non è stato possibile tracciare
            // alcun veicolo; la linea viene comunque disegnata sulla mappa (sotto)
            if (lista.isEmpty())
            {
                pannelloInformazioni.setAvvisoTracciamento("<html><u><i>Attenzione: non è stato<br> possibile tracciare alcun mezzo.</i></u></html>");
            }
        }
        else
            pannelloInformazioni.setAvvisoTracciamento("");

        CustomWaypointPainter.setPosizioniMezzi(lista);
        CustomWaypointPainter.setTracciamentoAttivo(true);

        List<GeoPosition> percorso = StaticGTFS.getPercorso(routeId);
        Mappa.disegnaLinea(percorso);

        Mappa.getMapViewer().repaint();
    }

    private void gestisciAvvisi(String routeId)
    {
        String problema = DynamicGTFS.getServiceAlert(routeId);
        if (!problema.isBlank() && !avvisoMostrato)
        {
            avvisoMostrato = true;
            padre.mostraAvviso(problema);
        }
    }

    /**
     * Restituisce una stringa che specifica quale mezzo è identificato dal numero passato come parametro.
     * @param tipo int che specifica un tipo di mezzo.
     * @return stringa con il nome del mezzo ottenuto.
     */
    public String getTipoMezzoString(int tipo)
    {
        return switch (tipo) {
            case 0 -> "Tram";
            case 1 -> "Metropolitana";
            case 2 -> "Treno";
            case 3 -> "Autobus";
            default -> "";
        };
    }

    /**
     * Restituisce l'ultima fermata selezionata.
     * @return CustomWaypoint che specifica la fermata attualmente selezionata.
     */
    public CustomWaypoint getUltimaFermata()
    {
        return ultimaFermataSelezionata;
    }

    /**
     * Metodo invocato dal pulsante per cambiare lo stato del tracciamento (attivo o inattivo).
     */
    public void tracciaMezzi()
    {
        if (tracciamentoAttivo)
        {
            tracciamentoAttivo = false;
            mostraMezzi.setText(" Mostra mezzi sulla linea ");
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

    /**
     * Effettua il reset del GestoreInformazioni, annullando l'eventuale tracciamento e smettendo
     * di cercare aggiornamenti sulla linea o fermata precedentemente specificata.
     */
    public void resetGestore()
    {
        if (task != null) task.cancel(true);
        CustomWaypointPainter.setTracciamentoAttivo(false);
        deselezionaLinea();
        tracciamentoAttivo = false;
        mostraMezzi.setText("  Mostra mezzi sulla linea  ");
        pannelloInformazioni.setAvvisoPrevisione("");
        pannelloInformazioni.resetPannello();
    }
}