package br.net.mirante.singular.form.wicket.grid;

import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.junit.Assert;
import org.junit.Test;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.comuns.STypeEMail;
import br.net.mirante.singular.form.wicket.test.base.AbstractSingularFormTest;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSRow;

public class NewRowTest extends AbstractSingularFormTest {

    STypeEMail email;
    STypeString nome;
    STypeInteger idade;

    @Override
    protected void populateMockType(STypeComposite<?> mockType) {

        nome = mockType.addFieldString("nome");
        nome.as(AtrBasic.class).label("Nome")
                .as(AtrBootstrap::new).newRow()
                .as(AtrBootstrap::new).colPreference(6);

        idade = mockType.addFieldInteger("idade");
        idade.as(AtrBasic.class).label("Idade")
                .as(AtrBootstrap::new).newRow()
                .as(AtrBootstrap::new).colPreference(2);

        email = mockType.addFieldEmail("email");
        email.as(AtrBasic.class).label("E-mail")
                .as(AtrBootstrap::new).newRow()
                .as(AtrBootstrap::new).colPreference(8);
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
