package View;

import Model.CustomWaypoint;
import Model.Route;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class InformazioniFermata
{
    private final JPanel pannello;
    private final JLabel indicazioneFermata;
    private final JLabel nome;
    private final JLabel tipoMezzo;
    private final JLabel lineeServite;
    private JPanel pulsantiLinee;

    private static final Color rossoScuro = new Color(143, 51, 51);

    public InformazioniFermata()
    {
        pannello = new JPanel();
        pannello.setBackground(rossoScuro);
        //pannello.setPreferredSize(new Dimension(200, pannello.getPreferredSize().height)); //forse? (non proprio responsive)

        indicazioneFermata = new JLabel("Fermata selezionata:");
        nome = new JLabel("Seleziona una fermata.");
        tipoMezzo = new JLabel("");
        lineeServite = new JLabel("");
        lineeServite.setBackground(rossoScuro);

        pulsantiLinee = new JPanel();
        pulsantiLinee.setBackground(rossoScuro);
        pulsantiLinee.setLayout(new BoxLayout(pulsantiLinee, BoxLayout.Y_AXIS));

        pannello.setLayout(new BoxLayout(pannello, BoxLayout.Y_AXIS));
        pannello.add(indicazioneFermata);
        pannello.add(nome);
        pannello.add(tipoMezzo);
        pannello.add(lineeServite);
        pannello.add(pulsantiLinee);
    }

    public JPanel getPannello()
    {
        return this.pannello;
    }

    public void setNome(String nome)
    {
        this.nome.setText(nome + '\n');
    }

    public void setTipoMezzo(String tipoMezzo)
    {
        System.out.println();
        this.tipoMezzo.setText("Tipo mezzo: " + tipoMezzo);
    }

    public void impostaInfo(CustomWaypoint fermata)
    {
        nome.setText(fermata.getNome());

        if (fermata.getId().startsWith("ITO"))
            tipoMezzo.setText("Tipo mezzo: Metropolitana");
        else
            tipoMezzo.setText("Tipo mezzo: Autobus");
    }

    public void setLineeServite(ArrayList<Route> linee)
    {
        pulsantiLinee.removeAll();

        for (Route r : linee)
        {
            //infoLinee.append("- ").append(r.getId()).append(", ").append(r.getUrl()).append('\n');
            //infoLinee.append("- ").append(r.getId()).append('\n');

            JButton pulsanteLinea = new JButton(r.getId());
            pulsanteLinea.setBorderPainted(false);
            pulsanteLinea.setBackground(rossoScuro);
            pulsanteLinea.setMaximumSize(new Dimension(Integer.MAX_VALUE, pulsanteLinea.getPreferredSize().height));
            pulsanteLinea.setAlignmentX(Component.CENTER_ALIGNMENT);

            pulsanteLinea.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String linea = pulsanteLinea.getText();
                    System.out.println(linea);
                }
            });

            pulsantiLinee.add(pulsanteLinea);
        }

        lineeServite.setText("Linee servite:");
        pannello.revalidate();
        pannello.repaint();
    }

    public void resetPannello()
    {
        System.out.println("reset pannello");
    }
}
