package br.net.mirante.singular.form.wicket.helpers;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.RefService;
import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.document.DefaultServiceRegistry;
import br.net.mirante.singular.form.document.RefSDocumentFactory;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocument;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.document.ServiceRegistry;
import br.net.mirante.singular.form.document.TypeLoader;
import br.net.mirante.singular.form.wicket.SingularFormContextWicket;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.component.SingularForm;
import br.net.mirante.singular.form.wicket.component.SingularValidationButton;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;

public class DummyPage extends WebPage {

    final public SFormConfig<String> mockFormConfig = new MockFormConfig();
    protected ViewMode viewMode = ViewMode.EDITION;
    protected AnnotationMode annotationMode = AnnotationMode.NONE;
    protected SIComposite currentInstance;
    protected Consumer<STypeComposite> typeBuilder;
    protected Function<SType, SIComposite> instanceCreator;

    private SingularForm<?> form = new SingularForm<>("form");

    private SingularFormPanel<String> singularFormPanel = new SingularFormPanel<String>("singularFormPanel", mockFormConfig) {
        @Override
        protected SInstance createInstance(SFormConfig<String> singularFormConfig) {
            return createCurrentInstance(buildBaseType());
        }

        @Override
        public ViewMode getViewMode() { return viewMode;    }

        @Override
        public AnnotationMode annotation() {    return annotationMode;  }
    };

    private Optional<SType<?>> buildBaseType() {
        Optional<SType<?>> baseType = mockFormConfig.getTypeLoader().loadType("mockType");
        baseType.ifPresent((x) -> {
            if (baseType.get() instanceof STypeComposite) {
                typeBuilder.accept((STypeComposite) baseType.get());
            }
        });
        return baseType;
    }

    private SInstance createCurrentInstance(Optional<SType<?>> baseType) {
        Optional.of(instanceCreator).ifPresent((x) -> {
                currentInstance = instanceCreator.apply(baseType.get());
            });
        return currentInstance ;
    }

    private SingularValidationButton singularValidationButton = new SingularValidationButton("validate-btn", singularFormPanel.getRootInstance()) {
        @Override
        protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {}
    };

    public DummyPage() {
        add(form.add(singularFormPanel, singularValidationButton));
    }

    public Form<?> getForm() {
        return form;
    }

    public SingularFormPanel<String> getSingularFormPanel() {
        return singularFormPanel;
    }

    public SingularValidationButton getSingularValidationButton() {
        return singularValidationButton;
    }

    public void setAsVisualizationView() {  viewMode = ViewMode.VISUALIZATION;  }
    public void setAsEditView() {  viewMode = ViewMode.EDITION;  }

    public void enableAnnotation() { annotationMode = AnnotationMode.EDIT; }

    public SIComposite getCurrentInstance() { return currentInstance; }

    public void setInstanceCreator(Function<SType, SIComposite> instanceCreator) {
        this.instanceCreator = instanceCreator;
    }

    public void setTypeBuilder(Consumer<STypeComposite> typeBuilder) {
        this.typeBuilder = typeBuilder;
    }
}

class MockFormConfig implements SFormConfig<String>, Serializable {

    private final MockSDocumentFactory documentFactory = new MockSDocumentFactory();
    private final MockTypeLoader mockTypeLoader = new MockTypeLoader();

    @Override
    public SDocumentFactory getDocumentFactory() {
        return documentFactory;
    }

    @Override
    public TypeLoader<String> getTypeLoader() {
        return mockTypeLoader;
    }
}

class MockSDocumentFactory extends SDocumentFactory implements Serializable {

    private final DefaultServiceRegistry defaultServiceRegistry = new DefaultServiceRegistry();

    private final SingularFormContextWicket singularFormContextWicket = new Context();

    {
        defaultServiceRegistry.bindLocalService(SingularFormContextWicket.class, new RefService<SingularFormContextWicket>() {
            @Override
            public SingularFormContextWicket get() {
                return singularFormContextWicket;
            }
        });
    }

    @Override
    public RefSDocumentFactory getDocumentFactoryRef() {
        final MockSDocumentFactory documentFactory = this;
        return new RefSDocumentFactory() {
            @Override
            protected SDocumentFactory retrieve() {
                return documentFactory;
            }
        };
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return defaultServiceRegistry;
    }

    @Override
    protected void setupDocument(SDocument document) {}

    private class Context implements SingularFormContextWicket, Serializable {
        @Override
        public UIBuilderWicket getUIBuilder() {
            return new UIBuilderWicket();
        }

        @Override
        public ServiceRegistry getServiceRegistry() {
            return defaultServiceRegistry;
        }
    }
}

class MockTypeLoader extends TypeLoader<String> implements Serializable {

    transient private final SDictionary dictionary;

    {
        dictionary = SDictionary.create();
    }

    @Override
    protected Optional<RefType> loadRefTypeImpl(String typeId) {
        return Optional.of(new RefType() {
            @Override
            protected SType<?> retrieve() {
                final SType<?> typeOptional = dictionary.getTypeOptional(typeId);
                if (typeOptional != null) {
                    return typeOptional;
                } else {
                    return dictionary.createNewPackage(typeId).createCompositeType("mockRoot");
                }
            }
        });
    }

    @Override
    protected Optional<SType<?>> loadTypeImpl(String typeId) {
        final SType<?> typeOptional = dictionary.getTypeOptional(typeId);
        if (typeOptional != null) {
            return Optional.of(typeOptional);
        } else {
            return Optional.of(dictionary.createNewPackage(typeId).createCompositeType("mockRoot"));
        }
    }
}