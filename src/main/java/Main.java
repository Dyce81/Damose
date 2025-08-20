import Controller.DatabaseManager;
import Controller.StaticGTFS;
import Controller.Wifi;
import Model.CustomWaypoint;
import Model.GestoreWaypoint;
import View.Frame;
import View.Mappa;

//disclaimer: il progetto potrebbe essere organizzato meglio - magari separando ulteriormente la logica
//della creazione delle fermate, creando un'altra classe WaypointManager che si occupa di piazzare
//i vari waypoint - volendo può anche essere istanziata e usata da Mappa.java

public class Main
{
    public static void main(String[] args)
    {
        //Wifi wifi = new Wifi();
        //wifi.wifi_controller();
        Wifi.wifi_controller();

        Frame frame = new Frame(600, 800, "Damose");
        StaticGTFS.iniziaPROVVISORIO();
        GestoreWaypoint elab_fermate = new GestoreWaypoint();
        //frame.imposta_painter_mappa(elab_fermate.posiziona_fermate());
        //elab_fermate.posiziona_fermate(frame.mappa);
        elab_fermate.posizionaFermate(frame.mappa);
        //elab_fermate.CustomMouseListener(frame.mappa.mappa); //da rivedere -- vedere disclaimer sopra
        elab_fermate.CustomMouseListener(Mappa.getMapViewer());
        frame.listaFermate = GestoreWaypoint.listaFermate;
        //frame.imposta_combo_box(GestoreWaypoint.nomi_fermate);
        CustomWaypoint.setPannello(frame.getPannelloInformazioni());
        frame.imposta_combo_box(); //se questa riga viene spostata sopra, la combobox NON funziona,
        //quindi più tardi questa cosa è da aggiustare perché è indecente :(
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.createUsersTable();

        Wifi.impostaFrame(frame, frame.getMappa());
    }
}