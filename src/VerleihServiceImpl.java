import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diese Klasse implementiert das Interface VerleihService. Siehe dortiger
 * Kommentar.
 * 
 * @author SE2-Team
 * @version SoSe 2021
 */
class VerleihServiceImpl extends AbstractObservableService
        implements VerleihService
{
    /**
     * Diese Map speichert für jedes eingefügte Medium die dazugehörige
     * Verleihkarte. Ein Zugriff auf die Verleihkarte ist dadurch leicht über
     * die Angabe des Mediums möglich. Beispiel: _verleihkarten.get(medium)
     */
    private Map<Medium, Verleihkarte> _verleihkarten;

    /**
     * Der Medienbestand.
     */
    private MedienbestandService _medienbestand;

    /**
     * Der Kundenstamm.
     */
    private KundenstammService _kundenstamm;

    /**
     * Konstruktor. Erzeugt einen neuen VerleihServiceImpl.
     * 
     * @param kundenstamm Der KundenstammService.
     * @param medienbestand Der MedienbestandService.
     * @param initialBestand Der initiale Bestand.
     * 
     */
    public VerleihServiceImpl(KundenstammService kundenstamm,
            MedienbestandService medienbestand,
            List<Verleihkarte> initialBestand)
    {
        _verleihkarten = erzeugeVerleihkartenBestand(initialBestand);
        _kundenstamm = kundenstamm;
        _medienbestand = medienbestand;
    }

    @Override
	public void verleiheAn(Kunde kunde, List<Medium> medien, Datum ausleihDatum)
	{
        // Vorbedingung: Der Kunde muss im Kundenstamm vorhanden sein.
        // Ohne diese Prüfung könnten Medien an einen nicht existierenden Kunden verliehen werden.j
        assert kundeImBestand(kunde) : "Vorbedingung verletzt: Kunde ist nicht im Bestand";

        // Vorbedingung: Alle Medien müssen derzeit NICHT verliehen sein.
        // Nur nicht-verliehene Medien dürfen verliehen werden.
        assert sindAlleNichtVerliehen(medien) : "Vorbedingung verletzt: Einige Medien sind bereits verliehen";

        // Vorbedingung: Das Ausleihdatum darf nicht null sein.
        // Es wird zur Berechnung der Rückgabefrist und Verleihkarte benötigt.
        assert ausleihDatum != null : "Vorbedingung verletzt: ausleihDatum ist null";

	    for (Medium medium : medien)
	    {
	        Verleihkarte karte = new Verleihkarte(kunde, medium, ausleihDatum);
	
	    }
	
	    informiereUeberAenderung();
	}

	@Override
	public boolean istVerleihenMoeglich(Kunde kunde, List<Medium> medien)
	{

	    // Der Kunde muss existieren.
	    assert kundeImBestand(kunde) : "Vorbedingung verletzt: Kunde nicht im Bestand";

	    // Es muss sich um bekannte Medien handeln.
	    assert medienImBestand(medien) : "Vorbedingung verletzt: Medien nicht im Bestand";

	    return sindAlleNichtVerliehen(medien);
	}

	@Override
	public Kunde getEntleiherFuer(Medium medium)
	{
	    // Das Medium muss derzeit verliehen sein, sonst gibt es keinen Entleiher.
	    assert istVerliehen(medium) : "Vorbedingung verletzt: Medium ist nicht verliehen";


	    Verleihkarte verleihkarte = _verleihkarten.get(medium);
	    return verleihkarte.getEntleiher();
	}

	@Override
	public List<Medium> getAusgelieheneMedienFuer(Kunde kunde)
	{
	    // Der Kunde muss im System registriert sein.
	    assert kundeImBestand(kunde) : "Vorbedingung verletzt: Kunde nicht im Bestand";

	    List<Medium> result = new ArrayList<Medium>();
	    for (Verleihkarte verleihkarte : _verleihkarten.values())
	    {
	        if (verleihkarte.getEntleiher()
	            .equals(kunde))
	        {
	            result.add(verleihkarte.getMedium());
	        }
	    }
	    return result;
	}

	@Override
    public List<Verleihkarte> getVerleihkarten()
    { 

        return new ArrayList<Verleihkarte>(_verleihkarten.values());
    }

    @Override
	public void nimmZurueck(List<Medium> medien, Datum rueckgabeDatum)
	{
     // Die Medien müssen derzeit verliehen sein.
        assert sindAlleVerliehen(medien) : "Vorbedingung verletzt: Einige Medien sind nicht verliehen";
        
	    for (Medium medium : medien)
	    {
	        _verleihkarten.remove(medium);
	    }
	    informiereUeberAenderung();
	}

	@Override
    public boolean istVerliehen(Medium medium)
    {
	    // Das Medium muss bekannt sein.
	    assert mediumImBestand(medium) : "Vorbedingung verletzt: Medium nicht im Bestand";

        return _verleihkarten.get(medium) != null;
    }

    @Override
    public boolean sindAlleNichtVerliehen(List<Medium> medien)
    {
        // Es darf keine unbekannten Medien geben.
        assert medienImBestand(medien) : "Vorbedingung verletzt: Medien nicht im Bestand";

        boolean result = true;
        for (Medium medium : medien)
        {
            if (istVerliehen(medium))
            {
                result = false;
            }
        }
        return result;
    }

    @Override
    public boolean sindAlleVerliehen(List<Medium> medien)
    {
        // Es darf keine unbekannten Medien geben.
        assert medienImBestand(medien) : "Vorbedingung verletzt: Medien nicht im Bestand";

        boolean result = true;
        for (Medium medium : medien)
        {
            if (!istVerliehen(medium))
            {
                result = false;
            }
        }
        return result;
    }

    @Override
    public boolean kundeImBestand(Kunde kunde)
    {
        // Der Kunde muss im System registriert sein.
        assert kundeImBestand(kunde) : "Vorbedingung verletzt: Kunde nicht im Bestand";

        return _kundenstamm.enthaeltKunden(kunde);
    }

    @Override
    public boolean mediumImBestand(Medium medium)
    {
        // Das übergebene Medium darf nicht null sein.
        assert medium != null : "Vorbedingung verletzt: medium ist null";

        return _medienbestand.enthaeltMedium(medium);
    }

    @Override
    public boolean medienImBestand(List<Medium> medien)
    {
        // Die Medienliste darf nicht null sein.
        assert medien != null : "Vorbedingung verletzt: medien ist null";

        // Die Medienliste darf nicht leer sein.
        assert !medien.isEmpty() : "Vorbedingung verletzt: medien ist leer";

        boolean result = true;
        for (Medium medium : medien)
        {
            if (!mediumImBestand(medium))
            {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
	public List<Verleihkarte> getVerleihkartenFuer(Kunde kunde)
	{
        // Der Kunde muss im System registriert sein.
        assert kundeImBestand(kunde) : "Vorbedingung verletzt: Kunde ist nicht im Bestand";

	    List<Verleihkarte> result = new ArrayList<Verleihkarte>();
	    for (Verleihkarte verleihkarte : _verleihkarten.values())
	    {
	        if (verleihkarte.getEntleiher()
	            .equals(kunde))
	        {
	            result.add(verleihkarte);
	        }
	    }
	    return result;
	}

	@Override
    public Verleihkarte getVerleihkarteFuer(Medium medium)
    {
	    // Das Medium muss derzeit verliehen sein, sonst gibt es keine Verleihkarte.
	    assert istVerliehen(medium) : "Vorbedingung verletzt: Medium ist nicht verliehen";

        return _verleihkarten.get(medium);
    }

    /**
	 * Erzeugt eine neue HashMap aus dem Initialbestand.
	 */
	private HashMap<Medium, Verleihkarte> erzeugeVerleihkartenBestand(
	        List<Verleihkarte> initialBestand)
	{
	    HashMap<Medium, Verleihkarte> result = new HashMap<Medium, Verleihkarte>();
	    for (Verleihkarte verleihkarte : initialBestand)
	    {
	        result.put(verleihkarte.getMedium(), verleihkarte);
	    }
	    return result;
	}

}
