package View;

import javax.swing.*;

public class LoginPage
{
    public LoginPage()
    {
        JDialog loginPage = new JDialog();
        loginPage.setSize(600,800);
        loginPage.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
        loginPage.setLocationRelativeTo(null);
        loginPage.setModal(true);

        loginPage.setVisible(true);
    }
}
