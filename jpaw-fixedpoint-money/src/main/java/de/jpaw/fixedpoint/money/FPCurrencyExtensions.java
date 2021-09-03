package de.jpaw.fixedpoint.money;

import de.jpaw.api.iso.impl.JavaCurrencyDataProvider;

public class FPCurrencyExtensions {
    public static FPCurrency EUR = new FPCurrency(JavaCurrencyDataProvider.instance.get("EUR"));

    // suffix-like methods, as Xtend syntax sugar
    static public FPAmount Euro(long gross) {
        return new FPAmount(EUR, gross * 100);
    }


}
