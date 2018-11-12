package org.opensingular.form.document;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.country.brazil.STypeAddress;
import org.opensingular.internal.lib.commons.util.SingularIOUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

/**
 * @author Daniel C. Bordin
 * @since 2018-10-17
 */
public class RefTypeTest {

    @Test
    public void simpleCase() {
        List<RefType> types = new ArrayList<>();
        types.add(RefType.of(STypeDecimal.class));
        types.add(RefType.of(STypeAddress.class));
        types.add(types.get(1).createSubReference(STypeBoolean.class));

        assertNotSame(types.get(0).get().getDictionary(), types.get(1).get().getDictionary());
        assertSame(types.get(1).get().getDictionary(), types.get(2).get().getDictionary());

        List<RefType> types2 = SingularIOUtils.serializeAndDeserialize(types);

        assertNotSame(types2.get(0).get().getDictionary(), types2.get(1).get().getDictionary());
        assertSame(types2.get(1).get().getDictionary(), types2.get(2).get().getDictionary());
    }

    @Test
    public void ofSupplier() {
        Assertions.assertThatThrownBy(() -> RefType.of(() -> null).get()).isExactlyInstanceOf(
                SingularFormException.class).hasMessageContaining("returned null");

        List<RefType> types = new ArrayList<>();
        types.add(RefType.of(() -> SDictionary.create().getType(STypeString.class)));
        types.add(RefType.of(() -> SDictionary.create().getType(STypeString.class)));
        types.add(types.get(1).createSubReference(STypeInteger.class));

        List<RefType> types2 = SingularIOUtils.serializeAndDeserialize(types);

        assertNotSame(types2.get(0).get().getDictionary(), types2.get(1).get().getDictionary());
        assertSame(types2.get(1).get().getDictionary(), types2.get(2).get().getDictionary());
    }

}