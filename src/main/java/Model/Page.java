package Model;

import View.LoginPage;

import javax.swing.*;
import java.awt.*;

public class Page
{
    protected JDialog page;
    protected final Color rossoscuro = new Color(143, 51, 51);
    protected final Color rosso = new Color(175, 62, 62);

    public void chiudiPagina(JDialog pagina, boolean riapri, int delay)
    {
        Timer timer = new Timer(delay, e2 -> {
            pagina.dispose();
            if (riapri)
            {
                LoginPage loginPage = new LoginPage();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
}
