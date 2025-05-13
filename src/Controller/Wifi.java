package Controller;

import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class Wifi
{
    public void wifi_controller()
    {
        //questa cosa non mi convice al 100% - probabilmente da rivedere
        Timer timer_controllo_wifi = new Timer();
        TimerTask task_controllo = new TimerTask()
        {
            @Override
            public void run()
            {
                //TODO: fare cose effettivamente
                if (wifi_connesso()) System.out.println("WiFi connesso");
                else
                {
                    System.out.println("WiFi non connesso :(");
                    //return;
                }
            }
        };

        timer_controllo_wifi.scheduleAtFixedRate(task_controllo, 0, 30000);
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
