/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.context.SingularFormContext;

public interface SingularFormContextWicket extends SingularFormContext {

    public UIBuilderWicket getUIBuilder();
}