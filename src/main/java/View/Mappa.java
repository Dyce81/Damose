package View;

import Controller.WiFi;

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
import java.util.List;

public class Mappa
{
    private static JXMapViewer mappa;
    private static Painter<JXMapViewer> painter = null;
    private static boolean compound = false;

    private static final TileFactoryInfo info = new OSMTileFactoryInfo();
    private static final DefaultTileFactory tileFactory = new DefaultTileFactory(info);

    private static final TileFactoryInfo offlineInfo = new OSMTileFactoryInfo("offline", "file:///" + System.getProperty("user.home").replace('\\', '/') + "/.jxmapviewer2/tile.openstreetmap.org");
    private static final TileFactory offlineTileFactory = new DefaultTileFactory(offlineInfo);

    //costruttore
    public Mappa(JFrame frame)
    {
        mappa = new JXMapViewer();

        if (WiFi.connesso())
        {
            mappa.setTileFactory(tileFactory);

            tileFactory.setThreadPoolSize(8);

            //Imposta una cache per le tiles
            File cacheDir = new File(System.getProperty("user.home").replace('\\', '/') + "/.jxmapviewer2/tile.openstreetmap");
            tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, false));
        }
        else
        {
            mappa.setTileFactory(offlineTileFactory);
        }

        //Metti Roma al centro della mappa
        GeoPosition posizioneRoma = new GeoPosition(41.90, 12.48);
        mappa.setAddressLocation(posizioneRoma);
        mappa.setZoom(3);

        frame.add(mappa, BorderLayout.CENTER);

        MouseInputListener input_mouse = new PanMouseInputListener(mappa);
        mappa.addMouseListener(input_mouse);
        mappa.addMouseMotionListener(input_mouse);
        mappa.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mappa));
        mappa.addKeyListener(new PanKeyListener(mappa));
    }

    public void impostaPosizione(double latitude, double longitude)
    {
        GeoPosition posizione = new GeoPosition(latitude, longitude);
        mappa.setAddressLocation(posizione);
        mappa.setZoom(2);
    }

    public static void setPainter(Painter<JXMapViewer> p)
    {
        //if (compound) return;
        if (painter == null) painter = p;

        mappa.setOverlayPainter(p);
    }

    public static JXMapViewer getMapViewer()
    {
        return mappa;
    }

    public void cambiaStatoMappa()
    {
        if (WiFi.connesso())
        {
            if (mappa.getTileFactory() != tileFactory)
                mappa.setTileFactory(tileFactory);
        }
        else
        {
            if (mappa.getTileFactory() != offlineTileFactory)
                mappa.setTileFactory(offlineTileFactory);
        }
    }

    public static void disegnaLinea(List<GeoPosition> percorso)
    {
        // Viene creato un painter con la route disegnate + il painter attualmente utilizzato
        // (quello con i waypoint)

        //TODO: il campo compound è commentato perché non ricordo cosa fa - comunque per adesso
        // non pare influenzare nulla, quindi continuiamo ad usare il programma, e se non accade
        // niente di male, lo togliamo
        RoutePainter rPainter = new RoutePainter(percorso);
        List<Painter<JXMapViewer>> painters = new ArrayList<>();
        painters.add(painter);
        painters.add(rPainter);
        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
        //compound = false;
        setPainter(painter);
        //compound = true;

        //mappa.zoomToBestFit(new HashSet<>(percorso), 0.7);
    }
}