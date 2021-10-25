package de.jpaw.api.iso.impl;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Set;

import de.jpaw.api.iso.CurrencyData;
import de.jpaw.api.iso.CurrencyDataProvider;

public final class JavaCurrencyDataProvider implements CurrencyDataProvider {
    public static final JavaCurrencyDataProvider INSTANCE = new JavaCurrencyDataProvider();

    private JavaCurrencyDataProvider() { }

    public static final class JavaCurrencyData implements CurrencyData {
        private final Currency currency;
        private JavaCurrencyData(final Currency currency) {
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
    public CurrencyData get(final String key) {
        try {
            return new JavaCurrencyData(Currency.getInstance(key));
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public void set(final String key, final CurrencyData data) {
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
        final Set<Currency> allJavaCurrencies = Currency.getAvailableCurrencies();
        final List<CurrencyData> result = new ArrayList<>(allJavaCurrencies.size());
        for (final Currency c : allJavaCurrencies) {
            result.add(INSTANCE.get(c.getCurrencyCode()));
        }
        return result;
    }
}
