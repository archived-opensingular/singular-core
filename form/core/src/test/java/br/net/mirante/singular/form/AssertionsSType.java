package br.net.mirante.singular.form;

import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.type.core.STypeDecimal;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;

import java.util.Objects;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Apoio a escrita de asserções referente a um SType e seu conteúdo.
 *
 * @author Daniel C. Bordin
 */
public class AssertionsSType {

    private final SType<?> type;

    AssertionsSType(SType<?> type) {
        this.type = type;
    }

    public SType<?> getTarget() {
        return type;
    }

    public AssertionsSType isNotNull() {
        assertThat(type).isNotNull();
        return this;
    }

    public AssertionsSType field(String fieldPath) {
        is(STypeComposite.class);
        return new AssertionsSType(type.getLocalType(fieldPath));
    }

    private AssertionsSType is(Class<?> typeClass) {
        assertThat(type).isInstanceOf(typeClass);
        return this;
    }

    private AssertionsSType is(Class<?> typeString, String fieldPath) {
        AssertionsSType assertions = this;
        if (fieldPath != null) {
            assertions = field(fieldPath);
        }
        return assertions.is(typeString);
    }

    public AssertionsSType isComposite() {
        return is(STypeComposite.class);
    }

    public AssertionsSType isComposite(String fieldPath, int expectedFieldsSize) {
        return isComposite(fieldPath).sizeIs(expectedFieldsSize);
    }

    private AssertionsSType isComposite(String fieldPath) {
        return is(STypeComposite.class, fieldPath);
    }

    private AssertionsSType sizeIs(int expectedFieldsSize) {
        compositeSize(expectedFieldsSize);
        return this;
    }

    public AssertionsSType isComposite(int expectedSize) {
        isComposite();
        compositeSize(expectedSize);
        return this;
    }

    private void compositeSize(int expectedSize) {
        assertThat(((STypeComposite) type).getFields().size()).isEqualTo(expectedSize);
    }

    public AssertionsSType isList(String fieldPath) {
        return is(STypeList.class, fieldPath);
    }

    public AssertionsSType listElementType(String fieldPath) {
        AssertionsSType field = isList(fieldPath);
        return new AssertionsSType(((STypeList<?, ?>) field.getTarget()).getElementsType());
    }

    public AssertionsSType isString() {
        return is(STypeString.class);
    }

    public AssertionsSType isString(String fieldPath) {
        return is(STypeString.class, fieldPath);
    }

    public AssertionsSType isInteger(String fieldPath) {
        return is(STypeInteger.class, fieldPath);
    }

    public AssertionsSType isDecimal(String fieldPath) {
        return is(STypeDecimal.class, fieldPath);
    }

    public AssertionsSType isAttribute(AtrRef<?, ?, ?> attr, Object expected) {
        Object actual = type.getAttributeValue(attr);
        if (!Objects.equals(actual, expected)) {
            throw new AssertionError(erroMsg(
                    "Valor não esperado para o atributo '" + attr.getNameFull() + "'\nEsperado:" + expected +
                            "\nAtual   :" + actual));
        }
        return this;
    }

    private String erroMsg(String msg) {
        return "No tipo '" + type.getName() + "': " + msg;
    }

    public AssertionsSType isRequired() {
        return isAttribute(SPackageBasic.ATR_REQUIRED, true);
    }

    public AssertionsSType isNotRequired() {
        return isAttribute(SPackageBasic.ATR_REQUIRED, false);
    }
}
