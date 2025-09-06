package View;

import Model.Route;

import javax.swing.*;
import java.awt.*;

public class RoutesComboBoxRenderer extends JLabel implements ListCellRenderer<Route>
{
    public RoutesComboBoxRenderer()
    {
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
        setPreferredSize(new Dimension(130, 40));
        setIconTextGap(12);
        setBorder(BorderFactory.createEmptyBorder(3, 9, 3, 3));
    }

    public Component getListCellRendererComponent(
            JList<? extends Route> list,
            Route value,
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

        setText(value.nome());

        return this;
    }
}
