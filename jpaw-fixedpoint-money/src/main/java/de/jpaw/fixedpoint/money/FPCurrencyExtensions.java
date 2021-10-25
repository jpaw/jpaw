package de.jpaw.fixedpoint.money;

import de.jpaw.api.iso.impl.JavaCurrencyDataProvider;

public final class FPCurrencyExtensions {
   private FPCurrencyExtensions() { }

    public static FPCurrency EUR = new FPCurrency(JavaCurrencyDataProvider.INSTANCE.get("EUR"));

    // suffix-like methods, as Xtend syntax sugar
    public static FPAmount Euro(long gross) {
        return new FPAmount(EUR, gross * 100);
    }


}
