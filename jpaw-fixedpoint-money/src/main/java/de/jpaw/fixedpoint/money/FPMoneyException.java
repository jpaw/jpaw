package de.jpaw.fixedpoint.money;

import de.jpaw.util.ApplicationException;

/**
 * The FPMoneyException class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Extends the generic ApplicationException class in order to provide error details which are
 *          specific to currency conversion and monetary rounding.
 */

public class FPMoneyException extends ApplicationException {
    /**
     *
     */
    private static final long serialVersionUID = 5464721260916479900L;

    private static final int OFFSET = (CL_PARAMETER_ERROR * CLASSIFICATION_FACTOR) + 19000; // offset for all codes in this class
    private static final int OFFSET_ILE = (CL_INTERNAL_LOGIC_ERROR * CLASSIFICATION_FACTOR) + 19000; // offset for all codes in this class

    static public final int ILLEGAL_CURRENCY_CODE        = OFFSET + 1;
    static public final int ILLEGAL_NUMBER_OF_DECIMALS   = OFFSET + 2;
    static public final int UNDEFINED_AMOUNTS            = OFFSET + 3;
    static public final int TAX_EXCEED_GROSS             = OFFSET + 4;
    static public final int SUM_MISMATCH                 = OFFSET + 5;
    static public final int ROUNDING_PROBLEM             = OFFSET + 6;
    static public final int SIGNS_DIFFER                 = OFFSET + 7;
    static public final int INCOMPATIBLE_OPERANDS        = OFFSET + 8;
    static public final int INCORRECT_NUMBER_TAX_AMOUNTS = OFFSET + 9;
    static public final int NOT_AN_ISO4217_CODE          = OFFSET + 10;

    static public final int UNEXPECTED_ROUNDING_PROBLEM  = OFFSET_ILE + 21;

    static {
        codeToDescription.put(ILLEGAL_CURRENCY_CODE        , "Currency code may not be null, must have 3 upper case characters length");
        codeToDescription.put(ILLEGAL_NUMBER_OF_DECIMALS   , "The number of fractional digits must be between 0 and 6");
        codeToDescription.put(UNDEFINED_AMOUNTS            , "Both gross and net amounts were null");
        codeToDescription.put(TAX_EXCEED_GROSS             , "Tax amounts exceed gross amount");
        codeToDescription.put(SUM_MISMATCH                 , "Gross, net and tax amounts provided, but the sum of net and tax does not match gross");
        codeToDescription.put(ROUNDING_PROBLEM             , "Problem during rounding (precision loss due to too many provided decimal digits)");
        codeToDescription.put(SIGNS_DIFFER                 , "The signs of tax and net amount are not consistent");
        codeToDescription.put(UNEXPECTED_ROUNDING_PROBLEM  , "Unexpected exception from constructor");
        codeToDescription.put(INCOMPATIBLE_OPERANDS        , "The operands differ in either currency or number of tax amounts");
        codeToDescription.put(INCORRECT_NUMBER_TAX_AMOUNTS , "Incorrect number of tax amounts supplied");
        codeToDescription.put(NOT_AN_ISO4217_CODE          , "No decimals supplied and provided code not an official ISO 4217 code");
    }

    public FPMoneyException(int errorCode, String message) {
        super(errorCode, message);
    }

    public FPMoneyException(int errorCode) {
        this(errorCode, null);
    }
}
