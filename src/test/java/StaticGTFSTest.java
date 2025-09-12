import Controller.StaticGTFS;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StaticGTFSTest
{
    // Le linee delle metro sono 248, 249, 305, 342

    @Test
    void lineaDellaMetroCorretta()
    {
        StaticGTFS.inizializzaDati();
        assertTrue(StaticGTFS.lineaDellaMetro("248"));
    }

}