/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
