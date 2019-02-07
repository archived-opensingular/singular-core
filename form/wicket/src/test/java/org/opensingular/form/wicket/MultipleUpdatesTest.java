package org.opensingular.form.wicket;

import org.apache.wicket.util.tester.WicketTestCase;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SInstance;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.SIInteger;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.generic.SIGenericComposite;
import org.opensingular.form.type.generic.STGenericComposite;
import org.opensingular.form.view.SViewListByTable;
import org.opensingular.form.wicket.panel.SingularFormPanel;

import javax.annotation.Nonnull;

public class MultipleUpdatesTest extends WicketTestCase {
    /*tester.debugComponentTrees(); use this to print de componentpaths*/
    @Test
    public void updateListenerShouldBeExecutedOnlyOnceWhenTriggered() {
        SingularFormPanel formPanel = tester.startComponentInPage(new SingularFormPanel("id", STTeste.class));
        //Clicka no link de adicionar 15x
        for (int i = 0; i < 15; i++) {
            tester.executeAjaxEvent("id:generated:_:1:_:1:_:1:_:1:_:2:_:1:_:1:_:1:_:1:_fo:_ft:_:1:_add", "click");
        }
        //ajax update no decimo elemento (field nome)
        tester.executeAjaxEvent("id:generated:_:1:_:1:_:1:_:1:_:2:_:1:_:1:_:1:_:1:_fo:_co:_:1:not-empty-content:_b:_e:118:_r:_:1:_:1:_:4:nome", "singular:process");
        SITeste teste = (SITeste) formPanel.getInstance();

        int updateCount = 0;
        for (SITesteList testeList : teste.getField(teste.getType().testeList)) {
            updateCount += testeList.getField(testeList.getType().updateCount).getInteger();
        }
        assertThat(updateCount, Matchers.is(1));
    }

    @SInfoPackage
    public static class SPackageTest extends SPackage {

    }

    @SInfoType(spackage = SPackageTest.class)
    public static class STTeste extends STGenericComposite<SITeste> {

        public STTeste() {
            super(SITeste.class);
        }

        public STypeList<STTesteList, SITesteList> testeList;

        @Override
        protected void onLoadType(@Nonnull TypeBuilder tb) {
            testeList = addFieldListOf("testeList", STTesteList.class);

            testeList.withView(new SViewListByTable());
        }
    }

    @SInfoType(spackage = SPackageTest.class)
    public static class STTesteList extends STGenericComposite<SITesteList> {

        public STTesteList() {
            super(SITesteList.class);
        }

        public STypeString  nome;
        public STypeInteger updateCount;

        @Override
        protected void onLoadType(@Nonnull TypeBuilder tb) {
            nome = addFieldString("nome");
            updateCount = addFieldInteger("updateCount");
            updateCount.setInitialValue(0);

            this.asAtr().dependsOn(nome);
            this.withUpdateListener(this::update);
        }

        private void update(SInstance sInstance) {
            SIInteger siUpdateCount = sInstance.findNearestOrException(updateCount);
            siUpdateCount.setValue(1);
        }
    }

    public static class SITesteList extends SIGenericComposite<STTesteList> {
    }

    public static class SITeste extends SIGenericComposite<STTeste> {
    }
}
