package org.opensingular.form.wicket.test.base;

import org.opensingular.form.curriculo.SPackageCurriculo;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.wicket.SingularFormConfigWicketImpl;
import org.opensingular.form.wicket.SingularFormContextWicket;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.component.SingularForm;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.model.SInstanceRootModel;
import org.opensingular.singular.util.wicket.bootstrap.layout.BSContainer;
import org.opensingular.singular.util.wicket.bootstrap.layout.BSGrid;
import org.opensingular.singular.util.wicket.panel.FormPanel;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Supplier;

import static org.opensingular.singular.util.wicket.util.WicketUtils.findContainerRelativePath;

public class TestFormWicketBuild  {

    WicketTester tester;
    protected SDictionary dicionario;
    private SingularFormContextWicket singularFormContext = new SingularFormConfigWicketImpl().createContext();

    @Before
    public void setUpDicionario() {
        dicionario = SDictionary.create();
    }

    @Before
    public void setUp() {
        tester = new WicketTester(new WebApplication() {
            @Override
            public Class<? extends Page> getHomePage() {
                return null;
            }
        });
        tester.getApplication().getMarkupSettings().setDefaultMarkupEncoding("utf-8");
    }

    protected static SInstance createIntance(Supplier<SType<?>> typeSupplier) {
        RefType ref = new RefType() {
            @Override
            protected SType<?> retrieve() {
                return typeSupplier.get();
            }
        };
        return SDocumentFactory.empty().createInstance(ref);
    }

    @Test
    public void testBasic() {
        BSGrid    rootContainer = new BSGrid("teste");
        TestPanel testPanel     = buildTestPanel(rootContainer);

        SIString instancia = (SIString) createIntance(() -> {
            PackageBuilder pb = dicionario.createNewPackage("teste");
            STypeString tipoCidade = pb.createType("cidade", STypeString.class);
            tipoCidade.asAtr().label("Cidade").editSize(21);
            return tipoCidade;
        });

        IModel<SIString> mCidade = new SInstanceRootModel<SIString>(instancia);
        mCidade.getObject().setValue("Brasilia");
        WicketBuildContext ctx = new WicketBuildContext(rootContainer.newColInRow(), testPanel.getBodyContainer(), mCidade);
        singularFormContext.getUIBuilder().build(ctx, ViewMode.EDIT);

        tester.startComponentInPage(testPanel);
        Assertions.assertThat(mCidade.getObject().getValue()).isEqualTo("Brasilia");

        FormTester formTester = tester.newFormTester("body-child:container:form");
        formTester.setValue(findContainerRelativePath(formTester.getForm(), "cidade").get(), "Guará");
        formTester.submit();

        Assertions.assertThat(mCidade.getObject().getValue()).isEqualTo("Guará");
    }

    @Test
    public void testCurriculo() {
        BSGrid rootContainer = new BSGrid("teste");
        TestPanel testPanel = buildTestPanel(rootContainer);

        SIComposite instancia = (SIComposite) createIntance(() -> {
            dicionario.loadPackage(SPackageCurriculo.class);
            return dicionario.getType(SPackageCurriculo.TIPO_CURRICULO);
        });

        IModel<SIComposite> mCurriculo = new SInstanceRootModel<SIComposite>(instancia);
        WicketBuildContext ctx = new WicketBuildContext(rootContainer.newColInRow(), testPanel.getBodyContainer(), mCurriculo);
//        UIBuilderWicket.buildForEdit(ctx, mCurriculo);


        tester.startComponentInPage(testPanel);
        FormTester formTester = tester.newFormTester("body-child:container:form");
        formTester.submit();
    }

    private TestPanel buildTestPanel(BSGrid rootContainer){
        SingularForm<Object> form = new SingularForm<>("form");

        TestPanel testPanel = new TestPanel("body-child"){
            @Override
            public Component buildContainer(String id) {
                return new FormPanel(id, form) {
                    @Override
                    protected Component newFormBody(String id) {
                        return new BSContainer<>(id).appendTag("div", rootContainer);
                    }
                };
            }
        };
        return testPanel;
    }
}
