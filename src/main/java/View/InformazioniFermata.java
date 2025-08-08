package View;

import Controller.DynamicGTFS;
import Controller.ReaderStaticGTFS;
import Controller.Wifi;
import Model.*;
import com.google.transit.realtime.GtfsRealtime;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.transit.realtime.GtfsRealtime.*;

public class InformazioniFermata
{
    private final JPanel pannello;
    private final JLabel indicazioneFermata;
    private final JLabel nome;
    private final JLabel tipoMezzo;
    private final JLabel lineeServite;
    private JPanel pulsantiLinee;
    private final JPanel infoLinea;

    private static final Color rossoScuro = new Color(143, 51, 51);

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
        infoLinea.setBackground(rossoScuro);

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

    public void setNome(String nome)
    {
        this.nome.setText(nome + '\n');
    }

    public void setTipoMezzo(String tipoMezzo)
    {
        System.out.println();
        this.tipoMezzo.setText("Tipo mezzo: " + tipoMezzo);
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
            pulsanteLinea.setAlignmentX(Component.CENTER_ALIGNMENT);

            pulsanteLinea.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mostraInfoLinea(pulsanteLinea.getText());
                }
            });
            pulsantiLinee.add(pulsanteLinea);
        }
        lineeServite.setText("Linee servite:");
        pannello.revalidate();
        pannello.repaint();
    }

    public void mostraInfoLinea(String id)
    {
        //la ricerca manuale dovrebbe essere fatta solo se !Wifi.WiFi, altrimenti si usano i dati
        //GTFS dinamici. e comunque va ottimizzato qui perché ci mette circa 10 secondi per
        //trovare la prossima linea

        JLabel prossimoArrivo = new JLabel("Prossimo arrivo: ");
        JLabel testoLinea = new JLabel("Linea selezionata: " + id);

        infoLinea.removeAll();
        CustomWaypoint fermata = ElaboratoreFermate.ultimaFermata;

        if (Wifi.WiFi)
        {
            //DynamicGTFS.getVehiclePosition();
            String tempo = DynamicGTFS.getTripUpdate(id, fermata.getId());
            if (tempo.isEmpty()) tempo = "(Nessun orario previsto)";

            prossimoArrivo.setText("Prossimo arrivo: " + tempo);

            infoLinea.add(testoLinea);
            infoLinea.add(prossimoArrivo);
            pannello.scrollRectToVisible(new Rectangle(infoLinea.getBounds()));

            return;
        }

        //infoLinea.removeAll();
        //CustomWaypoint fermata = ElaboratoreFermate.ultimaFermata;

        //JLabel testoLinea = new JLabel("Linea selezionata: " + id);
        //JLabel prossimoArrivo = new JLabel("Prossimo arrivo: ");

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

        if (prossimoStopTime.isPresent())
        {
            StopTime st = prossimoStopTime.get();
            System.out.println(st);
        }

        infoLinea.add(testoLinea);

        pannello.scrollRectToVisible(new Rectangle(infoLinea.getBounds()));
    }

    public void resetPannello()
    {
        System.out.println("reset pannello");
    }
}
