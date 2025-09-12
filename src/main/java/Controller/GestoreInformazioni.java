package Controller;

import Model.*;
import View.CustomWaypointPainter;
import View.Mappa;
import View.Frame;
import View.PannelloInformazioni;
import org.jxmapviewer.viewer.GeoPosition;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GestoreInformazioni {

    private final Frame padre;
    private final PannelloInformazioni pannelloInformazioni;
    private static JButton mostraMezzi;
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> task;
    private boolean tracciamentoAttivo = false;
    private boolean avvisoMostrato = false;
    private boolean chiamataDaComboBox = false;
    private CustomWaypoint ultimaFermataSelezionata;

    public GestoreInformazioni(Frame padre, PannelloInformazioni pannelloInformazioni)
    {
        this.padre = padre;
        this.pannelloInformazioni = pannelloInformazioni;

        mostraMezzi = new JButton("  Mostra mezzi sulla linea  ");
        mostraMezzi.setPreferredSize(new Dimension(185, 30));
        mostraMezzi.setMaximumSize(new Dimension(185, mostraMezzi.getPreferredSize().height));
        mostraMezzi.addActionListener(e -> tracciaMezzi());
        mostraMezzi.setBackground(new Color(175, 62, 62));
        mostraMezzi.setForeground(Color.WHITE);
        mostraMezzi.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        mostraMezzi.setBorderPainted(true);
    }

    //metodo che riceve il waypoint selezionato
    public void selezionaFermata(CustomWaypoint fermata) {
        // Deseleziona la fermata precedente se esiste
        if (ultimaFermataSelezionata != null) {
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

    public void deselezionaFermata() {
        if (ultimaFermataSelezionata != null) {
            ultimaFermataSelezionata.deseleziona();
            resetGestore();
            ultimaFermataSelezionata = null;
        }
    }

    public void deselezionaLinea() {
        Mappa.disegnaLinea(new ArrayList<>());
        Mappa.getMapViewer().setOverlayPainter(GestoreWaypoint.getWaypointPainter());
        Mappa.getMapViewer().repaint();
    }

    private ArrayList<Route> trovaLineePerFermata(String stopId) {
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
        } else {
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


    public static JButton getMostraMezziButton() {
        return mostraMezzi;
    }

    public void mostraInfoLinea(String routeId, boolean daComboBox)
    {
        Route linea = StaticGTFS.getLinea(routeId);
        CustomWaypoint fermata = this.getUltimaFermata();
        if (linea == null) return;

        pannelloInformazioni.updateLineaInfo(routeId, getTipoMezzoString(linea.tipo()), "<html>Calcolo del prossimo arrivo in corso...</html>");
        pannelloInformazioni.mostraInfoLineaUI(routeId, daComboBox);
        avvisoMostrato = false;

        if (task != null) task.cancel(true);
        task = scheduler.scheduleAtFixedRate(() -> gestisciAggiornamento(routeId, fermata, daComboBox), 0, 5, TimeUnit.SECONDS);
    }

    private void gestisciAggiornamento(String routeId, CustomWaypoint fermata, boolean daComboBox)
    {
        if (!StaticGTFS.lineaDellaMetro(routeId) && !daComboBox) {gestisciProssimoArrivo(routeId, fermata);}

        if (tracciamentoAttivo) {gestisciTracciamento(routeId);}

        gestisciAvvisi(routeId);
    }

    private void gestisciProssimoArrivo(String routeId, CustomWaypoint fermata)
    {
        String tempo = DynamicGTFS.getTripUpdate(routeId, fermata.getId());
        if (tempo.isBlank()) {
            tempo = StaticGTFS.getTripUpdate(fermata.getId(), routeId);
            if (tempo.isBlank()) {
                pannelloInformazioni.setProssimoArrivo("<html>Impossibile calcolare l'orario di arrivo.</html>");
            } else {
                pannelloInformazioni.setAvvisoPrevisione("<html><u><i>Attenzione: questo orario non<br>è basato su dati in tempo reale,<br>ma è l'orario di arrivo<br>programmato.</i></u></html>");
                pannelloInformazioni.setProssimoArrivo("<html>Prossimo arrivo previsto: " + tempo + "</html>");
            }
        } else {
            pannelloInformazioni.setAvvisoPrevisione("");
            String stato = DynamicGTFS.getStato(DynamicGTFS.getUltimoTripDescriptor());
            int ritardo = DynamicGTFS.getRitardo(DynamicGTFS.getUltimoTripUpdate());
            String ritardoTesto = (ritardo < 0) ? "Anticipo stimato: " + (ritardo * -1) + " minuti." : "Ritardo stimato: " + ritardo + " minuti.";
            pannelloInformazioni.setStatoCorsa(stato, ritardoTesto);
            pannelloInformazioni.setProssimoArrivo("Prossimo arrivo previsto: " + tempo);
        }
    }

    private void gestisciTracciamento(String routeId)
    {
        pannelloInformazioni.setAvvisoTracciamento("");

        ArrayList<GeoPosition> lista = DynamicGTFS.getVehiclePosition(routeId);
        if (lista.isEmpty() && !StaticGTFS.lineaDellaMetro(routeId)) {
            lista = StaticGTFS.getPosizioneVeicolo(routeId);
            if (lista.isEmpty()) {
                pannelloInformazioni.setAvvisoTracciamento("<html><u><i>Attenzione: non è stato<br> possibile tracciare alcun mezzo.</i></u></html>");
            }
        } else {
            pannelloInformazioni.setAvvisoTracciamento("");
        }

        CustomWaypointPainter.setPosizioniMezzi(lista);
        CustomWaypointPainter.setTracciamentoAttivo(true);
        List<GeoPosition> percorso = StaticGTFS.getPercorso(routeId);
        Mappa.disegnaLinea(percorso);
        Mappa.getMapViewer().repaint();
    }

    private void gestisciAvvisi(String routeId) {
        String problema = DynamicGTFS.getServiceAlert(routeId);
        if (!problema.isBlank() && !avvisoMostrato) {
            avvisoMostrato = true;
            padre.mostraAvviso(problema);
        }
    }

    public String getTipoMezzoString(int tipo) {
        return switch (tipo) {
            case 0 -> "Tram";
            case 1 -> "Metropolitana";
            case 2 -> "Treno";
            case 3 -> "Autobus";
            default -> "";
        };
    }

    public CustomWaypoint getUltimaFermata() {
        return ultimaFermataSelezionata;
    }

    public void tracciaMezzi() {
        if (tracciamentoAttivo) {
            tracciamentoAttivo = false;
            mostraMezzi.setText("  Mostra mezzi sulla linea  ");
            CustomWaypointPainter.setTracciamentoAttivo(false);
            if (!chiamataDaComboBox) {
                Mappa.getMapViewer().setOverlayPainter(GestoreWaypoint.getWaypointPainter());
            }
        } else {
            tracciamentoAttivo = true;
            mostraMezzi.setText("Nascondi mezzi sulla linea");
        }
        Mappa.getMapViewer().repaint();
    }

    public void resetGestore() {
        if (task != null) task.cancel(true);
        CustomWaypointPainter.setTracciamentoAttivo(false);
        deselezionaLinea();
        tracciamentoAttivo = false;
        mostraMezzi.setText("  Mostra mezzi sulla linea  ");
        pannelloInformazioni.setAvvisoPrevisione("");
        pannelloInformazioni.resetPannello();
    }
}