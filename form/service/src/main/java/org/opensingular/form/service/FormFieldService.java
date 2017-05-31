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
import java.util.ArrayList;
import java.util.HashMap;
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
        Map<FormCacheFieldEntity, FormCacheValueEntity> mapFields = new HashMap<>();
        loadMap(mapFields, instance, formVersion);
        deleteOldValues(formVersion);
        saveMap(mapFields);
    }

    private void deleteOldValues(FormVersionEntity formVersion) {
        formCacheValueDAO.deleteValuesFromVersion(formVersion.getCod());
    }

    private void loadMap(Map<FormCacheFieldEntity, FormCacheValueEntity> mapFields, SInstance instance, FormVersionEntity formVersion) {
        FormTypeEntity formType = formVersion.getFormEntity().getFormType();
        List<SInstance> fieldsInInstance = ((SIComposite) instance).getFields();

        List<String> fieldsToIndex = new ArrayList<>();

        for (SInstance field : fieldsInInstance) {
            if (field instanceof SIAttachment) {
                continue;
            }

            if (field instanceof SIList) {
                LoadMapWithItensFromList(mapFields, (SIList)field, formVersion);
            }

            if (field instanceof SIComposite) {
                SIComposite compositeField = (SIComposite) field;
                if (compositeField.getFields().size() > 0) {
                   loadMap(mapFields, compositeField, formVersion);
                }
            }

            //TODO resolver a indicacao de persitencia para tipos compostos
//            if (field.asAtrPersistence().isPersistent()) {
                System.out.println(field.getName() + " é persistente: " + field.asAtrPersistence().isPersistent());

                String fieldName = field.getType().getName().replace(formType.getAbbreviation() + ".", "");
                FormCacheFieldEntity formField = new FormCacheFieldEntity(fieldName, formType);
                FormCacheValueEntity formValue = new FormCacheValueEntity(formField, formVersion, field);
                mapFields.put(formField, formValue);
//            }

        }
    }

    private void LoadMapWithItensFromList(Map<FormCacheFieldEntity, FormCacheValueEntity> mapFields, SIList listField, FormVersionEntity formVersion) {
        for (Object subCampo : listField.getValues()) {
            loadMap(mapFields, (SInstance) subCampo, formVersion);
        }
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