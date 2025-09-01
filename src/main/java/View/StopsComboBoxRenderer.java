package View;

import Model.CustomWaypoint;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class StopsComboBoxRenderer extends JLabel implements ListCellRenderer<CustomWaypoint>
{
    private static final Image iconaAutobus = new ImageIcon("assets/autobus_icona.png").getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
    private static final Image iconaMetro = new ImageIcon("assets/metro_icona.png").getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);

    //TODO: forse usare questo al posto di creare una nuova ImageIcon ogni volta???? Vedere sotto
    private static final ImageIcon imgIconAutobus = new ImageIcon(iconaMetro);

    public StopsComboBoxRenderer()
    {
        setOpaque(true);
        setHorizontalAlignment(LEADING);
        setVerticalAlignment(CENTER);
        setPreferredSize(new Dimension(270, 40));
        setIconTextGap(12);
        setBorder(BorderFactory.createEmptyBorder(3, 9, 3, 3));
    }

    public Component getListCellRendererComponent(
            JList<? extends CustomWaypoint> list,
            CustomWaypoint value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    )
    {
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        if (Objects.equals(value.getId(), "null"))
        {
            setIcon(null);
        } else {
            if (value.getId().startsWith("ITO"))
                setIcon(new ImageIcon(iconaMetro)); //TODO: questa creazione ogni volta di un oggetto non è proprio carina, vedere se si può risolvere in modo carino
            else
                setIcon(new ImageIcon(iconaAutobus));
        }

        setText(value.getNome());

        return this;
    }
}
