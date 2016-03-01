package br.net.mirante.singular.pet.module.flow.metadata;

import br.net.mirante.singular.flow.core.property.MetaDataRef;
import br.net.mirante.singular.pet.module.spring.security.ServerContext;

import java.util.ArrayList;
import java.util.List;

public class PetServerMetaDataValue {

    public static final PetServerMetaDataKey KEY = new PetServerMetaDataKey(PetServerMetaDataKey.class.getName(), PetServerMetaDataValue.class);

    private List<ServerContext> contexts = new ArrayList<>(2);

    PetServerMetaDataValue() {

    }

    public PetServerMetaDataValue analise() {
        contexts.add(ServerContext.ANALISE);
        return this;
    }

    public PetServerMetaDataValue peticionamento() {
        contexts.add(ServerContext.PETICIONAMENTO);
        return this;
    }

    public boolean isEnabledOn(ServerContext context) {
        return contexts.contains(context);
    }


    public static class PetServerMetaDataKey extends MetaDataRef<PetServerMetaDataValue> {


        private PetServerMetaDataKey(String name, Class<PetServerMetaDataValue> valueClass) {
            super(name, valueClass);
        }
    }


}
