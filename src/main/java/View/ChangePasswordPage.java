package View;

import Controller.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChangePasswordPage
{
    public ChangePasswordPage()
    {
        JDialog dialog = new JDialog();
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setLayout(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBounds(30, 0, 240, 150);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(Box.createVerticalStrut(15));

        JTextField utente = new JTextField("Inserisci l'username");
        utente.setAlignmentX(Component.CENTER_ALIGNMENT);
        utente.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                utente.selectAll();
            }
        });
        panel.add(utente);
        panel.add(Box.createVerticalStrut(5));

        JTextField vecchiaPassword = new JTextField("Inserisci la tua password");
        vecchiaPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        vecchiaPassword.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                vecchiaPassword.selectAll();
            }
        });
        panel.add(vecchiaPassword);
        panel.add(Box.createVerticalStrut(5));

        JTextField nuovaPassword = new JTextField("inserisci la tua nuova password");
        nuovaPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        nuovaPassword.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                nuovaPassword.selectAll();
            }
        });
        panel.add((nuovaPassword));
        panel.add(Box.createVerticalStrut(5));

        JLabel avviso = new JLabel("default");
        avviso.setForeground(dialog.getBackground());
        avviso.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton cambiaPassword = new JButton("Cambia Password");
        cambiaPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(cambiaPassword);
        cambiaPassword.addActionListener(e -> {
            if (!DatabaseManager.changePassword(utente.getText(), vecchiaPassword.getText(), nuovaPassword.getText()))
            {
                avviso.setText("Username o password errata.");
                avviso.setForeground(Color.red);
            }
            else {
                avviso.setText("Cambio password riuscito");
                avviso.setForeground(new Color(4, 175, 27));
                LoginPage.chiudiPagina(dialog, false);
            }
        });
        panel.add(Box.createVerticalStrut(5));

        panel.add(avviso);
        dialog.add(panel);
        dialog.setVisible(true);
    }
}
