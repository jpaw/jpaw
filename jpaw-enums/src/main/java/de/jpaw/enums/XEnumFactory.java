package de.jpaw.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Factory class which returns an XEnum instance for a given token or name.
 * It is not a factory in the classic sense that a new object is created, rather the unique instance for that token is returned.
 * There is one instance of this class per XEnum class. */
public class XEnumFactory<E extends AbstractXEnumBase<E>> {
    private final int maxTokenLength;
    private final String pqon;              // partially qualified class name of the base
    private final Class<E> baseClass;
    private final Map<String, E> tokenToXEnum = new ConcurrentHashMap<>();
    private final Map<String, E> nameToXEnum = new ConcurrentHashMap<>();
    private final Map<Enum<?>, E> baseEnumToXEnum = new ConcurrentHashMap<>();
    private static final Map<String, XEnumFactory<?>> REGISTRY = new ConcurrentHashMap<>(200);
    private static final Map<Class<? extends AbstractXEnumBase<?>>, XEnumFactory<?>> CLASS_REGISTRY
      = new ConcurrentHashMap<>(200);
    // private final List<Class<? extends E>> listOfSubclasses = new ArrayList<E>(10);   // remembers all XEnum classes which use this factory
    private E nullToken = null;     // stores an instance which has the empty token

    // TODO: should only be invoked from XEnum classes. How to verify this? (C++ "friend" needed here...)
    public XEnumFactory(final int maxTokenLength, final Class<E> baseClass, final String pqon) {
        this.maxTokenLength = maxTokenLength;
        this.pqon = pqon;
        this.baseClass = baseClass;
        // "this" is not yet fully constructed...
//      if (REGISTRY.put(pqon, this) != null) {
//          throw something;
//      };
        // we do it later instead...
    }
    public static final XEnumFactory<?> getFactoryByPQON(final String pqon) {
        return REGISTRY.get(pqon);
    }
    public static final XEnumFactory<?> getFactoryByClass(final Class<? extends AbstractXEnumBase<?>> xenumClass) {
        final XEnumFactory<?> result = CLASS_REGISTRY.get(xenumClass);
        if (result == null)
            throw new IllegalArgumentException("No XEnumFactory registered for class " + xenumClass.getCanonicalName());
        return result;
    }
    public void publishInstance(final E e) {
        if (tokenToXEnum.put(e.getToken(), e) != null)
            throw new IllegalArgumentException(e.getClass().getSimpleName() + ": duplicate token " + e.getToken() + " for base XEnum " + pqon);
        if (nameToXEnum.put(e.name(), e) != null)
            throw new IllegalArgumentException(e.getClass().getSimpleName() + ": duplicate name " + e.name() + " for base XEnum " + pqon);
        baseEnumToXEnum.put(e.getBaseEnum(), e);
        // possibly store it as the null token
        if (e.getToken().length() == 0)
            nullToken = e;
    }
    public void register(final String thisPqon, final Class<? extends AbstractXEnumBase<E>> xenumClass) {
        REGISTRY.put(thisPqon, this);
        CLASS_REGISTRY.put(xenumClass, this);
    }

    public Class<E> getBaseClass() {
        return baseClass;
    }
    public E getByToken(final String token) {
        return tokenToXEnum.get(token);
    }
    public E getByName(final String name) {
        return nameToXEnum.get(name);
    }
    public E getByEnum(final Enum<?> enumVal) {
        return baseEnumToXEnum.get(enumVal);
    }
    // return an instance which has the token "", or null if no such exists
    public E getNullToken() {
        return nullToken;
    }
    public int getMaxTokenLength() {
        return maxTokenLength;
    }
    public String getPqon() {
        return pqon;
    }
    public E getByTokenWithNull(final String token) {
        return token == null ? nullToken : tokenToXEnum.get(token);
    }
    // same as getByEnum, but throw an exception if the instance isn't known
    public E of(final Enum<?> enumVal) {
        if (enumVal == null)
            return null;
        final E myEnum = baseEnumToXEnum.get(enumVal);
        if (myEnum == null)
            throw new IllegalArgumentException(
              enumVal.getClass().getSimpleName() + "." + enumVal.name() + " is not a valid instance for " + baseClass.getSimpleName());
        return myEnum;
    }

    // array conversion
    public E[] of(final Enum<?>[] arrayOfEnums) {
        if (arrayOfEnums == null)
            return null;
        // the following line does not compile. Why not? B the bound, the lower object type should be known!
        //E[] result = new E [arrayOfEnums.length];
        // the following line does compile, but has a warning of course
        @SuppressWarnings("unchecked")
        final E[] result = (E[]) new AbstractXEnumBase[arrayOfEnums.length];
        for (int i = 0; i < arrayOfEnums.length; ++i) {
            result[i] = of(arrayOfEnums[i]);
        }
        return result;
    }
    public List<E> of(final List<Enum<?>> listOfEnums) {
        if (listOfEnums == null)
            return null;
        final List<E> result = new ArrayList<>(listOfEnums.size());
        for (final Enum<?> i : listOfEnums) {
            result.add(of(i));
        }
        return result;
    }
    public Set<E> of(final Set<Enum<?>> setOfEnums) {
        if (setOfEnums == null)
            return null;
        final Set<E> result = new HashSet<>(setOfEnums.size());
        for (final Enum<?> i : setOfEnums) {
            result.add(of(i));
        }
        return result;
    }

    /** Returns the number of different instances for this xenum. */
    public int size() {
        return tokenToXEnum.size();
    }

    /** Returns a copy of the list of values. */
    public List<E> valuesAsList() {
        return Collections.unmodifiableList(new ArrayList<>(tokenToXEnum.values()));  // cast should not be required...
    }
}
