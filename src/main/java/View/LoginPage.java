package View;

import Controller.DatabaseManager;

import javax.swing.*;
import java.awt.*;
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

        Color green = new Color(4, 175, 27);

        //pannello in cui inserire i componenti
        JPanel panel = new JPanel();
        panel.setBounds(68, 20, 300, 210);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        loginPage.add(panel);


        //scritta di benvenuto
        JLabel benvenuto = new JLabel("benvenuto");
        benvenuto.setText("BENVENUTO NELLA PAGINA DI LOGIN");
        benvenuto.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(benvenuto);
        panel.add(Box.createVerticalStrut(10));


        //campo per l'username
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


        //campo per la password
        //si potrebbe usare JPasswordField invece di JTextField
        JTextField password = new JTextField("usare dai 6 ai 12 caratteri", 3);
        password.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
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


        //tasto di accesso o registrazione
        JButton acc_reg = new JButton("acc_reg");
        acc_reg.setText("Accedi");
        acc_reg.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(acc_reg);
        panel.add(Box.createVerticalStrut(10));
        JLabel avviso = new JLabel("testo deafault");
        avviso.setForeground(loginPage.getBackground());
        avviso.setAlignmentX(Component.CENTER_ALIGNMENT);
        acc_reg.addActionListener(e -> {
            String username_input = username.getText();
            String password_input = password.getText();

            if (username_input.length() < 5)
            {
                avviso.setText("username troppo corto!");
                avviso.setForeground(Color.RED);
            }

            else if (username_input.length() > 15)
            {
                avviso.setText("username troppo lungo!");
                avviso.setForeground(Color.RED);
            }

            else if (password_input.length() < 6)
            {
                avviso.setText("password troppo corta!");
                avviso.setForeground(Color.RED);
            }

            else if (password_input.length() > 12)
            {
                avviso.setText("password troppo lunga!");
                avviso.setForeground(Color.RED);
            }

            else
            {
                int delay = 1200;

                //se è in fase di registrazione
                if (acc_reg.getText().equals("Registrati"))
                {
                    DatabaseManager.addUser(username_input, password_input);
                    avviso.setText("Registrazione completata! Accesso eseguito!");
                    avviso.setForeground(green);

                    Timer timer = new Timer(delay, e1 -> {
                        // Chiama il metodo dispose() per chiudere il JDialog
                        loginPage.dispose();
                        DatabaseManager.logged = true;
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
                //se è in fase di accesso
                else if (acc_reg.getText().equals("Accedi"))
                {
                    String hashed_pswd = DatabaseManager.getUserPasswordHash(username_input);
                    if (hashed_pswd != null && hashed_pswd.equals(password_input)) {
                        avviso.setText("Accesso eseguito!");
                        avviso.setForeground(green);

                        Timer timer = new Timer(delay, e2 -> {
                            loginPage.dispose();
                            DatabaseManager.logged = true;
                        });
                        timer.setRepeats(false);
                        timer.start();
                    }
                }
            }
            });


        //cambia tra modalità di accesso e registrazione
        JButton cambia_mod = new JButton("cambia_modalità");
        cambia_mod.setText("Non sei registrato? Registrati ora!");
        cambia_mod.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(cambia_mod);
        panel.add(Box.createVerticalStrut(10));
        cambia_mod.addActionListener(e -> {

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

        });


        panel.add(avviso);

        loginPage.add(panel);
        loginPage.setVisible(true);
    }
}
