import Controller.StaticGTFS;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

//import org.junit.Test;

//import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.*;

class StaticGTFSTest
{
    // Le linee delle metro sono 248, 249, 305, 342

    @Test
    void lineaDellaMetroNonDeveEsistere()
    {
        StaticGTFS.inizializzaDati();
        assertFalse(StaticGTFS.lineaDellaMetro("0"));
    }

    @Test
    void lineaDellaMetroCorretta()
    {
        StaticGTFS.inizializzaDati();
        assertTrue(StaticGTFS.lineaDellaMetro("248"));
    }

}