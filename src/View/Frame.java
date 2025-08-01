package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import Model.CustomWaypoint;
import com.sun.tools.javac.Main;

public class Frame {
    public final JFrame frame;
    public final Mappa mappa;
    ////public ArrayList<Fermata> lista_fermate;
    public ArrayList<CustomWaypoint> listaFermate;

    private final JComboBox testoFermata = new JComboBox(); //TODO: può essere definito nel costruttore passando direttamente l'array dei nomi delle fermate
    private final InformazioniFermata pannelloInformazioni;

    private CustomWaypoint ultimaFermata;

    public Frame(int height, int width, String title)
    {
        //Creazione finestra e definizione dimensione e operazione di chiusura
        frame = new JFrame(title);
        frame.setLayout(new BorderLayout());
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        //Casella testo e pulsante per la ricerca delle fermate
        JPanel pannello_sup = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 5));
        testoFermata.setEditable(true);
        testoFermata.addActionListener(this::cercaFermata); //imposta actionListener della comboBox (quando viene selezionata un elemento)

        mappa = new Mappa(frame);

        //Pannello informazioni laterale per le fermate
        pannelloInformazioni = new InformazioniFermata();
        frame.add(pannelloInformazioni.getPannello(), BorderLayout.WEST);

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

        pannello_sup.add(testoFermata);
        pannello_sup.add(profileButton);
        pannello_sup.setBackground(new Color(175, 62, 62));

        frame.add(pannello_sup, BorderLayout.PAGE_START);
        frame.setVisible(true);
    }

    //questa funzione riempie la combo box con i nomi delle fermate
    public void imposta_combo_box(ArrayList<String> nomi)
    {
        for (String s : nomi)
            testoFermata.addItem(s);
    }

    //la ricerca delle fermate è gestita dal frame tramite questo metodo
    private void cercaFermata(ActionEvent e)
    {
        if (testoFermata.getSelectedItem() == null) return; //magari con codice di errore
        String nomeFermata = testoFermata.getSelectedItem().toString();

        //la fermata precedentemente selezionata (se è presente) non serve più
        if (ultimaFermata != null)
            ultimaFermata.deseleziona();

        //cerca la fermata dentro la lista fermate;
        for (CustomWaypoint f : this.listaFermate)
        {
            if (f.getNome().equals(nomeFermata)) //fermata trovata
            {
                mappa.cambia_posizione(f.getLatitudine(), f.getLongitudine());
                ultimaFermata = f;
                f.seleziona();
                mostraInformazioni(f);
                break;
            }
        }
    }

    //Questo metodo mostra le informazioni della fermata selezionata (al lato della finestra?)
    public void mostraInformazioni(CustomWaypoint fermata)
    {
        pannelloInformazioni.setNome(fermata.getNome());

        //PROVA!!!!
        ArrayList<String> tripsIds = new ArrayList<>();
        /*for (Map<String, String> orario : orari)
        {
            if (orario.get(""));
        }*/
    }

    public InformazioniFermata getPannelloInformazioni()
    {
        return this.pannelloInformazioni;
    }
}
