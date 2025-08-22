package Controller;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class WiFi
{
    public static boolean WiFi = false;
    public static View.Frame riferimentoFrame;
    public static View.Mappa riferimentoMappa;

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

    //Usa una finestra modale per avvertire l'utente dello stato della connessione
    public static void mostraStatoConnessione(JFrame frame)
    {
        JDialog dialogStatoWiFi = new JDialog(frame, "Messaggio", false);
        dialogStatoWiFi.setSize(new Dimension(200, 100));

        JLabel testoStato = new JLabel();

        if (WiFi)
            testoStato.setText("Connessione tornata (?)");
        else
            testoStato.setText("Connessione assente");

        dialogStatoWiFi.add(testoStato);

        dialogStatoWiFi.setVisible(true);
    }
}
