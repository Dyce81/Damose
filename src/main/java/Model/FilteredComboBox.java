package Model;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

// Questa classe è stata realizzata praticamente copiando
// https://stackoverflow.com/questions/27753375/jcombobox-search-list

public class FilteredComboBox<T> extends JComboBox<T>
{
    private final List<T> entries;

    public List<T> getEntries()
    {
        return entries;
    }

    public FilteredComboBox()
    {
        this.entries = new ArrayList<>();
        this.setEditable(true);

        final JTextField textField = (JTextField) this.getEditor().getEditorComponent();

        textField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                SwingUtilities.invokeLater(() -> filtra(textField.getText()));
            }
        });
    }

    @Override
    public void addItem(T oggetto)
    {
        entries.add(oggetto);
    }

    public void filtra(String testo)
    {
        if (testo.isBlank())
        {
            this.setModel(
                    new DefaultComboBoxModel(entries.toArray())
            );
            this.setSelectedItem("");
            this.showPopup();
            return;
        }

        List<T> elementiFiltrati = new ArrayList<>();

        for (T elemento : getEntries())
        {
            if (elemento.toString().toLowerCase().contains(testo.toLowerCase()))
            {
                elementiFiltrati.add(elemento);
            }
        }

        if (!elementiFiltrati.isEmpty())
        {
            this.setModel(
                    new DefaultComboBoxModel(
                            elementiFiltrati.toArray()
                    )
            );
            this.setSelectedItem(testo);
            this.showPopup();
        }
        else
        {
            this.hidePopup();
        }
    }
}
