package Model;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ComboBoxRenderer extends JLabel implements ListCellRenderer
{
    private final ArrayList<ImageIcon> immagini = new ArrayList<>();

    public ComboBoxRenderer()
    {
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);

        immagini.add(new ImageIcon("assets/autobus_icona.png"));
        immagini.add(new ImageIcon("assets/metro_icona.png"));
    }

    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    )
    {
        int indiceSelezionato = ((Integer)value).intValue();

        if (isSelected)
        {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        ImageIcon icona = immagini.get(0);
        setIcon(icona);

        setText("prova");

        return this;
    }
}
