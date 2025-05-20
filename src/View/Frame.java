package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

import Model.Fermata;
import org.jxmapviewer.painter.Painter;

public class Frame {
    final JFrame frame;
    public Mappa mappa;
    private JComboBox testo_fermata = new JComboBox();
    public ArrayList<Fermata> lista_fermate;

    public Frame(int height, int width, String title)
    {
        //Creazione finestra e definizione dimensione e operazione di chiusura
        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Casella testo e pulsante per la ricerca delle fermate
        JPanel pannello_sup = new JPanel();
        testo_fermata.setEditable(true);
        testo_fermata.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                //System.out.println(lista_fermate);
                cerca_fermata(e);
            }
        });
        pannello_sup.add(testo_fermata);

        mappa = new Mappa(frame);

        frame.add(pannello_sup, BorderLayout.PAGE_START);
        frame.setVisible(true);
    }

    public void imposta_painter_mappa(Painter p)
    {
        mappa.set_painter(p);
    }

    //questa funzione, oltre ad impostare la combo box, riempie l'array contenente tutte le fermate
    //public void imposta_combo_box(ArrayList<String> nomi)
    public void imposta_combo_box(ArrayList<String> nomi)
    {
        for (String s : nomi)
            testo_fermata.addItem(s);
    }

    private void cerca_fermata(ActionEvent e)
    {
        if (testo_fermata.getSelectedItem() == null) return; //magari con codice di errore
        String nome_fermata = testo_fermata.getSelectedItem().toString();

        //cerca la fermata dentro la lista fermate;
        for (Fermata f : this.lista_fermate)
        {
            if (Objects.equals(nome_fermata, f.nome_fermata)) //al posto di nome_fermata == f.nome_fermata
            {
                mappa.cambia_posizione(f.get_latitudine(), f.get_longitudine());
                break;
            }
        }
    }
}
