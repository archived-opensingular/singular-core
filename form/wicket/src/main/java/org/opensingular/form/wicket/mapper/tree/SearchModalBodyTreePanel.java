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
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class SearchModalBodyTreePanel extends Panel implements Loggable {

    private static final String PANEL_SCRIPT = "SearchModalBodyTreePanel.js";

    private final IModel<List<? extends TreeNode>> nodes = new ListModel();
    private final IModel<String> nodeSelectedModel = new Model<>();
    private final IModel<String> viewParams = new Model<>();
    private final HiddenField<String> nodeSelected = new HiddenField<>("nodeSelected", nodeSelectedModel);
    private final Map<String, TreeNode> cache = new HashMap();
    private final WicketBuildContext ctx;
    private final IConsumer<AjaxRequestTarget> selectCallback;
    private final IConsumer<AjaxRequestTarget> clearCallback;

    private SViewTree viewTree;

    SearchModalBodyTreePanel(String id, WicketBuildContext ctx, IConsumer<AjaxRequestTarget> selectCallback,
                             IConsumer<AjaxRequestTarget> clearCallback) {
        super(id);
        this.ctx = ctx;
        this.viewTree = (SViewTree) ctx.getView();
        this.selectCallback = selectCallback;
        this.clearCallback = clearCallback;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        final PackageResourceReference customJS = new PackageResourceReference(getClass(), PANEL_SCRIPT);
        response.render(JavaScriptReferenceHeaderItem.forReference(customJS));
        response.render(OnDomReadyHeaderItem.forScript("treeView.create(" + viewParams.getObject() + ")"));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        nodes.setObject(loadTree());
        populateParamsTree();

        Form form = new Form("formHidden");
        form.setOutputMarkupId(false);
        form.add(nodeSelected);

        add(buildSelectButton());
        add(buildClearButton());
        add(form);
    }

    private void populateParamsTree() {
        JSONObject json = new JSONObject();
        json.put("data", treeJson(nodes.getObject(), viewTree.isOpen()));
        json.put("hidden", stringfyId(nodeSelected));
        json.put("showOnlyMatches", viewTree.isShowOnlyMatches());
        json.put("showOnlyMatchesChildren", viewTree.isShowOnlyMatchesChildren());
        json.put("onlyLeafSelected", viewTree.isSelectOnlyLeafs());
        viewParams.setObject(json.toString());
    }

    private Component buildClearButton() {
        return new AjaxButton("selectNode") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (nodeSelectedModel.getObject() != null) {
                    populateInstance(cache.get(nodeSelectedModel.getObject()));
                    selectCallback.accept(target);
                }
                nodeSelectedModel.setObject(null);
            }
        };
    }

    private AjaxButton buildSelectButton() {
        return new AjaxButton("clearNode") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                clearCallback.accept(target);
            }
        };
    }

    private List<? extends TreeNode> loadTree() {
        clearCache();
        TreeProvider<Serializable> provider = getInstance().asAtrProvider().getTreeProvider();
        List<Serializable> nodes = provider.load(ProviderContext.of(getInstance()));
        return nodes.stream().map(node -> new TreeNodeImpl(null, node, 0, getInstance().asAtrProvider().getIdFunction(),
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

    private void populateInstance(TreeNode tree) {
        Optional<TreeNode> optional = Optional.of(tree);
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

    private JSONObject treeJson(TreeNode<? extends TreeNode> node, boolean open) {
        JSONObject json = new JSONObject();
        json.put("id", node.getId());
        json.put("text", node.getDisplayLabel());
        json.put("state", stateShowTree(open));
        if (node.isLeaf()) {
            json.put("type", "leaf");
        } else {
            json.put("type", "open");
            List<JSONObject> childs = childrenNodes(node, open);
            json.put("children", childs);
        }
        return json;
    }

    private List<JSONObject> childrenNodes(TreeNode<? extends TreeNode> node, boolean open) {
        List<JSONObject> childs = new ArrayList<>();
        node.getChildrens().forEach(t -> childs.add(treeJson(t, open)));
        return childs;
    }

    private JSONObject stateShowTree(boolean open) {
        JSONObject opened = new JSONObject();
        opened.put("opened", open);
        return opened;
    }

    private List<JSONObject> treeJson(List<? extends TreeNode> nodes, boolean open) {
        List<JSONObject> jsons = new ArrayList<>(nodes.size());
        nodes.forEach(n -> jsons.add(treeJson(n, open)));
        return jsons;
    }

    private String stringfyId(Component c) {
        return "'" + c.getMarkupId(true) + "'";
    }
}