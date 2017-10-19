/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.service;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeAttachmentList;
import org.opensingular.form.persistence.dao.FormCacheFieldDAO;
import org.opensingular.form.persistence.dao.FormCacheValueDAO;
import org.opensingular.form.persistence.dao.FormVersionDAO;
import org.opensingular.form.persistence.dto.InstanceFormDTO;
import org.opensingular.form.persistence.entity.FormCacheFieldEntity;
import org.opensingular.form.persistence.entity.FormCacheValueEntity;
import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.form.persistence.entity.FormTypeEntity;
import org.opensingular.form.persistence.entity.FormVersionEntity;
import org.opensingular.form.type.core.attachment.SIAttachment;


import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Named
@Transactional
public class FormFieldService implements IFormFieldService {

    @Inject
    private FormCacheFieldDAO formCacheFieldDAO;

    @Inject
    private FormCacheValueDAO formCacheValueDAO;

    @Inject
    private FormVersionDAO formVersionDAO;

    @Override
    public void saveFields(SInstance instance, FormTypeEntity formType, FormVersionEntity formVersion) {
        Map<FormCacheFieldEntity, FormCacheValueEntity> mapFields = new LinkedHashMap<>();
        loadMapFromInstance(mapFields, instance, formVersion, null);
        deleteOldValues(formVersion);
        saveMap(mapFields);
        setFormVersionAsIndexed(formVersion.getCod());
    }

    private void setFormVersionAsIndexed(Long cod) {
        Optional<FormVersionEntity> formVersion = formVersionDAO.get(cod);
        formVersion.ifPresent(f -> {
            f.setIndexed('S');
            formVersionDAO.save(f);
        });
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void saveFields(List<InstanceFormDTO> instances) {
        for (InstanceFormDTO dto : instances) {
            FormEntity form = dto.getForm();
            saveFields(dto.getInstance(), form.getFormType(), form.getCurrentFormVersionEntity());
        }
    }

    private void deleteOldValues(FormVersionEntity formVersion) {
        formCacheValueDAO.deleteValuesFromVersion(formVersion.getCod());
    }

    /**
     * Cria um mapa onde a chave é o nome do campo e o valor é a informação contida no campo.
     * Se a instancia for do tipo attachment ela é ignorada automaticamente.
     * Listas de valores são processadas de forma recursiva e devem informar o código de agrupamento dos valores
     * @param mapFields
     * @param instance
     * @param formVersion
     */
    private void loadMapFromInstance(Map<FormCacheFieldEntity, FormCacheValueEntity> mapFields, SInstance instance, FormVersionEntity formVersion, FormCacheValueEntity parent) {
        List<SInstance> fieldsInInstance = ((SIComposite) instance).getFields();

        for (SInstance field : fieldsInInstance) {
            if (! field.asAtrIndex().isPersistent() || field instanceof SIAttachment){
                continue;
            }

            if (field instanceof SIList && !(field.getType() instanceof STypeAttachmentList)) {
                LoadMapWithItensFromList(mapFields, (SIList) field, formVersion, parent);
            }

            if (field instanceof SIComposite) {
                SIComposite compositeField = (SIComposite) field;
                FormCacheValueEntity parentFormValue = addItemToMap(mapFields, field, formVersion, parent);
                loadMapFromInstance(mapFields, compositeField, formVersion, parentFormValue);
            }


            if (!(field instanceof SIComposite) && !(field instanceof SIList) && field.getValue() != null) {
                addItemToMap(mapFields, field, formVersion, parent);
            }

        }
    }

    private void LoadMapWithItensFromList(Map<FormCacheFieldEntity, FormCacheValueEntity> mapFields, SIList listField, FormVersionEntity formVersion, FormCacheValueEntity parent) {
        FormCacheValueEntity listFormValue = addItemToMap(mapFields, listField,formVersion, parent);

        for (Object subCampo : listField.getValues()) {
            if (subCampo instanceof SIComposite) {
                FormCacheValueEntity parentItem = addItemToMap(mapFields, (SInstance) subCampo, formVersion, parent);
                loadMapFromInstance(mapFields, (SInstance) subCampo, formVersion, parentItem);
            } else {
                loadMapFromInstance(mapFields, (SInstance) subCampo, formVersion, listFormValue);
            }
        }
    }

    private FormCacheValueEntity addItemToMap(Map<FormCacheFieldEntity, FormCacheValueEntity> mapFields, SInstance field, FormVersionEntity formVersion, FormCacheValueEntity parent) {
        FormTypeEntity formType = formVersion.getFormEntity().getFormType();

        String               fieldName = field.getType().getName().replace(formType.getAbbreviation() + ".", "");
        FormCacheFieldEntity formField = new FormCacheFieldEntity(fieldName, formType);
        FormCacheValueEntity formValue = new FormCacheValueEntity(formField, formVersion, field, parent);
        mapFields.put(formField, formValue);
        return formValue;

    }


    private void saveMap(Map<FormCacheFieldEntity, FormCacheValueEntity> mapFields) {
        mapFields.forEach((field, value) -> {
            field = formCacheFieldDAO.saveOrFind(field);
            value.setCacheField(field);
            formCacheValueDAO.save(value);
        });
    }
}

