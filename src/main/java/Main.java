import Controller.*;
import Controller.GestoreWaypoint;
import View.Frame;
import View.LoadingScreen;
import View.PannelloInformazioni;
import Controller.WiFi;

import javax.swing.*;

//disclaimer: il progetto potrebbe essere organizzato meglio - magari separando ulteriormente la logica
//della creazione delle fermate, creando un'altra classe WaypointManager che si occupa di piazzare
//i vari waypoint - volendo può anche essere istanziata e usata da Mappa.java

public class Main
{
    public static void main(String[] args)
    {
        LoadingScreen loadingScreen = new LoadingScreen();
        loadingScreen.setVisible(true);

        DatabaseManager.createUsersTable();
        DatabaseManager.createPreferencesTables();

        //per debugging
        DatabaseManager.printAllUsers();

        StaticGTFS.inizializzaDati();
        WiFi.inizializza();

        loadingScreen.dispose();

        SwingUtilities.invokeLater(() -> {
            Frame frame = new Frame(600, 800, "Damose");

            PannelloInformazioni pannelloInformazioni = frame.getPannelloInformazioni();

            GestoreInformazioni gestoreInformazioni = new GestoreInformazioni(frame, pannelloInformazioni);
            frame.setGestoreInformazioni(gestoreInformazioni);

            GestoreWaypoint gestoreWaypoint = new GestoreWaypoint(gestoreInformazioni);
            gestoreWaypoint.posizionaFermate();

            frame.impostaComboBox(); //se questa riga viene spostata sopra, la combobox NON funziona,
            //quindi più tardi questa cosa è da aggiustare perché è indecente :(

            WiFi.impostaFrame(frame, frame.getMappa());
        });
    }
}