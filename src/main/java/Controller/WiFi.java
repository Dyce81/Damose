package Controller;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class WiFi
{
    public static boolean WiFi = false;
    private static View.Frame riferimentoFrame;
    private static View.Mappa riferimentoMappa;

    public static void impostaFrame(View.Frame frame, View.Mappa mappa)
    {
        riferimentoFrame = frame;
        riferimentoMappa = mappa;
    }

    public static void wifi_controller()
    {
        //questa cosa non mi convice al 100% - probabilmente da rivedere
        Timer timer_controllo_wifi = new Timer();
        TimerTask task_controllo = new TimerTask()
        {
            @Override
            public void run()
            {
                //TODO: fare cose effettivamente
                if (wifi_connesso())
                {
                    WiFi = true;
                    if (riferimentoFrame != null)
                    {
                        riferimentoFrame.cambiaStatoWiFi();
                        riferimentoMappa.cambiaStatoMappa();
                    }
                }
                else if (!wifi_connesso()) {
                    WiFi = false;
                    if (riferimentoFrame != null)
                    {
                        riferimentoFrame.cambiaStatoWiFi();
                        riferimentoMappa.cambiaStatoMappa();
                    }
                }
            }
        };

        timer_controllo_wifi.scheduleAtFixedRate(task_controllo, 0, 10000);
        //TODO: il "period" reale dovrebbe essere 30000
    }

    public static boolean wifi_connesso()
    {
        try {
            InetAddress net = InetAddress.getByName("google.com");
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
