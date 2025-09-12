package View;

import Controller.DatabaseManager;
import Controller.LoginManager;
import Model.Page;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Questa classe crea la pagina per il cambio password.
 */
public class ChangePasswordPage extends Page
{
    /**
     * Costruttore.
     */
    public ChangePasswordPage()
    {
        page = new JDialog();
        page.setSize(300, 200);
        page.setLocationRelativeTo(null);
        page.setModal(true);
        page.setLayout(null);
        page.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel backPanel = new JPanel();
        backPanel.setBounds(0, 0, 450, 280);
        backPanel.setBackground(rossoscuro);
        backPanel.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBounds(45, 0, 200, 150);
        panel.setBackground(rossoscuro);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(Box.createVerticalStrut(20));

        JTextField vecchiaPassword = new JTextField("Inserisci la tua password");
        vecchiaPassword.setForeground(Color.DARK_GRAY);
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
        nuovaPassword.setForeground(Color.DARK_GRAY);
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
        conferma.setForeground(Color.DARK_GRAY);
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
        avviso.setForeground(rossoscuro);
        avviso.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton cambiaPassword = new JButton("Cambia Password");
        impostaButton(cambiaPassword);
        cambiaPassword.setPreferredSize(new Dimension(125, 25));
        cambiaPassword.setMaximumSize(new Dimension(125, 25));
        panel.add(cambiaPassword);
        cambiaPassword.addActionListener(e -> {
            if (!nuovaPassword.getText().equals(conferma.getText()))
            {
                avviso.setText("Le due password sono diverse");
                avviso.setForeground(Color.WHITE);
            }
            else if (!DatabaseManager.changePassword(LoginManager.username, vecchiaPassword.getText(), nuovaPassword.getText()))
            {
                avviso.setText("Username o password errata.");
                avviso.setForeground(Color.WHITE);
            }
            else {
                avviso.setText("Cambio password riuscito");
                avviso.setForeground(verde);
                chiudiPagina(page, false, 1200);
            }
        });
        panel.add(Box.createVerticalStrut(5));

        panel.add(avviso);
        backPanel.add(panel);
        page.add(backPanel);
        page.setVisible(true);
    }
}
