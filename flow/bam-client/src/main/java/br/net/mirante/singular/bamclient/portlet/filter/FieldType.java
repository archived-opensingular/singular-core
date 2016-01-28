package br.net.mirante.singular.bamclient.portlet.filter;


import br.net.mirante.singular.bamclient.exeptions.BamClientExeption;

public enum FieldType {

    BOOLEAN(Boolean.class),
    INTEGER(Integer.class),
    TEXT(String.class),
    TEXTAREA,
    SELECTION,
    PERIOD,
    PERIOD_AGGREGATION(PeriodAggregation.class),
    DEFAULT;

    private Class[] defaultTypeForClasses;

    FieldType(Class... defaultTypeForClasses) {
        this.defaultTypeForClasses = defaultTypeForClasses;
    }

    public static FieldType getDefaultTypeForClass(Class clazz) {
        for (FieldType fieldType : values()) {
            for (Class compatibleClass : fieldType.defaultTypeForClasses) {
                if (clazz.equals(compatibleClass)) {
                    return fieldType;
                }
            }
        }
        throw new BamClientExeption("Não existe um tipo default para a classe " + clazz);
    }
}
