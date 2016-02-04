package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.util.xml.MParser;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class STypeListTest {

    private STypeComposite<? extends SIComposite> baseType;
    private STypeString name, content;
    private STypeLista<STypeComposite<SIComposite>, SIComposite> listType;

    @Before
    public void setup() {
        SDictionary dict = SDictionary.create();
        PacoteBuilder pkt = dict.criarNovoPacote("pkt");
        baseType = pkt.createTipoComposto("baseType");
        name = baseType.addCampoString("name");
        listType = baseType.addCampoListaOfComposto("listField", "subStuff");
        content = listType.getTipoElementos().addCampoString("content");
    }

    @Test
    public void setCompositeValue() throws Exception{
        SIComposite original = baseType.novaInstancia();
        original.getDescendant(name).setValor("My first name");
        SIComposite e1 = original.getDescendant(listType).addNovo();
        e1.getDescendant(content).setValor("My first content");

        assertThat(xml(original)).contains("My first name").contains("My first content");

        String backup = xml(original.getDescendant(listType));

        assertThat(xml(original.getDescendant(listType)))
                .doesNotContain("My first name").contains("My first content");

        original.getDescendant(name).setValor("My second name");
        e1.getDescendant(content).setValor("My second content");

        assertThat(xml(original)).contains("My second name").contains("My second content");

        original.getDescendant(listType)
                .setValor(MformPersistenciaXML.fromXML(listType, MParser.parse(backup)));

        assertThat(xml(original)).contains("My second name").contains("My first content");

    }

    private String xml(SInstance original) {
        return MformPersistenciaXML.toXML(original).toString();
    }
}
