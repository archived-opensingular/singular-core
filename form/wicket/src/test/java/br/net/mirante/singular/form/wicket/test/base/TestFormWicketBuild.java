package br.net.mirante.singular.form.wicket.test.base;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.findContainerRelativePath;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import br.net.mirante.singular.form.curriculo.mform.SPackageCurriculo;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.wicket.test.AbstractWicketFormTest;
import br.net.mirante.singular.form.wicket.SingularFormConfigWicketImpl;
import br.net.mirante.singular.form.wicket.SingularFormContextWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.panel.FormPanel;

public class TestFormWicketBuild extends AbstractWicketFormTest {

    WicketTester tester;
    private SingularFormContextWicket singularFormContext = new SingularFormConfigWicketImpl().createContext();

    @Before
    public void setUp() {
        tester = new WicketTester(new WebApplication() {
            @Override
            public Class<? extends Page> getHomePage() {
                return null;
            }
        });
    }

    @Test
    public void testBasic() {
        BSGrid rootContainer = new BSGrid("teste");
        TestPanel testPanel = buildTestPanel(rootContainer);

        SIString instancia = (SIString) createIntance(() -> {
            PackageBuilder pb = dicionario.createNewPackage("teste");
            STypeString tipoCidade = pb.createTipo("cidade", STypeString.class);
            tipoCidade.as(AtrBasic.class).label("Cidade").tamanhoEdicao(21);
            return tipoCidade;
        });

        IModel<SIString> mCidade = new MInstanceRootModel<SIString>(instancia);
        mCidade.getObject().setValue("Brasilia");
        WicketBuildContext ctx = new WicketBuildContext(rootContainer.newColInRow(), testPanel.getBodyContainer(), mCidade);
        singularFormContext.getUIBuilder().build(ctx, ViewMode.EDITION);

        tester.startComponentInPage(testPanel);
        Assertions.assertThat("Brasilia").isEqualTo(mCidade.getObject().getValue());

        FormTester formTester = tester.newFormTester("body-child:container:form");
        formTester.setValue(findContainerRelativePath(formTester.getForm(), "cidade").get(), "Guará");
        formTester.submit();

        Assertions.assertThat("Guará").isEqualTo(mCidade.getObject().getValue());
    }

    @Test
    public void testCurriculo() {
        BSGrid rootContainer = new BSGrid("teste");
        TestPanel testPanel = buildTestPanel(rootContainer);

        SIComposite instancia = (SIComposite) createIntance(() -> {
            dicionario.loadPackage(SPackageCurriculo.class);
            return dicionario.getType(SPackageCurriculo.TIPO_CURRICULO);
        });

        IModel<SIComposite> mCurriculo = new MInstanceRootModel<SIComposite>(instancia);
        WicketBuildContext ctx = new WicketBuildContext(rootContainer.newColInRow(), testPanel.getBodyContainer(), mCurriculo);
//        UIBuilderWicket.buildForEdit(ctx, mCurriculo);


        tester.startComponentInPage(testPanel);
        FormTester formTester = tester.newFormTester("body-child:container:form");
        formTester.submit();
    }

    private TestPanel buildTestPanel(BSGrid rootContainer){
        Form<Object> form = new Form<>("form");

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
