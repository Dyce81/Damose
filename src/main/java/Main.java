import Controller.*;
import Controller.GestoreWaypoint;
import View.Frame;
import View.LoadingScreen;
import View.PannelloInformazioni;
import Controller.WiFi;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * In questa classe è definito il main del programma.
 */
public class Main
{
    public static void main(String[] args)
    {
        Logger jxMapViewerLogger = Logger.getLogger("org.jxmapviewer");
        jxMapViewerLogger.setLevel(Level.OFF);

        LoadingScreen loadingScreen = new LoadingScreen();
        loadingScreen.setVisible(true);

        DatabaseManager.createUsersTable();
        DatabaseManager.createPreferencesTables();

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

            frame.impostaComboBox();

            WiFi.impostaFrame(frame, frame.getMappa());
        });
    }
}