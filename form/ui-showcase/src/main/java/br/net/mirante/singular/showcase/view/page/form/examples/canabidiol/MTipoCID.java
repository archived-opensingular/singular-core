package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.wicket.AtrBootstrap;
import br.net.mirante.singular.showcase.view.page.form.examples.canabidiol.dao.CIDDAO;
import br.net.mirante.singular.showcase.view.page.form.examples.canabidiol.model.CapituloCID;
import br.net.mirante.singular.showcase.view.page.form.examples.canabidiol.model.CategoriaCID;
import br.net.mirante.singular.showcase.view.page.form.examples.canabidiol.model.GrupoCID;
import br.net.mirante.singular.showcase.view.page.form.examples.canabidiol.model.SubCategoriaCID;

import javax.inject.Inject;

@MInfoTipo(nome = "MTipoCID", pacote = MPacotePeticaoCanabidiol.class)
public class MTipoCID extends MTipoComposto<MIComposto> implements CanabidiolUtil {

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
        capitulo
                .setProviderOpcoes(instancia -> {
                    MILista<?> lista = capitulo.novaLista();
                    for (CapituloCID cap : ciddao.listCapitulos()) {
                        lista.addElement(capitulo.create(cap.getId(), cap.getDescricao()));
                    }
                    return lista;
                });

        MTipoComposto<?> grupo = this.addCampoComposto("grupo");
        grupo
                .as(AtrBasic::new)
                .label("Grupo")
                .visivel(false)
                .visivel(inst -> hasValue(inst, capitulo))
                .dependsOn(capitulo)
                .as(AtrBootstrap::new)
                .colPreference(3);
        grupo
                .setProviderOpcoes(instancia -> {
                    String idCapituo = getValue(instancia, capitulo);
                    MILista<?> lista = grupo.novaLista();
                    for (GrupoCID grp : ciddao.listGrupoByIdCapitulo(idCapituo)) {
                        lista.addElement(grupo.create(grp.getId(), grp.getDescricao()));
                    }
                    return lista;
                });


        MTipoComposto<?> categoria = this.addCampoComposto("categoria");
        categoria
                .as(AtrBasic::new)
                .label("Categoria")
                .visivel(false)
                .visivel(inst -> hasValue(inst, grupo))
                .dependsOn(grupo)
                .as(AtrBootstrap::new)
                .colPreference(3);
        categoria
                .setProviderOpcoes(instancia -> {
                    String idGrp = getValue(instancia, grupo);
                    MILista<?> lista = categoria.novaLista();
                    for (CategoriaCID cat : ciddao.listCategoriasByIdGrupo(idGrp)) {
                        lista.addElement(categoria.create(cat.getId(), cat.getDescricao()));
                    }
                    return lista;
                });

        MTipoComposto<?> subcategoria = this.addCampoComposto("subcategoria");
        subcategoria
                .as(AtrBasic::new)
                .label("Sub-Categoria")
                .visivel(false)
                .visivel(inst -> {
                    String idCategoria = getValue(inst, categoria);
                    return ciddao.listSubCategoriasByIdCategoria(idCategoria).size() > 0;
                })
                .dependsOn(categoria)
                .as(AtrBootstrap::new)
                .colPreference(3);

        subcategoria
                .setProviderOpcoes(instancia -> {
                    String idCategoria = getValue(instancia, categoria);
                    MILista<?> lista = subcategoria.novaLista();
                    for (SubCategoriaCID subCat : ciddao.listSubCategoriasByIdCategoria(idCategoria)) {
                        lista.addElement(subcategoria.create(subCat.getId(), subCat.getDescricao()));
                    }
                    return lista;
                });

    }


}
