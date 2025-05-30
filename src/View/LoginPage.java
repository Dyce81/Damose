package View;

import Controller.SpringUtilities;

import javax.swing.*;
import java.awt.*;

public class LoginPage
{
    public LoginPage()
    {
        JDialog loginPage = new JDialog();
        loginPage.setSize(500,400);
        loginPage.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        loginPage.setLocationRelativeTo(null);
        loginPage.setModal(true);

        loginPage.setLayout(new BorderLayout());

        JLabel benvenuto = new JLabel("benvenuto");
        benvenuto.setText("Benvenuto nella pagina di Login");
        benvenuto.setHorizontalAlignment(SwingConstants.CENTER);

        SpringLayout layout = new SpringLayout();
        JPanel user_pswd = new JPanel(layout);

        JTextField username = new JTextField(5);
        JLabel usrn_txt = new JLabel("username:");
        usrn_txt.setLabelFor(username);
        user_pswd.add(usrn_txt);
        user_pswd.add(username);

        JTextField password = new JTextField(5);
        JLabel pswd_txt = new JLabel("password:");
        user_pswd.add(pswd_txt);
        user_pswd.add(password);

        loginPage.add(user_pswd, BorderLayout.CENTER);
        SpringUtilities.makeCompactGrid(user_pswd, 2, 2, 6, 6, 6, 6);

        JButton registrati = new JButton("registrati");
        registrati.setText("Non sei registrato? Registrati ora!");
        registrati.setHorizontalAlignment(SwingConstants.CENTER);


        loginPage.add(benvenuto, BorderLayout.PAGE_START);
        loginPage.add(registrati, BorderLayout.PAGE_END);

        loginPage.setVisible(true);
    }
}
