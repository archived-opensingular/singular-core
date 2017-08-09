package org.opensingular.lib.commons.ref;

import java.io.Serializable;
import java.util.Optional;

public final class Out<T extends Serializable> implements Serializable {
    private T value;
    public Out() {}
    public Out(T value) {
        set(value);
    }
    public T get() {
        return value;
    }
    public T set(T value) {
        T oldValue = this.value;
        this.value = value;
        return oldValue;
    }
    public Optional<T> asOptional() {
        return Optional.ofNullable(value);
    }
}
