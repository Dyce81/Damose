package Controller;

import View.Frame;
import View.Mappa;

import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

public class WiFi
{
    private static Frame riferimentoFrame;
    private static Mappa riferimentoMappa;

    public static void impostaFrame(Frame frame, Mappa mappa)
    {
        riferimentoFrame = frame;
        riferimentoMappa = mappa;
    }

    // Questo metodo inizializza il timer del WiFi, che controlla (ogni 30 secondi) se lo stato della
    // connessione è cambiato. Se è cambiato, allora chiama dei metodi del Frame per cambiare la parte
    // grafica della finestra che notifica l'utente della connessione al WiFi
    public static void inizializza()
    {
        Timer timer = new Timer();
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                if (riferimentoFrame != null)
                {
                    riferimentoFrame.cambiaStatoWiFi();
                    riferimentoMappa.cambiaStatoMappa();
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 10000);
        //TODO: il "period" reale dovrebbe essere 30000
    }

    public static boolean connesso()
    {
        try
        {
            URL url = new URI("https://www.google.com/").toURL();
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(3000);
            conn.connect();
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }
}
