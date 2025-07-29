package View;

import javax.swing.*;
import java.awt.*;

public class InformazioniFermata
{
    private final JPanel pannello;
    private final JLabel nome;

    public InformazioniFermata()
    {
        pannello = new JPanel();
        pannello.setBackground(new Color(143, 51, 51));
        pannello.setPreferredSize(new Dimension(200, pannello.getHeight())); //forse? (non proprio responsive)
        nome = new JLabel("Seleziona una fermata.");

        pannello.add(nome);
    }

    public JPanel getPannello()
    {
        return this.pannello;
    }

    public void setNome(String nome)
    {
        this.nome.setText(nome);
    }
}
