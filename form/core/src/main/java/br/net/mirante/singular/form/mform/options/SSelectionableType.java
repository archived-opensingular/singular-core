/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.SType;

@SuppressWarnings({ "rawtypes" })
public interface SSelectionableType<BASE extends SType>  extends SSelectionable {

    public SOptionsProvider getOptionsProvider();

    /**
     * This method is API internal use only, do not call it.
     * Use withSelection* methods instead.
     * @param p
     *  MoptionsProvider instance or lambda.
     */
    public void setOptionsProvider(SOptionsProvider p);

    default public boolean hasOptionsProvider() {
        return getOptionsProvider() != null;
    }


}
