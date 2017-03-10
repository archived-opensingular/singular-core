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

package org.opensingular.singular.form.showcase.view.page.form.crud;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.internal.xml.MElement;
import org.opensingular.form.internal.xml.MParser;
import org.opensingular.form.wicket.component.BFModalBorder;
import org.opensingular.form.wicket.component.SingularFormWicket;
import org.opensingular.form.wicket.enums.AnnotationMode;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.feedback.SFeedbackPanel;
import org.opensingular.lib.wicket.util.datatable.BSDataTable;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.lib.wicket.util.datatable.BaseDataProvider;
import org.opensingular.lib.wicket.util.datatable.column.BSActionColumn;
import org.opensingular.lib.wicket.util.modal.BSModalBorder.ButtonStyle;
import org.opensingular.lib.wicket.util.modal.BSModalBorder.Size;
import org.opensingular.lib.wicket.util.output.BOutputPanel;
import org.opensingular.lib.wicket.util.resource.Icone;
import org.opensingular.lib.wicket.util.tab.BSTabPanel;
import org.opensingular.singular.form.showcase.dao.form.ExampleDataDAO;
import org.opensingular.singular.form.showcase.dao.form.ExampleDataDTO;
import org.opensingular.singular.form.showcase.dao.form.ShowcaseTypeLoader;
import org.opensingular.singular.form.showcase.view.SingularWicketContainer;
import org.opensingular.singular.form.showcase.view.page.form.FormVO;
import org.opensingular.singular.form.showcase.view.template.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

public class CrudContent extends Content implements SingularWicketContainer<CrudContent, Void> {

    private final static Logger LOGGER = LoggerFactory.getLogger(CrudContent.class);

    private BSDataTable<ExampleDataDTO, String> listTable;
    private IModel<FormVO>                      selectedTemplate;

    private final BFModalBorder deleteModal  = new BFModalBorder("deleteModal");
    private final BFModalBorder viewXmlModal = new BFModalBorder("viewXmlModal");

    @Inject
    private ExampleDataDAO dao;

    @Inject
    @Named("showcaseTypeLoader")
    private ShowcaseTypeLoader dictionaryLoader;

    private ExampleDataDTO currentModel;

    public CrudContent(String id, StringValue type) {
        super(id, false, true);
        setActiveTemplate(type);
    }

    private void setActiveTemplate(StringValue type) {
        if (!type.isEmpty()) {
            selectedTemplate = new Model<>(new FormVO(dictionaryLoader.findEntryByType(type.toString())));
        } else {
            selectedTemplate = new Model<>(new FormVO(null, null));
        }
    }

    @Override
    protected void onInitialize() {

        super.onInitialize();

        add(new SingularFormWicket<>("optionsForm").add(setUpTemplatesOptions()));
        add(new SingularFormWicket<>("delete-form").add(deleteModal));
        add(setUpInsertButton());
        listTable = setupDataTable();
        add(listTable);
        add(viewXmlModal);
        add(new SFeedbackPanel("feedback", this));

        deleteModal.setTitleText(Model.of(getString("label.delete.message")));
        deleteModal.addButton(ButtonStyle.PRIMARY, Model.of(getString("label.button.ok")), new AjaxButton("delete-btn") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dao.remove(currentModel);
                currentModel = null;
                updateListTableFromModal(target);
                deleteModal.hide(target);
            }
        });
        deleteModal.addButton(ButtonStyle.DEFAULT, Model.of(getString("label.button.cancel")), new AjaxButton("cancel-btn") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                deleteModal.hide(target);
            }
        });
        deleteModal.setSize(Size.SMALL);

    }

    private DropDownChoice setUpTemplatesOptions() {
        final List<FormVO> options = dictionaryLoader.getEntries().stream()
                .map(FormVO::new)
                .collect(Collectors.toList());

        return new DropDownChoice<FormVO>("options", selectedTemplate, options, new ChoiceRenderer<>("key", "key")) {
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }
        };
    }

    private MarkupContainer setUpInsertButton() {
        return new SingularFormWicket<>("form").add(new AjaxButton("insert") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                PageParameters params = new PageParameters().add(FormPage.TYPE_NAME, selectedTemplate.getObject().getTypeName());
                setResponsePage(FormPage.class, params);
            }

            @Override
            public boolean isVisible() {
                return selectedTemplate != null && selectedTemplate.getObject().getKey() != null;
            }

        });
    }

    private BSDataTable<ExampleDataDTO, String> setupDataTable() {

        final BSDataTableBuilder<ExampleDataDTO, String, IColumn<ExampleDataDTO, String>> builder = new BSDataTableBuilder<>(createDataProvider());

        final Supplier<BSActionColumn<ExampleDataDTO, String>> $action = () -> new BSActionColumn<>($m.ofValue(""));

        builder
                .appendPropertyColumn(getMessage("label.table.column.id"), "id", ExampleDataDTO::getId)
                .appendPropertyColumn(getMessage("label.table.column.descricao"), "description", ExampleDataDTO::getDescription)
                .appendPropertyColumn(getMessage("label.table.column.dt.edicao"), "editionDate", ExampleDataDTO::getEditionDate)
                .appendColumn($action.get().appendAction(getMessage("label.table.column.edit"), Icone.PENCIL_SQUARE,
                        (target, model) -> {
                            setResponsePage(FormPage.class, new PageParameters()
                                    .add(FormPage.TYPE_NAME, selectedTemplate.getObject().getTypeName())
                                    .add(FormPage.MODEL_ID, model.getObject().getId())
                                    .add(FormPage.VIEW_MODE, ViewMode.EDIT));
                        }))
                .appendColumn($action.get().appendAction(getMessage("label.table.column.visualizar"), Icone.EYE,
                        (target, model) -> {
                            setResponsePage(FormPage.class, new PageParameters()
                                    .add(FormPage.TYPE_NAME, selectedTemplate.getObject().getTypeName())
                                    .add(FormPage.MODEL_ID, model.getObject().getId())
                                    .add(FormPage.VIEW_MODE, ViewMode.READ_ONLY));
                        }));
        addAnnotationColumnIfNeeded(builder);
        addAnnotationEditColumnIfNeeded(builder);
        builder.appendColumn($action.get().appendAction(getMessage("label.table.column.delete"), Icone.MINUS, this::deleteSelected))
                .appendColumn($action.get().appendAction(getMessage("label.table.column.visualizar.xml"), Icone.CODE, this::viewXml))
                .setRowsPerPage(10);
        return builder.build("data-list");
    }

    private void addAnnotationColumnIfNeeded(BSDataTableBuilder<ExampleDataDTO, String, IColumn<ExampleDataDTO, String>> builder) {
        builder.appendColumn(new BSActionColumn<ExampleDataDTO, String>($m.ofValue("")) {
            @Override
            public String getCssClass() {
                return (hasAnnotations() ? " " : " hidden ") + super.getCssClass();
            }
        }.appendAction(getMessage("label.table.column.analisar"), Icone.COMMENT, (target, model) -> {
            setResponsePage(FormPage.class, new PageParameters()
                    .add(FormPage.TYPE_NAME, selectedTemplate.getObject().getTypeName())
                    .add(FormPage.MODEL_ID, model.getObject().getId())
                    .add(FormPage.VIEW_MODE, ViewMode.READ_ONLY)
                    .add(FormPage.ANNOTATION, AnnotationMode.EDIT));
        }));
    }

    private void addAnnotationEditColumnIfNeeded(BSDataTableBuilder<ExampleDataDTO, String,
            IColumn<ExampleDataDTO, String>> builder) {
        builder.appendColumn(new BSActionColumn<ExampleDataDTO, String>($m.ofValue("")) {
            @Override
            public String getCssClass() {
                return (hasAnnotations() ? " " : " hidden ") + super.getCssClass();
            }
        }.appendAction(getMessage("label.table.column.exigencia"), Icone.PENCIL, (target, model) -> {
            setResponsePage(FormPage.class, new PageParameters()
                    .add(FormPage.TYPE_NAME, selectedTemplate.getObject().getTypeName())
                    .add(FormPage.MODEL_ID, model.getObject().getId())
                    .add(FormPage.VIEW_MODE, ViewMode.EDIT)
                    .add(FormPage.ANNOTATION, AnnotationMode.READ_ONLY));
        }));
    }

    private boolean hasAnnotations() {
        boolean hasAnntations = false;
        if (selectedTemplate.getObject().getType() != null && selectedTemplate.getObject().getType().isComposite()) {
            STypeComposite<?> type = (STypeComposite<?>) selectedTemplate.getObject().getType();
            for (SType<?> i : type.getFields()) {
                hasAnntations |= i.asAtrAnnotation().isAnnotated();
            }
        }
        return hasAnntations;
    }

    private BaseDataProvider<ExampleDataDTO, String> createDataProvider() {
        return new BaseDataProvider<ExampleDataDTO, String>() {

            @Override
            public long size() {
                return dao.count(selectedTemplate.getObject().getTypeName());
            }

            @Override
            public Iterator<? extends ExampleDataDTO> iterator(int first, int count, String property,
                                                               boolean asc) {
                return dao.list(selectedTemplate.getObject().getTypeName(), first, count, Optional.ofNullable(property), asc).iterator();
            }
        };
    }

    private void deleteSelected(AjaxRequestTarget target, IModel<ExampleDataDTO> model) {
        currentModel = model.getObject();
        deleteModal.show(target);
    }

    private void viewXml(AjaxRequestTarget target, IModel<ExampleDataDTO> model) {

        final String     xmlPersistencia = model.getObject().getXml();
        final String     xmlTabulado     = getXmlTabulado(xmlPersistencia);
        final String     definicao       = getDefinicao(model.getObject().getType());
        final BSTabPanel xmlTabs         = new BSTabPanel("xmlTabs");

        final Function<String, BOutputPanel> creator = val -> new BOutputPanel(BSTabPanel.TAB_PANEL_ID, $m.ofValue(val));

        xmlTabs.addTab(getString("label.xml.tabulado"), creator.apply(xmlTabulado));
        xmlTabs.addTab(getString("label.xml.persistencia"), creator.apply(xmlPersistencia));
        xmlTabs.addTab(getString("label.definicao"), creator.apply(definicao));

        if (hasAnnotations()) {
            String xmlAnnotations = getXmlTabulado(model.getObject().getAnnnotations());
            xmlTabs.addTab(getString("label.xml.anotacao"), new BOutputPanel(BSTabPanel.TAB_PANEL_ID, $m.ofValue(xmlAnnotations)));
        }

        viewXmlModal.addOrReplace(xmlTabs);
        viewXmlModal.show(target);
        viewXmlModal.setSize(BFModalBorder.Size.LARGE);
    }

    private String getXmlTabulado(String xmlString) {
        if (StringUtils.isNotEmpty(xmlString)) {
            try {
                final MElement     xml    = MParser.parse(xmlString);
                final StringWriter sw     = new StringWriter();
                final PrintWriter  writer = new PrintWriter(sw);
                xml.printTabulado(writer);
                return sw.toString();
            } catch (SAXException | IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return StringUtils.EMPTY;
    }

    private String getDefinicao(String typeName) {

        final StringBuilder  definicaoOutput = new StringBuilder();

        Optional.ofNullable(dictionaryLoader.findEntryByType(typeName))
                .map(ShowcaseTypeLoader.TemplateEntry::getType)
                .map(SType::getPackage)
                .ifPresent(pckg -> pckg.debug(definicaoOutput));

        return definicaoOutput.toString();
    }

    private void updateListTableFromModal(AjaxRequestTarget target) {
        target.add(listTable);
    }

    @Override
    protected WebMarkupContainer getBreadcrumbLinks(String id) {
        return new Fragment(id, "breadcrumbForm", this);
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return new ResourceModel("label.content.title");
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return new ResourceModel("label.content.subtitle");
    }
}
