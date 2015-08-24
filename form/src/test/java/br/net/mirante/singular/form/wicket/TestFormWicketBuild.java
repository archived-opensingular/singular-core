package br.net.mirante.singular.form.wicket;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoString;
import junit.framework.TestCase;

public class TestFormWicketBuild extends TestCase {

    public void testVeryBasic() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");
        MTipoString tipoCidade = pb.createTipo("cidade", MTipoString.class);
        tipoCidade.as(AtrBasic.class).label("Cidade").tamanhoEdicao(21);

        MIString iCidade = tipoCidade.novaInstancia();

        FormComponent<?> comp = UIBuilderWicket.createForEdit(iCidade);
        assertTrue(comp instanceof TextField);
        assertEquals("Cidade", comp.getLabel().getObject());
    }
}
