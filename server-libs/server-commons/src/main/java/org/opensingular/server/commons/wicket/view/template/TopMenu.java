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

package org.opensingular.server.commons.wicket.view.template;

import org.opensingular.server.commons.spring.security.SecurityUtil;
import org.opensingular.server.commons.wicket.SingularSession;
import org.opensingular.lib.wicket.util.template.SkinOptions;
import org.opensingular.lib.wicket.util.template.SkinOptions.Skin;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.Optional;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

public class TopMenu extends Panel {

    private   boolean     withSideBar;
    protected SkinOptions option;

    public TopMenu(String id, boolean withSideBar, SkinOptions option) {
        super(id);
        this.withSideBar = withSideBar;
        this.option = option;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        buildContent();
    }

    protected void buildContent() {
        queue(new WebMarkupContainer("sideBarToggle").setVisible(withSideBar));
        queue(new Label("nome", $m.ofValue(SingularSession.get().getName())));

        WebMarkupContainer avatar    = new WebMarkupContainer("codrh");
        Optional<String>   avatarSrc = Optional.ofNullable(null);
        avatarSrc.ifPresent(src -> avatar.add($b.attr("src", src)));
        queue(avatar);
        avatar.setVisible(avatarSrc.isPresent());

        WebMarkupContainer logout = new WebMarkupContainer("logout");
        logout.add($b.attr("href", SecurityUtil.getLogoutPath()));
        queue(logout);

        final WebMarkupContainer opcoesVisuais = new WebMarkupContainer("opcoes-visuais");
        opcoesVisuais.setRenderBodyOnly(true);
        opcoesVisuais.setVisible(option.options().size() > 1);
        opcoesVisuais.queue(buildSkinOptions());

        queue(opcoesVisuais);
    }

    protected ListView buildSkinOptions() {
        return new ListView<Skin>("skin_options", option.options()) {
            @Override
            protected void populateItem(ListItem item) {
                final Skin skin = (Skin) item.getModel().getObject();
                item.add(buildSelectSkinLink(skin));
                item.queue(new Label("label", skin.getName()));
            }
        };
    }

    private StatelessLink buildSelectSkinLink(final Skin skin) {
        return new StatelessLink("change_action") {
            public void onClick() {
                option.selectSkin(skin);
                refreshPage();
            }
        };
    }

    private void refreshPage() {
        setResponsePage(getPage());
    }
}
