package br.net.mirante.singular.showcase.component.layout;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MListMasterDetailView;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.util.comuns.MTipoAnoMes;
import br.net.mirante.singular.form.wicket.AtrBootstrap;

public class CaseMasterDetailNestedPackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        MTipoComposto<?> testForm = pb.createTipoComposto("testForm");

        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> experiencias = testForm.addCampoListaOfComposto("experienciasProfissionais", "experiencia");
        final MTipoComposto<?> experiencia = experiencias.getTipoElementos();
        final MTipoAnoMes dtInicioExperiencia = experiencia.addCampo("inicio", MTipoAnoMes.class, true);
        final MTipoAnoMes dtFimExperiencia = experiencia.addCampo("fim", MTipoAnoMes.class);
        final MTipoString empresa = experiencia.addCampoString("empresa", true);
        final MTipoString atividades = experiencia.addCampoString("atividades");

        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> cargos = experiencia.addCampoListaOfComposto("cargos", "cargo");
        final MTipoComposto<?> cargo = cargos.getTipoElementos();
        final MTipoString nome = cargo.addCampoString("nome", true);
        final MTipoAnoMes dtInicioCargo = cargo.addCampo("inicio", MTipoAnoMes.class, true);
        final MTipoAnoMes dtFimCargo = cargo.addCampo("fim", MTipoAnoMes.class);


        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> pets = cargo.addCampoListaOfComposto("pets", "pet");
        final MTipoComposto pet = pets.getTipoElementos();
        final MTipoString nomeDoPet = pet.addCampoString("nome", true);
        final MTipoString tipoDoPet = pet.addCampoString("tipo", true)
                .withSelectionOf("Gatinho", "Cachorrinho", "Papagaio");
        final MTipoInteger idadePet = pet.addCampoInteger("idade");

        {
            //@destacar:bloco
            experiencias
                    .withView(MListMasterDetailView::new)
                    .as(AtrBasic::new).label("Experiências profissionais");
            //@destacar:fim
            dtInicioExperiencia
                    .as(AtrBasic::new).label("Data inicial")
                    .as(AtrBootstrap::new).colPreference(2);
            dtFimExperiencia
                    .as(AtrBasic::new).label("Data final")
                    .as(AtrBootstrap::new).colPreference(2);
            empresa
                    .as(AtrBasic::new).label("Empresa")
                    .as(AtrBootstrap::new).colPreference(8);
            //@destacar:bloco
            cargos
                    .withView(MListMasterDetailView::new)
                    .as(AtrBasic::new).label("Cargos na empresa");
            dtInicioCargo
                    .as(AtrBasic::new).label("Data inicial")
                    .as(AtrBootstrap::new).colPreference(4);
            dtFimCargo
                    .as(AtrBasic::new).label("Data final")
                    .as(AtrBootstrap::new).colPreference(4);
            nome
                    .as(AtrBasic::new).label("Nome")
                    .as(AtrBootstrap::new).colPreference(4);
            pets
                    .withView(new MListMasterDetailView()
                            .col(nomeDoPet)
                            .col(tipoDoPet))
                    .as(AtrBasic::new).label("Animais de estimação no trabalho");
            nomeDoPet
                    .as(AtrBasic::new).label("Nome")
                    .as(AtrBootstrap::new).colPreference(4);
            tipoDoPet
                    .withSelectView()
                    .as(AtrBasic::new).label("Tipo")
                    .as(AtrBootstrap::new).colPreference(4);
            idadePet
                    .as(AtrBasic::new).label("Idade")
                    .as(AtrBootstrap::new).colPreference(4);
            //@destacar:fim
            atividades
                    .withTextAreaView()
                    .as(AtrBasic::new).label("Atividades Desenvolvidas");
        }

    }
}
