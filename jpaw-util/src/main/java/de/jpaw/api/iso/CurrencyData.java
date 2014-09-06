package de.jpaw.api.iso;

/** Specifies the properties which can be retrieved for a given currency.
 * The data can be retrieved from the standard Java Currency class, the ICU currency class, or a database based provider.
 * 
 * @author Michael Bischoff
 *
 */
public interface CurrencyData {
    /** Returns the ISO 4217 code. */
    public String getCurrencyCode();

    /** Returns the ISO numeric code. */
    public int getNumericCode();

    /** Returns the currency symbol, for the default locale. */
    public String getSymbol();
    
    /** Returns the description, for the default locale. */
    public String getDisplayName();
    
    /** Returns the default number of decimals. */
    public int getDefaultFractionDigits();

}
