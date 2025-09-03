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
        panel.setBounds(68, 20, 450, 210);

        if (LoginManager.logged) {creaIfLogged(panel);}
        else {creaIfNotLogged(panel);}

        page.add(panel);
        page.setVisible(true);
    }

    public void creaIfLogged(JPanel panel)
    {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(450, Integer.MAX_VALUE));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setLayout(new BoxLayout(scrollPane, BoxLayout.Y_AXIS));
        List<String> fermate = FavoritesManager.getFavoriteStops(LoginManager.username);
        List<String> linee = FavoritesManager.getFavoriteLines(LoginManager.username);
    }

    public void creaIfNotLogged(JPanel panel)
    {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

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


}

