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

package org.opensingular.studio.core.view;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.opensingular.studio.core.util.StudioWicketUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class StudioHeader extends Panel {

    private WebMarkupContainer rightNavbar = new WebMarkupContainer("rightNavbar");

    public StudioHeader(String id) {
        super(id);
        addHomeAnchor();
        addRightNavbar();
        addUsername();
    }

    private void addRightNavbar() {
        rightNavbar.add(new Behavior() {
            @Override
            public void onConfigure(Component component) {
                super.onConfigure(component);
                component.setVisible(getUserDetails().isPresent());
            }
        });
        add(rightNavbar);
    }

    private void addHomeAnchor() {
        WebMarkupContainer anchor = new WebMarkupContainer("homeAnchor");
        anchor.add(new Behavior() {
            @Override
            public void onComponentTag(Component component, ComponentTag tag) {
                super.onComponentTag(component, tag);
                String path = StudioWicketUtils.getServerContextPath();
                if (StringUtils.isBlank(path)) {
                    path = "/";
                }
                tag.put("href", path);
            }
        });
        add(anchor);
    }

    private void addUsername() {
        rightNavbar.add(new Label("username", getUserDetails().map(UserDetails::getUsername).orElse("")));
    }

    private Optional<Authentication> getAuth() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    private Optional<UserDetails> getUserDetails() {
        return getAuth()
                .map(Authentication::getPrincipal)
                .filter(auth -> auth instanceof UserDetails)
                .map(userDetails -> (UserDetails) userDetails);
    }


}
