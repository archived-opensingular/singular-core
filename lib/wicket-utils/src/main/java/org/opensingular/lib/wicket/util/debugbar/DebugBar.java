/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.debugbar;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.devutils.debugbar.IDebugBarContributor;
import org.apache.wicket.event.IEvent;

@SuppressWarnings("serial")
public class DebugBar extends org.apache.wicket.devutils.debugbar.DebugBar {

    public DebugBar(String id) {
        super(id);
    }

    public DebugBar(String id, boolean initiallyExpanded) {
        super(id, initiallyExpanded);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        List<IDebugBarContributor> list = org.apache.wicket.devutils.debugbar.DebugBar.getContributors(getApplication());
        if (list != null) {

        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.add(new Behavior() {
            @Override
            public void onEvent(Component component, IEvent<?> event) {
                super.onEvent(component, event);
                Object o = event.getPayload();
                if (o instanceof AjaxRequestTarget) {
                    ((AjaxRequestTarget) o).add(component);
                }
            }
        });
        this.add($b.attr("style", "z-index: 99999;"));
    }
}
