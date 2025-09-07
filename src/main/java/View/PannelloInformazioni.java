package View;

import Controller.*;
import Model.CustomWaypoint;
import Model.Route;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PannelloInformazioni {

    private final JPanel pannello;
    private final JLabel nome;
    private final JLabel lineeServite;
    private final JPanel pulsantiLinee;
    private final JPanel infoLinea;
    private final JLabel statoCorsa;
    private final JLabel ritardoCorsa;
    private final JLabel avvisoPrevisione;
    private final JLabel avvisoTracciamento;
    private final JLabel tipoMezzo;
    private final JLabel testoLinea;
    private final JLabel prossimoArrivo;
    private final JButton tastoPref;
    private static final Color rossoScuro = new Color(143, 51, 51);
    private static final Color rosso = new Color(175, 62, 62);

    public PannelloInformazioni() {
        pannello = new JPanel();
        pannello.setBackground(rossoScuro);
        pannello.setLayout(new BoxLayout(pannello, BoxLayout.Y_AXIS));
        pannello.setAlignmentX(Component.LEFT_ALIGNMENT);
        pannello.setPreferredSize(new Dimension(200, 750));
        pannello.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));
        // Rimuovi la dimensione preferita fissa per permettere lo scroll
        // pannello.setPreferredSize(new Dimension(200, 800));

        nome = new JLabel("Seleziona una fermata.");
        nome.setForeground(Color.WHITE);

        lineeServite = new JLabel("");
        lineeServite.setForeground(Color.WHITE);

        pulsantiLinee = new JPanel();
        pulsantiLinee.setBackground(rossoScuro);
        pulsantiLinee.setLayout(new BoxLayout(pulsantiLinee, BoxLayout.Y_AXIS));
        pulsantiLinee.setAlignmentX(Component.LEFT_ALIGNMENT);
        pulsantiLinee.setPreferredSize(new Dimension(200, 30));

        infoLinea = new JPanel();
        infoLinea.setLayout(new BoxLayout(infoLinea, BoxLayout.Y_AXIS));
        infoLinea.setBackground(rosso);
        infoLinea.setForeground(Color.WHITE);
        infoLinea.setPreferredSize(new Dimension(210, infoLinea.getPreferredSize().height));
        infoLinea.setAlignmentX(Component.LEFT_ALIGNMENT);

        tastoPref = new JButton();
        tastoPref.setVisible(false);
        tastoPref.setPreferredSize(new Dimension(185, 30));
        tastoPref.setMaximumSize(new Dimension(185, tastoPref.getPreferredSize().height));
        tastoPref.setBackground(rosso);
        tastoPref.setForeground(Color.WHITE);
        tastoPref.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        tastoPref.setBorderPainted(true);

        tipoMezzo = new JLabel("");
        tipoMezzo.setForeground(Color.WHITE);

        testoLinea = new JLabel("");
        testoLinea.setForeground(Color.WHITE);

        prossimoArrivo = new JLabel("");
        prossimoArrivo.setForeground(Color.WHITE);

        statoCorsa = new JLabel("");
        statoCorsa.setForeground(Color.WHITE);

        ritardoCorsa = new JLabel("");
        ritardoCorsa.setForeground(Color.WHITE);

        avvisoPrevisione = new JLabel("");
        avvisoPrevisione.setForeground(Color.WHITE);

        avvisoTracciamento = new JLabel("");
        avvisoTracciamento.setForeground(Color.WHITE);

        JLabel fermataSelez = new JLabel("Fermata selezionata:");
        fermataSelez.setForeground(Color.WHITE);

        pannello.add(Box.createVerticalStrut(5));
        pannello.add(fermataSelez);
        pannello.add(nome);
        pannello.add(Box.createVerticalStrut(5));
        pannello.add(tastoPref);
        pannello.add(Box.createVerticalStrut(5));
        pannello.add(lineeServite);
        pannello.add(pulsantiLinee);
        pannello.add(infoLinea);
    }

    public JPanel getPannello() {
        return this.pannello;
    }

    public void impostaFermata(CustomWaypoint fermata) {
        nome.setText(fermata.getNome());
        if (LoginManager.logged) {
            tastoPref.setVisible(true);
            impostaFermataPref(fermata.getNome());
        } else {
            tastoPref.setVisible(false);
        }
    }

    public void impostaFermataPref(String nomeFermata) {
        for (var al : tastoPref.getActionListeners()) {
            tastoPref.removeActionListener(al);
        }

        tastoPref.addActionListener(e -> {
            String user = LoginManager.username;
            boolean presente = FavoritesManager.isFavoriteStopPresent(DatabaseManager.getUserId(user), nomeFermata);

            if (presente) {
                FavoritesManager.removeFavoriteStop(user, nomeFermata);
                tastoPref.setText("Aggiungi ai preferiti");
            } else {
                FavoritesManager.addFavoriteStop(user, nomeFermata);
                tastoPref.setText("Rimuovi dai preferiti");
            }
        });

        // Imposta il testo iniziale del pulsante
        String user = LoginManager.username;
        boolean presente = FavoritesManager.isFavoriteStopPresent(DatabaseManager.getUserId(user), nomeFermata);
        if (presente) {
            tastoPref.setText("Rimuovi dai preferiti");
        } else {
            tastoPref.setText("Aggiungi ai preferiti");
        }
    }

    public void setLineeServite(ArrayList<Route> linee, GestoreInformazioni gestore) {
        pulsantiLinee.removeAll();
        for (Route r : linee) {
            JButton pulsanteLinea = new JButton(StaticGTFS.getNomeRealeMetro(r.id()));
            pulsanteLinea.setAlignmentX(Component.CENTER_ALIGNMENT);
            pulsanteLinea.setHorizontalAlignment(SwingConstants.CENTER);
            pulsanteLinea.setPreferredSize(new Dimension(150, 30));
            pulsanteLinea.setBorderPainted(false);
            pulsanteLinea.setBackground(rossoScuro);
            pulsanteLinea.setForeground(Color.WHITE);
            pulsanteLinea.setMaximumSize(new Dimension(Integer.MAX_VALUE, pulsanteLinea.getPreferredSize().height));
            pulsanteLinea.addActionListener(e -> gestore.mostraInfoLinea(r.id(), false));
            pulsantiLinee.add(pulsanteLinea);
        }
        lineeServite.setText("Linee servite:");
        pannello.revalidate();
        pannello.repaint();
    }

    public void mostraInfoLineaUI(String routeId, boolean daComboBox) {
        infoLinea.removeAll(); // Pulisci il pannello delle informazioni della linea
        infoLinea.add(testoLinea);
        infoLinea.add(tipoMezzo);
        infoLinea.add(prossimoArrivo);
        infoLinea.add(statoCorsa);
        infoLinea.add(ritardoCorsa);
        infoLinea.add(avvisoPrevisione);
        infoLinea.add(avvisoTracciamento);
        infoLinea.revalidate();
        infoLinea.repaint();

        // Rimuovi il pulsante dei mezzi prima di aggiungerlo
        if (GestoreInformazioni.getMostraMezziButton().getParent() != null) {
            GestoreInformazioni.getMostraMezziButton().getParent().remove(GestoreInformazioni.getMostraMezziButton());
        }
        pannello.add(GestoreInformazioni.getMostraMezziButton());
        pannello.revalidate();
        pannello.repaint();
    }

    public void setProssimoArrivo(String tempo) {
        prossimoArrivo.setText(tempo);
    }

    public void setAvvisoPrevisione(String avviso) {
        avvisoPrevisione.setText(avviso);
    }

    public void setStatoCorsa(String stato, String ritardo) {
        statoCorsa.setText("Stato corsa: " + stato);
        ritardoCorsa.setText(ritardo);
    }

    public void setAvvisoTracciamento(String avviso) {
        avvisoTracciamento.setText("<html>" + avviso + "</html>");
    }

    public void updateLineaInfo(String routeId, String tipo, String arrivo) {
        testoLinea.setText("Linea selezionata: " + routeId);
        tipoMezzo.setText("Tipo mezzo: " + tipo);
        prossimoArrivo.setText(arrivo);
    }

    public void resetPannello() {
        nome.setText("Seleziona una fermata.");
        lineeServite.setText("");

        pulsantiLinee.removeAll();
        pulsantiLinee.revalidate();
        pulsantiLinee.repaint();

        infoLinea.removeAll();
        infoLinea.revalidate();
        infoLinea.repaint();

        // Rimuovi anche il pulsante preferiti e quello dei mezzi
        tastoPref.setVisible(false);
        if (GestoreInformazioni.getMostraMezziButton().getParent() != null) {
            pannello.remove(GestoreInformazioni.getMostraMezziButton());
        }

        pannello.revalidate();
        pannello.repaint();
    }
}