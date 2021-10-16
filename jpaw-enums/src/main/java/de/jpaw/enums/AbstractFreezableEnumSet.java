package de.jpaw.enums;

import java.util.AbstractCollection;
import java.util.Collection;

public abstract class AbstractFreezableEnumSet<E extends Enum<E>> extends AbstractCollection<E> implements GenericEnumSetMarker<E> {
    private static final long serialVersionUID = 34398390989170000L + 66;

    // allow to make the set immutable
    private transient boolean _was$Frozen = false;      // current state of this instance

    @Override
    public final boolean was$Frozen() {
        return _was$Frozen;
    }
    protected final void verify$Not$Frozen() {
        if (_was$Frozen)
            throw new RuntimeException("Setter called for frozen instance of class " + getClass().getName());
    }
    @Override
    public final void freeze() {
        _was$Frozen = true;
    }

    /** Let this instance have the same contents as that. */
    @Override
    public final void assign(final Collection<E> that) {
        clear();
        if (that != null) {
            for (E o : that) {
                add(o);
            }
        }
    }
}
