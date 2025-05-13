package View;

import javax.swing.*;
import java.awt.*;

public class Frame {
    private JFrame frame;

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

        Mappa mappa = new Mappa(frame);

        frame.setVisible(true);
    }
}
