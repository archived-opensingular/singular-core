/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.document;

import org.opensingular.form.internal.util.SerializableReference;

/**
 * É uma referência a uma {@link SDocumentFactory} que pode ser serializada com
 * segurança e posteriormente (depois de deserializado) será capaz de localizar
 * a refência à factory, que em geral não é serializável. Particularmente útil
 * na integração do formulário com interfaces web.
 *
 * @author Daniel C. Bordin
 */
public abstract class RefSDocumentFactory extends SerializableReference<SDocumentFactory> {

    public RefSDocumentFactory() {
    }

    public RefSDocumentFactory(SDocumentFactory documentFactory) {
        super(documentFactory);
    }
}
