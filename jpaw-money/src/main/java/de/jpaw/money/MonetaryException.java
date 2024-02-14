/*
 * Copyright 2012 Michael Bischoff
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jpaw.money;

import de.jpaw.util.ApplicationException;

/**
 * The MonetaryException class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Extends the generic ApplicationException class in order to provide error details which are
 *          specific to currency conversion and monetary rounding.
 */

public class MonetaryException extends ApplicationException {
    /**
     *
     */
    private static final long serialVersionUID = 5464727960916479900L;

    private static final int OFFSET = (CL_PARAMETER_ERROR * CLASSIFICATION_FACTOR) + 18000; // offset for all codes in this class
    private static final int OFFSET_ILE = (CL_INTERNAL_LOGIC_ERROR * CLASSIFICATION_FACTOR) + 18000; // offset for all codes in this class

    public static final int ILLEGAL_CURRENCY_CODE        = OFFSET + 1;
    public static final int ILLEGAL_NUMBER_OF_DECIMALS   = OFFSET + 2;
    public static final int UNDEFINED_AMOUNTS            = OFFSET + 3;
    public static final int TAX_EXCEED_GROSS             = OFFSET + 4;
    public static final int SUM_MISMATCH                 = OFFSET + 5;
    public static final int ROUNDING_PROBLEM             = OFFSET + 6;
    public static final int SIGNS_DIFFER                 = OFFSET + 7;
    public static final int INCOMPATIBLE_OPERANDS        = OFFSET + 8;
    public static final int INCORRECT_NUMBER_TAX_AMOUNTS = OFFSET + 9;
    public static final int NOT_AN_ISO4217_CODE          = OFFSET + 10;

    public static final int UNEXPECTED_ROUNDING_PROBLEM  = OFFSET_ILE + 21;

    static {
        registerRange(OFFSET, false, MonetaryException.class, ApplicationLevelType.CORE_LIBRARY);

        registerCode(ILLEGAL_CURRENCY_CODE,         "Currency code may not be null, must have 3 upper case characters length");
        registerCode(ILLEGAL_NUMBER_OF_DECIMALS,    "The number of fractional digits must be between 0 and 6");
        registerCode(UNDEFINED_AMOUNTS,             "Both gross and net amounts were null");
        registerCode(TAX_EXCEED_GROSS,              "Tax amounts exceed gross amount");
        registerCode(SUM_MISMATCH,                  "Gross, net and tax amounts provided, but the sum of net and tax does not match gross");
        registerCode(ROUNDING_PROBLEM,              "Problem during rounding (precision loss due to too many provided decimal digits)");
        registerCode(SIGNS_DIFFER,                  "The signs of tax and net amount are not consistent");
        registerCode(UNEXPECTED_ROUNDING_PROBLEM,   "Unexpected exception from constructor");
        registerCode(INCOMPATIBLE_OPERANDS,         "The operands differ in either currency or number of tax amounts");
        registerCode(INCORRECT_NUMBER_TAX_AMOUNTS,  "Incorrect number of tax amounts supplied");
        registerCode(NOT_AN_ISO4217_CODE,           "No decimals supplied and provided code not an official ISO 4217 code");
    }

    public MonetaryException(int errorCode, String message) {
        super(errorCode, message);
    }

    public MonetaryException(int errorCode) {
        this(errorCode, null);
    }
}
