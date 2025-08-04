package View;

import Model.CustomWaypoint;
import Model.Route;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class InformazioniFermata
{
    private final JPanel pannello;
    private final JLabel nome;
    private final JLabel tipoMezzo;
    private final JTextArea lineeServite;

    public InformazioniFermata()
    {
        pannello = new JPanel();
        pannello.setBackground(new Color(143, 51, 51));
        pannello.setPreferredSize(new Dimension(200, pannello.getHeight())); //forse? (non proprio responsive)

        nome = new JLabel("Seleziona una fermata.");
        tipoMezzo = new JLabel("");
        lineeServite = new JTextArea("");
        lineeServite.setBackground(new Color(143, 51, 51));
        lineeServite.setEditable(false);

        pannello.setLayout(new BoxLayout(pannello, BoxLayout.Y_AXIS));
        pannello.add(nome);
        pannello.add(tipoMezzo);
        pannello.add(lineeServite);
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
            tipoMezzo.setText("Metropolitana");
        else
            tipoMezzo.setText("Autobus");
    }

    public void setLineeServite(ArrayList<Route> linee)
    {
        StringBuilder infoLinee = new StringBuilder("Linee servite:\n");
        for (Route r : linee)
        {
            //infoLinee.append("- ").append(r.getId()).append(", ").append(r.getUrl()).append('\n');
            infoLinee.append("- ").append(r.getId()).append('\n');
        }

        lineeServite.setText(infoLinee.toString());
    }
}
