package View;

import Controller.DatabaseManager;
import Controller.LoginManager;
import Model.Page;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChangePasswordPage extends Page
{
    public ChangePasswordPage()
    {
        page = new JDialog();
        page.setSize(300, 200);
        page.setLocationRelativeTo(null);
        page.setModal(true);
        page.setLayout(null);
        page.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBounds(30, 0, 240, 150);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(Box.createVerticalStrut(15));

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

        JTextField conferma = new JTextField("conferma la nuova password");
        conferma.setAlignmentX(Component.CENTER_ALIGNMENT);
        conferma.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                conferma.selectAll();
            }
        });
        panel.add(conferma);
        panel.add(Box.createVerticalStrut(5));

        JLabel avviso = new JLabel("default");
        avviso.setForeground(page.getBackground());
        avviso.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton cambiaPassword = new JButton("Cambia Password");
        cambiaPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(cambiaPassword);
        cambiaPassword.addActionListener(e -> {
            if (!nuovaPassword.getText().equals(conferma.getText()))
            {
                avviso.setText("Le due password sono diverse");
                avviso.setForeground(Color.red);
            }
            else if (!DatabaseManager.changePassword(LoginManager.username, vecchiaPassword.getText(), nuovaPassword.getText()))
            {
                avviso.setText("Username o password errata.");
                avviso.setForeground(Color.red);
            }
            else {
                avviso.setText("Cambio password riuscito");
                avviso.setForeground(new Color(4, 175, 27));
                chiudiPagina(page, false, 1200);
            }
        });
        panel.add(Box.createVerticalStrut(5));

        panel.add(avviso);
        page.add(panel);
        page.setVisible(true);
    }
}
