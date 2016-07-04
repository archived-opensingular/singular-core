package br.net.mirante.singular.server.module.wicket.view.util.form;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.persistence.FormKey;
import br.net.mirante.singular.form.service.IFormService;
import br.net.mirante.singular.form.type.basic.AtrBasic;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.server.commons.config.SingularServerConfiguration;
import br.net.mirante.singular.server.commons.exception.SingularServerException;
import br.net.mirante.singular.server.commons.flow.metadata.ServerContextMetaData;
import br.net.mirante.singular.server.commons.persistence.entity.form.Petition;
import br.net.mirante.singular.server.commons.wicket.SingularSession;
import br.net.mirante.singular.server.commons.wicket.view.form.AbstractFormPage;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

@SuppressWarnings("serial")
@MountPath("/view")
public class FormPage extends AbstractFormPage<Petition> {


    @Inject
    private SingularServerConfiguration singularServerConfiguration;

    public FormPage() {
        this(new FormPageConfig());
    }

    public FormPage(FormPageConfig config) {
        super(Petition.class,  config, null);
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return $m.get(()->content.getSingularFormPanel().getRootTypeSubtitle());
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

    @Override
    protected void onNewPetitionCreation(Petition petition, FormPageConfig config) {
        super.onNewPetitionCreation(petition, config);
        //TODO (por Daniel Bordin) O código abaixo, não fui eu quem fez, faz sentido? Como não sei onde fica essa
        // página não testei.
        singularServerConfiguration.processDefinitionFormNameMap().forEach((key, value) -> {
            if (value.equals(config.processType)) {
                petition.setProcessType(Flow.getProcessDefinition(key).getKey());
            }
        });
    }

    @Override
    protected String getIdentifier() {
        return Optional.ofNullable(currentModel)
                .map(IModel::getObject)
                .map(Petition::getCod)
                .map(Object::toString).orElse(null);

    }
}
