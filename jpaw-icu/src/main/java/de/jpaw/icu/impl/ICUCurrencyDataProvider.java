package de.jpaw.icu.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ibm.icu.util.Currency;

import de.jpaw.api.iso.CurrencyData;
import de.jpaw.api.iso.CurrencyDataProvider;

public final class ICUCurrencyDataProvider implements CurrencyDataProvider {
    public static final ICUCurrencyDataProvider INSTANCE = new ICUCurrencyDataProvider();

    private ICUCurrencyDataProvider() { }

    public static final class ICUCurrencyData implements CurrencyData {
        private final Currency currency;
        private ICUCurrencyData(Currency currency) {
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
            return new ICUCurrencyData(Currency.getInstance(key));
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
        Set<Currency> allICUCurrencies = Currency.getAvailableCurrencies();
        List<CurrencyData> result = new ArrayList<CurrencyData>(allICUCurrencies.size());
        for (Currency c : allICUCurrencies) {
            result.add(INSTANCE.get(c.getCurrencyCode()));
        }
        return result;
    }
}
