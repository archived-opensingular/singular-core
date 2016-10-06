package org.opensingular.form.wicket.grid;

import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.util.STypeEMail;
import org.opensingular.form.wicket.helpers.SingularFormBaseTest;
import org.opensingular.singular.util.wicket.bootstrap.layout.BSRow;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.junit.Assert;
import org.junit.Test;

public class NewRowTest extends SingularFormBaseTest {

    STypeEMail email;
    STypeString nome;
    STypeInteger idade;

    @Override
    protected void buildBaseType(STypeComposite<?> mockType) {

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
        BSRow row1 = findRowForType(nome);
        BSRow row2 = findRowForType(idade);
        BSRow row3 = findRowForType(email);
        Assert.assertNotEquals(row1, row2);
        Assert.assertNotEquals(row1, row3);
        Assert.assertNotEquals(row2, row3);
    }

    BSRow findRowForType(SType<?> type) {
        return findFormComponentsByType(type).findFirst().get()
                .visitParents(BSRow.class, new IVisitor<BSRow, BSRow>() {
                    @Override
                    public void component(BSRow row, IVisit<BSRow> visit) {
                        visit.stop(row);
                    }
                });
    }

}
