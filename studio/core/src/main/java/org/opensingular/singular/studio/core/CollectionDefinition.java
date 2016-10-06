package org.opensingular.singular.studio.core;

import org.opensingular.form.SType;

import java.io.Serializable;

/**
 * @author Daniel C. Bordin
 */
public interface CollectionDefinition<TYPE extends SType<?>> extends Serializable {

    /**
     * configuracao do renderizador do studio-form:
     * Listagem: listagem, filtros, paginacao, exclusao, acoes
     * Form: editar, novo, visualizar
     * @param builder - builder para configuração da listagem e da tela de edição / visualização etc.
     * @param type - tipo que raiz representando o studio-form
     *
     */
    public void configEditor(CollectionEditorConfigBuilder builder, TYPE type);


    /**
     * Configurações que serão utilizadas para montar a casca do sinuglar studio
     * tais como configurações de menu e de permissoes globais do crud
     * @param builder - builder da estrutura externa ao studio-form
     */
    public void collectionInfo(CollectionInfoBuilder<TYPE> builder);
}
