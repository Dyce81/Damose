package View;

import Controller.FavoritesManager;
import Controller.GestoreInformazioni;
import Controller.LoginManager;
import Controller.StaticGTFS;
import Model.CustomWaypoint;
import Model.Page;
import Model.Route;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;

public class FavoritesPage extends Page
{
    private final GestoreInformazioni gestoreInformazioni;

    public FavoritesPage(GestoreInformazioni gestoreInform)
    {
        gestoreInformazioni = gestoreInform;
        page = new JDialog();
        page.setSize(450, 280);
        page.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        page.setLocationRelativeTo(null);
        page.setModal(true);
        page.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBackground(rossoscuro);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        if (LoginManager.logged) {creaIfLogged(panel);}
        else {creaIfNotLogged(panel);}

        page.add(panel);
        page.setVisible(true);
    }

    public void creaIfLogged(JPanel panel)
    {
        panel.setBounds(0, 0, 450, 240);
        page.setBackground(rossoscuro);

        JPanel scrollPanel = new JPanel();
        scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(scrollPanel);
        scrollPane.setPreferredSize(new Dimension(420, scrollPanel.getPreferredSize().height));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JButton lineeFermate = new JButton("Mostra Linee");
        lineeFermate.setPreferredSize(new Dimension(120, 30));
        lineeFermate.setMaximumSize(new Dimension(120, 30));
        impostaButton(lineeFermate);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lineeFermate);
        panel.add(Box.createVerticalStrut(5));

        disegnaPreferiti(FavoritesManager.getFavoriteStops(LoginManager.username), scrollPanel, true, gestoreInformazioni);

        lineeFermate.addActionListener(e -> {
            scrollPanel.removeAll();
            List<String> linee = FavoritesManager.getFavoriteLines(LoginManager.username);
            List<String> fermate = FavoritesManager.getFavoriteStops(LoginManager.username);
            if (lineeFermate.getText().equals("Mostra Linee"))
            {
                lineeFermate.setText("Mostra Fermate");
                disegnaPreferiti(linee, scrollPanel, false, gestoreInformazioni);
            }
            else if (lineeFermate.getText().equals("Mostra Fermate"))
            {
                lineeFermate.setText("Mostra Linee");
                disegnaPreferiti(fermate, scrollPanel, true, gestoreInformazioni);
            }
            scrollPanel.repaint();
        });

        panel.add(scrollPane);

    }

    public void creaIfNotLogged(JPanel panel)
    {
        panel.setBounds(0, 0, 450, 280);
        panel.add(Box.createVerticalStrut(75));

        JLabel loggati = new JLabel("DEVI EFFETTUARE IL LOGIN");
        loggati.setForeground(Color.WHITE);
        loggati.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel loggati2 = new JLabel("PER VEDERE I TUI PREFERITI");
        loggati2.setForeground(Color.WHITE);
        loggati2.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(loggati);
        panel.add(loggati2);

        panel.add(Box.createVerticalStrut(20));

        JButton apriLogin = new JButton("APRI PAGINA LOGIN");
        apriLogin.setPreferredSize(new Dimension(125, 30));
        apriLogin.setMaximumSize(new Dimension(125, 30));
        impostaButton(apriLogin);
        panel.add(apriLogin);
        apriLogin.addActionListener(e -> {
            chiudiPagina(this.page, false, 0);
            new LoginPage();
        });

    }

    void disegnaPreferiti(List<String> preferiti, JPanel scrollPanel, boolean fermata, GestoreInformazioni gestoreInform)
    {
        if (preferiti.isEmpty())
        {
            scrollPanel.setBackground(rossoscuro);
        }
        for (String preferito : preferiti)
        {
            JPanel miniPanel = new JPanel();
            miniPanel.setPreferredSize(new Dimension(420, 55));
            miniPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
            miniPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            miniPanel.setBackground(rossoscuro);
            JButton vedi = new JButton();
            vedi.setPreferredSize(new Dimension(50, 50));
            vedi.setIcon((new ImageIcon("assets/fermata-selezionata50x50.png")));
            impostaButton(vedi);
            miniPanel.add(vedi);
            vedi.addActionListener(e -> {
                gestoreInform.deselezionaFermata();
                gestoreInform.deselezionaLinea();
                if (fermata)
                {
                    chiudiPagina(page, false, 100);
                    String idFermataSelez = StaticGTFS.getIdFermata(preferito);
                    CustomWaypoint fermataSelez = StaticGTFS.getFermata(idFermataSelez);
                    gestoreInformazioni.selezionaFermata(fermataSelez);
                    if (fermataSelez != null) {
                        Mappa.impostaPosizione(fermataSelez.getLatitudine(), fermataSelez.getLongitudine());
                    }
                }
                else {
                    chiudiPagina(page, false, 100);
                    List<GeoPosition> percorso = StaticGTFS.getPercorso(preferito);
                    Route lineaSelez = StaticGTFS.getLinea(preferito);
                    Mappa.disegnaLinea(percorso);
                    Mappa.getMapViewer().zoomToBestFit(new HashSet<>(percorso), 0.7);
                    if (lineaSelez != null) {
                        gestoreInformazioni.mostraInfoLinea(lineaSelez.id(), false);
                    }
                }
            });
            JButton rimuovi = new JButton();
            rimuovi.setPreferredSize(new Dimension(50, 50));
            rimuovi.setIcon(new ImageIcon("assets/favorite2.png"));
            impostaButton(rimuovi);
            miniPanel.add(rimuovi);
            rimuovi.addActionListener(e2 -> {
                rimuovi.setIcon(new ImageIcon("assets/favorite.png"));
                if (fermata) {FavoritesManager.removeFavoriteStop(LoginManager.username, preferito);}
                else {FavoritesManager.removeFavoriteLine(LoginManager.username, preferito);}
            });
            JLabel testoPref = new JLabel();
            if (fermata)  {testoPref.setText(preferito.toUpperCase());}
            else {testoPref.setText("LINEA " + preferito.toUpperCase());}
            testoPref.setForeground(Color.WHITE);
            miniPanel.add(testoPref);

            scrollPanel.add(miniPanel);
        }
    }
}

