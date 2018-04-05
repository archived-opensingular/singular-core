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

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.json.JSONObject;
import org.opensingular.form.SInstance;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.form.provider.ProviderContext;
import org.opensingular.form.provider.TreeProvider;
import org.opensingular.form.view.SViewTree;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.util.Loggable;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class SearchModalBodyTreePanel extends Panel implements Loggable {

    private static final String PANEL_SCRIPT = "SearchModalBodyTreePanel.js";

    private final IModel<List<? extends TreeNode>> nodes = new ListModel();
    private final IModel<String> nodeSelectedModel = new Model<>();
    private final HiddenField<String> nodeSelected = new HiddenField<>("nodeSelected", nodeSelectedModel);
    private final Map<String, TreeNode> cache = new HashMap();
    private final WicketBuildContext ctx;
    private final IConsumer<AjaxRequestTarget> selectCallback;
    private SViewTree viewTree;

    SearchModalBodyTreePanel(String id, WicketBuildContext ctx, IConsumer<AjaxRequestTarget> selectCallback) {
        super(id);
        this.ctx = ctx;
        this.viewTree = (SViewTree) ctx.getView();
        this.selectCallback = selectCallback;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        final PackageResourceReference customJS = new PackageResourceReference(getClass(), PANEL_SCRIPT);
        response.render(JavaScriptReferenceHeaderItem.forReference(customJS));
        response.render(OnDomReadyHeaderItem.forScript("treeView.create(" + toJsonTree(nodes.getObject(), viewTree.isOpen()) +
                ","+ stringfyId(nodeSelected)+"," + viewTree.isOnlyLeafSelect() + ")"));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        nodes.setObject(loadTree());

        Form form = new Form("formHidden");
        form.setOutputMarkupId(false);
        form.add(nodeSelected);

        add(buildSelectButton());
        add(form);
    }

    private AjaxButton buildSelectButton() {
        return new AjaxButton("selectNode") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (nodeSelectedModel.getObject() != null) {
                    populateInstance(Optional.of(cache.get(nodeSelectedModel.getObject())));
                    selectCallback.accept(target);
                }
                nodeSelectedModel.setObject(null);
            }
        };
    }

    private List<? extends TreeNode> loadTree() {
        clearCache();
        TreeProvider<Serializable> provider = getInstance().asAtrProvider().getTreeProvider();
        List<Serializable> nodes = provider.load(ProviderContext.of(getInstance()));
        return nodes.stream().map(n -> new TreeNodeImpl(null, n, 0, getInstance().asAtrProvider().getIdFunction(),
                getInstance().asAtrProvider().getDisplayFunction(), provider::loadChildren))
                .map(this::cacheId)
                .collect(Collectors.toList());
    }

    private TreeNode cacheId(TreeNode treeNode) {
        cache.put(treeNode.getId().toString(), treeNode);
        if (treeNode.hasChildren()) {
            treeNode.getChildrens().forEach(c -> cacheId((TreeNode) c));
        }
        return treeNode;
    }

    private void clearCache() {
        if (!CollectionUtils.isEmpty(cache)) {
            cache.clear();
        }
    }

    private void populateInstance(Optional<TreeNode> optional) {
        optional.ifPresent(treeNode -> {
                SInstanceConverter converter = getInstance().asAtrProvider().getConverter();
                if (converter != null) {
                    converter.fillInstance(getInstance(), treeNode.getValue());
                }
            }
        );
    }

    private SInstance getInstance() {
        return ctx.getModel().getObject();
    }

    private JSONObject toJsonTree(TreeNode<? extends TreeNode> node, boolean open) {
        JSONObject json = new JSONObject();
        if (!node.isLeaf()) {
            List<JSONObject> childs = new ArrayList<>();
            node.getChildrens().forEach(t -> childs.add(toJsonTree(t, open)));
            json.put("children", childs);
        } else if (node.isLeaf()) {
            json.put("type", "leaf");
        }
        json.put("id", node.getId());
        json.put("text", node.getDisplayLabel());
        JSONObject opened = new JSONObject();
        opened.put("opened", open);
        json.put("state", opened);
        return json;
    }

    private List<JSONObject> toJsonTree(List<? extends TreeNode> nodes, boolean open) {
        List<JSONObject> jsons = new ArrayList<>(nodes.size());
        nodes.forEach(n -> jsons.add(toJsonTree(n, open)));
        return jsons;
    }

    private String stringfyId(Component c) {
        return "'" + c.getMarkupId(true) + "'";
    }
}