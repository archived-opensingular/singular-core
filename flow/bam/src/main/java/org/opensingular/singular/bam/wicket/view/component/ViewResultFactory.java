/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.bam.wicket.view.component;

import org.apache.wicket.model.IModel;

import org.opensingular.singular.bamclient.portlet.PortletConfig;
import org.opensingular.singular.bamclient.portlet.PortletContext;

public interface ViewResultFactory<C extends PortletConfig<C>> {

    ViewResultPanel create(String id, IModel<C> config, IModel<PortletContext> filter);
}
