package View;

import Controller.FavoritesManager;
import Controller.LoginManager;
import Model.Page;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FavoritesPage extends Page
{
    public FavoritesPage()
    {
        page = new JDialog();
        page.setSize(450, 280);
        page.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        page.setLocationRelativeTo(null);
        page.setModal(true);
        page.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBounds(0, 10, 450, 270);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        if (LoginManager.logged) {creaIfLogged(panel);}
        else {creaIfNotLogged(panel);}

        page.add(panel);
        page.setVisible(true);
    }

    public void creaIfLogged(JPanel panel)
    {
        JPanel scrollPanel = new JPanel();
        scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(scrollPanel);
        scrollPane.setPreferredSize(new Dimension(420, Integer.MAX_VALUE));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JButton lineeFermate = new JButton("Mostra Linee");
        lineeFermate.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lineeFermate);
        lineeFermate.addActionListener(e -> {
            scrollPanel.removeAll();
            scrollPanel.repaint();
            if (lineeFermate.getText().equals("Mostra Linee"))
            {
                List<String> linee = FavoritesManager.getFavoriteLines(LoginManager.username);
                lineeFermate.setText("Mostra Fermate");
                disegnaPreferiti(linee, scrollPanel, false);


            }
            else if (lineeFermate.getText().equals("Mostra Fermate"))
            {
                lineeFermate.setText("Mostra Linee");
                List<String> fermate = FavoritesManager.getFavoriteStops(LoginManager.username);
                disegnaPreferiti(fermate, scrollPanel, true);
            }
        });

        panel.add(scrollPane);

    }

    public void creaIfNotLogged(JPanel panel)
    {
        panel.add(Box.createVerticalStrut(50));

        JLabel loggati = new JLabel("DEVI EFFETTUARE IL LOGIN");
        loggati.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel loggati2 = new JLabel("PER VEDERE I TUI PREFERITI");
        loggati2.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(loggati);
        panel.add(loggati2);

        panel.add(Box.createVerticalStrut(20));

        JButton apriLogin = new JButton("APRI PAGINA LOGIN");
        apriLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(apriLogin);
        apriLogin.addActionListener(e -> {
            chiudiPagina(this.page, false, 0);
            new LoginPage();
        });

    }

    void disegnaPreferiti(List<String> preferiti, JPanel scrollPanel, boolean fermata)
    {
        for (String linea : preferiti)
        {
            JPanel miniPanel = new JPanel();
            miniPanel.setPreferredSize(new Dimension(420, 55));
            miniPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
            miniPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            JButton rimuovi = new JButton("");
            rimuovi.setPreferredSize(new Dimension(50, 50));
            rimuovi.setIcon(new ImageIcon("assets/favorite2.png"));
            miniPanel.add(rimuovi);
            rimuovi.addActionListener(e2 -> {
                rimuovi.setIcon(new ImageIcon("assets/favorite.png"));
                if (fermata) {FavoritesManager.removeFavoriteStop(LoginManager.username, linea);}
                else {FavoritesManager.removeFavoriteLine(LoginManager.username, linea);}
            });
            miniPanel.add(new JLabel(linea.toUpperCase()));

            scrollPanel.add(miniPanel);
        }
    }
}

