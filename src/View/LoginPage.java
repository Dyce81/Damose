package View;

import Controller.SpringUtilities;

import javax.swing.*;
import java.awt.*;

public class LoginPage
{
    public LoginPage()
    {
        //setup finestra
        JDialog loginPage = new JDialog();
        loginPage.setSize(450,270);
        loginPage.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        loginPage.setLocationRelativeTo(null);
        loginPage.setModal(true);
        loginPage.setLayout(null);

        //pannello in cui inserire i componenti
        JPanel panel = new JPanel();
        panel.setBounds(68, 20, 300, 180);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        loginPage.add(panel);

        JLabel benvenuto = new JLabel("benvenuto");
        benvenuto.setText("Benvenuto nella pagina di Login");
        benvenuto.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(benvenuto);
        panel.add(Box.createVerticalStrut(10));

        JTextField username = new JTextField(3);
        String username_input = username.getText();
        username.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel usrn_txt = new JLabel("username:");
        usrn_txt.setLabelFor(username);
        usrn_txt.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(usrn_txt);
        panel.add(username);
        panel.add(Box.createVerticalStrut(10));

        JTextField password = new JTextField(3);
        String password_input = password.getText();
        password.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel pswd_txt = new JLabel("password:");
        pswd_txt.setLabelFor(password);
        pswd_txt.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(pswd_txt);
        panel.add(password);
        panel.add(Box.createVerticalStrut(10));

        JButton accedi = new JButton("accedi");
        accedi.setText("Accedi");
        accedi.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(accedi);
        panel.add(Box.createVerticalStrut(10));

        JButton registrati = new JButton("registrati");
        registrati.setText("Non sei registrato? Registrati ora!");
        registrati.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(registrati);

        loginPage.add(panel);
        loginPage.setVisible(true);
    }
}
