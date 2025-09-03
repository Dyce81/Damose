package Model;

import View.LoginPage;

import javax.swing.*;

public class Page
{
    protected JDialog page;

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
