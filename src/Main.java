import Controller.Wifi;
import Model.ElaboratoreFermate;
import View.Frame;

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
        ElaboratoreFermate elab_fermate = new ElaboratoreFermate();
        //frame.imposta_painter_mappa(elab_fermate.posiziona_fermate());
        elab_fermate.posiziona_fermate(frame.mappa);
        elab_fermate.CustomMouseListener(frame.mappa.mappa); //da rivedere -- vedere disclaimer sopra
        frame.lista_fermate = ElaboratoreFermate.lista_fermate;
        frame.imposta_combo_box(ElaboratoreFermate.nomi_fermate);
    }
}