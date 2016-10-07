/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.io;

import org.opensingular.form.SInstance;
import org.opensingular.form.internal.xml.MElement;

public class PersistenceBuilderXML {

    private boolean persistId = true;
    private boolean persistNull = false;
    private boolean persistAttributes = false;

    public PersistenceBuilderXML withPersistId(boolean v) {
        persistId = v;
        return this;
    }

    public PersistenceBuilderXML withPersistNull(boolean v) {
        persistNull = v;
        return this;
    }

    public PersistenceBuilderXML withPersistAttributes(boolean v) {
        persistAttributes = v;
        return this;
    }

    public boolean isPersistId() {
        return persistId;
    }

    public boolean isPersistNull() {
        return persistNull;
    }

    public boolean isPersistAttributes() {
        return persistAttributes;
    }

    public MElement toXML(SInstance instancia) {
        return MformPersistenciaXML.toXML(null, null, instancia, this);
    }

}
