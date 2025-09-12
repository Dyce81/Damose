package View;

import Controller.LoginManager;
import Model.Page;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Questa classe crea la pagina di Login / LogOut
 */
public class LoginPage extends Page
{
    /**
     * Costruttore.
     */
    public LoginPage()
    {
        //setup finestra
        page = new JDialog();
        page.setSize(450,280);
        page.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        page.setLocationRelativeTo(null);
        page.setModal(true);
        page.setLayout(null);
        page.setForeground(rossoscuro);

        //pannello che ricopre la pagina
        JPanel backPanel = new JPanel();
        backPanel.setBounds(0, 0, 450, 280);
        backPanel.setBackground(rossoscuro);
        backPanel.setLayout(null);

        //pannello in cui inserire i componenti
        JPanel panel = new JPanel();
        panel.setBounds(68, 20, 300, 210);
        panel.setBackground(rossoscuro);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        if (LoginManager.logged) {creaPaginaLogout(panel, page);}
        else {creaPaginaLogin(panel, page);}


        page.add(backPanel);
        backPanel.add(panel);
        page.setVisible(true);
    }


    private void creaPaginaLogin(JPanel panel, JDialog dialog)
    {
        //scritta di benvenuto
        JLabel benvenuto = new JLabel("benvenuto");
        benvenuto.setText("BENVENUTO NELLA PAGINA DI LOGIN");
        benvenuto.setAlignmentX(Component.CENTER_ALIGNMENT);
        benvenuto.setForeground(Color.WHITE);
        panel.add(benvenuto);
        panel.add(Box.createVerticalStrut(10));


        //campo per l'username
        JTextField username = new JTextField("usare dai 5 ai 15 caratteri",3);
        username.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                username.selectAll();
            }
        });
        username.setForeground(Color.LIGHT_GRAY);
        username.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel usrn_txt = new JLabel("username:");
        usrn_txt.setLabelFor(username);
        usrn_txt.setAlignmentX(Component.CENTER_ALIGNMENT);
        usrn_txt.setForeground(Color.WHITE);
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
        pswd_txt.setForeground(Color.WHITE);
        panel.add(pswd_txt);
        panel.add(password);
        panel.add(Box.createVerticalStrut(10));


        //tasto di accesso o registrazione
        JButton acc_reg = new JButton("acc_reg");
        acc_reg.setText("Accedi");
        acc_reg.setPreferredSize(new Dimension(75, 25));
        acc_reg.setMaximumSize(new Dimension(75, 25));
        impostaButton(acc_reg);
        panel.add(acc_reg);
        panel.add(Box.createVerticalStrut(10));
        JLabel avviso = new JLabel("testo deafault");
        avviso.setForeground(rossoscuro);
        avviso.setAlignmentX(Component.CENTER_ALIGNMENT);
        acc_reg.addActionListener(e -> {
            String username_input = username.getText();
            String password_input = password.getText();

            switch (LoginManager.controllaCredenziali(username_input, password_input))
            {
                case 0:
                    avviso.setText("username troppo corto!");
                    avviso.setForeground(Color.WHITE);
                case 1:
                    avviso.setText("username troppo lungo!");
                    avviso.setForeground(Color.WHITE);
                case 2:
                    avviso.setText("password troppo corta!");
                    avviso.setForeground(Color.WHITE);
                case 3:
                    avviso.setText("password troppo lunga!");
                    avviso.setForeground(Color.WHITE);
                case 4:

            }
            {
                //se è in fase di registrazione
                if (acc_reg.getText().equals("Registrati"))
                {
                    if (LoginManager.registra(username_input, password_input))
                    {
                        avviso.setText("Registrazione completata! Accesso eseguito!");
                        avviso.setForeground(verde);

                        chiudiPagina(dialog, false, 1200);
                    }

                    else {
                        avviso.setText("Registrazione fallita, utente già registrato.");
                        avviso.setForeground(Color.WHITE);
                    }
                }

                //se è in fase di accesso
                else if (acc_reg.getText().equals("Accedi"))
                {
                    if (LoginManager.accedi(username_input, password_input))
                    {
                        avviso.setText("Accesso eseguito!");
                        avviso.setForeground(verde);

                        chiudiPagina(dialog, false, 1200);
                    }
                    else {
                        avviso.setText("Accesso fallito, password o username errato.");
                        avviso.setForeground(Color.WHITE);
                    }
                }
            }
        });


        //cambia tra modalità di accesso e registrazione
        JButton cambia_mod = new JButton("cambia_modalità");
        cambia_mod.setText("Non sei registrato? Registrati ora!");
        impostaButton(cambia_mod);
        cambia_mod.setPreferredSize(new Dimension(205, 25));
        cambia_mod.setMaximumSize(new Dimension(205, 25));
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

    private void creaPaginaLogout(JPanel panel, JDialog dialog)
    {
        JLabel benvenuto = new JLabel("HAI GIA EFFETTUATO IL LOGIN");
        benvenuto.setAlignmentX(Component.CENTER_ALIGNMENT);
        benvenuto.setForeground(Color.WHITE);
        panel.add(benvenuto);

        panel.add(Box.createVerticalStrut(5));

        JButton disconnetti = new JButton("Disconnetti");
        impostaButton(disconnetti);
        disconnetti.setPreferredSize(new Dimension(100, 25));
        disconnetti.setMaximumSize(new Dimension(100, 25));
        panel.add(disconnetti);
        disconnetti.addActionListener(e -> {
            LoginManager.disconnetti();
            chiudiPagina(dialog, true, 1200);

        });
        panel.add(Box.createVerticalStrut(10));

        JLabel cambia = new JLabel("Se vuoi cambiare password:");
        cambia.setAlignmentX(Component.CENTER_ALIGNMENT);
        cambia.setForeground(Color.WHITE);
        panel.add(cambia);

        panel.add(Box.createVerticalStrut(5));

        JButton cambiaPassword = new JButton("Cambia Password");
        impostaButton(cambiaPassword);
        cambiaPassword.setPreferredSize(new Dimension(125, 25));
        cambiaPassword.setMaximumSize(new Dimension(125, 25));
        panel.add(cambiaPassword);
        //crea la pagina per cambiare la password
        cambiaPassword.addActionListener(e -> new ChangePasswordPage());

        panel.add(Box.createVerticalStrut(10));

        JLabel elimina = new JLabel("Se vuoi eliminare il tuo account:");
        elimina.setAlignmentX(Component.CENTER_ALIGNMENT);
        elimina.setForeground(Color.WHITE);
        panel.add(elimina);

        panel.add(Box.createVerticalStrut(5));

        JTextField nomeAccount = new JTextField("Inserisci l'username", 2);
        nomeAccount.setAlignmentX(Component.CENTER_ALIGNMENT);
        nomeAccount.setHorizontalAlignment(SwingConstants.CENTER);
        nomeAccount.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                nomeAccount.selectAll();
            }
        });
        panel.add(nomeAccount);

        panel.add(Box.createVerticalStrut(5));

        JLabel avviso = new JLabel("testo deafault");
        avviso.setForeground(rossoscuro);
        avviso.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton eliminaAccount = new JButton("Elimina Account");
        impostaButton(eliminaAccount);
        eliminaAccount.setPreferredSize(new Dimension(100, 25));
        eliminaAccount.setMaximumSize(new Dimension(100, 25));
        panel.add(eliminaAccount);
        eliminaAccount.addActionListener(e -> {
            if (LoginManager.eliminaAccount(nomeAccount.getText()))
            {
                chiudiPagina(dialog, true, 1200);
                avviso.setText("Eliminazione riuscita");
                avviso.setForeground(verde);
            }
            else {
                avviso.setText("username errato");
                avviso.setForeground(Color.WHITE);
            }
        });

        panel.add(Box.createVerticalStrut(5));
        panel.add(avviso);
    }
}
