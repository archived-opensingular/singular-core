/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bam.view.component;

import static br.net.mirante.singular.bam.form.FilterPackageFactory.ROOT;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.json.JSONObject;

import br.net.mirante.singular.bamclient.portlet.PortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletContext;
import br.net.mirante.singular.bamclient.portlet.PortletQuickFilter;
import br.net.mirante.singular.flow.core.authorization.AccessLevel;
import br.net.mirante.singular.bam.form.FilterPackageFactory;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.context.SFormConfig;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.util.xml.MElement;
import br.net.mirante.singular.form.wicket.component.SingularSaveButton;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.bam.service.FlowMetadataFacade;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import br.net.mirante.singular.bam.wicket.UIAdminSession;

public class PortletPanel<C extends PortletConfig> extends Panel {

    @Inject
    private FlowMetadataFacade flowMetadataFacade;

    @Inject
    @Named("bamFilterformConfig")
    private SFormConfig<String> singularFormConfig;

    private final IModel<C> config;
    private final IModel<PortletContext> context;
    private final IModel<String> footerLabel;
    private final BSModalBorder modalBorder = new BSModalBorder("filter");
    private final Form portletForm = new Form("porletForm");

    public PortletPanel(String id, C config, String processDefinitionCode, int portletIndex) {
        this(id, config, processDefinitionCode, portletIndex, null);
    }

    public PortletPanel(String id, C config, String processDefinitionCode, int portletIndex, String footer) {
        super(id);
        Objects.requireNonNull(config, "Configuração é obrigatória");
        final PortletContext portletContext = new PortletContext();
        portletContext.setPortletIndex(portletIndex);
        portletContext.setProcessDefinitionCode(processDefinitionCode);
        portletContext.setProcessDefinitionKeysWithAccess(getProcesseDefinitionsKeysWithAcess());
        portletContext.setFilterClassName(config.getFilterClassName());
        this.config = Model.of(config);
        this.context = Model.of(portletContext);
        footerLabel = Model.of(buildFooterLabel(footer));
    }

    protected ViewResultPanel buildViewResult() {
        return PortletViewConfigResolver.newViewResult("portletContent", config, context);
    }

    private Set<String> getProcesseDefinitionsKeysWithAcess() {
        return flowMetadataFacade.listProcessDefinitionKeysWithAccess(UIAdminSession.get().getUserId(), AccessLevel.LIST);
    }

    @Override
    protected void onInitialize() {
        final PortletConfig config = this.config.getObject();
        super.onInitialize();

        portletForm.add(new Label("title", Model.of(config.getTitle())));
        portletForm.add(new Label("subtitle", Model.of(config.getSubtitle())));

        portletForm.add(buildQuickFilters());
        portletForm.add(buildOpenFilterButton());
        portletForm.add(buildViewResult());

        buildFilters();

        modalBorder.setTitleText(Model.of(String.format("Filtrar %s", config.getTitle().toLowerCase())));
        portletForm.add(modalBorder);

        portletForm.add($b.classAppender(config.getPortletSize().getBootstrapSize()));

        add(portletForm);
        portletForm.setOutputMarkupId(true);
        setOutputMarkupId(true);
        queue(new WebMarkupContainer("footer")
                .add(new Label("footerLabel", footerLabel)));
    }

    private String buildFooterLabel(String footer) {
        if (footer == null) {
            return "";
        } else {
            return String.format("%s: %s", getString("label.chart.process"), footer);
        }
    }

    private SType<?> createPortletFilterType() {
        SType<?> type = new FilterPackageFactory(config.getObject().getFilterConfigs(), singularFormConfig.getServiceRegistry(),
                context.getObject().getProcessDefinitionCode())
                .createFilterPackage();
        return type;
    }

    private void buildFilters() {

        final SingularFormPanel<?> panel = new SingularFormPanel<String>("singularFormPanel", singularFormConfig) {
            @Override
            protected SInstance createInstance(SFormConfig<String> singularFormConfig) {
                RefType refType = new RefType() {
                    @Override
                    protected SType<?> retrieve() {
                        return PortletPanel.this.createPortletFilterType();
                    }
                };
                return singularFormConfig.getDocumentFactory().createInstance(refType);
            }
        };

        final SingularSaveButton saveButton = new SingularSaveButton("filterButton") {
            @Override
            protected void handleSaveXML(AjaxRequestTarget target, MElement element) {
                if (element != null) {
                    final String jsonSource = element.toJSONString();
                    final Optional<JSONObject> filtro = Optional.ofNullable(new JSONObject(jsonSource).getJSONObject(ROOT));
                    context.getObject().setSerializedJSONFilter(filtro.map(JSONObject::toString).orElse(null));
                } else {
                    context.getObject().setSerializedJSONFilter(null);
                }
                modalBorder.hide(target);
            }

            @Override
            protected void onValidationError(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                super.onValidationError(target, form, instanceModel);
                modalBorder.hide(target);
                modalBorder.show(target);
            }

            @Override
            public IModel<? extends SInstance> getCurrentInstance() {
                return panel.getRootInstance();
            }
        };
        saveButton.add($b.attr("value", getString("label.filter")));
        modalBorder.addButton(BSModalBorder.ButtonStyle.PRIMARY, Model.of(getString("label.filter")), saveButton);
        modalBorder.add(panel);
    }

    protected Component buildOpenFilterButton() {
        return new AjaxButton("openFilterButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                modalBorder.show(target);
            }

            @Override
            protected void onConfigure() {
                setVisible(!config.getObject().getFilterConfigs().isEmpty());
                super.onConfigure();
            }
        };
    }

    private Component buildQuickFilters() {
        return new ListView<PortletQuickFilter>("quickFilterOptions", config.getObject().getQuickFilter()) {
            @Override
            protected void populateItem(ListItem<PortletQuickFilter> item) {
                item.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        target.add(getParent());
                        context.getObject().setQuickFilter(item.getModelObject());
                    }
                });
                item.add(WicketUtils.$b.onConfigure(component -> {
                    component.add(new ClassAttributeModifier() {
                        @Override
                        protected Set<String> update(Set<String> oldClasses) {
                            if (item.getModelObject().equals(context.getObject().getQuickFilter())) {
                                oldClasses.add("active");
                            } else {
                                oldClasses.remove("active");
                            }
                            return oldClasses;
                        }
                    });
                }));
                item.setOutputMarkupId(true);
                item.add(new Label("filterLabel", Model.of(item.getModelObject().getLabel())));
            }
        };
    }

}