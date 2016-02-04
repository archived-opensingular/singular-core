package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.io.FormSerializationUtil;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.util.xml.MElement;
import br.net.mirante.singular.form.util.xml.MParser;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class STypeCompositeTest {

    private STypeComposite<? extends SIComposite> baseType, subStuff;
    private STypeString name, content;

    @Before public void setup() {
        SDictionary dict = SDictionary.create();
        PacoteBuilder pkt = dict.criarNovoPacote("pkt");
        baseType = pkt.createTipoComposto("baseType");
        name = baseType.addCampoString("name");
        subStuff = baseType.addCampoComposto("subStuff");
        content = subStuff.addCampoString("content");
    }

    @Test public void setCompositeValue() throws Exception{
        SIComposite original = baseType.novaInstancia();
        original.getDescendant(name).setValor("My first name");
        original.getDescendant(content).setValor("My first content");

        assertThat(xml(original)).contains("My first name").contains("My first content");

        String backup = xml(original.getDescendant(subStuff));

        assertThat(xml(original.getDescendant(subStuff)))
                .doesNotContain("My first name").contains("My first content");

        original.getDescendant(name).setValor("My second name");
        original.getDescendant(content).setValor("My second content");

        assertThat(original.getDescendant(name).getValor()).isEqualTo("My second name");
        assertThat(original.getDescendant(content).getValor()).isEqualTo("My second content");

        original.getDescendant(subStuff)
                .setValor(MformPersistenciaXML.fromXML(subStuff, MParser.parse(backup)));

        assertThat(original.getDescendant(name).getValor()).isEqualTo("My second name");
        assertThat(original.getDescendant(content).getValor()).isEqualTo("My first content");

    }

    private String xml(SIComposite original) {
        return MformPersistenciaXML.toXML(original).toString();
    }

}
