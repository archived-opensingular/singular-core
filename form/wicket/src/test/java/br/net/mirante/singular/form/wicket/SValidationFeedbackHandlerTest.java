package br.net.mirante.singular.form.wicket;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.function.Supplier;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;

import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.curriculo.mform.SPackageCurriculo;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.wicket.component.SingularForm;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.SInstanceRootModel;
import br.net.mirante.singular.form.wicket.test.base.TestPanel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.panel.FormPanel;

public class SValidationFeedbackHandlerTest {
    WicketTester                      tester;
    protected SDictionary             dicionario;
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

    private TestPanel buildTestPanel(BSGrid rootContainer) {
        SingularForm<Object> form = new SingularForm<>("form");

        TestPanel testPanel = new TestPanel("body-child") {
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

    @Test
    public void testBasic() {
        BSGrid rootContainer = new BSGrid("teste");
        TestPanel testPanel = buildTestPanel(rootContainer);
        tester.startComponentInPage(testPanel);

        SIComposite instancia = (SIComposite) createIntance(() -> {
            dicionario.loadPackage(SPackageCurriculo.class);
            return dicionario.getType(SPackageCurriculo.TIPO_CURRICULO);
        });

        IModel<SIComposite> mCurriculo = new SInstanceRootModel<SIComposite>(instancia);
        WicketBuildContext ctx = new WicketBuildContext(rootContainer.newColInRow(), testPanel.getBodyContainer(), mCurriculo);
        singularFormContext.getUIBuilder().build(ctx, ViewMode.EDITION);

//        Visits.visit(testPanel, (c, v) -> {
//            System.out.println(c.getMetaData(SValidationFeedbackHandler.MDK) + " " + c.getPageRelativePath());
//        });

        Set<? extends SInstance> lowerBound = SValidationFeedbackHandler.collectLowerBoundInstances(testPanel);
//        System.out.println(lowerBound);
        assertFalse(lowerBound.isEmpty());
    }
}
