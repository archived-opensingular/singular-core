package org.opensingular.form.document;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.country.brazil.STypeAddress;
import org.opensingular.internal.lib.commons.util.SingularIOUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author Daniel C. Bordin
 * @since 2018-10-17
 */
public class RefDictionaryTest {

    @Test
    public void simpleCase() {
        RefDictionary refDic1 = RefDictionary.newBlank();
        List<RefType> types = new ArrayList<>();
        types.add(refDic1.refType(STypeDecimal.class));
        types.add(refDic1.refType(STypeAddress.class));
        types.add(types.get(0).createSubReference(STypeBoolean.class));

        assertSame(types.get(0).get().getDictionary(), refDic1.get());
        assertSame(types.get(1).get().getDictionary(), refDic1.get());
        assertSame(types.get(2).get().getDictionary(), refDic1.get());

        List<RefType> types2 = SingularIOUtils.serializeAndDeserialize(types);

        assertSame(types2.get(0).get().getDictionary(), types2.get(1).get().getDictionary());
        assertSame(types2.get(1).get().getDictionary(), types2.get(2).get().getDictionary());
    }

    @Test
    public void refTypeByFunctionVerifications() {
        Assertions.assertThatThrownBy(() -> RefDictionary.newBlank().refType(dic -> null).get()).isExactlyInstanceOf(
                SingularFormException.class).hasMessageContaining("returned null");

        Assertions.assertThatThrownBy(
                () -> RefDictionary.newBlank().refType(dic -> SDictionary.create().getType(STypeString.class)).get())
                .isExactlyInstanceOf(SingularFormException.class).hasMessageContaining(
                "dictionary reference different of the provided");

        RefDictionary refDic = RefDictionary.newBlank();
        RefType ref = refDic.refType(dic -> dic.get().getType(STypeString.class));
        assertTrue(ref instanceof RefTypeFromRefDictionary);
        assertSame(refDic, ((RefTypeFromRefDictionary) ref).getRefDictionary());
        assertSame(refDic.get(), ref.get().getDictionary());
    }
}