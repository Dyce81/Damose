import Controller.DatabaseManager;
import Controller.ReaderStaticGTFS;
import Controller.Wifi;
import Model.CustomWaypoint;
import Model.ElaboratoreFermate;
import View.Frame;
import View.Mappa;

//disclaimer: il progetto potrebbe essere organizzato meglio - magari separando ulteriormente la logica
//della creazione delle fermate, creando un'altra classe WaypointManager che si occupa di piazzare
//i vari waypoint - volendo può anche essere istanziata e usata da Mappa.java

public class Main
{
    public static void main(String[] args)
    {
        Wifi wifi = new Wifi();
        wifi.wifi_controller();

        Frame frame = new Frame(600, 800, "Damose");
        ReaderStaticGTFS.iniziaPROVVISORIO();
        System.out.println(ReaderStaticGTFS.getPosizioneVeicolo("70293", "71"));
        ElaboratoreFermate elab_fermate = new ElaboratoreFermate();
        //frame.imposta_painter_mappa(elab_fermate.posiziona_fermate());
        //elab_fermate.posiziona_fermate(frame.mappa);
        elab_fermate.posizionaFermate(frame.mappa);
        //elab_fermate.CustomMouseListener(frame.mappa.mappa); //da rivedere -- vedere disclaimer sopra
        elab_fermate.CustomMouseListener(Mappa.getMapViewer());
        frame.listaFermate = ElaboratoreFermate.listaFermate;
        //frame.imposta_combo_box(ElaboratoreFermate.nomi_fermate);
        CustomWaypoint.setPannello(frame.getPannelloInformazioni());
        frame.imposta_combo_box(); //se questa riga viene spostata sopra, la combobox NON funziona,
        //quindi più tardi questa cosa è da aggiustare perché è indecente :(
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.createUsersTable();
    }
}