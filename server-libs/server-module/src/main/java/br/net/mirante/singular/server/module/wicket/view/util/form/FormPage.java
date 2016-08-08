package br.net.mirante.singular.server.module.wicket.view.util.form;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.server.commons.config.SingularServerConfiguration;
import br.net.mirante.singular.server.commons.persistence.entity.form.OldPetitionEntity;
import br.net.mirante.singular.server.commons.wicket.view.form.AbstractFormPage;
import br.net.mirante.singular.server.commons.wicket.view.form.FormPageConfig;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.wicketstuff.annotation.mount.MountPath;

import javax.inject.Inject;
import java.util.Optional;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

@SuppressWarnings("serial")
@MountPath("/view")
public class FormPage extends AbstractFormPage<OldPetitionEntity> {


    @Inject
    private SingularServerConfiguration singularServerConfiguration;

    public FormPage() {
        this(null);
    }

    public FormPage(FormPageConfig config) {
        super(OldPetitionEntity.class, config);
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return $m.get(() -> content.getSingularFormPanel().getRootTypeSubtitle());
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return $m.get(() -> {
            if (getIdentifier() == null) {
                return new ResourceModel("label.form.content.title", "Nova Solicitação").getObject();
            } else {
                return currentModel.getObject().getDescription();
            }
        });
    }

    @Override
    protected void onNewPetitionCreation(OldPetitionEntity oldPetitionEntity, FormPageConfig config) {
        super.onNewPetitionCreation(oldPetitionEntity, config);
        if (config.containsProcessDefinition()) {
            oldPetitionEntity.setProcessType(Flow.getProcessDefinition(config.getProcessDefinition()).getKey());
        }
    }

    @Override
    protected String getIdentifier() {
        return Optional.ofNullable(currentModel)
                .map(IModel::getObject)
                .map(OldPetitionEntity::getCod)
                .map(Object::toString).orElse(null);

    }
}
