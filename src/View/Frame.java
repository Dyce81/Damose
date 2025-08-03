package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import Controller.ReaderStaticGTFS;
import Model.*;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

//TODO: la classe inizia ad essere un po' troppo lunga, quindi più tardi sarebbe meglio scomporre in varie classi il frame

public class Frame {
    public final JFrame frame;
    public final Mappa mappa;
    public ArrayList<CustomWaypoint> listaFermate;

    private final JComboBox<CustomWaypoint> testoFermata = new JComboBox<>();
    private final JComboBox<Route> testoLinea = new JComboBox<>();
    //private final JComboBox<String> testoLinea = new JComboBox<>();
    private final InformazioniFermata pannelloInformazioni;

    private CustomWaypoint ultimaFermata;

    public Frame(int height, int width, String title)
    {
        //Creazione finestra e definizione dimensione e operazione di chiusura
        frame = new JFrame(title);
        frame.setLayout(new BorderLayout());
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        //Casella testo e pulsante per la ricerca delle fermate
        JPanel pannello_sup = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 5));

        testoFermata.setEditable(true);
        testoFermata.addActionListener(this::cercaFermata); //imposta actionListener della comboBox (quando viene selezionata un elemento)
        testoFermata.setRenderer(new ComboBoxRenderer());
        testoFermata.setMaximumRowCount(5);

        testoLinea.setEditable(true);
        testoLinea.addActionListener(this::cercaLinea);
        testoLinea.setRenderer(new RoutesComboBoxRenderer());
        testoLinea.setMaximumRowCount(5);

        /*testoFermata.setRenderer(new ListCellRenderer<String>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel testo = new JLabel(value);
                //testo.setIcon(value.getIcona());

                if (isSelected)
                {
                    testo.setBackground(list.getSelectionBackground());
                } else {
                    testo.setBackground(list.getBackground());
                }

                return testo;
            }
        });*/

        mappa = new Mappa(frame);

        //Pannello informazioni laterale per le fermate
        pannelloInformazioni = new InformazioniFermata();
        frame.add(pannelloInformazioni.getPannello(), BorderLayout.WEST);

        //tasto per l'accesso alla pagina di login
        ImageIcon profileIcon = new ImageIcon("assets/profile-logo.png");
        JButton profileButton = new JButton();
        profileButton.setIcon(profileIcon);
        profileButton.setContentAreaFilled(false);
        profileButton.setBorder(null);
        profileButton.setPreferredSize(new Dimension(50, 50));

        //accesso alla pagina di login
        profileButton.addActionListener(new ActionListener()
        {public void actionPerformed(ActionEvent e)
            {
                LoginPage loginPage = new LoginPage();
            }
        });

        pannello_sup.add(testoLinea);
        pannello_sup.add(testoFermata);
        pannello_sup.add(profileButton);
        pannello_sup.setBackground(new Color(175, 62, 62));

        frame.add(pannello_sup, BorderLayout.PAGE_START);
        frame.setVisible(true);
    }

    //questa funzione riempie la combo box con i nomi delle fermate
    /*public void imposta_combo_box(ArrayList<String> nomi)
    {
        /*testoFermata.setRenderer(new DefaultListCellRenderer()
        {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
            {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Icon icona = new Icon()
            }
        });

        for (String s : nomi)
            testoFermata.addItem(s);
    }*/

    //Fa esattamente quello che sembra
    public void imposta_combo_box()
    {
        testoFermata.addItem(new CustomWaypoint("null", "- Seleziona una fermata -", new GeoPosition(0, 0)));
        for (CustomWaypoint f : listaFermate)
        {
            testoFermata.addItem(f);
        }

        testoLinea.addItem(new Route("null", "- Seleziona una linea -", -1, ""));
        for (Route l : ReaderStaticGTFS.routes)
        {
            testoLinea.addItem(l);
        }
    }

    //la ricerca delle fermate è gestita dal frame tramite questo metodo
    private void cercaFermata(ActionEvent e)
    {
        if (testoFermata.getSelectedItem() == null) return; //magari con codice di errore
        String nomeFermata = testoFermata.getSelectedItem().toString();

        //la fermata precedentemente selezionata (se è presente) non serve più
        if (ultimaFermata != null)
            ultimaFermata.deseleziona();

        //cerca la fermata dentro la lista fermate;
        for (CustomWaypoint f : this.listaFermate)
        {
            if (f.getNome().equals(nomeFermata)) //fermata trovata
            {
                mappa.cambia_posizione(f.getLatitudine(), f.getLongitudine());
                ultimaFermata = f;
                f.seleziona();
                //mostraInformazioni(f);
                break;
            }
        }
    }

    private void cercaLinea(ActionEvent e)
    {
        if (testoLinea.getSelectedItem() == null) return;
        //System.out.println(testoLinea.getSelectedItem());
        String nomeLinea = testoLinea.getSelectedItem().toString();

        //provvisorio!!!!
        ArrayList<Trip> viaggi = ReaderStaticGTFS.trips.stream()
                .filter(trip -> trip.getRouteId().equals(nomeLinea)).collect(Collectors.toCollection(ArrayList::new));

        if (viaggi.isEmpty()) return;

        Trip viaggioSelezionato = viaggi.getFirst();

        /*ArrayList<GeoPosition> percorso = ReaderStaticGTFS.stopTimes.stream()
                .filter(st -> st.getTripId().equals(viaggioSelezionato.getId()))
                .sorted(Comparator.comparingInt(StopTime::getStopSequenza))
                .map(st -> {
                    CustomWaypoint f = listaFermate.stream()
                            .filter(s -> s.getId().equals(st.getStopId()))
                            .findFirst()
                            .orElse(null);
                    return f != null ? new GeoPosition(f.getLatitudine(), f.getLongitudine()) : null;
                })
                .filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));*/

        System.out.println(viaggioSelezionato.getShapeId());

        List<GeoPosition> percorso = ReaderStaticGTFS.shapes.stream()
                .filter(sp -> sp.getId().equals(viaggioSelezionato.getShapeId()))
                .sorted(Comparator.comparingInt(PuntoShape::getSequenza))
                .map(sp -> new GeoPosition(sp.getLatitudine(), sp.getLongitudine()))
                .collect(Collectors.toList());

        System.out.println(percorso);

        RoutePainter routePainter = new RoutePainter(percorso);

        List<Painter<JXMapViewer>> painters = new ArrayList<>();
        painters.add(mappa.painter);
        painters.add(routePainter);
        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
        mappa.set_painter(painter);
    }

    //Questo metodo mostra le informazioni della fermata selezionata (al lato della finestra?)
    /*public void mostraInformazioni(CustomWaypoint fermata)
    {
        pannelloInformazioni.setNome(fermata.getNome());

        //PROVA!!!!
        ArrayList<String> tripsIds = new ArrayList<>();
        /*for (Map<String, String> orario : orari)
        {
            if (orario.get(""));
        }
    }*/

    public InformazioniFermata getPannelloInformazioni()
    {
        return this.pannelloInformazioni;
    }
}
