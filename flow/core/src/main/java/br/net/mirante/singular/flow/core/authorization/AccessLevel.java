package br.net.mirante.singular.flow.core.authorization;

public enum AccessLevel {
    /**
     * Listar
     */
    LIST("Listar"),
    /**
     * Detalhar
     */
    DETAIL("Detalhar");
    
    private final String name;

    private AccessLevel(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
