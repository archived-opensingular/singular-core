package br.net.mirante.singular.flow.util.props;

public class MetaDataRef<K> {

    private final String name;
    private final Class<K> valueClass;

    public MetaDataRef(String name, Class<K> valueClass) {
        this.name = name;
        this.valueClass = valueClass;
    }

    public String getName() {
        return name;
    }

    public Class<K> getValueClass() {
        return valueClass;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return name.equals(((MetaDataRef<?>) obj).name);
    }
}
