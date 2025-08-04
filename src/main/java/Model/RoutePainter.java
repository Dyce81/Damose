package Model;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class RoutePainter implements Painter<JXMapViewer>
{
    private final List<GeoPosition> percorso;

    public RoutePainter(List<GeoPosition> percorso)
    {
        this.percorso = percorso;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int width, int height) {
        g = (Graphics2D) g.create();

        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(4));

        drawRoute(g, map);

        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(3));

        drawRoute(g, map);

        g.dispose();
    }

    private void drawRoute(Graphics2D g, JXMapViewer map)
    {
        int lastX = 0;
        int lastY = 0;

        boolean primo = true;

        for (GeoPosition gp : percorso)
        {
            Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());

            if (primo)
                primo = false;
            else
                g.drawLine(lastX, lastY, (int) pt.getX(), (int) pt.getY());

            lastX = (int) pt.getX();
            lastY = (int) pt.getY();
        }
    }
}
