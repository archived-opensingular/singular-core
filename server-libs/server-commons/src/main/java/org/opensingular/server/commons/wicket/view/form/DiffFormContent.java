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

package org.opensingular.server.commons.wicket.view.form;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.document.RefType;
import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.form.persistence.entity.FormTypeEntity;
import org.opensingular.form.persistence.entity.FormVersionEntity;
import org.opensingular.form.service.IFormService;
import org.opensingular.form.util.diff.DocumentDiff;
import org.opensingular.form.util.diff.DocumentDiffUtil;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSGrid;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSLabel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSRow;
import org.opensingular.lib.wicket.util.datatable.BSDataTable;
import org.opensingular.lib.wicket.util.output.BOutputPanel;
import org.opensingular.server.commons.form.FormActions;
import org.opensingular.server.commons.persistence.entity.form.DraftEntity;
import org.opensingular.server.commons.persistence.entity.form.FormPetitionEntity;
import org.opensingular.server.commons.persistence.entity.form.PetitionEntity;
import org.opensingular.server.commons.service.FormPetitionService;
import org.opensingular.server.commons.service.PetitionService;
import org.opensingular.server.commons.util.DispatcherPageParameters;
import org.opensingular.server.commons.wicket.view.template.Content;
import org.opensingular.server.commons.wicket.view.util.DispatcherPageUtil;

import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public class DiffFormContent<P extends PetitionEntity> extends Content {

    @Inject
    private PetitionService<P> petitionService;

    @Inject
    protected FormPetitionService<P> formPetitionService;

    @Inject
    protected IFormService formService;

    @Inject
    @Named("formConfigWithDatabase")
    protected SFormConfig<String> singularFormConfig;

    private final FormPageConfig config;

    protected BSDataTable<DocumentDiff, String> tabela;
    protected DocumentDiff diff;
    protected BSGrid contentGrid = new BSGrid("content");

    private FormVersionEntity newerFormVersion;
    private FormVersionEntity originalFormVersion;

    public DiffFormContent(String id, FormPageConfig config) {
        super(id);
        this.config = config;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        build();
    }

    private void build() {

        PetitionEntity petition = petitionService.findPetitionByCod(Long.valueOf(config.getPetitionId()));
        FormTypeEntity mainFormType = petition.getMainForm().getFormType();
        DraftEntity    draftEntity = petition.currentEntityDraftByType(mainFormType.getAbbreviation());

        SInstance original = null;
        SInstance newer;

        Date originalDate = null;
        Date newerDate;

        if (draftEntity != null) {
            Optional<FormPetitionEntity> lastForm = formPetitionService.findLastFormPetitionEntityByTypeName(petition.getCod(), mainFormType.getAbbreviation());
            if (lastForm.isPresent()) {
                FormEntity originalForm = lastForm.get().getForm();
                original = loadSInstance(originalForm);
                originalFormVersion = originalForm.getCurrentFormVersionEntity();
                originalDate = originalFormVersion.getInclusionDate();
            }

            newerFormVersion = draftEntity.getForm().getCurrentFormVersionEntity();
            FormEntity newerForm = newerFormVersion.getFormEntity();
            newer = loadSInstance(newerForm);
            newerDate = draftEntity.getEditionDate();


        } else {
            List<FormVersionEntity> formPetitionEntities = petitionService.buscarDuasUltimasVersoesForm(Long.valueOf(config.getPetitionId()));

            originalFormVersion = formPetitionEntities.get(1);
            original = loadSInstanceVersion(originalFormVersion);
            originalDate = originalFormVersion.getInclusionDate();

            newerFormVersion = formPetitionEntities.get(0);
            newer = loadSInstanceVersion(newerFormVersion);
            newerDate = newerFormVersion.getInclusionDate();

        }

        diff = DocumentDiffUtil.calculateDiff(original, newer).removeUnchangedAndCompact();

        queue(contentGrid);
        adicionarDatas(originalDate, newerDate);
        queue(new DiffVisualizer("diff", diff));
    }

    private void adicionarDatas(Date originalDate, Date newerDate) {
        BSRow container = contentGrid.newRow();
        appendDate(container, "Data da modificação anterior:", originalDate);
        appendDate(container, "Data da modificação atual:", newerDate);

        WebMarkupContainer link = new WebMarkupContainer("oldVersionLink");
        link.add($b.attr("target", String.format("version%s", originalFormVersion.getCod())));
        link.add($b.attr("href", mountUrlOldVersion()));

        contentGrid.newRow().newCol(2)
                .newFormGroup()
                .appendLabel(new BSLabel("label", $m.ofValue("")))
                .newTemplateTag(tt -> "<a class='btn' wicket:id='oldVersionLink'><span wicket:id='label'></span></a>")
                .add(link.add(new Label("label", "Versão anterior do formulário")));
    }

    private String mountUrlOldVersion() {
        StringBuilder url = new StringBuilder();
        url.append(DispatcherPageUtil.getBaseURL())
                .append('?')
                .append(String.format("%s=%s", DispatcherPageParameters.ACTION, FormActions.FORM_VIEW.getId()))
                .append(String.format("&%s=%s", DispatcherPageParameters.FORM_VERSION_KEY, originalFormVersion.getCod()));

        for (Map.Entry<String, String> entry : config.getAdditionalParams().entrySet()) {
            url.append(String.format("&%s=%s", entry.getKey(), entry.getValue()));
        }

        return url.toString();
    }

    private void appendDate(BSRow container, String labelCampo, Date data) {
        container.newCol(2)
            .newFormGroup()
            .appendLabel(new BSLabel("label", labelCampo))
            .appendTag("div", new BOutputPanel("data", $m.ofValue(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(data))));
    }

    private SInstance loadSInstance(final FormEntity form) {
        return formService.loadSInstance(formService.keyFromObject(form.getCod()), new RefType() {
            @Override
            protected SType<?> retrieve() {
                return singularFormConfig.getTypeLoader().loadTypeOrException(form.getFormType().getAbbreviation());
            }
        }, singularFormConfig.getDocumentFactory());
    }

    private SInstance loadSInstanceVersion(FormVersionEntity formVersion) {
        FormEntity        form      = formVersion.getFormEntity();
        return formService.loadSInstance(formService.keyFromObject(form.getCod()), new RefType() {
            @Override
            protected SType<?> retrieve() {
                return singularFormConfig.getTypeLoader().loadTypeOrException(form.getFormType().getAbbreviation());
            }
        }, singularFormConfig.getDocumentFactory(), formVersion.getCod());
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return Model.of("");
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return Model.of("");
    }

}