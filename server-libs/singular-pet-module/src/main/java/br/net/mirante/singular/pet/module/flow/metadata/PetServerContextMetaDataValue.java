package br.net.mirante.singular.pet.module.flow.metadata;

import br.net.mirante.singular.flow.core.property.MetaDataRef;
import br.net.mirante.singular.pet.module.spring.security.ServerContext;

import java.util.ArrayList;
import java.util.List;

public class PetServerContextMetaDataValue {

    public static final PetServerMetaDataKey KEY = new PetServerMetaDataKey(PetServerMetaDataKey.class.getName(), PetServerContextMetaDataValue.class);

    private List<ServerContext> contexts = new ArrayList<>(2);

    PetServerContextMetaDataValue() {

    }

    public PetServerContextMetaDataValue analise() {
        contexts.add(ServerContext.ANALISE);
        return this;
    }

    public PetServerContextMetaDataValue peticionamento() {
        contexts.add(ServerContext.PETICIONAMENTO);
        return this;
    }

    public boolean isEnabledOn(ServerContext context) {
        return contexts.contains(context);
    }


    public static class PetServerMetaDataKey extends MetaDataRef<PetServerContextMetaDataValue> {


        private PetServerMetaDataKey(String name, Class<PetServerContextMetaDataValue> valueClass) {
            super(name, valueClass);
        }
    }


}
