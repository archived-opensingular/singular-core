package br.net.mirante.singular.form.mform.freemarker;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Date;

import org.fest.assertions.api.AbstractAssert;
import org.fest.assertions.api.StringAssert;
import org.junit.Before;
import org.junit.Test;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.core.STypeString;

public class FormFreemarkerUtilTest {

    private STypeComposite<? extends SIComposite> curriculoType;
    private STypeComposite<SIComposite> dadosType;
    private STypeList<STypeComposite<SIComposite>, SIComposite> certificadosType;

    @Before public void setup() {
        SDictionary dict = SDictionary.create();
        PackageBuilder pkt = dict.createNewPackage("pkt");
        curriculoType = pkt.createCompositeType("curriculo");

        dadosType = curriculoType.addFieldComposite("dados");
        dadosType.addFieldString("nome");
        dadosType.addFieldInteger("idade");
        dadosType.addFieldString("time");

        certificadosType = curriculoType.addFieldListOfComposite("certificados", "certificado");
        STypeComposite<SIComposite> certificadoType = certificadosType.getElementsType();
        certificadoType.addFieldString("nome");
        certificadoType.addFieldDate("data");
    }

    @Test
    public void compositeSimpleTest() {
        SIComposite curriculo = curriculoType.newInstance();
        curriculo.setValue("dados.nome", "Paulo Silva");
        curriculo.setValue("dados.idade", 20);

        SIComposite certificado = (SIComposite) curriculo.getFieldList("certificados").addNew();
        certificado.setValue("nome", "Java");
        certificado.setValue("data", new Date(2016 - 1900, 3 - 1, 10));

        certificado = (SIComposite) curriculo.getFieldList("certificados").addNew();
        certificado.setValue("nome", "Oracle");

        assertMerge(curriculo, "dados", "Nome: ${nome}").isEqualTo("Nome: Paulo Silva");
        assertMerge(curriculo, "dados", "Idade: ${idade}").isEqualTo("Idade: 20");
        assertMerge(curriculo, "dados", "Time: ${time!}").isEqualTo("Time: ");

        assertMerge(curriculo, null, "Nome: ${dados.nome}").isEqualTo("Nome: Paulo Silva");
        assertMerge(curriculo, null, "Idade: ${dados.idade}").isEqualTo("Idade: 20");
        assertMerge(curriculo, null, "Idade: ${dados.idade.value()}").isEqualTo("Idade: 20");
        assertMerge(curriculo, null, "Time: ${dados.time!\"não informado\"}").isEqualTo("Time: ");
        assertMerge(curriculo, null, "Time: ${dados.time._inst.value()!\"não informado\"}").isEqualTo("Time: não informado");

        assertMerge(curriculo, "dados.idade", "Idade: ${_inst}").isEqualTo("Idade: 20");
        assertMerge(curriculo, "dados.idade", "Idade: ${toStringDisplayDefault()}").isEqualTo("Idade: 20");
        assertMerge(curriculo, "dados.idade", "Idade: ${toStringDisplay()}").isEqualTo("Idade: 20");

        assertMerge(curriculo, null, "C1: ${certificados[0].nome}").isEqualTo("C1: Java");
        assertMerge(curriculo, null, "C1: ${certificados[0].data}").isEqualTo("C1: 10/03/2016");
        assertMerge(curriculo, "certificados[0].data", "C1: ${_inst?string.iso}").isEqualTo("C1: 2016-03-10");
        assertMerge(curriculo, "certificados[0].data", "C1: ${toStringDisplayDefault()}").isEqualTo("C1: 10/03/2016");

        assertMerge(curriculo, null, "<#list certificados as c>${c.nome};</#list>").isEqualTo("Java;Oracle;");
    }

    private static AbstractAssert<StringAssert, String> assertMerge(SIComposite composite, String path, String templateString) {
        SInstance instance = path == null ? composite : composite.getField(path);
        return assertThat(FormFreemarkerUtil.merge(instance, templateString));
    }

    @Test
    public void testMixedFieldsWithMethods() {
        SDictionary dict = SDictionary.create();
        PackageBuilder pkt = dict.createNewPackage("pkt");
        STypeComposite<? extends SIComposite> recordType = pkt.createCompositeType("record");
        STypeString nameType = recordType.addFieldString("name");
        recordType.addFieldString("value");
        recordType.addFieldString("toStringDisplayDefault");

        // recordType.asAtrBasic().displayString("xpto");

        SIComposite instance = recordType.newInstance();
        instance.setValue("name", "A");
        instance.setValue("value", "B");
        instance.setValue("toStringDisplayDefault", "C");

        assertMerge(instance, null, "${name}").isEqualTo("A");
        assertMerge(instance, null, "${value}").isEqualTo("B");
        assertMerge(instance, null, "${toStringDisplayDefault}").isEqualTo("C");

        assertMerge(instance, null, "${_inst.name}").isEqualTo("A");
        assertMerge(instance, null, "${_inst.name.value()}").isEqualTo("A");
        assertMerge(instance, null, "${_inst.name._inst.value()}").isEqualTo("A");
        assertMerge(instance, null, "<#list _inst.value() as i>${i}</#list>").isEqualTo("ABC");
        assertMerge(instance, null, "${_inst.toStringDisplayDefault()!\"X\"}").isEqualTo("X");

        assertMerge(instance, "name", "${_inst}").isEqualTo("A");
        assertMerge(instance, "value", "${_inst}").isEqualTo("B");
        assertMerge(instance, "toStringDisplayDefault", "${_inst}").isEqualTo("C");

        nameType.asAtrBasic().displayString("#${_inst.value()}#");

        assertMerge(instance, null, "${name}").isEqualTo("A");
        assertThat(instance.getField("name").toStringDisplay()).isEqualTo("#A#");
        assertMerge(instance, null, "${name.toStringDisplay()}").isEqualTo("#A#");
        assertMerge(instance, "name", "${_inst}").isEqualTo("A");
        assertMerge(instance, "name", "${toStringDisplay()}").isEqualTo("#A#");

    }
}
