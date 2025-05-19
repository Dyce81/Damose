package View;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import org.jxmapviewer.painter.Painter;

public class Frame {
    final JFrame frame;
    public Mappa mappa;
    private JComboBox testo_fermata = new JComboBox();

    public Frame(int height, int width, String title)
    {
        //Creazione finestra e definizione dimensione e operazione di chiusura
        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Casella testo e pulsante per la ricerca delle fermate
        JPanel pannello_sup = new JPanel();
        testo_fermata.setEditable(true);
        pannello_sup.add(testo_fermata);

        mappa = new Mappa(frame);

        frame.add(pannello_sup, BorderLayout.PAGE_START);
        frame.setVisible(true);
    }

    public void imposta_painter_mappa(Painter p)
    {
        mappa.set_painter(p);
    }

    public void imposta_combo_box(ArrayList<String> nomi)
    {
        for (String s : nomi)
            testo_fermata.addItem(s);
    }
}
