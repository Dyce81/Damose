package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Objects;
import View.LoginPage;

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
        frame.setLayout(new BorderLayout());
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);


        //Casella testo e pulsante per la ricerca delle fermate
        JPanel pannello_sup = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 5));;
        testo_fermata.setEditable(true);
        testo_fermata.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                //System.out.println(lista_fermate);
                cerca_fermata(e);
            }
        });

        mappa = new Mappa(frame);

        //tasto per l'accesso alla pagina di login
        ImageIcon profileIcon = new ImageIcon("assets/profile-logo.png");
        JButton profileButton = new JButton();
        profileButton.setIcon(profileIcon);
        profileButton.setContentAreaFilled(false);
        profileButton.setBorder(null);
        profileButton.setPreferredSize(new Dimension(50, 50));

        //accesso alla pagina di login
        profileButton.addActionListener(new ActionListener()
        {public void actionPerformed(ActionEvent e)
            {
                LoginPage loginPage = new LoginPage();
            }
        });

        pannello_sup.add(testo_fermata);
        pannello_sup.add(profileButton);
        pannello_sup.setBackground(new Color(175, 62, 62));

        frame.add(pannello_sup, BorderLayout.PAGE_START);
        frame.setVisible(true);
    }

    /*public void imposta_painter_mappa(Painter p)
    {
        mappa.set_painter(p);
    }*/

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
            if (Objects.equals(nome_fermata, f.nome_fermata)) //Al posto di nome_fermata == f.nome_fermata
            {
                mappa.cambia_posizione(f.get_latitudine(), f.get_longitudine());
                f.toggle_selezione();
                break;
            }
        }
    }
}
