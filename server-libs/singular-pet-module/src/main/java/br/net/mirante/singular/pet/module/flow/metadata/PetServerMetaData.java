package br.net.mirante.singular.pet.module.flow.metadata;

import br.net.mirante.singular.flow.core.property.MetaDataRef;

public class PetServerMetaData extends MetaDataRef<PetServerMetaDataValue> {

    public static final PetServerMetaData KEY = new PetServerMetaData(PetServerMetaData.class.getName(), PetServerMetaDataValue.class);

    private PetServerMetaData(String name, Class<PetServerMetaDataValue> valueClass) {
        super(name, valueClass);
    }

    public static PetServerMetaDataValue enable(){
        return new PetServerMetaDataValue();
    }

}
