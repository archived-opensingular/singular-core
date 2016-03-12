package br.net.mirante.singular.form.mform;

public interface SAttributeEnabled {

    default <V> void setAttributeValue(AtrRef<?, ?, V> atr, V value) {
        setAttributeValue(atr, null, value);
    }

    default <V> void setAttributeValue(AtrRef<?, ?, V> atr, String subPath, V value) {
        getDictionary().loadPackage(atr.getPackageClass());
        setAttributeValue(atr.getNameFull(), subPath, value);
    }

    default <V> void setAttributeValue(SAttribute defAttribute, Object value) {
        setAttributeValue(defAttribute.getName(), null, value);
    }

    default void setAttributeValue(String attributeName, Object value) {
        setAttributeValue(attributeName, null, value);
    }

    void setAttributeValue(String fullNameAttribute, String subPath, Object value);

    <V> V getAttributeValue(String fullNameAttribute, Class<V> resultClass);

    default <T> T getAttributeValue(AtrRef<?, ?, ?> atr, Class<T> resultClass) {
        getDictionary().loadPackage(atr.getPackageClass());
        return getAttributeValue(atr.getNameFull(), resultClass);
    }

    default <V> V getAttributeValue(AtrRef<?, ?, V> atr) {
        getDictionary().loadPackage(atr.getPackageClass());
        return getAttributeValue(atr.getNameFull(), atr.getValueClass());
    }

    default Object getAttributeValue(String fullName) {
        return getAttributeValue(fullName, null);
    }

    SDictionary getDictionary();

}
