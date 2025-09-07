package View;

import Controller.GestoreInformazioni;
import Controller.StaticGTFS;
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

    private static final Color rossoScuro = new Color(143, 51, 51);
    private static final Color rosso = new Color(175, 62, 62);

    public PannelloInformazioni() {
        pannello = new JPanel();
        pannello.setPreferredSize(new Dimension(200, Integer.MAX_VALUE));
        pannello.setBackground(rossoScuro);
        pannello.setLayout(new BoxLayout(pannello, BoxLayout.Y_AXIS));

        nome = new JLabel("Seleziona una fermata.");
        nome.setForeground(Color.WHITE);

        lineeServite = new JLabel("");
        lineeServite.setForeground(Color.WHITE);

        pulsantiLinee = new JPanel();
        pulsantiLinee.setBackground(rossoScuro);
        pulsantiLinee.setLayout(new BoxLayout(pulsantiLinee, BoxLayout.Y_AXIS));

        infoLinea = new JPanel();
        infoLinea.setLayout(new BoxLayout(infoLinea, BoxLayout.Y_AXIS));
        infoLinea.setBackground(rosso);

        tipoMezzo = new JLabel("");
        testoLinea = new JLabel("");
        prossimoArrivo = new JLabel("");
        statoCorsa = new JLabel("");
        ritardoCorsa = new JLabel("");
        avvisoPrevisione = new JLabel("");
        avvisoTracciamento = new JLabel("");

        pannello.add(new JLabel("Fermata selezionata:"));
        pannello.add(nome);
        pannello.add(lineeServite);
        pannello.add(pulsantiLinee);
        pannello.add(infoLinea);
    }

    public JPanel getPannello() {
        return this.pannello;
    }

    public void impostaNome(CustomWaypoint fermata) {
        nome.setText(fermata.getNome());
    }

    public void setLineeServite(ArrayList<Route> linee, GestoreInformazioni gestore) {
        pulsantiLinee.removeAll();
        for (Route r : linee) {
            JButton pulsanteLinea = new JButton(StaticGTFS.getNomeRealeMetro(r.id()));
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
        infoLinea.removeAll();
        infoLinea.add(testoLinea);
        infoLinea.add(tipoMezzo);
        infoLinea.add(prossimoArrivo);
        infoLinea.add(statoCorsa);
        infoLinea.add(ritardoCorsa);
        infoLinea.add(avvisoPrevisione);
        infoLinea.add(avvisoTracciamento);
        pannello.add(GestoreInformazioni.getMostraMezziButton());
        pannello.revalidate();
        pannello.repaint();
    }

    public void setProssimoArrivo(String tempo) {
        prossimoArrivo.setText("Prossimo arrivo previsto: " + tempo);
    }

    public void setAvvisoPrevisione(String avviso) {
        avvisoPrevisione.setText(avviso);
    }

    public void setStatoCorsa(String stato, String ritardo) {
        statoCorsa.setText("Stato corsa: " + stato);
        ritardoCorsa.setText("Ritardo stimato: " + ritardo);
    }

    public void setAvvisoTracciamento(String avviso) {
        avvisoTracciamento.setText(avviso);
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
        if (GestoreInformazioni.getMostraMezziButton().getParent() != null) {
            pannello.remove(GestoreInformazioni.getMostraMezziButton());
        }
    }
}