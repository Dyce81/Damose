package View;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;

public class Mappa
{
    //costruttore
    Mappa(JFrame frame)
    {
        //Definizione mappa - TODO: commentare tutto
        JXMapViewer mappa = new JXMapViewer();

        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mappa.setTileFactory(tileFactory);

        tileFactory.setThreadPoolSize(8);

        //Metti Roma al centro della mappa
        GeoPosition posizione_roma = new GeoPosition(41.90, 12.48);
        mappa.setAddressLocation(posizione_roma);
        mappa.setZoom(7);

        frame.add(mappa, BorderLayout.CENTER);

        MouseInputListener input_mouse = new PanMouseInputListener(mappa);
        mappa.addMouseListener(input_mouse);
        mappa.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mappa));
        mappa.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mappa));
    }
}
