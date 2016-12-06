package org.opensingular.server.commons.service;


import org.apache.commons.collections.CollectionUtils;
import org.opensingular.flow.core.service.IUserService;
import org.opensingular.flow.persistence.entity.ProcessInstanceEntity;
import org.opensingular.flow.persistence.entity.TaskDefinitionEntity;
import org.opensingular.form.SFormUtil;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SInstances;
import org.opensingular.form.SType;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.document.RefType;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.dao.FormAnnotationDAO;
import org.opensingular.form.persistence.dao.FormAnnotationVersionDAO;
import org.opensingular.form.persistence.dao.FormDAO;
import org.opensingular.form.persistence.dao.FormVersionDAO;
import org.opensingular.form.persistence.entity.FormAnnotationEntity;
import org.opensingular.form.persistence.entity.FormAnnotationVersionEntity;
import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.form.persistence.entity.FormTypeEntity;
import org.opensingular.form.persistence.entity.FormVersionEntity;
import org.opensingular.form.service.IFormService;
import org.opensingular.form.type.core.annotation.AtrAnnotation;
import org.opensingular.form.type.core.annotation.SIAnnotation;
import org.opensingular.form.util.transformer.Value;
import org.opensingular.lib.support.persistence.enums.SimNao;
import org.opensingular.server.commons.persistence.dao.form.DraftDAO;
import org.opensingular.server.commons.persistence.dao.form.FormPetitionDAO;
import org.opensingular.server.commons.persistence.entity.form.DraftEntity;
import org.opensingular.server.commons.persistence.entity.form.FormPetitionEntity;
import org.opensingular.server.commons.persistence.entity.form.FormVersionHistoryEntity;
import org.opensingular.server.commons.persistence.entity.form.PetitionContentHistoryEntity;
import org.opensingular.server.commons.persistence.entity.form.PetitionEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Transactional
public class FormPetitionService<P extends PetitionEntity> {

    @Inject
    protected IFormService formPersistenceService;

    @Inject
    private IUserService userService;

    @Inject
    protected FormPetitionDAO formPetitionDAO;

    @Inject
    protected DraftDAO draftDAO;

    @Inject
    protected FormDAO formDAO;

    @Inject
    protected FormVersionDAO formVersionDAO;

    @Inject
    protected FormAnnotationDAO formAnnotationDAO;

    @Inject
    protected FormAnnotationVersionDAO formAnnotationVersionDAO;

    public FormPetitionEntity findFormPetitionEntityByCod(Long cod) {
        return formPetitionDAO.find(cod);
    }

    public Optional<FormPetitionEntity> findFormPetitionEntityByTypeName(Long petitionPK, String typeName) {
        return Optional.ofNullable(formPetitionDAO.findFormPetitionEntityByTypeName(petitionPK, typeName));
    }

    public Optional<FormPetitionEntity> findFormPetitionEntityByTypeNameAndTask(Long petitionPK, String typeName, Integer taskDefinitionEntityPK) {
        return Optional.ofNullable(formPetitionDAO.findFormPetitionEntityByTypeNameAndTask(petitionPK, typeName, taskDefinitionEntityPK));
    }

    public Optional<FormPetitionEntity> findLastFormPetitionEntityByTypeName(Long petitionPK, String typeName) {
        return Optional.ofNullable(formPetitionDAO.findLastFormPetitionEntityByTypeName(petitionPK, typeName));
    }

    public FormKey saveFormPetition(P petition,
                                    SInstance instance,
                                    boolean mainForm,
                                    SFormConfig config) {

        final Integer      codActor;
        FormKey            key;
        FormPetitionEntity formPetitionEntity;

        codActor = userService.getUserCodIfAvailable();
        formPetitionEntity = findFormPetitionEntity(petition, instance.getType().getName(), mainForm);

        if (formPetitionEntity == null) {
            formPetitionEntity = newFormPetitionEntity(petition, mainForm);
            petition.getFormPetitionEntities().add(formPetitionEntity);
        }

        DraftEntity currentDraftEntity = formPetitionEntity.getCurrentDraftEntity();
        if (currentDraftEntity == null) {
            currentDraftEntity = createNewDraftWithoutSave();
        }

        saveOrUpdateDraft(instance, currentDraftEntity, config, codActor);
        formPetitionEntity.setCurrentDraftEntity(currentDraftEntity);
        formPetitionDAO.saveOrUpdate(formPetitionEntity);
        key = formPersistenceService.keyFromObject(currentDraftEntity.getForm().getCod());

        return key;
    }

    private FormPetitionEntity newFormPetitionEntity(P petition, boolean mainForm) {
        FormPetitionEntity formPetitionEntity = new FormPetitionEntity();
        formPetitionEntity.setPetition(petition);
        if (mainForm) {
            formPetitionEntity.setMainForm(SimNao.SIM);
        } else {
            formPetitionEntity.setMainForm(SimNao.NAO);
            formPetitionEntity.setTaskDefinitionEntity(petition.getProcessInstanceEntity().getCurrentTask().getTask().getTaskDefinition());
        }
        formPetitionDAO.saveOrUpdate(formPetitionEntity);
        return formPetitionEntity;
    }


    public FormPetitionEntity findFormPetitionEntity(P petition, String typeName, boolean mainForm) {

        FormPetitionEntity formPetitionEntity;

        final Predicate<FormPetitionEntity> byName = x -> {
            if (x.getForm() != null) {
                return typeName.equals(x.getForm().getFormType().getAbbreviation());
            } else if (x.getCurrentDraftEntity() != null) {
                return typeName.equals(x.getCurrentDraftEntity().getForm().getFormType().getAbbreviation());
            }
            return false;
        };

        final Predicate<FormPetitionEntity> byTask = x -> {
            return x.getTaskDefinitionEntity().equals(getCurrentTaskDefinition(petition).orElse(null));
        };

        if (mainForm) {
            formPetitionEntity = petition.getFormPetitionEntities().stream()
                    .filter(byName)
                    .findFirst().orElse(null);
        } else {
            formPetitionEntity = petition.getFormPetitionEntities().stream()
                    .filter(byName)
                    .filter(byTask)
                    .findFirst()
                    .orElse(null);
        }

        return formPetitionEntity;
    }

    private DraftEntity saveOrUpdateDraft(SInstance instance, DraftEntity draftEntity, SFormConfig config, Integer actor) {

        SIComposite draft;

        if (draftEntity.getForm() != null) {
            draft = loadByCodAndType(config, draftEntity.getForm().getCod(), instance.getType().getName());
        } else {
            draft = newInstance(config, instance.getType().getName());
        }

        instance.getDocument().persistFiles();
        copyValuesAndAnnotations(instance, draft);

        draftEntity.setForm(formPersistenceService.loadFormEntity(formPersistenceService.insertOrUpdate(draft, actor)));
        draftEntity.setEditionDate(new Date());

        draftDAO.saveOrUpdate(draftEntity);

        return draftEntity;
    }

    private DraftEntity createNewDraftWithoutSave() {
        final DraftEntity draftEntity = new DraftEntity();
        draftEntity.setStartDate(new Date());
        draftEntity.setEditionDate(new Date());
        return draftEntity;
    }

    /**
     * Consolida todos os rascunhos da petição
     * @param petition
     * @param formConfig
     * @return as novas entidades criadas
     */
    public List<FormEntity> consolidateDrafts(P petition, SFormConfig formConfig) {
        return petition.getFormPetitionEntities()
                .stream()
                .filter(formPetitionEntity -> formPetitionEntity.getCurrentDraftEntity() != null)
                .map(formPetitionEntity -> consolidadeDraft(formConfig, formPetitionEntity))
                .collect(Collectors.toList());
    }

    /**
     * Consolida o rascunho, copiando os valores do rascunho para o form principal criando versão inicial ou gerando nova versão
     * @param formConfig
     * @param formPetitionEntity
     * @return a nova versão criada
     */
    private FormEntity consolidadeDraft(SFormConfig formConfig, FormPetitionEntity formPetitionEntity) {

        final DraftEntity draft;
        final String      type;
        final SIComposite draftInstance;
        final SIComposite formInstance;
        final boolean     isFirstVersion;
        final FormKey     key;

        draft = draftDAO.get(formPetitionEntity.getCurrentDraftEntity().getCod());

        type = draft.getForm().getFormType().getAbbreviation();
        draftInstance = loadByCodAndType(formConfig, draft.getForm().getCod(), type);

        isFirstVersion = formPetitionEntity.getForm() == null;

        if (isFirstVersion) {
            formInstance = newInstance(formConfig, type);
        } else {
            formInstance = loadByCodAndType(formConfig, formPetitionEntity.getForm().getCod(), type);
        }

        copyValuesAndAnnotations(draftInstance, formInstance);

        if (isFirstVersion) {
            final Integer codActor = userService.getUserCodIfAvailable();
            key = formPersistenceService.insert(formInstance, codActor);
        } else {
            key = formPersistenceService.newVersion(formInstance, userService.getUserCodIfAvailable());
        }

        formPetitionEntity.setForm(formPersistenceService.loadFormEntity(key));
        formPetitionEntity.setCurrentDraftEntity(null);

        deassociateFormVersions(draft.getForm());
        draftDAO.delete(draft);
        formPetitionDAO.save(formPetitionEntity);

        return formPetitionEntity.getForm();
    }


    /**
     * Deletes all form versions associated with the given @param form.
     * It also delete all annotations and annotations versions associated with each version.
     *
     * @param form
     */
    public void deassociateFormVersions(FormEntity form) {
        if (form != null) {
            form.setCurrentFormVersionEntity(null);
            formDAO.saveOrUpdate(form);
            List<FormVersionEntity> fves = formVersionDAO.findVersions(form);
            if (!CollectionUtils.isEmpty(fves)) {
                Iterator<FormVersionEntity> it = fves.iterator();
                for (; it.hasNext(); ) {
                    FormVersionEntity fve = it.next();
                    deleteFormVersion(fve);
                }
            }
        }
    }

    private void deleteFormVersion(FormVersionEntity fve) {
        if (fve != null) {
            if (!CollectionUtils.isEmpty(fve.getFormAnnotations())) {
                Iterator<FormAnnotationEntity> it = fve.getFormAnnotations().iterator();
                for (; it.hasNext(); ) {
                    FormAnnotationEntity fae = it.next();
                    deleteAnnotation(fae);
                    it.remove();
                }
            }
            formVersionDAO.delete(fve);
        }
    }

    private void deleteAnnotation(FormAnnotationEntity fae) {
        if (fae != null) {
            FormAnnotationVersionEntity formAnnotationVersionEntity = fae.getAnnotationCurrentVersion();
            fae.setAnnotationCurrentVersion(null);
            formAnnotationDAO.saveOrUpdate(fae);
            deleteAnnotationVersion(formAnnotationVersionEntity);
            if (!CollectionUtils.isEmpty(fae.getAnnotationVersions())) {
                Iterator<FormAnnotationVersionEntity> it = fae.getAnnotationVersions().iterator();
                for (; it.hasNext(); ) {
                    FormAnnotationVersionEntity fave = it.next();
                    deleteAnnotationVersion(fave);
                    it.remove();
                }
            }
            formAnnotationDAO.delete(fae);
        }
    }

    private void deleteAnnotationVersion(FormAnnotationVersionEntity fave) {
        formAnnotationVersionDAO.delete(fave);
    }

    private void copyValuesAndAnnotations(SInstance source, SInstance target) {
        Value.copyValues(source, target);
        SIList<SIAnnotation> annotations = source.asAtrAnnotation().persistentAnnotations();
        if (annotations != null) {
            for (SIAnnotation sourceAnnotation : annotations) {
                //obtem o caminho completo da instancia anotada no formulario raiz
                SInstances.findDescendantById(source, sourceAnnotation.getTargetId()).ifPresent(si -> {
                    String pathFromRoot = si.getPathFromRoot();
                    //localiza a instancia correspondente no formulario destino
                    SInstance targetInstance;
                    if (pathFromRoot == null){
                        targetInstance =((SIComposite) target);
                    } else {
                        targetInstance = ((SIComposite) target).getField(pathFromRoot);
                    }
                    //Copiando todos os valores da anotação (inclusive o id na sinstance antiga)
                    SIAnnotation targetAnnotation = targetInstance.as(AtrAnnotation::new).annotation();
                    Value.copyValues(sourceAnnotation, targetAnnotation);
                    //Corrigindo o ID
                    targetAnnotation.setTarget(targetInstance);
                });

            }
        }
    }

    public SIComposite loadByCodAndType(SFormConfig config, Long cod, String type) {
        final FormKey formKey = formPersistenceService.keyFromObject(cod);
        final RefType refTypeByName = new RefType() {
            @Override
            protected SType<?> retrieve() {
                return config.getTypeLoader().loadTypeOrException(type);
            }
        };
        return (SIComposite) formPersistenceService.loadSInstance(formKey, refTypeByName, config.getDocumentFactory());
    }

    public SIComposite newInstance(SFormConfig config, String type) {
        final RefType refTypeByName = new RefType() {
            @Override
            protected SType<?> retrieve() {
                return config.getTypeLoader().loadTypeOrException(type);
            }
        };
        return (SIComposite) config.getDocumentFactory().createInstance(refTypeByName);
    }

    private Optional<TaskDefinitionEntity> getCurrentTaskDefinition(P petition) {
        ProcessInstanceEntity processInstanceEntity = petition.getProcessInstanceEntity();
        if (processInstanceEntity != null) {
            return Optional.of(processInstanceEntity.getCurrentTask().getTask().getTaskDefinition());
        }
        return Optional.empty();
    }

    public FormTypeEntity findFormTypeFromVersion(Long formVersionPK) {
        final FormVersionEntity formVersionEntity = formPersistenceService.loadFormVersionEntity(formVersionPK);
        return formVersionEntity.getFormEntity().getFormType();
    }

    public void removeFormPetitionEntity(PetitionEntity p, Class<? extends SType<?>> type, TaskDefinitionEntity taskDefinition) {
        findFormPetitionEntityByTypeNameAndTask(
                p.getCod(),
                SFormUtil.getTypeName(type),
                taskDefinition.getCod()
        ).ifPresent(x -> {
            p.getFormPetitionEntities().remove(x);
            formPetitionDAO.delete(x);
        });
    }

    public FormVersionHistoryEntity createFormVersionHistory(PetitionContentHistoryEntity contentHistory, FormPetitionEntity formPetition) {

        final FormVersionHistoryEntity formVersionHistoryEntity;
        final FormEntity               currentFormEntity;

        formVersionHistoryEntity = new FormVersionHistoryEntity();
        currentFormEntity = formPersistenceService.loadFormEntity(formPersistenceService.keyFromObject(formPetition.getForm().getCod()));

        formVersionHistoryEntity.setMainForm(formPetition.getMainForm());
        formVersionHistoryEntity.setCodFormVersion(currentFormEntity.getCurrentFormVersionEntity().getCod());
        formVersionHistoryEntity.setCodPetitionContentHistory(contentHistory.getCod());
        formVersionHistoryEntity.setFormVersion(currentFormEntity.getCurrentFormVersionEntity());
        formVersionHistoryEntity.setPetitionContentHistory(contentHistory);

        return formVersionHistoryEntity;

    }

}