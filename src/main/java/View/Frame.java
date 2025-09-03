package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import Controller.StaticGTFS;
import Controller.WiFi;
import Model.*;
import org.jxmapviewer.viewer.GeoPosition;

//TODO: la classe inizia ad essere un po' troppo lunga, quindi più tardi sarebbe meglio scomporre in varie classi il frame

public class Frame extends JFrame{
    private final JFrame frame;
    private final Mappa mappa;

    private final FilteredComboBox<CustomWaypoint> testoFermata = new FilteredComboBox<>();
    private final FilteredComboBox<Route> testoLinea = new FilteredComboBox<>();

    private final InformazioniFermata pannelloInformazioni;
    private final JLabel testoWiFi;

    private boolean finestraAvvisoAperta = false;

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
        JPanel pannelloSuperiore = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 5));

        testoFermata.setEditable(true);
        testoFermata.addActionListener(this::cercaFermata); //imposta actionListener della comboBox (quando viene selezionata un elemento)
        testoFermata.setRenderer(new StopsComboBoxRenderer());
        testoFermata.setMaximumRowCount(5);

        testoLinea.setEditable(true);
        testoLinea.addActionListener(this::cercaLinea);
        testoLinea.setRenderer(new RoutesComboBoxRenderer());
        testoLinea.setMaximumRowCount(5);

        mappa = new Mappa(frame);

        //Pannello informazioni laterale per le fermate
        pannelloInformazioni = new InformazioniFermata(this);
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
        profileButton.addActionListener(e -> new LoginPage());

        //tasto per accesso alle impostazioni
        ImageIcon settingsIcon = new ImageIcon("assets/settings.png");
        JButton settings = new JButton();
        settings.setIcon(settingsIcon);
        settings.setContentAreaFilled(false);
        settings.setBorder(null);
        settings.setPreferredSize(new Dimension(50, 50));

        //accesso alla pagina delle impostazioni
        settings.addActionListener(e -> new SettingsPage());

        pannelloSuperiore.add(profileButton);
        pannelloSuperiore.add(testoLinea);
        pannelloSuperiore.add(testoFermata);
        pannelloSuperiore.add(settings);
        pannelloSuperiore.setBackground(new Color(175, 62, 62));

        frame.add(pannelloSuperiore, BorderLayout.PAGE_START);
        frame.setVisible(true);
    }

    //Fa esattamente quello che sembra
    public void impostaComboBox()
    {
        for (CustomWaypoint f : StaticGTFS.stops)
        {
            testoFermata.addItem(f);
        }
        testoFermata.filtra("");
        testoFermata.setSelectedItem("- Seleziona una fermata -");
        testoFermata.hidePopup();

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
        if (testoFermata.getSelectedItem() == null) return; //magari con codice di errore
        String nomeFermata = testoFermata.getSelectedItem().toString();

        //la fermata precedentemente selezionata (se è presente) non serve più
        if (GestoreWaypoint.ultimaFermata != null)
            GestoreWaypoint.ultimaFermata.deseleziona();

        //cerca la fermata dentro la lista fermate;
        for (CustomWaypoint f : StaticGTFS.stops)
        {
            if (f.getNome().equals(nomeFermata)) //fermata trovata
            {
                mappa.impostaPosizione(f.getLatitudine(), f.getLongitudine());
                GestoreWaypoint.ultimaFermata = f;
                f.seleziona();
                //mostraInformazioni(f);
                break;
            }
        }
    }

    private void cercaLinea(ActionEvent e)
    {
        if (testoLinea.getSelectedItem() == null) return;
        String nomeLinea = testoLinea.getSelectedItem().toString();

        pannelloInformazioni.resetPannello();

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

    public void cambiaStatoWiFi()
    {
        if (WiFi.connesso())
        {
            testoWiFi.setText("WiFi connesso");
            testoWiFi.setBackground(verde);
        }
        else
        {
            testoWiFi.setText("WiFi non connesso");
            testoWiFi.setBackground(rosso);
        }

        testoWiFi.repaint();
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
        if (finestraAvvisoAperta) return;

        JDialog avviso = new JDialog(frame, "Problema sulla linea!", false);
        avviso.setSize(600, 200);
        avviso.setLocationRelativeTo(frame);
        avviso.add(new JLabel(testo));
        avviso.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) { finestraAvvisoAperta = true; }

            @Override
            public void windowClosing(WindowEvent e) { finestraAvvisoAperta = false; }

            @Override
            public void windowClosed(WindowEvent e) { finestraAvvisoAperta = false; }

            @Override
            public void windowIconified(WindowEvent e) {}

            @Override
            public void windowDeiconified(WindowEvent e) {}

            @Override
            public void windowActivated(WindowEvent e) {}

            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        avviso.setVisible(true);
    }
}
