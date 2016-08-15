package br.net.mirante.singular.server.module.wicket.view.util.form;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.server.commons.config.SingularServerConfiguration;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
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
public class FormPage extends AbstractFormPage<PetitionEntity> {


    @Inject
    private SingularServerConfiguration singularServerConfiguration;

    public FormPage() {
        this(null);
    }

    public FormPage(FormPageConfig config) {
        super(PetitionEntity.class, config);
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
//                return currentModel.getObject().getDescription();
                return "";
            }
        });
    }

    @Override
    protected String getIdentifier() {
        return Optional.ofNullable(currentModel)
                .map(IModel::getObject)
                .map(PetitionEntity::getCod)
                .map(Object::toString).orElse(null);

    }
}
