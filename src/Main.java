
import java.util.ArrayList;

import Controller.Wifi;
import Model.ElaboratoreFermate;
import View.Frame;
import Controller.ReaderStaticGTFS;
import Model.Fermata;
import org.jxmapviewer.viewer.*;

public class Main
{
    public static void main(String[] args)
    {
        Wifi wifi = new Wifi();
        wifi.wifi_controller();

        Frame frame = new Frame(600, 800, "Damose");
        ElaboratoreFermate elab_fermate = new ElaboratoreFermate();
        frame.imposta_painter_mappa(elab_fermate.posiziona_fermate());
    }
}