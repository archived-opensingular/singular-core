package org.opensingular.form.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.annotation.Nonnull;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.support.TestFormSupport;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;

@FixMethodOrder
public class RelationalMultilevelSQLTest extends TestFormSupport {

    private FormPersistenceInRelationalDB<STypePostoAtendimento, SIComposite> postoAtendimentoRepository;

    @Before
    public void setUp() {
        postoAtendimentoRepository = new FormPersistenceInRelationalDB<>(db, documentFactory,
                RelationalMultilevelSQLTest.STypePostoAtendimento.class);
        assertNotNull(postoAtendimentoRepository);
    }

    @Test
    public void loadForm() {
        List<SIComposite> siComposites = postoAtendimentoRepository.loadAll();
        assertEquals(1, siComposites.size());

        SIComposite postoAtendimentoInstance = siComposites.get(0);
        assertEquals(1, postoAtendimentoInstance.getField("id").getValue());
        assertEquals("cov@anvisa.gov.br", postoAtendimentoInstance.getField("email").getValue());

        SIComposite enderecoInstance = (SIComposite) postoAtendimentoInstance.getField("endereco");
        assertEquals(1, enderecoInstance.getField("id").getValue());
        assertEquals("72260856", enderecoInstance.getField("cep").getValue());

        SIComposite postoExternoInstance = (SIComposite) postoAtendimentoInstance.getField("postoExterno");
        assertEquals(1, postoExternoInstance.getField("idPostoAtendimento").getValue());
        assertEquals(15, postoExternoInstance.getField("pessoaJuridica.id").getValue());

        SIComposite pessoaJuridicaInstance = (SIComposite) postoExternoInstance.getField("pessoaJuridica");
        assertEquals(15, pessoaJuridicaInstance.getField("id").getValue());
        assertEquals("28177688000107", pessoaJuridicaInstance.getField("cnpj").getValue());
    }

    @SInfoType(spackage = FormTestPackage.class)
    public static final class STypeEndereco extends STypeComposite<SIComposite> {

        public STypeInteger id;
        public STypeString cep;

        @Override
        protected void onLoadType(@Nonnull TypeBuilder tb) {
            id = addFieldInteger("id");
            cep = addFieldString("cep");

            this.asSQL().table("DBFORM.TB_ENDERECO").tablePK("CO_SEQ_ENDERECO");
            id.asSQL().column("CO_SEQ_ENDERECO");
            cep.asSQL().column("NO_CEP");
        }
    }

    @SInfoType(spackage = FormTestPackage.class)
    public static final class STypePostoExterno extends STypeComposite<SIComposite> {

        public STypeInteger idPostoAtendimento;
        public STypePessoaJuridica pessoaJuridica;

        @Override
        protected void onLoadType(@Nonnull TypeBuilder tb) {
            idPostoAtendimento = addFieldInteger("idPostoAtendimento");
            pessoaJuridica = addField("pessoaJuridica", STypePessoaJuridica.class);

            this.asSQL().table("DBFORM.TB_POSTO_EXTERNO").tablePK("CO_POSTO_ATENDIMENTO, ID_PESSOA_JURIDICA")
                    .addTableFK("CO_POSTO_ATENDIMENTO", STypePostoAtendimento.class)
                    .addTableFK("ID_PESSOA_JURIDICA", pessoaJuridica.getClass());

            idPostoAtendimento.asSQL().column("CO_POSTO_ATENDIMENTO");
        }
    }

    @SInfoType(spackage = FormTestPackage.class)
    public static final class STypePostoAtendimento extends STypeComposite<SIComposite> {

        public STypeInteger id;
        public STypeString email;
        public STypeEndereco endereco;
        public STypePostoExterno postoExterno;

        @Override
        protected void onLoadType(@Nonnull TypeBuilder tb) {
            id = addFieldInteger("id");
            email = addFieldString("email");
            endereco = addField("endereco", STypeEndereco.class);
            postoExterno = addField("postoExterno", STypePostoExterno.class);

            this.asSQL().table("DBFORM.TB_POSTO_ATENDIMENTO").tablePK("CO_SEQ_POSTO_ATENDIMENTO")
                    .addTableFK("CO_SEQ_ENDERECO", endereco.getClass());
            id.asSQL().column("CO_SEQ_POSTO_ATENDIMENTO");
            email.asSQL().column("DS_EMAIL");
        }
    }

    @SInfoType(spackage = FormTestPackage.class)
    public static final class STypePessoaJuridica extends STypeComposite<SIComposite> {

        public STypeInteger id;
        public STypeString cnpj;

        @Override
        protected void onLoadType(@Nonnull TypeBuilder tb) {
            id = addFieldInteger("id");
            cnpj = addFieldString("cnpj");

            this.asSQL().table("DBFORM.TB_PESSOA_JURIDICA").tablePK("ID_PESSOA_JURIDICA");
            id.asSQL().column("ID_PESSOA_JURIDICA");
            cnpj.asSQL().column("NU_CNPJ");
        }

    }

    @SInfoPackage(name = FormTestPackage.PACKAGE_NAME)
    public static class FormTestPackage extends SPackage {

        public static final String PACKAGE_NAME = "form.sample";

        @Override
        protected void onLoadPackage(@Nonnull PackageBuilder pb) {
            pb.loadPackage(SPackageFormPersistence.class);
        }
    }

}
