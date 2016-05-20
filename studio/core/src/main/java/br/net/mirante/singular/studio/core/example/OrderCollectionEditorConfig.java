package br.net.mirante.singular.studio.core.example;

import br.net.mirante.singular.studio.core.CollectionEditorConfig;
import br.net.mirante.singular.studio.core.EditorConfigBuilder;
import br.net.mirante.singular.studio.core.CollectionInfoBuilder;


public class OrderCollectionEditorConfig implements CollectionEditorConfig<STypeOrder> {

    @Override
    public void collectionInfo(CollectionInfoBuilder<STypeOrder> builder) {
        builder
                .form(STypeOrder.class);
    }

    @Override
    public void configEditor(EditorConfigBuilder cfg, STypeOrder type) {
        cfg
                .list()
                .column(type.id)
                .column(type.description)
                .rowsPerPage(10)
                .disableQuickFilter()
                .sortBy(type.id)
                .delete("Tem certeza que deseja excluir a Compra número ${0}", type.id)
                        //Expadir para o delete, insert, view e edit:
                        // perfil que pode realizar cada operação
                        // gancho para regra de negócio anterior a deleção: sendo possível inclusive não fazer a deleção e apresentar msg
                        // gancho para regra de negócio após a deleção: sendo possível inclusive não fazer a deleção e apresentar msg
                .disableDelete()
                .disableInsert()
                .disableView()
                .disableEdit()
                .form()
                        //desabilita validacao
                .validateBeforeSave(false)
                        //configura botao de salvar
                .disableSave()
                        //configura botão de validar
                .disableValidate()
                        //configura botão de fechar
                .disableClose();
    }


}
