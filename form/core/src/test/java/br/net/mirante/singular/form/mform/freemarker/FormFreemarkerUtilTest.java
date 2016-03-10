package br.net.mirante.singular.form.mform.freemarker;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeLista;

public class FormFreemarkerUtilTest {

    private STypeComposite<? extends SIComposite> curriculoType;
    private STypeComposite<SIComposite> dadosType;
    private STypeLista<STypeComposite<SIComposite>, SIComposite> certificadosType;

    @Before public void setup() {
        SDictionary dict = SDictionary.create();
        PackageBuilder pkt = dict.createNewPackage("pkt");
        curriculoType = pkt.createTipoComposto("curriculo");

        dadosType = curriculoType.addCampoComposto("dados");
        dadosType.addCampoString("nome");
        dadosType.addCampoInteger("idade");
        dadosType.addCampoString("time");

        certificadosType = curriculoType.addCampoListaOfComposto("certificados", "certificado");
        STypeComposite<SIComposite> certificadoType = certificadosType.getTipoElementos();
        certificadoType.addCampoString("nome");
        certificadoType.addCampoData("data");
    }

    @Test
    public void compositeSimpleTest() {
        SIComposite curriculo = curriculoType.novaInstancia();
        curriculo.setValor("dados.nome", "Paulo Silva");
        curriculo.setValor("dados.idade", 20);

        SIComposite certificado = (SIComposite) curriculo.getFieldList("certificados").addNovo();
        certificado.setValor("nome", "Java");
        certificado.setValor("data", new Date(2016 - 1900, 3 - 1, 10));

        certificado = (SIComposite) curriculo.getFieldList("certificados").addNovo();
        certificado.setValor("nome", "Oracle");

        SIComposite dados = curriculo.getFieldRecord("dados");

        assertThat(FormFreemarkerUtil.merge(dados, "Nome: ${nome}")).isEqualTo("Nome: Paulo Silva");
        assertThat(FormFreemarkerUtil.merge(dados, "Idade: ${idade}")).isEqualTo("Idade: 20");
        assertThat(FormFreemarkerUtil.merge(dados, "Time: ${time!}")).isEqualTo("Time: ");

        assertThat(FormFreemarkerUtil.merge(curriculo, "Nome: ${dados.nome}")).isEqualTo("Nome: Paulo Silva");
        assertThat(FormFreemarkerUtil.merge(curriculo, "Idade: ${dados.idade}")).isEqualTo("Idade: 20");
        assertThat(FormFreemarkerUtil.merge(curriculo, "Time: ${dados.time!\"não informado\"}")).isEqualTo("Time: não informado");

        assertThat(FormFreemarkerUtil.merge(curriculo, "C1: ${certificados[0].nome}")).isEqualTo("C1: Java");
        assertThat(FormFreemarkerUtil.merge(curriculo, "C1: ${certificados[0].data}")).isEqualTo("C1: 10/03/2016");

        assertThat(FormFreemarkerUtil.merge(curriculo, "<#list certificados as c>${c.nome};</#list>")).isEqualTo("Java;Oracle;");
    }
}
