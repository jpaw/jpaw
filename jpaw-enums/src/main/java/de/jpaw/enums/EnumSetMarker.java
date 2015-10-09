package de.jpaw.enums;

/** Marker interface, implemented by all EnumSet types, to allow quick detection. */
public interface EnumSetMarker {
    // utility string for conversion between integral enum sets and alphanumeric ones, if the tokens are the ones from below (up to 63 characters = bits in unsigned long)
    public static final String STANDARD_TOKENS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_";
}
