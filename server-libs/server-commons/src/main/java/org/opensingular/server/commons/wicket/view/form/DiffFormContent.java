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
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

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
import org.opensingular.lib.wicket.util.bootstrap.layout.BSCol;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSGrid;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSLabel;
import org.opensingular.lib.wicket.util.datatable.BSDataTable;
import org.opensingular.lib.wicket.util.output.BOutputPanel;
import org.opensingular.server.commons.persistence.entity.form.DraftEntity;
import org.opensingular.server.commons.persistence.entity.form.FormPetitionEntity;
import org.opensingular.server.commons.persistence.entity.form.PetitionEntity;
import org.opensingular.server.commons.service.FormPetitionService;
import org.opensingular.server.commons.service.PetitionService;
import org.opensingular.server.commons.wicket.view.template.Content;

import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;

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

    private final String petitionId;

    protected BSDataTable<DocumentDiff, String> tabela;
    protected DocumentDiff diff;
    protected BSGrid contentGrid = new BSGrid("content");

    public DiffFormContent(String id, String petitionId) {
        super(id);
        this.petitionId = petitionId;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        build();
    }

    private void build() {

        PetitionEntity petition = petitionService.findPetitionByCod(Long.valueOf(petitionId));
        FormTypeEntity mainFormType = petition.getMainForm().getFormType();
        DraftEntity    draftEntity = petition.currentEntityDraftByType(mainFormType.getAbbreviation());

        SInstance original;
        SInstance newer;

        Date originalDate;
        Date newerDate;

        if (draftEntity != null) {
            FormVersionEntity currentFormVersionEntity = draftEntity.getForm().getCurrentFormVersionEntity();
            Optional<FormPetitionEntity> lastForm = formPetitionService.findLastFormPetitionEntityByTypeName(petition.getCod(), mainFormType.getAbbreviation());

            FormEntity originalForm = lastForm.get().getForm();
            original = loadSInstance(originalForm);
            originalDate = originalForm.getCurrentFormVersionEntity().getInclusionDate();

            FormEntity newerForm = currentFormVersionEntity.getFormEntity();
            newer = loadSInstance(newerForm);
            newerDate = draftEntity.getEditionDate();


        } else {
            List<FormVersionEntity> formPetitionEntities = petitionService.buscarDuasUltimasVersoesForm(Long.valueOf(petitionId));

            FormVersionEntity originalFormVersion = formPetitionEntities.get(1);
            original = loadSInstanceVersion(originalFormVersion);
            originalDate = originalFormVersion.getInclusionDate();

            FormVersionEntity newerFormVersion = formPetitionEntities.get(0);
            newer = loadSInstanceVersion(newerFormVersion);
            newerDate = newerFormVersion.getInclusionDate();

        }

        diff = DocumentDiffUtil.calculateDiff(original, newer).removeUnchangedAndCompact();

        queue(contentGrid);
        adicionarDatas(originalDate, newerDate);
        queue(new DiffVisualizer("diff", diff));
    }

    private void adicionarDatas(Date originalDate, Date newerDate) {
        appendDate("Data da modificação anterior:", originalDate);
        appendDate("Data da modificação atual:", newerDate);
    }

    private void appendDate(String labelCampo, Date data) {
        BSCol col = contentGrid.newRow().newCol(6);
        final BSLabel    label     = new BSLabel("label", labelCampo);
        final BSControls formGroup = col.newFormGroup();

        formGroup.appendLabel(label);

        final BOutputPanel comp = new BOutputPanel("dataAntiga", $m.ofValue(new SimpleDateFormat("dd/MM/yyyy").format(data)));
        formGroup.appendTag("div", comp);
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