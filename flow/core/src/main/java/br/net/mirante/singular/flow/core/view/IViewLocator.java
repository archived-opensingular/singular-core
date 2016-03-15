/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.view;

import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskInstance;

public interface IViewLocator {

    public Lnk getDefaultHrefFor(ProcessInstance processInstance);

    public Lnk getDefaultHrefFor(TaskInstance taskInstance);

}
