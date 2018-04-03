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

package org.opensingular.form.wicket.mapper.tree;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.json.JSONObject;
import org.opensingular.form.SInstance;
import org.opensingular.form.provider.Config;
import org.opensingular.form.provider.ProviderContext;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.mapper.search.SearchModalBodyPanel;
import org.opensingular.lib.commons.lambda.IConsumer;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class SearchModalBodyTreePanel extends SearchModalBodyPanel {

    private static final String PANEL_SCRIPT = "SearchModalBodyTreePanel.js";

    private IModel<List<? extends TreeNode>> nodes = new ListModel();

    public SearchModalBodyTreePanel(String id, WicketBuildContext ctx, IConsumer<AjaxRequestTarget> selectCallback) {
        super(id, ctx, selectCallback);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        final PackageResourceReference customJS = new PackageResourceReference(getClass(), PANEL_SCRIPT);
        response.render(JavaScriptReferenceHeaderItem.forReference(customJS));
        response.render(OnDomReadyHeaderItem.forScript("treeView.create(" + toJsonTree(nodes.getObject()) + ");"));
    }

    @Override
    protected AjaxButton buildFilterButton() {
        return new AjaxButton(FILTER_BUTTON_ID) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                IModel<? extends SInstance> instanceModel = getInnerSingularFormPanel().getInstanceModel();
                target.appendJavaScript("treeView.find('" + instanceModel.getObject().getField("nome").getValue() + "')");
            }
        };
    }

    @Override
    public WebMarkupContainer buildResultTable(Config config) {
        return new WebMarkupContainer(RESULT_TABLE_ID);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        nodes.setObject(getResults());
    }

    private List<? extends TreeNode> getResults() {
        ProviderContext providerContext = new ProviderContext();
        providerContext.setInstance(getCtx().getRootContext().getCurrentInstance());
        providerContext.setFilterInstance(getInnerSingularFormPanel().getInstance());
        return getInstance().asAtrProvider().getFilteredProvider().load(providerContext);
    }

    private static JSONObject toJsonTree(TreeNode<? extends TreeNode> node) {
        JSONObject json = new JSONObject();
        if (!node.isLeaf()) {
            List<JSONObject> childs = new ArrayList<>();
            node.getChildrens().forEach(t -> childs.add(toJsonTree(t)));
            json.put("children", childs);
        } else if (node.isLeaf()) {
            json.put("type", "leaf");
        }
        json.put("id", node.getId());
        json.put("text", node.getDisplayLabel());
        json.put("state", new JSONObject().put("opened", "true").toString());
        return json;
    }

    private List<JSONObject> toJsonTree(List<? extends TreeNode> nodes) {
        List<JSONObject> jsons = new ArrayList<>(nodes.size());
        nodes.forEach(n -> jsons.add(toJsonTree(n)));
        return jsons;
    }
}