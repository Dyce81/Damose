package View;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.geom.Rectangle2D;

class ProgressBarUI extends BasicProgressBarUI {

    private final Color foregroundColor;
    private final Color backgroundColor;
    private Color stringColor;

    public ProgressBarUI(Color foreground, Color background, Color stringColor) {
        this.foregroundColor = foreground;
        this.backgroundColor = background;
        this.stringColor = stringColor;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, progressBar.getWidth(), progressBar.getHeight());

        if ( (progressBar.getPercentComplete()*100) > 48)
        {
            stringColor = new Color(175, 62, 62);
        }

        int progressWidth = (int) (progressBar.getPercentComplete() * progressBar.getWidth());
        g2.setColor(foregroundColor);
        g2.fillRect(0, 0, progressWidth, progressBar.getHeight());

        if (progressBar.isStringPainted()) {
            g2.setColor(stringColor);
            g2.setFont(progressBar.getFont());

            String progressText = progressBar.getString();
            FontMetrics metrics = g2.getFontMetrics();
            Rectangle2D textBounds = metrics.getStringBounds(progressText, g2);

            int textX = (int) ((progressBar.getWidth() - textBounds.getWidth()) / 2);
            int textY = (int) ((progressBar.getHeight() - textBounds.getHeight()) / 2 + metrics.getAscent());

            g2.drawString(progressText, textX, textY);
        }

        g2.dispose();
    }
}
