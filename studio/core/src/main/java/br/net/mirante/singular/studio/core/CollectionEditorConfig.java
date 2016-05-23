package br.net.mirante.singular.studio.core;

import br.net.mirante.singular.form.SType;

/**
 * @author Daniel C. Bordin
 */
public interface CollectionEditorConfig<TYPE extends SType<?>> {

    /**
     * configuracao do renderizador do crud:
     * Listagem: listagem, filtros, paginacao, exclusao, acoes
     * Form: editar, novo, visualizar
     * @param builder - builder para configuração da listagem e da tela de edição / visualização etc.
     * @param type - tipo que raiz representando o CRUD
     *
     */
    public void configEditor(EditorConfigBuilder builder, TYPE type);


    /**
     * Configurações que serão utilizadas para montar a casca do sinuglar studio
     * tais como configurações de menu e de permissoes globais do crud
     * @param builder - builder da estrutura externa ao CRUD
     */
    public void collectionInfo(CollectionInfoBuilder<TYPE> builder);
}
