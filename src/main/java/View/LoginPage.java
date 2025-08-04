package View;

import Controller.CredentialChecker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginPage
{
    public LoginPage()
    {
        //setup finestra
        JDialog loginPage = new JDialog();
        loginPage.setSize(450,280);
        loginPage.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        loginPage.setLocationRelativeTo(null);
        loginPage.setModal(true);
        loginPage.setLayout(null);


        //pannello in cui inserire i componenti
        JPanel panel = new JPanel();
        panel.setBounds(68, 20, 300, 210);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        loginPage.add(panel);


        JLabel benvenuto = new JLabel("benvenuto");
        benvenuto.setText("BENVENUTO NELLA PAGINA DI LOGIN");
        benvenuto.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(benvenuto);
        panel.add(Box.createVerticalStrut(10));


        JTextField username = new JTextField("usare dai 5 ai 15 caratteri",3);
        username.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                // Quando il mouse viene cliccato sul campo di testo
                username.selectAll(); // Seleziona tutto il contenuto
            }
        });
        username.setForeground(Color.LIGHT_GRAY);
        username.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel usrn_txt = new JLabel("username:");
        usrn_txt.setLabelFor(username);
        usrn_txt.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(usrn_txt);
        panel.add(username);
        panel.add(Box.createVerticalStrut(10));


        //si potrebbe usare JPasswordField invece di JTextField
        JTextField password = new JTextField("usare dai 6 ai 12 caratteri", 3);
        password.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                // Quando il mouse viene cliccato sul campo di testo
                password.selectAll(); // Seleziona tutto il contenuto
            }
        });
        password.setForeground(Color.LIGHT_GRAY);
        password.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel pswd_txt = new JLabel("password:");
        pswd_txt.setLabelFor(password);
        pswd_txt.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(pswd_txt);
        panel.add(password);
        panel.add(Box.createVerticalStrut(10));


        JButton acc_reg = new JButton("acc_reg");
        acc_reg.setText("Accedi");
        acc_reg.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(acc_reg);
        panel.add(Box.createVerticalStrut(10));
        JLabel avviso = new JLabel("testo deafault");
        avviso.setForeground(loginPage.getBackground());
        avviso.setAlignmentX(Component.CENTER_ALIGNMENT);
        acc_reg.addActionListener(new ActionListener()
        {public void actionPerformed(ActionEvent e)
            {
                String username_input = username.getText();
                String password_input = password.getText();

                //se è in fase di registrazione
                if (acc_reg.getText().equals("Registrati"))
                {
                    if (CredentialChecker.registra_utente(username_input, password_input) == 5)
                    {
                        avviso.setText("Registrazione completata! Accesso eseguito!");
                        avviso.setForeground(Color.GREEN);
                    }
                    else if (CredentialChecker.registra_utente(username_input, password_input) == 0)
                    {
                        avviso.setText("username troppo corto!");
                        avviso.setForeground(Color.RED);
                    }
                    else if (CredentialChecker.registra_utente(username_input, password_input) == 1)
                    {
                        avviso.setText("username troppo lungo!");
                        avviso.setForeground(Color.RED);
                    }
                    else if (CredentialChecker.registra_utente(username_input, password_input) == 2)
                    {
                        avviso.setText("password troppo corta!");
                        avviso.setForeground(Color.RED);
                    }
                    else if (CredentialChecker.registra_utente(username_input, password_input) == 3)
                    {
                        avviso.setText("password troppo lunga!");
                        avviso.setForeground(Color.RED);
                    }
                }
                //se è in fase di accesso
                //else
                {
                    //controllo se username e password sono corretti
                }
            }
        });


        JButton cambia_mod = new JButton("cambia_modalità");
        cambia_mod.setText("Non sei registrato? Registrati ora!");
        cambia_mod.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(cambia_mod);
        panel.add(Box.createVerticalStrut(10));
        cambia_mod.addActionListener(new ActionListener()
        {public void actionPerformed(ActionEvent e)
        {

            if (acc_reg.getText().equals("Accedi"))

            {
                cambia_mod.setText("Hai già un account? Accedi ora!");
                acc_reg.setText("Registrati");
                benvenuto.setText("BENVENUTO NELLA PAGINA DI REGISTRAZIONE");
            }
            else if (acc_reg.getText().equals("Registrati"))
            {
                cambia_mod.setText("Non sei registrato? Registrati ora!");
                acc_reg.setText("Accedi");
                benvenuto.setText("BENVENUTO NELLA PAGINA DI LOGIN");
            }

        }
        });


        panel.add(avviso);

        loginPage.add(panel);
        loginPage.setVisible(true);
    }
}
