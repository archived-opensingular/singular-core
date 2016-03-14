/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.util;

import br.net.mirante.singular.lambda.IConsumer;
import org.apache.wicket.Component;

public interface IOnAfterPopulateItemConfigurable {

    IOnAfterPopulateItemConfigurable setOnAfterPopulateItem(IConsumer<Component> onAfterPopulateItem);
}
