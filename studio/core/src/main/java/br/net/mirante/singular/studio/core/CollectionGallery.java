package br.net.mirante.singular.studio.core;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.studio.util.SingularStudioCollectionScanner;

import java.util.ArrayList;
import java.util.List;

public class CollectionGallery {

    private List<CollectionCanvas> collectionCanvasList = new ArrayList<>();
    private String packagesToScan = "br.net.mirante.singular";

    private void initialize() {
        String[] packagesToScanArray = {""};
        if (packagesToScan != null) {
            packagesToScanArray = packagesToScan.replaceAll(" ", "").split(",");
        }
        List<CollectionDefinition<SType<?>>> collectionDefinitionList = SingularStudioCollectionScanner.scan(packagesToScanArray);
        for (CollectionDefinition<SType<?>> c : collectionDefinitionList) {
            CollectionCanvas canvas = new CollectionCanvas();
            canvas.collectionDefinition = c;
            /*collection info */
            CollectionInfoBuilder<SType<?>> collectionInfoBuilder = new CollectionInfoBuilder<>();
            c.collectionInfo(collectionInfoBuilder);
            canvas.collectionInfo = collectionInfoBuilder.getCollectionInfo();
            collectionCanvasList.add(canvas);
        }
    }

    public String getPackagesToScan() {
        return packagesToScan;
    }

    public void setPackagesToScan(String packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    private static class CollectionCanvas {
        CollectionDefinition<SType<?>> collectionDefinition;
        CollectionInfo<SType<?>> collectionInfo;
        IFunction<SType<?>, CollectionEditorConfig> editorConfigFunction = t -> {
            CollectionEditorConfigBuilder collectionEditorConfigBuilder = new CollectionEditorConfigBuilder();
            collectionDefinition.configEditor(collectionEditorConfigBuilder, t);
            return collectionEditorConfigBuilder.getEditor();
        };
    }


}
