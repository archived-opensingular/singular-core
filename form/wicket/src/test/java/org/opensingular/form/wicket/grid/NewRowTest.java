package org.opensingular.form.wicket.grid;

import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.junit.Test;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.util.STypeEMail;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSRow;

import static org.fest.assertions.api.Assertions.assertThat;

public class NewRowTest {

    private static STypeEMail email;
    private static STypeString nome;
    private static STypeInteger idade;

    private SingularDummyFormPageTester tester;

    private static void buildBaseType(STypeComposite<?> mockType) {

        nome = mockType.addFieldString("nome");
        nome.asAtr().label("Nome")
                .asAtrBootstrap().newRow()
                .asAtrBootstrap().colPreference(6);

        idade = mockType.addFieldInteger("idade");
        idade.asAtr().label("Idade")
                .asAtrBootstrap().newRow()
                .asAtrBootstrap().colPreference(2);

        email = mockType.addFieldEmail("email");
        email.asAtr().label("E-mail")
                .asAtrBootstrap().newRow()
                .asAtrBootstrap().colPreference(8);
    }

    @Test
    public void testIfEveryTypeIsInDiferentRow() {
        tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(NewRowTest::buildBaseType);
        tester.startDummyPage();

        BSRow nomeRow = findRowForType(nome);
        BSRow idadeRow = findRowForType(idade);
        BSRow emailRow = findRowForType(email);

        assertThat(nomeRow).isNotEqualTo(idadeRow);
        assertThat(nomeRow).isNotEqualTo(emailRow);
        assertThat(emailRow).isNotEqualTo(idadeRow);
    }

    BSRow findRowForType(SType<?> type) {
        return tester.getAssertionsForm().getSubCompomentWithType(type).getTargetOrException()
                .visitParents(BSRow.class, new IVisitor<BSRow, BSRow>() {
                    @Override
                    public void component(BSRow row, IVisit<BSRow> visit) {
                        visit.stop(row);
                    }
                });
    }

}
