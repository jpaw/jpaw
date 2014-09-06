package de.jpaw.api.iso.impl;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Set;

import de.jpaw.api.iso.CurrencyData;
import de.jpaw.api.iso.CurrencyDataProvider;

public class JavaCurrencyDataProvider implements CurrencyDataProvider {
    static public final JavaCurrencyDataProvider instance = new JavaCurrencyDataProvider();
    private JavaCurrencyDataProvider() {
    }
    
    public static class JavaCurrencyData implements CurrencyData {
        private final Currency currency;
        private JavaCurrencyData(Currency currency) {
            this.currency = currency;
        }

        @Override
        public String getCurrencyCode() {
            return currency.getCurrencyCode();
        }

        @Override
        public int getNumericCode() {
            return currency.getNumericCode();
        }

        @Override
        public String getSymbol() {
            return currency.getSymbol();
        }

        @Override
        public String getDisplayName() {
            return currency.getDisplayName();
        }

        @Override
        public int getDefaultFractionDigits() {
            return currency.getDefaultFractionDigits();
        }
        
    }
    @Override
    public CurrencyData get(String key) {
        try {
            return new JavaCurrencyData(Currency.getInstance(key)); 
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public void set(String key, CurrencyData data) {
        throw new UnsupportedOperationException("Cannot create new currencies");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Cannot clear the currency list");
    }

    @Override
    public void init() {
    }

    @Override
    public List<CurrencyData> getAll() {
        Set<Currency> allJavaCurrencies = Currency.getAvailableCurrencies();
        List<CurrencyData> result = new ArrayList<CurrencyData>(allJavaCurrencies.size());
        for (Currency c : allJavaCurrencies)
            result.add(instance.get(c.getCurrencyCode()));
        return result;
    }
}
