package View;

import Controller.Wifi;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.*;
import org.jxmapviewer.painter.Painter;

import org.jxmapviewer.cache.FileBasedLocalCache;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;
import java.awt.*;

public class Mappa
{
    public JXMapViewer mappa;

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
            TileFactoryInfo info = new OSMTileFactoryInfo("ZIP archive", "jar:file:/C:/Users/Dado/IdeaProjects/Damose/data/.jxmapviewer2/tile.openstreetmap.zip!");
            TileFactory tileFactory = new DefaultTileFactory(info);
            mappa.setTileFactory(tileFactory);
        }

        //Metti Roma al centro della mappa
        GeoPosition posizione_roma = new GeoPosition(41.90, 12.48);
        mappa.setAddressLocation(posizione_roma);
        mappa.setZoom(7);

        frame.add(mappa, BorderLayout.CENTER);

        MouseInputListener input_mouse = new PanMouseInputListener(mappa);
        mappa.addMouseListener(input_mouse);
        mappa.addMouseMotionListener(input_mouse);
        mappa.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mappa));
        mappa.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mappa));
        mappa.addKeyListener(new PanKeyListener(mappa));
    }

    public void set_painter(Painter p)
    {
        mappa.setOverlayPainter(p);
    }
}
