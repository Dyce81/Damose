package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import Controller.GestoreInformazioni;
import Controller.StaticGTFS;
import Controller.WiFi;
import Model.*;
import org.jxmapviewer.viewer.GeoPosition;

public class Frame extends JFrame {
    private final JFrame frame;
    private final Mappa mappa;

    private final FilteredComboBox<CustomWaypoint> testoFermata = new FilteredComboBox<>();
    private final FilteredComboBox<Route> testoLinea = new FilteredComboBox<>();

    private final PannelloInformazioni pannelloInformazioni;
    private GestoreInformazioni gestoreInformazioni;
    private final JLabel testoWiFi;

    private boolean finestraAvvisoAperta = false;

    public static final Color verde = new Color(22, 189, 88);
    public static final Color rosso = new Color(191, 63, 24);

    public Frame(int height, int width, String title) {
        //Creazione finestra e definizione dimensione e operazione di chiusura
        frame = new JFrame(title);
        frame.setLayout(new BorderLayout());
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        //Casella testo e pulsante per la ricerca delle fermate
        JPanel pannelloSuperiore = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 5));

        testoFermata.addActionListener(e -> {
            if (e.getModifiers() > 0)
                cercaFermata();
            // Se si modifica la JComboBox con la tastiera (digitando qualcosa), i Modifiers saranno
            // sempre pari a 0.
        });
        testoFermata.setRenderer(new StopsComboBoxRenderer());
        testoFermata.setMaximumRowCount(5);

        testoLinea.addActionListener(e -> {
            if (e.getModifiers() > 0)
                cercaLinea();
        });
        testoLinea.setRenderer(new RoutesComboBoxRenderer());
        testoLinea.setMaximumRowCount(5);

        mappa = new Mappa(frame);

        //Pannello informazioni laterale per le fermate
        pannelloInformazioni = new PannelloInformazioni();
        JScrollPane scrollPane = new JScrollPane(pannelloInformazioni.getPannello());
        scrollPane.setPreferredSize(new Dimension(210, Integer.MAX_VALUE));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(6);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(143, 51, 51)));
        frame.add(scrollPane, BorderLayout.WEST);

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
        ImageIcon favoritesIcon = new ImageIcon("assets/favorite.png");
        JButton favorites = new JButton();
        favorites.setIcon(favoritesIcon);
        favorites.setContentAreaFilled(false);
        favorites.setBorder(null);
        favorites.setPreferredSize(new Dimension(50, 50));

        //accesso alla pagina delle impostazioni
        favorites.addActionListener(e -> new FavoritesPage(gestoreInformazioni));

        pannelloSuperiore.add(profileButton);
        pannelloSuperiore.add(testoLinea);
        pannelloSuperiore.add(testoFermata);
        pannelloSuperiore.add(favorites);
        pannelloSuperiore.setBackground(new Color(175, 62, 62));

        frame.add(pannelloSuperiore, BorderLayout.PAGE_START);
        frame.setVisible(true);
    }

    //Fa esattamente quello che sembra
    public void impostaComboBox() {
        testoFermata.setPreferredSize(new Dimension(330, 40));
        testoLinea.setPreferredSize(new Dimension(160, 40));

        for (CustomWaypoint f : StaticGTFS.stops)
            testoFermata.addItem(f);

        testoFermata.filtra("");
        testoFermata.setSelectedItem("Seleziona una fermata");
        testoFermata.hidePopup();

        JTextField editor = (JTextField) testoFermata.getEditor().getEditorComponent();
        editor.setHorizontalAlignment(SwingConstants.CENTER);
        JTextComponent editor1 = (JTextComponent) testoFermata.getEditor().getEditorComponent();
        editor1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                editor.selectAll();}

            @Override
            public void focusLost(FocusEvent e) {
                editor.setText("Seleziona una fermata");
            }
        });

        for (Route l : StaticGTFS.routes)
            testoLinea.addItem(l);

        testoLinea.filtra("");
        testoLinea.setSelectedItem("Seleziona una linea");
        testoLinea.hidePopup();

        /*JTextField editor3 = (JTextField) testoLinea.getEditor().getEditorComponent();
        editor3.setHorizontalAlignment(SwingConstants.CENTER);
        JTextComponent editor2 = (JTextComponent) testoLinea.getEditor().getEditorComponent();
        editor2.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                editor2.selectAll();}
            @Override
            public void focusLost(FocusEvent e) {
                editor.setText("Seleziona una linea");
            }
        });*/
    }

    private void cercaFermata() {
        if (testoFermata.getSelectedItem() == null) return;
        String nomeFermata = testoFermata.getSelectedItem().toString();

        // La fermata precedentemente selezionata (se è presente) non serve più
        if (gestoreInformazioni.getUltimaFermata() != null)
            gestoreInformazioni.getUltimaFermata().deseleziona();

        // Cerca la fermata dentro la lista fermate (StaticGTFS.stops)
        for (CustomWaypoint f : StaticGTFS.stops) {
            if (f.getNome().equals(nomeFermata)) //fermata trovata
            {
                Mappa.impostaPosizione(f.getLatitudine(), f.getLongitudine());
                gestoreInformazioni.deselezionaLinea();
                gestoreInformazioni.resetGestore();
                gestoreInformazioni.selezionaFermata(f);
                f.seleziona();
                break;
            }
        }
    }

    private void cercaLinea() {
        if (testoLinea.getSelectedItem() == null) return;
        String nomeLinea = testoLinea.getSelectedItem().toString();

        pannelloInformazioni.resetPannello();

        List<GeoPosition> percorso = StaticGTFS.getPercorso(nomeLinea);
        gestoreInformazioni.deselezionaFermata();
        Route linea = StaticGTFS.getLinea(nomeLinea);
        if (linea != null) {
            gestoreInformazioni.mostraInfoLinea(linea.id(), true);
        }
        Mappa.disegnaLinea(percorso);
        Mappa.getMapViewer().zoomToBestFit(new HashSet<>(percorso), 0.7);

        //Questo controllo è molto sbarazzino
        if (!nomeLinea.equals("- Seleziona una linea -")) {
            //TODO: invocare altri metodi (non so quali) [CONTINUA DA QUI!!!]
            pannelloInformazioni.mostraInfoLineaUI(nomeLinea, true);
        }
    }

    public void cambiaStatoWiFi() {
        if (WiFi.connesso()) {
            testoWiFi.setText("WiFi connesso");
            testoWiFi.setBackground(verde);
        } else {
            testoWiFi.setText("WiFi non connesso");
            testoWiFi.setBackground(rosso);
        }

        testoWiFi.repaint();
    }

    public Mappa getMappa() {
        return mappa;
    }

    public PannelloInformazioni getPannelloInformazioni() {
        return this.pannelloInformazioni;
    }

    public void setGestoreInformazioni(GestoreInformazioni gestore) {
        this.gestoreInformazioni = gestore;
    }

    public void mostraAvviso(String testo)
    {
        if (finestraAvvisoAperta)
        {
            return;
        }
        if (testo == null || testo.isEmpty())
        {
            return;
        }

        JDialog avviso = new JDialog(frame, "Avviso", false);
        avviso.setLayout(new BorderLayout());

        JPanel pannelloAvviso = new JPanel();
        pannelloAvviso.setLayout(new BoxLayout(pannelloAvviso, BoxLayout.Y_AXIS));
        pannelloAvviso.setBackground(new Color(143, 51, 51));
        pannelloAvviso.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel testoAvviso = new JLabel("<html><p style='width: 400px; text-align: center;'>" + testo + "</p></html>");
        testoAvviso.setForeground(Color.WHITE);
        testoAvviso.setAlignmentX(Component.CENTER_ALIGNMENT);

        pannelloAvviso.add(Box.createVerticalGlue());
        pannelloAvviso.add(testoAvviso);
        pannelloAvviso.add(Box.createVerticalGlue());

        JButton chiudiButton = new JButton("Chiudi");
        chiudiButton.setPreferredSize(new Dimension(75, 25));
        chiudiButton.setBackground(new Color(175, 62, 62));
        chiudiButton.setForeground(Color.WHITE);
        chiudiButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        chiudiButton.setBorderPainted(true);
        chiudiButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(143, 51, 51));
        buttonPanel.add(chiudiButton);

        avviso.add(pannelloAvviso, BorderLayout.CENTER);
        avviso.add(buttonPanel, BorderLayout.SOUTH);

        avviso.pack();
        avviso.setMinimumSize(new Dimension(350, 200));

        avviso.setLocationRelativeTo(frame);

        chiudiButton.addActionListener(e -> avviso.dispose());

        avviso.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                finestraAvvisoAperta = true;
            }

            @Override
            public void windowClosing(WindowEvent e) {
                finestraAvvisoAperta = false;
            }

            @Override
            public void windowClosed(WindowEvent e) {
                finestraAvvisoAperta = false;
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        avviso.setVisible(true);
    }
}