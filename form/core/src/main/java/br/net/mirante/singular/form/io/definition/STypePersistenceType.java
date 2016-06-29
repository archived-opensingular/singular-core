package br.net.mirante.singular.form.io.definition;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;

@SInfoType(spackage = SPackageDefinitionPersitence.class, name = "type")
public class STypePersistenceType extends STypeComposite<SIPersistenceType> {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_MEMBERS = "members";

    public STypePersistenceType() {
        super(SIPersistenceType.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        addFieldString(FIELD_NAME);
        addFieldString(FIELD_TYPE);
        addFieldListOf(FIELD_MEMBERS, STypePersistenceType.class);
    }

}
