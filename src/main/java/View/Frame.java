package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import Controller.StaticGTFS;
import Controller.WiFi;
import Model.*;
import org.jxmapviewer.viewer.GeoPosition;

//TODO: la classe inizia ad essere un po' troppo lunga, quindi più tardi sarebbe meglio scomporre in varie classi il frame

public class Frame extends JFrame{
    public final JFrame frame;
    public final Mappa mappa;
    public ArrayList<CustomWaypoint> listaFermate;

    //private final JComboBox<CustomWaypoint> testoFermata = new JComboBox<>();
    private final FilteredComboBox<CustomWaypoint> testoFermata = new FilteredComboBox<>();

    //private final JComboBox<Route> testoLinea = new JComboBox<>();
    private final FilteredComboBox<Route> testoLinea = new FilteredComboBox<>();

    private final InformazioniFermata pannelloInformazioni;
    private final JLabel testoWiFi;

    private CustomWaypoint ultimaFermata;

    private static final Color verde = new Color(22, 189, 88);
    private static final Color rosso = new Color(191, 63, 24);

    public Frame(int height, int width, String title)
    {
        //Creazione finestra e definizione dimensione e operazione di chiusura
        frame = new JFrame(title);
        frame.setLayout(new BorderLayout());
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        //Casella testo e pulsante per la ricerca delle fermate
        JPanel pannello_sup = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 5));

        ////testoFermata.setEditable(true);
        testoFermata.addActionListener(this::cercaFermata); //imposta actionListener della comboBox (quando viene selezionata un elemento)
        /*testoFermata.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(e);
            }
        });*/
        testoFermata.setRenderer(new StopsComboBoxRenderer());
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
        JScrollPane pannello = new JScrollPane(pannelloInformazioni.getPannello());
        pannello.setPreferredSize(new Dimension(200, Integer.MAX_VALUE));
        pannello.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        pannello.getVerticalScrollBar().setUnitIncrement(6);
        pannello.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pannello.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 0, new Color(143, 51, 51)));
        frame.add(pannello, BorderLayout.WEST);
        //forse?

        testoWiFi = new JLabel("WiFi", SwingConstants.CENTER);
        testoWiFi.setBorder(new EmptyBorder(5, 5, 5, 5));
        testoWiFi.setOpaque(true);
        frame.add(testoWiFi, BorderLayout.SOUTH);

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

        //tasto per accesso alle impostazioni
        ImageIcon settingsIcon = new ImageIcon("assets/settings.png");
        JButton settings = new JButton();
        settings.setIcon(settingsIcon);
        settings.setContentAreaFilled(false);
        settings.setBorder(null);
        settings.setPreferredSize(new Dimension(50, 50));

        //accesso alla pagina delle impostazioni
        settings.addActionListener(new ActionListener()
        {public void actionPerformed(ActionEvent e)
        {
                SettingsPage settingsPage = new SettingsPage();
        }
        });

        pannello_sup.add(profileButton);
        pannello_sup.add(testoLinea);
        pannello_sup.add(testoFermata);
        pannello_sup.add(settings);
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
        testoFermata.filtra("");
        testoFermata.setSelectedItem("- Seleziona una fermata -");
        testoFermata.hidePopup();

        testoLinea.addItem(new Route("null", "- Seleziona una linea -", -1, ""));
        for (Route l : StaticGTFS.routes)
        {
            testoLinea.addItem(l);
        }
        testoLinea.filtra("");
        testoLinea.setSelectedItem("- Seleziona una linea -");
        testoLinea.hidePopup();
    }

    //la ricerca delle fermate è gestita dal frame tramite questo metodo
    private void cercaFermata(ActionEvent e)
    {
        if (testoFermata.getSelectedItem() == null || testoFermata.getSelectedIndex() == -1) return; //magari con codice di errore
        String nomeFermata = testoFermata.getSelectedItem().toString();

        //la fermata precedentemente selezionata (se è presente) non serve più
        if (GestoreWaypoint.ultimaFermata != null)
            GestoreWaypoint.ultimaFermata.deseleziona();

        //cerca la fermata dentro la lista fermate;
        for (CustomWaypoint f : this.listaFermate)
        {
            if (f.getNome().equals(nomeFermata)) //fermata trovata
            {
                mappa.cambia_posizione(f.getLatitudine(), f.getLongitudine());
                GestoreWaypoint.ultimaFermata = f;
                f.seleziona();
                //mostraInformazioni(f);
                break;
            }
        }
    }

    private void cercaLinea(ActionEvent e)
    {
        if (testoLinea.getSelectedItem() == null || testoLinea.getSelectedIndex() == -1) return;
        //System.out.println(testoLinea.getSelectedItem());
        String nomeLinea = testoLinea.getSelectedItem().toString();

        //provvisorio!!!! (PARTE DA QUI)
        /*
        ArrayList<Trip> viaggi = StaticGTFS.trips.stream()
                .filter(trip -> trip.getRouteId().equals(nomeLinea)).collect(Collectors.toCollection(ArrayList::new));

        if (viaggi.isEmpty()) return;

        Trip viaggioSelezionato = viaggi.getFirst();*/

        /*ArrayList<GeoPosition> percorso = StaticGTFS.stopTimes.stream()
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

        /*List<GeoPosition> percorso = StaticGTFS.shapes.stream()
                .filter(sp -> sp.getId().equals(viaggioSelezionato.getShapeId()))
                .sorted(Comparator.comparingInt(PuntoShape::getSequenza))
                .map(sp -> new GeoPosition(sp.getLatitudine(), sp.getLongitudine()))
                .collect(Collectors.toList());*/

        List<GeoPosition> percorso = StaticGTFS.getPercorso(nomeLinea);
        Mappa.disegnaLinea(percorso);
        Mappa.getMapViewer().zoomToBestFit(new HashSet<>(percorso), 0.7);
        //Questo controllo è molto sbarazzino
        if (!nomeLinea.equals("- Seleziona una linea -"))
        {
            //TODO: invocare altri metodi (non so quali) [CONTINUA DA QUI!!!]
            pannelloInformazioni.mostraInfoLinea(nomeLinea, true);
        }
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

    public void cambiaStatoWiFi()
    {
        if (WiFi.wifi_connesso()) {
            testoWiFi.setText("WiFi connesso");
            testoWiFi.setBackground(verde);
        } else {
            testoWiFi.setText("WiFi non connesso");
            testoWiFi.setBackground(rosso);
        }

        testoWiFi.repaint();
    }

    public JFrame getFrame()
    {
        return frame;
    }

    public Mappa getMappa()
    {
        return mappa;
    }

    public InformazioniFermata getPannelloInformazioni()
    {
        return this.pannelloInformazioni;
    }

    public void mostraAvviso(String testo)
    {
        JDialog avviso = new JDialog(frame, testo, true);
    }
}
