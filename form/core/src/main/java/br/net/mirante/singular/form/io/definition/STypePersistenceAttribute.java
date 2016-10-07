package br.net.mirante.singular.form.io.definition;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;

@SInfoType(spackage = SPackageDefinitionPersitence.class, name = "attribute")
public class STypePersistenceAttribute extends STypeComposite<SIPersistenceAttribute> {

    public STypePersistenceAttribute() {
        super(SIPersistenceAttribute.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {

    }

}
