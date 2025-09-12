package View;

import Controller.FileReader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Random;
import javax.swing.border.Border;

/**
 * Questa classe crea la schermata di caricamento iniziale.
 */
public class LoadingScreen extends JDialog {

    public static JProgressBar progressBar;

    /**
     * Costruttore.
     */
    public LoadingScreen()
    {
        Color rosso = new Color(175, 62, 62);

        setUndecorated(true);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Imposta il layout del pannello principale
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel panelDx = new JPanel();
        panelDx.setBackground(rosso);
        panelDx.setPreferredSize(new Dimension(100, 600));
        panel.add(panelDx, BorderLayout.EAST);

        JPanel panelSx = new JPanel();
        panelSx.setBackground(rosso);
        panelSx.setPreferredSize(new Dimension(100, 600));
        panel.add(panelSx, BorderLayout.WEST);

        JPanel panelInterno = new JPanel();
        panelInterno.setLayout(new BoxLayout(panelInterno, BoxLayout.Y_AXIS));
        panelInterno.setPreferredSize(new Dimension(600, 600));
        panelInterno.setBackground(rosso);

        panelInterno.add(Box.createVerticalStrut(130));

        ImageIcon logo = new ImageIcon("assets/DamoseLogo.png");
        JLabel imageLabel = new JLabel(logo);
        imageLabel.setSize(575, 175);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelInterno.add(imageLabel);
        panelInterno.add(Box.createVerticalStrut(10));

        // Aggiungi la progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressBar.setPreferredSize(new Dimension(100, 25));
        progressBar.setUI(new ProgressBarUI(Color.WHITE, rosso, Color.WHITE));

        Border border = BorderFactory.createLineBorder(rosso);

        progressBar.setBorder(border);


        panelInterno.add(progressBar);
        panelInterno.add(Box.createVerticalStrut(10));

        // Aggiungi il messaggio di testo
        JLabel loadingText = new JLabel("Caricamento in corso...", SwingConstants.CENTER);
        loadingText.setFont(new Font("Arial", Font.BOLD, 14));
        loadingText.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadingText.setForeground(Color.WHITE);
        panelInterno.add(loadingText);

        panelInterno.add(Box.createVerticalStrut(85));

        JLabel funFact = new JLabel("", SwingConstants.CENTER);
        funFact.setFont(new Font("Arial", Font.BOLD, 18));
        funFact.setAlignmentX(Component.CENTER_ALIGNMENT);
        funFact.setText(scegliFrase());
        funFact.setForeground(Color.WHITE);
        panelInterno.add(funFact);

        panel.add(panelInterno, BorderLayout.CENTER);
        add(panel);
    }

    /**
     * Aggiorna il valore della barra di caricamento.
     */
    public static void updateProgress(int value1, int value2, JProgressBar progressBar)
    {
        if (progressBar == null) return;

        if (value1 >= 0 && value2 <= 100)
        {for (int i = value1; i <= value2; i++) {
            progressBar.setValue(i);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println("Errore nel caricamento");
            }
        }}
    }

    String scegliFrase()
    {
        String[] lista;
        try {
            lista = FileReader.getFrasi("data/rome_static_gtfs/facts.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Random random = new Random();
        int num = random.nextInt(lista.length);

        return lista[num];
    }
}