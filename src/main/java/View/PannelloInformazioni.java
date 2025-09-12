package View;

import Controller.*;
import Model.CustomWaypoint;
import Model.Route;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Questa classe popola il pannello laterale delle informazioni.
 */
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
    private final JButton tastoFermataPref;
    private final JButton tastoLineaPref;
    private final JButton mostraMezzi;
    private static final Color rossoScuro = new Color(143, 51, 51);
    private static final Color rosso = new Color(175, 62, 62);

    /**
     * Costruttore.
     */
    public PannelloInformazioni() {
        pannello = new JPanel();
        pannello.setBackground(rossoScuro);
        pannello.setLayout(new BoxLayout(pannello, BoxLayout.Y_AXIS));
        pannello.setAlignmentX(Component.LEFT_ALIGNMENT);
        pannello.setPreferredSize(new Dimension(200, 750));
        pannello.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));

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
        infoLinea.setPreferredSize(new Dimension(pannello.getWidth(), infoLinea.getPreferredSize().height));
        infoLinea.setAlignmentX(Component.LEFT_ALIGNMENT);

        tastoFermataPref = new JButton();
        tastoFermataPref.setVisible(false);
        tastoFermataPref.setPreferredSize(new Dimension(185, 30));
        tastoFermataPref.setMaximumSize(new Dimension(185, tastoFermataPref.getPreferredSize().height));
        tastoFermataPref.setBackground(rosso);
        tastoFermataPref.setForeground(Color.WHITE);
        tastoFermataPref.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        tastoFermataPref.setBorderPainted(true);

        tastoLineaPref = new JButton();
        tastoLineaPref.setVisible(false);
        tastoLineaPref.setPreferredSize(new Dimension(185, 30));
        tastoLineaPref.setMaximumSize(new Dimension(185, tastoFermataPref.getPreferredSize().height));
        tastoLineaPref.setBackground(rosso);
        tastoLineaPref.setForeground(Color.WHITE);
        tastoLineaPref.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        tastoLineaPref.setBorderPainted(true);

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
        pannello.add(tastoFermataPref);
        pannello.add(Box.createVerticalStrut(5));
        pannello.add(lineeServite);
        pannello.add(pulsantiLinee);
        pannello.add(infoLinea);

        mostraMezzi = new JButton("  Mostra mezzi sulla linea  ");
        mostraMezzi.setPreferredSize(new Dimension(185, 30));
        mostraMezzi.setMaximumSize(new Dimension(185, mostraMezzi.getPreferredSize().height));
        mostraMezzi.setBackground(new Color(175, 62, 62));
        mostraMezzi.setForeground(Color.WHITE);
        mostraMezzi.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        mostraMezzi.setBorderPainted(true);
    }

    JPanel getPannello() {
        return this.pannello;
    }

    /**
     * Imposta la fermata visualizzata nel pannello.
     */
    public void impostaFermata(CustomWaypoint fermata) {
        nome.setText(fermata.getNome());
        if (LoginManager.logged) {
            tastoFermataPref.setVisible(true);
            impostaFermataPref(fermata.getNome());
        } else {
            tastoFermataPref.setVisible(false);
        }
    }

    private void impostaFermataPref(String nomeFermata) {
        for (var al : tastoFermataPref.getActionListeners()) {
            tastoFermataPref.removeActionListener(al);
        }
        String user = LoginManager.username;
        boolean presente = FavoritesManager.isFavoriteStopPresent(DatabaseManager.getUserId(user), nomeFermata);
        tastoFermataPref.addActionListener(e -> {
            if (presente) {
                FavoritesManager.removeFavoriteStop(user, nomeFermata);
                tastoFermataPref.setText("Aggiungi ai preferiti");
            } else {
                FavoritesManager.addFavoriteStop(user, nomeFermata);
                tastoFermataPref.setText("Rimuovi dai preferiti");
            }
        });
        if (presente) {
            tastoFermataPref.setText("Rimuovi dai preferiti");
        } else {
            tastoFermataPref.setText("Aggiungi ai preferiti");
        }
    }

    private void impostaLineaPref(String nomeLinea)
    {
        for (var al : tastoLineaPref.getActionListeners()) {
            tastoLineaPref.removeActionListener(al);
        }
        String user = LoginManager.username;
        boolean presente = FavoritesManager.isFavoriteLinePresent(DatabaseManager.getUserId(user), testoLinea.getText());
        tastoLineaPref.addActionListener(e -> {
            if (presente) {
                FavoritesManager.removeFavoriteLine(user, nomeLinea);
                tastoLineaPref.setText("Aggiungi ai preferiti");
            } else {
                FavoritesManager.addFavoriteLine(user, nomeLinea);
                tastoLineaPref.setText("Rimuovi dai preferiti");
            }
        });
        if (presente) {
            tastoLineaPref.setText("Rimuovi dai preferiti");
        } else {
            tastoLineaPref.setText("Aggiungi ai preferiti");
        }
    }

    /**
     * Ritorna il tasto per attivare la visualizzazione dei mezzi sulla mappa.
     */
    public JButton getMostraMezziButton() {return mostraMezzi;}

    /**
     * Imposta le linee servite da ogni fermata.
     * @param linee è la lista delle linee servite.
     * @param gestore è il gestore delle informazioni.
     */
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

    /**
     * Mostra le informazioni sulla linea selezionata.
     * @param routeId è l'Id della linea.
     * @param daComboBox indica se la chiamata della funzione deriva dalla selezione tramite ComboBox.
     */
    public void mostraInfoLineaUI(String routeId, boolean daComboBox) {
        infoLinea.removeAll();
        infoLinea.add(testoLinea);
        infoLinea.add(tastoLineaPref);

        if (LoginManager.logged)
        {
            tastoLineaPref.setVisible(true);
            impostaLineaPref(routeId);
        }
        else
        {
            tastoLineaPref.setVisible(false);
        }
        infoLinea.add(tipoMezzo);
        infoLinea.add(new JLabel("                                                                 "));

        if (!daComboBox && !StaticGTFS.lineaDellaMetro(routeId))
        {
            infoLinea.add(prossimoArrivo);
            infoLinea.add(statoCorsa);
            infoLinea.add(ritardoCorsa);
            infoLinea.add(avvisoPrevisione);
        }

        infoLinea.add(avvisoTracciamento);

        infoLinea.revalidate();
        infoLinea.repaint();

        // Rimuove il pulsante dei mezzi prima di aggiungerlo
        if (mostraMezzi.getParent() != null)
        {
            mostraMezzi.getParent().remove(mostraMezzi);
        }

        pannello.add(mostraMezzi);
        pannello.revalidate();
        pannello.repaint();
    }

    /**
     * Imposta il prossimo arrivo.
     */
    public void setProssimoArrivo(String tempo) {
        prossimoArrivo.setText(tempo);
    }

    /**
     * Imposta l'avviso se i dati sono previsti staticamente o meno.
     */
    public void setAvvisoPrevisione(String avviso) {
        avvisoPrevisione.setText(avviso);
    }

    /**
     * Imposta lo stato della corsa e l'eventuale ritardo.
     */
    public void setStatoCorsa(String stato, String ritardo) {
        statoCorsa.setText("Stato corsa: " + stato);
        ritardoCorsa.setText(ritardo);
    }

    /**
     * Imposta la segnalazione del caso in cui non è stato possibile tracciare i mezzi.
     */
    public void setAvvisoTracciamento(String avviso)
    {
        avvisoTracciamento.setText("<html>" + avviso + "</html>");
    }

    /**
     * Aggiorna le informazioni sulla linea.
     */
    public void updateLineaInfo(String routeId, String tipo, String arrivo) {
        testoLinea.setText("Linea selezionata: " + routeId);
        tipoMezzo.setText("Tipo mezzo: " + tipo);
        prossimoArrivo.setText(arrivo);
    }

    /**
     * Resetta il pannello informazioni.
     */
    public void resetPannello() {
        nome.setText("Seleziona una fermata.");
        lineeServite.setText("");

        pulsantiLinee.removeAll();
        pulsantiLinee.revalidate();
        pulsantiLinee.repaint();

        infoLinea.removeAll();
        infoLinea.revalidate();
        infoLinea.repaint();

        tastoFermataPref.setVisible(false);
        prossimoArrivo.setText("");
        avvisoTracciamento.setText("");
        if (mostraMezzi != null)
        {
            pannello.remove(mostraMezzi);
        }

        pannello.revalidate();
        pannello.repaint();
    }
}