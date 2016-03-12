package br.net.mirante.singular.form.mform;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import br.net.mirante.singular.form.mform.basic.view.SViewSelectionByRadio;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionBySelect;
import br.net.mirante.singular.form.mform.core.AtrFormula;
import br.net.mirante.singular.form.mform.core.SPackageCore;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;
import br.net.mirante.singular.form.mform.options.SSelectionableSimpleType;

@SuppressWarnings("rawtypes")
@SInfoType(name = "STypeSimple", spackage = SPackageCore.class)
public class STypeSimple<I extends SISimple<TIPO_NATIVO>, TIPO_NATIVO>
        extends SType<I>
        implements SSelectionableSimpleType<STypeSimple, TIPO_NATIVO> {

    private final Class<TIPO_NATIVO> valueClass;

    private transient Converter converter;

    protected SOptionsProvider optionsProvider;

    private String selectLabel;

    public STypeSimple() {
        this.valueClass = null;
    }

    protected STypeSimple(Class<? extends I> instanceClass, Class<TIPO_NATIVO> valueClass) {
        super(instanceClass);
        this.valueClass = valueClass;
    }

    // SELECTION OF BEGIN

    @Override
    public String getSelectLabel() {
        return selectLabel;
    }

    @Override
    public void setSelectLabel(String selectLabel) {
        this.selectLabel = selectLabel;
    }



    @Override
    public SOptionsProvider getOptionsProvider() {
        return optionsProvider;
    }

    @Override
    public void setOptionsProvider(SOptionsProvider p) {
        optionsProvider = p;
    }


    /**
     * Configura o tipo para utilizar a view {@link SViewSelectionBySelect}
     */
    @SuppressWarnings("unchecked")
    public STypeSimple<I, TIPO_NATIVO> withSelectView() {
        return (STypeSimple<I, TIPO_NATIVO>) super.withView(SViewSelectionBySelect::new);
    }

    /**
     * Configura o tipo para utilizar a view {@link SViewSelectionByRadio}
     */
    @SuppressWarnings("unchecked")
    public STypeSimple<I, TIPO_NATIVO> withRadioView() {
        return (STypeSimple<I, TIPO_NATIVO>) super.withView(SViewSelectionByRadio::new);
    }


    public AtrFormula asFormula() {
        return STranslatorForAttribute.of(this, new AtrFormula());
    }

    public TIPO_NATIVO convert(Object value) {
        if (value == null) {
            return null;
        } else if (valueClass.isInstance(value)) {
            return valueClass.cast(value);
        } else if (value instanceof String) {
            return fromString((String) value);
        }
        return convertNotNativeNotString(value);
    }

    protected TIPO_NATIVO convertNotNativeNotString(Object value) {
        return convertUsingApache(value);
    }

    protected String toStringPersistence(TIPO_NATIVO originalValue) {
        if (originalValue == null) {
            return null;
        }
        return originalValue.toString();
    }

    public TIPO_NATIVO fromStringPersistence(String originalValue) {
        return convert(originalValue, valueClass);
    }

    public String toStringDisplay(TIPO_NATIVO value) {
        return toStringPersistence(value);
    }

    public TIPO_NATIVO fromString(String value) {
        throw new RuntimeException("Não implementado");
    }

    @Override
    public <T extends Object> T convert(Object value, Class<T> resultClass) {
        if (value == null) {
            return null;
        } else if (resultClass.isAssignableFrom(valueClass)) {
            return resultClass.cast(convert(value));
        } else if (resultClass.isInstance(value)) {
            return resultClass.cast(value);
        } else if (resultClass.isAssignableFrom(String.class)) {
            if (valueClass.isInstance(value)) {
                return resultClass.cast(toStringPersistence(valueClass.cast(value)));
            }
            return resultClass.cast(value.toString());
        } else {
            Converter converter = ConvertUtils.lookup(value.getClass(), resultClass);
            if (converter != null) {
                return resultClass.cast(converter.convert(resultClass, value));
            }
        }

        throw createConversionError(value, resultClass);
    }

    protected final TIPO_NATIVO convertUsingApache(Object value) {
        if (converter == null) {
            converter = ConvertUtils.lookup(valueClass);
            if (converter == null) {
                throw createConversionError(value);
            }
        }
        return valueClass.cast(converter.convert(valueClass, value));
    }

    public final Class<TIPO_NATIVO> getValueClass() {
        return valueClass;
    }

    protected final RuntimeException createConversionError(Object value) {
        return createConversionError(value, null, null, null);
    }

    protected final RuntimeException createConversionError(Object valor, Class<?> resultClass) {
        return createConversionError(valor, resultClass, null, null);
    }

    protected final RuntimeException createConversionError(Object value, Class<?> resultClass, String complement, Exception e) {
        String msg = "O tipo '" + getClass().getName() + "' não consegue converter o valor '" + value + "' do tipo "
                + value.getClass().getName();
        if (resultClass != null) {
            msg += " para o tipo '" + resultClass.getName() + "'";
        }
        if (complement != null) {
            msg += complement;
        }
        if (e != null) {
            return new RuntimeException(msg, e);
        }
        return new RuntimeException(msg);
    }
}
