package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;
import br.net.mirante.singular.form.mform.util.transformer.FromPojoList;
import br.net.mirante.singular.form.mform.util.transformer.Val;
import br.net.mirante.singular.form.wicket.AtrBootstrap;
import br.net.mirante.singular.showcase.view.page.form.examples.canabidiol.dao.CIDDAO;
import br.net.mirante.singular.showcase.view.page.form.examples.canabidiol.model.CapituloCID;
import br.net.mirante.singular.showcase.view.page.form.examples.canabidiol.model.CategoriaCID;
import br.net.mirante.singular.showcase.view.page.form.examples.canabidiol.model.GrupoCID;
import br.net.mirante.singular.showcase.view.page.form.examples.canabidiol.model.SubCategoriaCID;

import javax.inject.Inject;

@MInfoTipo(nome = "MTipoCID", pacote = MPacotePeticaoCanabidiol.class)
public class MTipoCID extends MTipoComposto<MIComposto>  {

    @Inject // queria injetar :(
    private CIDDAO ciddao = new CIDDAO();

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        super.onCargaTipo(tb);

        MTipoComposto<?> capitulo = this.addCampoComposto("capitulo");
        capitulo
                .as(AtrBasic::new)
                .label("Capítulo")
                .as(AtrBootstrap::new)
                .colPreference(3);

        MTipoString idCapitulo = capitulo
                .addCampoString("id");
        MTipoString descricaoCapitulo = capitulo
                .addCampoString("descricao");
        MTipoString descricaoAbreviadaCapitulo = capitulo
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

        MTipoComposto<?> grupo = this.addCampoComposto("grupo");
        grupo
                .as(AtrBasic::new)
                .label("Grupo")
                .visivel(false)
                .visivel(inst -> Val.notNull(inst, (MTipoSimples)capitulo.getCampo("id")))
                .dependsOn(capitulo)
                .as(AtrBootstrap::new)
                .colPreference(3);

        MTipoString idGrupo = grupo
                .addCampoString("id");
        MTipoString descricaoGrupo = grupo
                .addCampoString("descricao");
        MTipoString descricaoAbreviadaGrupo = grupo
                .addCampoString("descricaoAbreviada");
        grupo
                .withSelectionFromProvider(descricaoGrupo, (instancia, listaBuilder) -> {
                    for (GrupoCID g : ciddao.listGrupoByIdCapitulo(Val.of(instancia, idCapitulo))) {
                        listaBuilder.add()
                                .set(idGrupo, g.getId())
                                .set(descricaoGrupo, g.getDescricao())
                                .set(descricaoAbreviadaGrupo, g.getDescricaoAbreviada());
                    }
                });


        MTipoComposto<?> categoria = this.addCampoComposto("categoria");
        categoria
                .as(AtrBasic::new)
                .label("Categoria")
                .visivel(false)
                .visivel(inst -> Val.notNull(inst, idGrupo))
                .dependsOn(grupo)
                .as(AtrBootstrap::new)
                .colPreference(3);

        MTipoString idCategoria = categoria
                .addCampoString("id");
        MTipoString descricaoCategoria = categoria
                .addCampoString("descricao");
        MTipoString descricaoAbreviadaCategoria = categoria
                .addCampoString("descricaoAbreviada");

        categoria
                .withSelectionFromProvider(descricaoCategoria, (instancia, listaBuilder) -> {
                    for (CategoriaCID c : ciddao.listCategoriasByIdGrupo(Val.of(instancia, idGrupo))) {
                        listaBuilder.add()
                                .set(idCategoria, c.getId())
                                .set(descricaoCategoria, c.getDescricao())
                                .set(descricaoAbreviadaCategoria, c.getDescricaoAbreviada());
                    }
                });

        MTipoComposto<?> subcategoria = this.addCampoComposto("subcategoria");
        subcategoria
                .as(AtrBasic::new)
                .label("Sub-Categoria")
                .visivel(false)
                .visivel(inst -> ciddao.listSubCategoriasByIdCategoria(Val.of(inst, idCategoria)).size() > 0)
                .dependsOn(categoria)
                .as(AtrBootstrap::new)
                .colPreference(3);

        MTipoString idSubCategoria = subcategoria
                .addCampoString("id");
        MTipoString descricaoSubCategoria = subcategoria
                .addCampoString("descricao");
        MTipoString descricaoSubAbreviadaCategoria = subcategoria
                .addCampoString("descricaoAbreviada");

        subcategoria.withSelectionFromProvider(descricaoSubCategoria, (MOptionsProvider) instancia ->
                new FromPojoList<SubCategoriaCID>(categoria, ciddao.listSubCategoriasByIdCategoria(Val.of(instancia, idCategoria)))
                        .map(idSubCategoria, c -> c.getId())
                        .map(descricaoSubCategoria, c -> c.getDescricao())
                        .map(descricaoSubAbreviadaCategoria, c -> c.getDescricaoAbreviada())
                        .build());

    }

}
