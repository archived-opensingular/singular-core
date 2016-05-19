package br.net.mirante.singular.server.module.wicket.view.util.form;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.wicketstuff.annotation.mount.MountPath;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.service.FormDTO;
import br.net.mirante.singular.form.service.IFormService;
import br.net.mirante.singular.form.type.basic.AtrBasic;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.persistence.entity.ProcessInstanceEntity;
import br.net.mirante.singular.server.commons.config.SingularServerConfiguration;
import br.net.mirante.singular.server.commons.exception.SingularServerException;
import br.net.mirante.singular.server.commons.flow.metadata.ServerContextMetaData;
import br.net.mirante.singular.server.commons.persistence.entity.form.Petition;
import br.net.mirante.singular.server.commons.service.AnalisePeticaoService;
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.server.commons.wicket.SingularSession;
import br.net.mirante.singular.server.commons.wicket.view.form.AbstractFormPage;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;

@SuppressWarnings("serial")
@MountPath("/view")
public class FormPage extends AbstractFormPage {

    private IModel<Petition> currentModel;
    
    private final IModel<FormDTO> formModel = $m.ofValue();

    @Inject
    private PetitionService petitionService;

    @Inject
    private AnalisePeticaoService analisePeticaoService;

    @Inject
    private IFormService formService;
    
    @Inject
    @Named("formConfigWithDatabase")
    private SFormConfig<String> singularFormConfig;

    @Inject
    private SingularServerConfiguration singularServerConfiguration;

    public FormPage() {
        super(new FormPageConfig());
    }

    public FormPage(FormPageConfig config) {
        super(config);
    }

    @SuppressWarnings("unchecked")
    protected List<MTransition> currentTaskTransitions() {
        return Optional
                .ofNullable(Flow.getTaskInstance(analisePeticaoService.findCurrentTaskByPetitionId(config.formId)))
                .map(TaskInstance::getFlowTask)
                .map(MTask::getTransitions)
                .orElse(Collections.EMPTY_LIST);
    }

    @Override
    protected void executeTransition(String transitionName, IModel<? extends SInstance> currentInstance) {
        analisePeticaoService.salvarExecutarTransicao(transitionName, currentModel.getObject(), formModel.getObject(), currentInstance.getObject());
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return new Model<String>() {
            @Override
            public String getObject() {
                return content.getSingularFormPanel().getRootTypeSubtitle();
            }
        };
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return $m.get(()-> {
            if (getIdentifier() == null) {
                return new ResourceModel("label.form.content.title", "Nova Solicitação").getObject();
            } else {
                return currentModel.getObject().getDescription();
            }
        });
    }

    protected SInstance createInstance(SDocumentFactory documentFactory, RefType refType) {
        if (formModel.getObject() == null || formModel.getObject().getCod() == null) {
            return documentFactory.createInstance(refType);
        } else {
            return formService.loadFormInstance(formModel.getObject().getCod(), refType, documentFactory);
        }
    }
    
    @Override
    protected ProcessInstanceEntity getProcessInstance() {
        return currentModel.getObject().getProcessInstanceEntity();
    }

    @Override
    protected void setProcessInstance(ProcessInstanceEntity pie) {
        currentModel.getObject().setProcessInstanceEntity(pie);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void saveForm(IModel<? extends SInstance> currentInstance) {
        petitionService.saveOrUpdate(updateDescription(currentInstance), formModel.getObject(), currentInstance.getObject());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void send(IModel<? extends SInstance> currentInstance) {
        petitionService.send(updateDescription(currentInstance), formModel.getObject(), currentInstance.getObject());
    }

    private Petition updateDescription(IModel<? extends SInstance> currentInstance){
        Petition petition = currentModel.getObject();
        if (currentInstance.getObject() instanceof SIComposite) {
            petition.setDescription(currentInstance.getObject().toStringDisplay());
        }
        return petition;
    }

    protected void loadOrCreateFormModel(String formId, String type, ViewMode viewMode, AnnotationMode annotationMode) {
        Petition peticao;
        FormDTO formDTO = null;
        if (formId == null || formId.isEmpty()) {
            peticao = new Petition();
            peticao.setType(type);
            singularServerConfiguration.processDefinitionFormNameMap().forEach((key, value) -> {
                if (value.equals(type)) {
                    peticao.setProcessType(Flow.getProcessDefinition(key).getKey());
                }
            });
            peticao.setCreationDate(new Date());
            peticao.setDescription("Nova Solicitação");
            peticao.setProcessName(recuperarNomeProcesso(type));
            
        } else {
            peticao = (Petition) petitionService.find(Long.valueOf(formId));
            if(peticao.getCodForm() != null){
                formDTO = formService.findForm(peticao.getCodForm());
            }
        }
        if(formDTO == null){
            formDTO = new FormDTO();
        }

        formModel.setObject(formDTO);
        currentModel = $m.ofValue(peticao);
    }

    @Override
    protected void configureCustomButtons(BSContainer<?> buttonContainer, BSContainer<?> modalContainer, ViewMode viewMode, AnnotationMode annotationMode, IModel<? extends SInstance> currentInstance) {
        List<MTransition> trans = currentTaskTransitions();
        if (CollectionUtils.isNotEmpty(trans) && (ViewMode.EDITION.equals(viewMode) || AnnotationMode.EDIT.equals(annotationMode))) {
            int index = 0;
            for (MTransition t : trans) {
                if (t.getMetaDataValue(ServerContextMetaData.KEY) != null && t.getMetaDataValue(ServerContextMetaData.KEY).isEnabledOn(SingularSession.get().getServerContext())) {
                    String btnId = "flow-btn" + index;
                    buildFlowTransitionButton(
                            btnId, buttonContainer,
                            modalContainer,  t.getName(),
                            currentInstance, viewMode);
                }
            }
        } else {
            buttonContainer.setVisible(false).setEnabled(false);
        }
    }
    
    private String recuperarNomeProcesso(String typeName) {
        STypeComposite<?> canabidiol = (STypeComposite<?>) singularFormConfig
                .getTypeLoader().loadType(typeName).orElseThrow(() -> new SingularServerException("Não foi possivel carregar o tipo"));
        return canabidiol.as(AtrBasic::new).getLabel();
    }

    @Override
    protected IModel<? extends Petition> getFormModel() {
        return currentModel;
    }

    @Override
    protected boolean hasProcess() {
        return currentModel.getObject().getProcessInstanceEntity() != null;
    }


    @Override
    protected String getIdentifier() {
        return Optional.ofNullable(currentModel)
                .map(IModel::getObject)
                .map(Petition::getCod)
                .map(Object::toString)
                .orElse(null);

    }
}
