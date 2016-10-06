/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.util;

import org.opensingular.lib.commons.lambda.IConsumer;
import org.apache.wicket.Component;

public interface IOnAfterPopulateItemConfigurable {

    IOnAfterPopulateItemConfigurable setOnAfterPopulateItem(IConsumer<Component> onAfterPopulateItem);
}
