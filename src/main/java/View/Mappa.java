package View;

import Controller.Wifi;

import Model.RoutePainter;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.*;
import org.jxmapviewer.painter.Painter;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Mappa
{
    public static JXMapViewer mappa;
    public static Painter<JXMapViewer> painter = null;
    public static boolean compound = false;

    //costruttore
    Mappa(JFrame frame)
    {
        mappa = new JXMapViewer();

        if (Wifi.WiFi)
        {
            TileFactoryInfo info = new OSMTileFactoryInfo();
            DefaultTileFactory tileFactory = new DefaultTileFactory(info);
            mappa.setTileFactory(tileFactory);

            tileFactory.setThreadPoolSize(8);

            //Imposta una cache per le tiles
            File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
            tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, false));
        }
        else
        {
            TileFactoryInfo info = new OSMTileFactoryInfo("offline", System.getProperty("user.home") + File.separator + ".jxmapviewer2/tile.openstreetmap");
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
        mappa.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mappa));
        mappa.addKeyListener(new PanKeyListener(mappa));
    }

    public void cambia_posizione(double latitude, double longitude)
    {
        //imposta la posizione effettiva
        GeoPosition posizione = new GeoPosition(latitude, longitude);
        mappa.setAddressLocation(posizione);
        mappa.setZoom(2);
    }

    public static void set_painter(Painter<JXMapViewer> p)
    {
        if (compound) return;
        if (painter == null) painter = p; //se painter non è già stato definito, impostalo correttamente

        mappa.setOverlayPainter(p);
    }

    public static JXMapViewer getMapViewer()
    {
        return mappa;
    }

    public static void disegnaLinea(List<GeoPosition> percorso)
    {
        RoutePainter rPainter = new RoutePainter(percorso);
        List<Painter<JXMapViewer>> painters = new ArrayList<>();
        painters.add(painter);
        painters.add(rPainter);
        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
        compound = false;
        set_painter(painter);
        compound = true;

        //mappa.zoomToBestFit(new HashSet<>(percorso), 0.7);
    }
}