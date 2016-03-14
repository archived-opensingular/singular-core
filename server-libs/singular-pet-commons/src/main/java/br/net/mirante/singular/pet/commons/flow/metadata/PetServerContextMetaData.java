package br.net.mirante.singular.pet.commons.flow.metadata;

import br.net.mirante.singular.flow.core.property.MetaDataRef;

public class PetServerContextMetaData extends MetaDataRef<PetServerContextMetaDataValue> {

    public static final PetServerContextMetaData KEY = new PetServerContextMetaData(PetServerContextMetaData.class.getName(), PetServerContextMetaDataValue.class);

    private PetServerContextMetaData(String name, Class<PetServerContextMetaDataValue> valueClass) {
        super(name, valueClass);
    }

    public static PetServerContextMetaDataValue enable(){
        return new PetServerContextMetaDataValue();
    }

}
