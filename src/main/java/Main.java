import Controller.DatabaseManager;
import Controller.StaticGTFS;
import Controller.WiFi;
import Model.CustomWaypoint;
import Model.GestoreWaypoint;
import View.Frame;
import View.LoadingScreen;
import View.Mappa;

//disclaimer: il progetto potrebbe essere organizzato meglio - magari separando ulteriormente la logica
//della creazione delle fermate, creando un'altra classe WaypointManager che si occupa di piazzare
//i vari waypoint - volendo può anche essere istanziata e usata da Mappa.java

public class Main
{
    public static void main(String[] args)
    {
        LoadingScreen loadingScreen = new LoadingScreen();
        loadingScreen.setVisible(true);

        DatabaseManager dbManager = new DatabaseManager();
        dbManager.createUsersTable();

        StaticGTFS.inizializzaDati();

        WiFi.wifi_controller();

        loadingScreen.dispose();

        Frame frame = new Frame(600, 800, "Damose");
        GestoreWaypoint elabFermate = new GestoreWaypoint();;
        elabFermate.posizionaFermate(frame.mappa);
        elabFermate.CustomMouseListener(Mappa.getMapViewer());
        frame.listaFermate = GestoreWaypoint.listaFermate;
        CustomWaypoint.setPannello(frame.getPannelloInformazioni());
        frame.imposta_combo_box(); //se questa riga viene spostata sopra, la combobox NON funziona,
        //quindi più tardi questa cosa è da aggiustare perché è indecente :(

        WiFi.impostaFrame(frame, frame.getMappa());


    }
}