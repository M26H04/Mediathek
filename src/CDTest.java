import org.junit.Test;

import static org.junit.Assert.*;

public class CDTest
{
    private static final String KOMMENTAR = "Kommentar";
    private static final String TITEL = "Titel";
    private static final String BEZEICHNUNG = "CD";
    private static final String INTERPRET = "CD Interpret";
    private static final int LAENGE = 100;
    private CD _cd1;
    private CD _cd2;

    public CDTest()
    {
        _cd1 = getMedium();
        _cd2 = getMedium();
    }

    @Test
    public void testGetMedienBezeichnung()
    {
        assertEquals(BEZEICHNUNG, _cd1.getMedienBezeichnung());
    }

    @Test
    public void testKonstruktor()
    {
        assertEquals(TITEL, _cd1.getTitel());
        assertEquals(KOMMENTAR, _cd1.getKommentar());
        assertEquals(LAENGE, _cd1.getSpiellaenge());
        assertEquals(INTERPRET, _cd1.getInterpret());
    }

    @Test
    /*
     * Von ein und der selben CD kann es mehrere Exemplare geben, die von
     * unterschiedlichen Personen ausgeliehen werden können.
     */
    public void testEquals()
    {
        assertNotEquals("Mehrere Exemplare der gleichen CD sollten ungleich sein", _cd1, _cd2);
        assertEquals("Dasselbe Exemplare der gleichen CD sollte gleich sein", _cd1, _cd1);
    }

    private CD getMedium()
    {
        return new CD(TITEL, KOMMENTAR, INTERPRET, LAENGE);
    }

}
