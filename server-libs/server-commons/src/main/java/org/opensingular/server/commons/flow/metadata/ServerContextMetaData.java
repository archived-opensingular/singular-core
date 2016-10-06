package org.opensingular.server.commons.flow.metadata;

import org.opensingular.flow.core.property.MetaDataRef;

public class ServerContextMetaData extends MetaDataRef<PetServerContextMetaDataValue> {

    public static final ServerContextMetaData KEY = new ServerContextMetaData(ServerContextMetaData.class.getName(), PetServerContextMetaDataValue.class);

    private ServerContextMetaData(String name, Class<PetServerContextMetaDataValue> valueClass) {
        super(name, valueClass);
    }

    public static PetServerContextMetaDataValue enable(){
        return new PetServerContextMetaDataValue();
    }

}
