package View;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.*;
import java.security.PrivateKey;

import org.jxmapviewer.painter.Painter;

public class Frame {
    final JFrame frame;
    public Mappa mappa;

    public Frame(int height, int width, String title)
    {
        //Creazione finestra e definizione dimensione e operazione di chiusura
        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Casella testo e pulsante per la ricerca delle fermate
        JPanel pannello_sup = new JPanel();
        JTextField testo_fermata = new JTextField(32);
        testo_fermata.setPreferredSize(new Dimension(300, 50));
        JButton avvia_ricerca_fermata = new JButton("Cerca fermata"); //TODO: disattiva se il campo è vuoto
        pannello_sup.add(testo_fermata);
        pannello_sup.add(avvia_ricerca_fermata);

        mappa = new Mappa(frame);

        frame.add(pannello_sup, BorderLayout.PAGE_START);
        frame.setVisible(true);
    }

    public void imposta_painter_mappa(Painter p)
    {
        mappa.set_painter(p);
    }
}
