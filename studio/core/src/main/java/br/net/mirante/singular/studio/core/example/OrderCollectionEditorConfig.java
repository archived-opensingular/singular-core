package br.net.mirante.singular.studio.core.example;

import br.net.mirante.singular.studio.core.CollectionEditorConfig;
import br.net.mirante.singular.studio.core.EditorConfigBuilder;
import br.net.mirante.singular.studio.core.type.AtrStudioConfig;

public class OrderCollectionEditorConfig implements CollectionEditorConfig<STypeOrder> {

    @Override
    public void configEditor(EditorConfigBuilder cfg) {
        cfg.setDocumentType(STypeOrder.class);
    }

    @Override
    public void complementTypeConfig(STypeOrder type) {
        type.description.as(AtrStudioConfig.class).defaultSearchCriteria(true);
    }
}
