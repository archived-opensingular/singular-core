/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.canabidiol;

import br.net.mirante.singular.exemplos.canabidiol.dao.CIDDAO;
import br.net.mirante.singular.exemplos.canabidiol.model.CapituloCID;
import br.net.mirante.singular.exemplos.canabidiol.model.CategoriaCID;
import br.net.mirante.singular.exemplos.canabidiol.model.GrupoCID;
import br.net.mirante.singular.exemplos.canabidiol.model.SubCategoriaCID;
import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.converter.SInstanceConverter;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.transformer.Value;

import javax.inject.Inject;

@SInfoType(spackage = SPackagePeticaoCanabidiol.class)
public class STypeCID extends STypeComposite<SIComposite> {

    @Inject // queria injetar :(
    private CIDDAO ciddao = new CIDDAO();

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        STypeComposite<SIComposite> capitulo = addFieldComposite("capitulo");
        capitulo
                .asAtrBasic()
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

        capitulo.selectionOf(CapituloCID.class)
                .id(CapituloCID::getId)
                .display(CapituloCID::getDescricao)
                .converter(new SInstanceConverter<CapituloCID, SIComposite>() {
                    @Override
                    public void fillInstance(SIComposite ins, CapituloCID obj) {
                        ins.setValue(idCapitulo, obj.getId());
                        ins.setValue(descricaoCapitulo, obj.getDescricao());
                        ins.setValue(descricaoAbreviadaCapitulo, obj.getDescricaoAbreviada());
                    }

                    @Override
                    public CapituloCID toObject(SIComposite ins) {
                        final String id = (String) ins.getValue(idCapitulo);
                        return ciddao.listCapitulos().stream()
                                .filter(c -> c.getId().equals(id))
                                .findFirst()
                                .orElse(null);
                    }
                }).simpleProvider(i -> ciddao.listCapitulos());

        STypeComposite<SIComposite> grupo = addFieldComposite("grupo");
        grupo
                .asAtrBasic()
                .required()
                .asAtrBasic()
                .label("Grupo")
                .visible(inst -> Value.notNull(inst, (STypeSimple) capitulo.getField("id")))
                .dependsOn(capitulo)
                .asAtrBootstrap()
                .colPreference(3);

        STypeString idGrupo = grupo
                .addFieldString("id");
        STypeString descricaoGrupo = grupo
                .addFieldString("descricao");
        STypeString descricaoAbreviadaGrupo = grupo
                .addFieldString("descricaoAbreviada");

        grupo.selectionOf(GrupoCID.class)
                .id(GrupoCID::getId)
                .display(GrupoCID::getDescricao)
                .converter(new SInstanceConverter<GrupoCID, SIComposite>() {
                    @Override
                    public void fillInstance(SIComposite ins, GrupoCID obj) {
                        ins.setValue(idGrupo, obj.getId());
                        ins.setValue(descricaoGrupo, obj.getDescricao());
                        ins.setValue(descricaoAbreviadaGrupo, obj.getDescricaoAbreviada());
                    }

                    @Override
                    public GrupoCID toObject(SIComposite ins) {
                        final String _idCapitulo = (String) ins.findNearestValue(idCapitulo).orElse(null);
                        final String _idGrupo = (String) ins.findNearestValue(idGrupo).orElse(null);
                        return ciddao.listGrupoByIdCapitulo(_idCapitulo).stream()
                                .filter(c -> c.getId().equals(_idGrupo))
                                .findFirst()
                                .orElse(null);
                    }
                }).simpleProvider(i -> ciddao.listGrupoByIdCapitulo((String)i.findNearestValue(idCapitulo).orElse(null)));

        STypeComposite<SIComposite> categoria = addFieldComposite("categoria");
        categoria
                .asAtrBasic()
                .required()
                .asAtrBasic()
                .label("Categoria")
                .visible(inst -> Value.notNull(inst, idGrupo))
                .dependsOn(grupo)
                .asAtrBootstrap()
                .colPreference(3);

        STypeString idCategoria = categoria
                .addFieldString("id");
        STypeString descricaoCategoria = categoria
                .addFieldString("descricao");
        STypeString descricaoAbreviadaCategoria = categoria
                .addFieldString("descricaoAbreviada");

        categoria.selectionOf(CategoriaCID.class)
                .id(CategoriaCID::getId)
                .display(CategoriaCID::getDescricao)
                .converter(new SInstanceConverter<CategoriaCID, SIComposite>() {
                    @Override
                    public void fillInstance(SIComposite ins, CategoriaCID obj) {
                        ins.setValue(idCategoria, obj.getId());
                        ins.setValue(descricaoCategoria, obj.getDescricao());
                        ins.setValue(descricaoAbreviadaCategoria, obj.getDescricaoAbreviada());
                    }

                    @Override
                    public CategoriaCID toObject(SIComposite ins) {
                        final String _idGrupo = (String) ins.findNearestValue(idGrupo).orElse(null);
                        final String _idCategoria = (String) ins.findNearestValue(idCategoria).orElse(null);
                        return ciddao.listCategoriasByIdGrupo(_idGrupo).stream()
                                .filter(c -> c.getId().equals(_idCategoria))
                                .findFirst()
                                .orElse(null);
                    }
                }).simpleProvider(i -> ciddao.listCategoriasByIdGrupo((String)i.findNearestValue(idGrupo).orElse(null)));

        STypeComposite<SIComposite> subcategoria = addFieldComposite("subcategoria");
        subcategoria
                .asAtrBasic()
                .required(inst -> ciddao.listSubCategoriasByIdCategoria(Value.of(inst, idCategoria)).size() > 0)
                .asAtrBasic()
                .label("Sub-Categoria")

                .visible(inst -> ciddao.listSubCategoriasByIdCategoria(Value.of(inst, idCategoria)).size() > 0)
                .dependsOn(categoria)
                .asAtrBootstrap()
                .colPreference(3);

        STypeString idSubCategoria = subcategoria
                .addFieldString("id");
        STypeString descricaoSubCategoria = subcategoria
                .addFieldString("descricao");
        STypeString descricaoSubAbreviadaCategoria = subcategoria
                .addFieldString("descricaoAbreviada");

        subcategoria.selectionOf(SubCategoriaCID.class)
                .id(SubCategoriaCID::getId)
                .display(SubCategoriaCID::getDescricao)
                .converter(new SInstanceConverter<SubCategoriaCID, SIComposite>() {
                    @Override
                    public void fillInstance(SIComposite ins, SubCategoriaCID obj) {
                        ins.setValue(idSubCategoria, obj.getId());
                        ins.setValue(descricaoSubCategoria, obj.getDescricao());
                        ins.setValue(descricaoSubAbreviadaCategoria, obj.getDescricaoAbreviada());
                    }

                    @Override
                    public SubCategoriaCID toObject(SIComposite ins) {
                        final String _idCategoria = (String) ins.findNearestValue(idCategoria).orElse(null);
                        final String _idSubCategoria = (String) ins.findNearestValue(idSubCategoria).orElse(null);
                        return ciddao.listSubCategoriasByIdCategoria(_idCategoria).stream()
                                .filter(c -> c.getId().equals(_idSubCategoria))
                                .findFirst()
                                .orElse(null);
                    }
                }).simpleProvider(i -> ciddao.listSubCategoriasByIdCategoria((String)i.findNearestValue(idCategoria).orElse(null)));

    }

}
