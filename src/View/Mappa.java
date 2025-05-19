package View;

import Controller.Wifi;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.*;
import org.jxmapviewer.painter.Painter;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Mappa
{
    private int zoom = 3;
    public JXMapViewer mappa;
    public Painter painter = null;

    //costruttore
    Mappa(JFrame frame)
    {
        mappa = new JXMapViewer();

        if (Wifi.wifi_connesso())
        {
            TileFactoryInfo info = new OSMTileFactoryInfo();
            DefaultTileFactory tileFactory = new DefaultTileFactory(info);
            mappa.setTileFactory(tileFactory);

            tileFactory.setThreadPoolSize(8);
        }
        else
        {
            TileFactoryInfo info = new OSMTileFactoryInfo("ZIP archive", "jar:file:/././data/.jxmapviewer2/tile.openstreetmap.zip!");
            TileFactory tileFactory = new DefaultTileFactory(info);
            mappa.setTileFactory(tileFactory);
        }

        //Metti Roma al centro della mappa
        GeoPosition posizione_roma = new GeoPosition(41.90, 12.48);
        mappa.setAddressLocation(posizione_roma);
        mappa.setZoom(3);

        frame.add(mappa, BorderLayout.CENTER);

        MouseInputListener input_mouse = new PanMouseInputListener(mappa);
        mappa.addMouseListener(input_mouse);
        mappa.addMouseMotionListener(input_mouse);
        mappa.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mappa)
        {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                // non ottimale
                int dir = e.getWheelRotation();
                zoom += 1 * dir;
                mappa.setZoom(zoom);
                if (zoom < 5) {
                    set_painter(painter);
                } else {
                    set_painter(null);
                }

            }
        });
        mappa.addKeyListener(new PanKeyListener(mappa));
    }

    public void set_painter(Painter p)
    {
        if (painter == null) painter = p; // se painter non è già stato definito, impostalo correttamente
        mappa.setOverlayPainter(p);
    }
}
