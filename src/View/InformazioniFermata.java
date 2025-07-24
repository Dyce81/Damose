package View;

import javax.swing.*;

public class InformazioniFermata
{
    private JPanel pannello;
    private JLabel nome;

    public InformazioniFermata()
    {
        pannello = new JPanel();
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
