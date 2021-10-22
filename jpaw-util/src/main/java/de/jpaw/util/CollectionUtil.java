package de.jpaw.util;

public final class CollectionUtil {

    private CollectionUtil() { }

    /** Pick a size for a map which avoids resizing for the default load factor (0.75).
     * Also leave some gap...
     * @param numEntries The number of expected entries
     * @return the size which allows a map without resizing
     */
    public static int mapInitialSize(int numEntries) {
        return numEntries <= 0 ? 0 : numEntries + ((1 + numEntries) >> 1);      // good for a load factor as low as 0.6667
    }
}
