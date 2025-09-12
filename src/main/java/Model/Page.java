package Model;

import View.LoginPage;

import javax.swing.*;
import java.awt.*;

public class Page
{
    protected JDialog page;
    protected final Color rossoscuro = new Color(143, 51, 51);
    protected final Color rosso = new Color(175, 62, 62);
    protected final Color verde = new Color(4, 175, 27);

    protected void chiudiPagina(JDialog pagina, boolean riapri, int delay)
    {
        Timer timer = new Timer(delay, e2 -> {
            pagina.dispose();
            if (riapri)
            {
                new LoginPage();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    protected void impostaButton(JButton button)
    {
        button.setBackground(rosso);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}
