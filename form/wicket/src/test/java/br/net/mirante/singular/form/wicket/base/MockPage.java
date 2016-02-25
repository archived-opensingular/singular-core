package br.net.mirante.singular.form.wicket.base;

import java.util.Optional;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;

import br.net.mirante.singular.form.mform.RefService;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.context.SFormConfig;
import br.net.mirante.singular.form.mform.document.DefaultServiceRegistry;
import br.net.mirante.singular.form.mform.document.RefSDocumentFactory;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.mform.document.ServiceRegistry;
import br.net.mirante.singular.form.mform.document.TypeLoader;
import br.net.mirante.singular.form.wicket.SingularFormContextWicket;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;

public abstract class MockPage extends WebPage {

    private SFormConfig<String> mockFormConfig = new MockFormConfig();

    public MockPage() {
        add(new Form("form").add(new SingularFormPanel<String>("singularFormPanel", mockFormConfig) {
            @Override
            protected SInstance createInstance(SFormConfig<String> singularFormConfig) {
                final Optional<SType<?>> mockType = mockFormConfig.getTypeLoader().loadType("mockType");
                if (mockType.isPresent()) {
                    if (mockType.get() instanceof STypeComposite) {
                        populateType((STypeComposite) mockType.get());
                    }
                }
                return mockFormConfig.getDocumentFactory().createInstance(new RefType() {
                    @Override
                    protected SType<?> retrieve() {
                        return mockType.get();
                    }
                });
            }
        }));
    }

    protected abstract void populateType(STypeComposite<?> mockType);
}

class MockFormConfig implements SFormConfig<String> {

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

class MockSDocumentFactory extends SDocumentFactory {

    private final DefaultServiceRegistry defaultServiceRegistry = new DefaultServiceRegistry();

    private final SingularFormContextWicket singularFormContextWicket = new SingularFormContextWicket() {
        @Override
        public UIBuilderWicket getUIBuilder() {
            return new UIBuilderWicket();
        }

        @Override
        public ServiceRegistry getServiceRegistry() {
            return defaultServiceRegistry;
        }
    };

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
    protected void setupDocument(SDocument document) {

    }
}

class MockTypeLoader extends TypeLoader<String> {

    private final SDictionary dictionary;

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
                    return dictionary.createNewPackage(typeId).createTipoComposto("mockRoot");
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
            return Optional.of(dictionary.createNewPackage(typeId).createTipoComposto("mockRoot"));
        }
    }
}