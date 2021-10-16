package de.jpaw.enums;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/** Marker interface, implemented by all EnumSet types, to allow quick detection. */
public interface GenericEnumSetMarker<E extends Enum<E>> extends Set<E>, Serializable, EnumSetMarker {

    /** Let this instance have the same contents as that. */
    void assign(Collection<E> that);
}
