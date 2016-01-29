package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import javax.inject.Inject;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.STypeSimples;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import br.net.mirante.singular.showcase.view.page.form.examples.canabidiol.dao.CIDDAO;
import br.net.mirante.singular.showcase.view.page.form.examples.canabidiol.model.CapituloCID;
import br.net.mirante.singular.showcase.view.page.form.examples.canabidiol.model.CategoriaCID;
import br.net.mirante.singular.showcase.view.page.form.examples.canabidiol.model.GrupoCID;
import br.net.mirante.singular.showcase.view.page.form.examples.canabidiol.model.SubCategoriaCID;

@MInfoTipo(nome = "MTipoCID", pacote = SPackagePeticaoCanabidiol.class)
public class STypeCID extends STypeComposto<SIComposite> {

    @Inject // queria injetar :(
    private CIDDAO ciddao = new CIDDAO();

    @Override
    protected void onLoadType(TipoBuilder tb) {
        super.onLoadType(tb);

        STypeComposto<?> capitulo = this.addCampoComposto("capitulo");
        capitulo
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Capítulo")
                .as(AtrBootstrap::new)
                .colPreference(3);

        STypeString idCapitulo = capitulo
                .addCampoString("id");
        STypeString descricaoCapitulo = capitulo
                .addCampoString("descricao");
        STypeString descricaoAbreviadaCapitulo = capitulo
                .addCampoString("descricaoAbreviada");
        capitulo
                .withSelectionFromProvider(descricaoCapitulo, (instancia, listBuilder) -> {
                    for (CapituloCID cap : ciddao.listCapitulos()) {
                        listBuilder.add()
                                .set(idCapitulo, cap.getId())
                                .set(descricaoCapitulo, cap.getDescricao())
                                .set(descricaoAbreviadaCapitulo, cap.getDescricaoAbreviada());
                    }
                });

        STypeComposto<?> grupo = this.addCampoComposto("grupo");
        grupo
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Grupo")
                .visivel(false)
                .visivel(inst -> Value.notNull(inst, (STypeSimples)capitulo.getCampo("id")))
                .dependsOn(capitulo)
                .as(AtrBootstrap::new)
                .colPreference(3);

        STypeString idGrupo = grupo
                .addCampoString("id");
        STypeString descricaoGrupo = grupo
                .addCampoString("descricao");
        STypeString descricaoAbreviadaGrupo = grupo
                .addCampoString("descricaoAbreviada");
        grupo
                .withSelectionFromProvider(descricaoGrupo, (instancia, listaBuilder) -> {
                    for (GrupoCID g : ciddao.listGrupoByIdCapitulo(Value.of(instancia, idCapitulo))) {
                        listaBuilder.add()
                                .set(idGrupo, g.getId())
                                .set(descricaoGrupo, g.getDescricao())
                                .set(descricaoAbreviadaGrupo, g.getDescricaoAbreviada());
                    }
                });


        STypeComposto<?> categoria = this.addCampoComposto("categoria");
        categoria
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("Categoria")
                .visivel(false)
                .visivel(inst -> Value.notNull(inst, idGrupo))
                .dependsOn(grupo)
                .as(AtrBootstrap::new)
                .colPreference(3);

        STypeString idCategoria = categoria
                .addCampoString("id");
        STypeString descricaoCategoria = categoria
                .addCampoString("descricao");
        STypeString descricaoAbreviadaCategoria = categoria
                .addCampoString("descricaoAbreviada");

        categoria
                .withSelectionFromProvider(descricaoCategoria, (instancia, listaBuilder) -> {
                    for (CategoriaCID c : ciddao.listCategoriasByIdGrupo(Value.of(instancia, idGrupo))) {
                        listaBuilder.add()
                                .set(idCategoria, c.getId())
                                .set(descricaoCategoria, c.getDescricao())
                                .set(descricaoAbreviadaCategoria, c.getDescricaoAbreviada());
                    }
                });

        STypeComposto<?> subcategoria = this.addCampoComposto("subcategoria");
        subcategoria
                .as(AtrCore::new)
                .obrigatorio(inst -> ciddao.listSubCategoriasByIdCategoria(Value.of(inst, idCategoria)).size() > 0)
                .as(AtrBasic::new)
                .label("Sub-Categoria")
                .visivel(false)
                .visivel(inst -> ciddao.listSubCategoriasByIdCategoria(Value.of(inst, idCategoria)).size() > 0)
                .dependsOn(categoria)
                .as(AtrBootstrap::new)
                .colPreference(3);

        STypeString idSubCategoria = subcategoria
                .addCampoString("id");
        STypeString descricaoSubCategoria = subcategoria
                .addCampoString("descricao");
        STypeString descricaoSubAbreviadaCategoria = subcategoria
                .addCampoString("descricaoAbreviada");

        subcategoria.withSelectionFromProvider(descricaoSubCategoria,(instancia, listaBuilder) -> {
            for (SubCategoriaCID c : ciddao.listSubCategoriasByIdCategoria(Value.of(instancia, idCategoria))) {
                listaBuilder.add()
                        .set(idSubCategoria, c.getId())
                        .set(descricaoSubCategoria, c.getDescricao())
                        .set(descricaoSubAbreviadaCategoria, c.getDescricaoAbreviada());
            }
        });

    }

}
