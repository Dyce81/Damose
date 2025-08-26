package Model;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

// Questa classe è stata realizzata praticamente copiando
// https://stackoverflow.com/questions/27753375/jcombobox-search-list

public class FilteredComboBox<T> extends JComboBox<T>
{
    private List<T> entries;

    public List<T> getEntries()
    {
        return entries;
    }

    public FilteredComboBox()
    {
        //super(entries.toArray());
        //this.entries = entries;
        this.entries = new ArrayList<>();
        this.setEditable(true);

        final JTextField textField = (JTextField) this.getEditor().getEditorComponent();

        textField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        filtra(textField.getText());
                    }
                });
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
            List<T> elementi = entries;
            this.setModel(
                    new DefaultComboBoxModel(elementi.toArray())
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
