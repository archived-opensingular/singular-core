package org.opensingular.singular.server.commons.flow.metadata;

import org.opensingular.singular.flow.core.property.MetaDataRef;
import org.opensingular.singular.server.commons.config.IServerContext;

import java.util.ArrayList;
import java.util.List;

public class PetServerContextMetaDataValue {

    public static final PetServerMetaDataKey KEY = new PetServerMetaDataKey(PetServerMetaDataKey.class.getName(), PetServerContextMetaDataValue.class);

    private List<IServerContext> contexts = new ArrayList<>(2);

    PetServerContextMetaDataValue() {

    }

    public PetServerContextMetaDataValue enableOn(IServerContext context) {
        contexts.add(context);
        return this;
    }


    public boolean isEnabledOn(IServerContext context) {
        return contexts.contains(context);
    }


    public static class PetServerMetaDataKey extends MetaDataRef<PetServerContextMetaDataValue> {


        private PetServerMetaDataKey(String name, Class<PetServerContextMetaDataValue> valueClass) {
            super(name, valueClass);
        }
    }


}
