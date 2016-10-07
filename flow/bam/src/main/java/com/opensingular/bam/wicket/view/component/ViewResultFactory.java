/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.wicket.view.component;

import org.apache.wicket.model.IModel;

import com.opensingular.bam.client.portlet.PortletConfig;
import com.opensingular.bam.client.portlet.PortletContext;

public interface ViewResultFactory<C extends PortletConfig<C>> {

    ViewResultPanel create(String id, IModel<C> config, IModel<PortletContext> filter);
}
