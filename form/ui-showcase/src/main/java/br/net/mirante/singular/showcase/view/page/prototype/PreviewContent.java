package br.net.mirante.singular.showcase.view.page.prototype;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.context.SFormConfig;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.showcase.view.template.Content;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * Created by nuk on 04/03/16.
 */
public class PreviewContent extends Content {

    @Inject
    @Named("formConfigWithDatabase")
    private SFormConfig<String> singularFormConfig;

    private final List<PrototypeContent.Field> fields;
    private SDictionary dictionary = SDictionary.create();

    public PreviewContent(String id, List<PrototypeContent.Field> fields) {
        super(id);
        this.fields = fields;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        PackageBuilder pkg = dictionary.createNewPackage("com.mirante.singular.preview");
        final STypeComposite<? extends SIComposite> root = pkg.createTipoComposto("root");
        for(PrototypeContent.Field f: fields){
            SType<?> type = dictionary.getType(f.typeName);
            root.addCampo(f.fieldName, STypeString.class);
        }
        queue(new SingularFormPanel<String>("singular-panel", singularFormConfig) {
            @Override
            protected SInstance createInstance(SFormConfig<String> singularFormConfig) {
                return SDocumentFactory.empty().createInstance(new RefType(){
                    protected SType<?> retrieve() { return root;    }
                });
            }
        });
    }

    @Override
    protected IModel<?> getContentTitlelModel() {
        return new ResourceModel("label.content.title");
    }

    @Override
    protected IModel<?> getContentSubtitlelModel() {
        return new ResourceModel("label.content.title");
    }

}
