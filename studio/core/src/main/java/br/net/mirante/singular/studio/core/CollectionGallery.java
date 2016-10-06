package br.net.mirante.singular.studio.core;

import org.opensingular.singular.form.SType;
import br.net.mirante.singular.studio.util.SingularStudioCollectionScanner;
import org.apache.commons.collections.ListUtils;

import java.util.ArrayList;
import java.util.List;

public class CollectionGallery {

    private List<CollectionCanvas> collectionCanvasList = null;
    private String packagesToScan = "br.net.mirante.singular";

    private void initialize() {
        if (collectionCanvasList == null) {
            collectionCanvasList = new ArrayList<>();
            String[] packagesToScanArray = {""};
            if (packagesToScan != null) {
                packagesToScanArray = packagesToScan.replaceAll(" ", "").split(",");
            }
            List<CollectionDefinition<SType<?>>> collectionDefinitionList = SingularStudioCollectionScanner.scan(packagesToScanArray);
            for (CollectionDefinition<SType<?>> c : collectionDefinitionList) {
            /*collection info */
                CollectionInfoBuilder<SType<?>> collectionInfoBuilder = new CollectionInfoBuilder<>();
                c.collectionInfo(collectionInfoBuilder);
                collectionCanvasList.add(new CollectionCanvas(c, collectionInfoBuilder.getCollectionInfo()));
            }
        }
    }

    public List<CollectionCanvas> getCollectionCanvas() {
        initialize();
        return (List<CollectionCanvas>) ListUtils.unmodifiableList(collectionCanvasList);
    }

    public String getPackagesToScan() {
        return packagesToScan;
    }

    public void setPackagesToScan(String packagesToScan) {
        this.packagesToScan = packagesToScan;
    }


    public CollectionCanvas getCollectionCanvas(Class<? extends CollectionDefinition> collectionDefinition) {
        return getCollectionCanvas()
                .stream()
                .filter(cc -> cc.getCollectionDefinition().getClass().equals(collectionDefinition))
                .findFirst()
                .get();
    }
}
