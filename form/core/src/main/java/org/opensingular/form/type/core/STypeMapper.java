package org.opensingular.form.type.core;

import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.context.UIComponentMapper;
import org.opensingular.form.type.basic.SPackageBasic;

@SInfoType(name = "Mapper", spackage = SPackageBasic.class)
public class STypeMapper extends STypeSimple<SIMapper, UIComponentMapper> {

    public STypeMapper() {
        super(SIMapper.class, UIComponentMapper.class);
    }

}
