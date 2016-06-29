package br.net.mirante.singular.form;

/**
 * Contém informações referente a instância de um atributo.
 *
 * @author Daniel C. Bordin
 */
public final class AttributeInstanceInfo {

    private final String name;

    private final SInstance instanceOwner;

    private final SType<?> typeOwner;

    /** Cira um instância de atributo que está associado a um SIntance. */
    AttributeInstanceInfo(String name, SInstance instanceOwner) {
        this.name = name;
        this.instanceOwner = instanceOwner;
        this.typeOwner = null;
    }

    /** Cira um instância de atributo que está associado a um SType. */
    AttributeInstanceInfo(String name, SType<?> typeOwner) {
        this.name = name;
        this.instanceOwner = null;
        this.typeOwner = typeOwner;
    }

    /** Nome completo do atributo. */
    public String getName() {
        return name;
    }

    /** Retorna a instância a qual o atributo associa um valor (pode ser null, se o atributo for de um tipo). */
    public SInstance getInstanceOwner() {
        return instanceOwner;
    }

    /** Retorna o tipo ao qual o atributo associa um valor (pode ser null, se o atributo for de instância). */
    public SType<?> getTypeOwner() {
        return typeOwner;
    }
}
