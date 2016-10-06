package org.opensingular.singular.form;

/**
 * Contém informações referente a definição de um atributo.
 *
 * @author Daniel C. Bordin
 */
final class AttributeDefinitionInfo {

    private final SType<?> owner;

    private final boolean selfReference;

    AttributeDefinitionInfo() {
        this(null, false);
    }

    AttributeDefinitionInfo(SType<?> owner) {
        this(owner, false);
    }

    AttributeDefinitionInfo(SType<?> owner, boolean selfReference) {
        this.owner = owner;
        this.selfReference = selfReference;
    }

    /**
     * Retorna o tipo dono do atributo (onde o atributo está definido). Pode ser null se o atributo estiver apenas
     * criado no pacote mas não associnado ainda em um tipo específico.
     */
    public SType<?> getOwner() {
        return owner;
    }

    /** Indica se o tipo de retorno do atributo deve ser do mesmo tipo do tipo que o contêm. */
    public boolean isSelfReference() {
        return selfReference;
    }
}
