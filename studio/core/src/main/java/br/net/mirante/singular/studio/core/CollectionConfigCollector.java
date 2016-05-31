package br.net.mirante.singular.studio.core;

import br.net.mirante.singular.commons.lambda.ISupplier;
import br.net.mirante.singular.form.SType;

/**
 * Controla a execução dos builders de forma a realizar a configuração apenas no momento necessário.
 *
 * @param <TYPE>
 */
public class CollectionConfigCollector<TYPE extends SType<?>> {

    private CollectionInfoBuilder<TYPE> collectionInfoBuilder = new CollectionInfoBuilder<>();
    private CollectionDefinition<TYPE> collectionEditorConfiguration;
    private ISupplier<TYPE> stypeSupplier;
    private CollectionEditorConfigBuilder collectionEditorBuilder;

    public CollectionConfigCollector(CollectionDefinition<TYPE> config, ISupplier<TYPE> stypeSupplier) {
        config.collectionInfo(collectionInfoBuilder);
        this.stypeSupplier = stypeSupplier;
        this.collectionEditorConfiguration = config;
    }

    /**
     * Execução tardia das configurações da coleção.
     *
     * @return
     */
    public CollectionEditorConfig getCollectionEditorConfiguration() {
        if (collectionEditorBuilder == null) {
            collectionEditorConfiguration.configEditor(collectionEditorBuilder = new CollectionEditorConfigBuilder(), stypeSupplier.get());
        }
        return collectionEditorBuilder.getEditor();
    }

    public CollectionInfo<TYPE> getCollectionInfoConfiguration() {
        return collectionInfoBuilder.getCollectionInfo();
    }
}
