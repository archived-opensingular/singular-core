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

package org.opensingular.form.wicket.mapper.search;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.document.RefType;
import org.opensingular.form.provider.*;
import org.opensingular.form.view.SViewSearchModal;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.util.Loggable;

import static org.opensingular.form.wicket.AjaxUpdateListenersFactory.SINGULAR_PROCESS_EVENT;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$b;

@SuppressWarnings("unchecked")
public abstract class SearchModalBodyPanel extends Panel implements Loggable {

    public static final String FILTER_BUTTON_ID = "filterButton";
    public static final String FORM_PANEL_ID = "formPanel";
    protected static final String RESULT_TABLE_ID  = "resultTable";

    private final WicketBuildContext ctx;
    private final SViewSearchModal view;
    private final IConsumer<AjaxRequestTarget> selectCallback;

    private SingularFormPanel innerSingularFormPanel;
    private MarkupContainer resultTable;

    protected SearchModalBodyPanel(String id, WicketBuildContext ctx, IConsumer<AjaxRequestTarget> selectCallback) {
        super(id);
        this.ctx = ctx;
        this.view = (SViewSearchModal) ctx.getView();
        this.selectCallback = selectCallback;
        validate();
    }

    private void validate() {
        if (getInstance().asAtrProvider().getFilteredProvider() == null) {
            throw new SingularFormException("O provider não foi informado", getInstance());
        }
        if (getInstance().asAtrProvider().getConverter() == null
                && (getInstance() instanceof SIComposite || getInstance() instanceof SIList)) {
            throw new SingularFormException("O tipo não é simples e o converter não foi informado.", getInstance());
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final AjaxButton filterButton;

        innerSingularFormPanel = buildInnerSingularFormPanel();
        filterButton = buildFilterButton();
        resultTable = buildResultTable(getConfig());

        add(innerSingularFormPanel);
        add(filterButton);
        add(resultTable);

        innerSingularFormPanel.add($b.onEnterDelegate(filterButton, SINGULAR_PROCESS_EVENT));

    }

    protected SingularFormPanel buildInnerSingularFormPanel() {

        final SingularFormPanel parentSingularFormPanel = this.visitParents(SingularFormPanel.class,
                (parent, visit) -> visit.stop(parent));

        SingularFormPanel p = new SingularFormPanel(FORM_PANEL_ID, true);
        p.setDocumentFactory(parentSingularFormPanel.getDocumentFactory().orElse(null));
        p.setInstanceFromType(RefType.of(() -> getConfig().getFilter()));

        return p;
    }

    public Config getConfig() {
        Config config = new Config();
        getFilteredProvider().configureProvider(config);
        return config;
    }

    protected SInstance getInstance() {
        return ctx.getModel().getObject();
    }

    protected FilteredPagedProvider getFilteredProvider() {
        FilteredProvider provider = getInstance().asAtrProvider().getFilteredProvider();

        if (!(provider instanceof FilteredPagedProvider)) {
            provider = new InMemoryFilteredPagedProviderDecorator<>(provider);
        }
        return (FilteredPagedProvider) provider;
    }

    protected AjaxButton buildFilterButton() {
        return new AjaxButton(FILTER_BUTTON_ID) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                target.add(resultTable);
            }
        };
    }

    public abstract WebMarkupContainer buildResultTable(Config config);

    public WicketBuildContext getCtx() {
        return ctx;
    }

    public SViewSearchModal getView() {
        return view;
    }

    protected IConsumer<AjaxRequestTarget> getSelectCallback() {
        return selectCallback;
    }

    protected SingularFormPanel getInnerSingularFormPanel() {
        return innerSingularFormPanel;
    }
}

