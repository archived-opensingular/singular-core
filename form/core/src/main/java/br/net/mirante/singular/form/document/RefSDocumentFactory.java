/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.document;

import br.net.mirante.singular.form.internal.util.SerializableReference;

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