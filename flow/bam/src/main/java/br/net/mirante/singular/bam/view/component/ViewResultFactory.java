/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bam.view.component;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.bamclient.portlet.PortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletContext;

public interface ViewResultFactory<C extends PortletConfig<C>> {

    ViewResultPanel create(String id, IModel<C> config, IModel<PortletContext> filter);
}
