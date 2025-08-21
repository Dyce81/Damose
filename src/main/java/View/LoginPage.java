package View;

import Controller.DatabaseManager;
import Controller.LoginManager;

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

        //pannello in cui inserire i componenti
        JPanel panel = new JPanel();
        panel.setBounds(68, 20, 300, 210);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        loginPage.add(panel);

        if (LoginManager.logged)
        {
            creaPaginaLogout(panel, loginPage);
        }
        else {creaPaginaLogin(panel, loginPage);}


        loginPage.add(panel);
        loginPage.setVisible(true);
    }

    public void chiudiPagina(JDialog pagina, boolean riapri)
    {
        int delay = 1200;
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


    public void creaPaginaLogin(JPanel panel, JDialog dialog)
    {
        Color green = new Color(4, 175, 27);

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
        avviso.setForeground(dialog.getBackground());
        avviso.setAlignmentX(Component.CENTER_ALIGNMENT);
        acc_reg.addActionListener(e -> {
            String username_input = username.getText();
            String password_input = password.getText();

            switch (LoginManager.controllaCredenziali(username_input, password_input))
            {
                case 0:
                    avviso.setText("username troppo corto!");
                    avviso.setForeground(Color.RED);
                case 1:
                    avviso.setText("username troppo lungo!");
                    avviso.setForeground(Color.RED);
                case 2:
                    avviso.setText("password troppo corta!");
                    avviso.setForeground(Color.RED);
                case 3:
                    avviso.setText("password troppo lunga!");
                    avviso.setForeground(Color.RED);
                case 4:

            }
            {
                //se è in fase di registrazione
                if (acc_reg.getText().equals("Registrati"))
                {
                    if (LoginManager.registra(username_input, password_input))
                    {
                        avviso.setText("Registrazione completata! Accesso eseguito!");
                        avviso.setForeground(green);

                        chiudiPagina(dialog, false);
                    }

                    else {System.out.println("Registrazione fallita, utente già registrato.");}
                }

                //se è in fase di accesso
                else if (acc_reg.getText().equals("Accedi"))
                {
                    if (LoginManager.accedi(username_input, password_input))
                    {
                        avviso.setText("Accesso eseguito!");
                        avviso.setForeground(green);

                        chiudiPagina(dialog, false);
                    }
                    else {System.out.println("Accesso fallito, password o username errato.");}
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
    }

    public void creaPaginaLogout(JPanel panel, JDialog dialog)
    {
        panel.add(Box.createVerticalStrut(30));
        JLabel benvenuto = new JLabel("HAI GIA EFFETTUATO IL LOGIN");
        benvenuto.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(benvenuto);
        panel.add(Box.createVerticalStrut(10));

        JButton disconnetti = new JButton("Disconnetti");
        disconnetti.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(disconnetti);
        disconnetti.addActionListener(e -> {
            LoginManager.disconnetti();
            chiudiPagina(dialog, true);

        });



    }
}
