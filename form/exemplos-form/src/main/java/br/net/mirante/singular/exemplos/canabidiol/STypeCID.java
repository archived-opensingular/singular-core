/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.canabidiol;

import javax.inject.Inject;

import br.net.mirante.singular.exemplos.canabidiol.dao.CIDDAO;
import br.net.mirante.singular.exemplos.canabidiol.model.CapituloCID;
import br.net.mirante.singular.exemplos.canabidiol.model.CategoriaCID;
import br.net.mirante.singular.exemplos.canabidiol.model.GrupoCID;
import br.net.mirante.singular.exemplos.canabidiol.model.SubCategoriaCID;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.transformer.Value;

@SInfoType(spackage = SPackagePeticaoCanabidiol.class)
public class STypeCID extends STypeComposite<SIComposite> {

    @Inject // queria injetar :(
    private CIDDAO ciddao = new CIDDAO();

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        STypeComposite<?> capitulo = addFieldComposite("capitulo");
        capitulo
                .asAtrCore()
                .required()
                .asAtrBasic()
                .label("Capítulo")
                .asAtrBootstrap()
                .colPreference(3);

        STypeString idCapitulo = capitulo
                .addFieldString("id");
        STypeString descricaoCapitulo = capitulo
                .addFieldString("descricao");
        STypeString descricaoAbreviadaCapitulo = capitulo
                .addFieldString("descricaoAbreviada");
        capitulo.withSelectView()
                .withSelectionFromProvider(descricaoCapitulo, (instancia, listBuilder) -> {
                    for (CapituloCID cap : ciddao.listCapitulos()) {
                        listBuilder.add()
                                .set(idCapitulo, cap.getId())
                                .set(descricaoCapitulo, cap.getDescricao())
                                .set(descricaoAbreviadaCapitulo, cap.getDescricaoAbreviada());
                    }
                });

        STypeComposite<?> grupo = addFieldComposite("grupo");
        grupo
                .asAtrCore()
                .required()
                .asAtrBasic()
                .label("Grupo")
                .visivel(inst -> Value.notNull(inst, (STypeSimple) capitulo.getField("id")))
                .dependsOn(capitulo)
                .asAtrBootstrap()
                .colPreference(3);

        STypeString idGrupo = grupo
                .addFieldString("id");
        STypeString descricaoGrupo = grupo
                .addFieldString("descricao");
        STypeString descricaoAbreviadaGrupo = grupo
                .addFieldString("descricaoAbreviada");
        grupo
                .withSelectView()
                .withSelectionFromProvider(descricaoGrupo, (instancia, listaBuilder) -> {
                    for (GrupoCID g : ciddao.listGrupoByIdCapitulo(Value.of(instancia, idCapitulo))) {
                        listaBuilder.add()
                                .set(idGrupo, g.getId())
                                .set(descricaoGrupo, g.getDescricao())
                                .set(descricaoAbreviadaGrupo, g.getDescricaoAbreviada());
                    }
                });


        STypeComposite<?> categoria = addFieldComposite("categoria");
        categoria
                .asAtrCore()
                .required()
                .asAtrBasic()
                .label("Categoria")
                .visivel(inst -> Value.notNull(inst, idGrupo))
                .dependsOn(grupo)
                .asAtrBootstrap()
                .colPreference(3);

        STypeString idCategoria = categoria
                .addFieldString("id");
        STypeString descricaoCategoria = categoria
                .addFieldString("descricao");
        STypeString descricaoAbreviadaCategoria = categoria
                .addFieldString("descricaoAbreviada");

        categoria
                .withSelectView()
                .withSelectionFromProvider(descricaoCategoria, (instancia, listaBuilder) -> {
                    for (CategoriaCID c : ciddao.listCategoriasByIdGrupo(Value.of(instancia, idGrupo))) {
                        listaBuilder.add()
                                .set(idCategoria, c.getId())
                                .set(descricaoCategoria, c.getDescricao())
                                .set(descricaoAbreviadaCategoria, c.getDescricaoAbreviada());
                    }
                });

        STypeComposite<?> subcategoria = addFieldComposite("subcategoria");
        subcategoria
                .asAtrCore()
                .required(inst -> ciddao.listSubCategoriasByIdCategoria(Value.of(inst, idCategoria)).size() > 0)
                .asAtrBasic()
                .label("Sub-Categoria")

                .visivel(inst -> ciddao.listSubCategoriasByIdCategoria(Value.of(inst, idCategoria)).size() > 0)
                .dependsOn(categoria)
                .asAtrBootstrap()
                .colPreference(3);

        STypeString idSubCategoria = subcategoria
                .addFieldString("id");
        STypeString descricaoSubCategoria = subcategoria
                .addFieldString("descricao");
        STypeString descricaoSubAbreviadaCategoria = subcategoria
                .addFieldString("descricaoAbreviada");

        subcategoria
                .withSelectView()
                .withSelectionFromProvider(descricaoSubCategoria, (instancia, listaBuilder) -> {
                    for (SubCategoriaCID c : ciddao.listSubCategoriasByIdCategoria(Value.of(instancia, idCategoria))) {
                        listaBuilder.add()
                                .set(idSubCategoria, c.getId())
                                .set(descricaoSubCategoria, c.getDescricao())
                                .set(descricaoSubAbreviadaCategoria, c.getDescricaoAbreviada());
                    }
                });

    }

}
