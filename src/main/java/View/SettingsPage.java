package View;

import javax.swing.*;

public class SettingsPage
{
    public SettingsPage()
    {
        JDialog settingsPage = new JDialog();
        settingsPage.setSize(450, 280);
        settingsPage.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        settingsPage.setLocationRelativeTo(null);
        settingsPage.setModal(true);

        settingsPage.setVisible(true);
    }
}
