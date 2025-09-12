package Controller;

import View.Frame;
import View.Mappa;

import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Questa classe si occupa di controllare lo stato del WiFi e notificare le altre classi di
 * qualsiasi cambiamento nella connessione.
 */
public class WiFi
{
    private static Frame riferimentoFrame;
    private static Mappa riferimentoMappa;

    /**
     * Ottiene i riferimenti del Frame e della Mappa istanziati in modo da poterli aggiornare
     * sullo stato del WiFi.
     * @param frame il riferimento dell'oggetto Frame istanziato nel Main.
     * @param mappa il riferimento dell'oggetto Mappa istanziato nel Frame.
     */
    public static void impostaFrame(Frame frame, Mappa mappa)
    {
        riferimentoFrame = frame;
        riferimentoMappa = mappa;
    }

    /**
     * Inizializza il timer del WiFi, che controlla (ogni 30 secondi) se lo stato della connessione
     * è cambiato. Se è cambiato, allora chiama dei metodi del Frame per cambiare la parte grafica
     * della finestra che notifica l'utente della connessione al WiFi
     */
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

        timer.scheduleAtFixedRate(task, 0, 30000);
    }

    /**
     * Tenta di ottenere lo stato della connessione.
     * @return true se il programma ha rilevato una connessione al WiFi, false altrimenti.
     */
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
