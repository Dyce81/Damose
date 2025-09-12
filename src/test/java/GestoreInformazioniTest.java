import Controller.GestoreInformazioni;
import View.Frame;
import View.PannelloInformazioni;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GestoreInformazioniTest
{
    @Test
    void ZeroShouldReturnTram()
    {
        var gestore = new GestoreInformazioni(new Frame(0,0, "test"), new PannelloInformazioni());
        assertEquals("Tram", gestore.getTipoMezzoString(0));
    }

    @Test
    void ThreeShouldReturnAutobus()
    {
        var gestore = new GestoreInformazioni(new Frame(0,0, "test"), new PannelloInformazioni());
        assertEquals("Autobus", gestore.getTipoMezzoString(3));
    }
}