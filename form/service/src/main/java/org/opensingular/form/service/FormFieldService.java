package org.opensingular.form.service;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeAttachmentList;
import org.opensingular.form.persistence.dao.FormCacheFieldDAO;
import org.opensingular.form.persistence.dao.FormCacheValueDAO;
import org.opensingular.form.persistence.entity.FormCacheFieldEntity;
import org.opensingular.form.persistence.entity.FormCacheValueEntity;
import org.opensingular.form.persistence.entity.FormTypeEntity;
import org.opensingular.form.persistence.entity.FormVersionEntity;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Transactional
public class FormFieldService implements IFormFieldService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormFieldService.class);

    @Inject
    private FormCacheFieldDAO formCacheFieldDAO;

    @Inject
    private FormCacheValueDAO formCacheValueDAO;

    @Override
    public void saveFields(SInstance instance, FormTypeEntity formType, FormVersionEntity formVersion) {
        Map<FormCacheFieldEntity, FormCacheValueEntity> mapFields = new LinkedHashMap<>();
        loadMapFromInstance(mapFields, instance, formVersion, null);
        deleteOldValues(formVersion);
        saveMap(mapFields);
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
            if (field instanceof SIAttachment) continue;

            if (field instanceof SIList && !(field.getType() instanceof STypeAttachmentList)) {
                LoadMapWithItensFromList(mapFields, (SIList) field, formVersion, parent);
            }

            if (field instanceof SIComposite) {
                SIComposite compositeField = (SIComposite) field;
                FormCacheValueEntity parentFormValue = addItemToMap(mapFields, field, formVersion, parent);
                loadMapFromInstance(mapFields, compositeField, formVersion, parentFormValue);
            }

//NOSONAR            if (field.asAtrPersistence().isPersistent()) {
            if(!(field instanceof SIComposite) && !(field instanceof SIList) && field.getValue() != null) {
                addItemToMap(mapFields, field, formVersion, parent);
            }
//NOSONAR            }

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
        LOGGER.info("Starting batch insert ");
        long startNanos = System.nanoTime();

        mapFields.forEach((field, value) -> {
            field = formCacheFieldDAO.saveOrFind(field);
            value.setCacheField(field);
            formCacheValueDAO.save(value);
        });

        long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
        LOGGER.info("Batch insert took " + duration + " millis");
    }
}

