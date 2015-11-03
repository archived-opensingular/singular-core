package br.net.mirante.singular.form.mform;

/**
 * Carrega as definições resolvendo dependências do dicionário bem como
 * carregando definições extras configuranda pela aplicação. Pode ser utilizada
 * para ler do classpath ou mesmo de um banco de dados.
 *
 * @author Daniel C. Bordin
 */
public abstract class MDicionarioLoader {

    private final MDicionarioLoader parent;

    public MDicionarioLoader() {
        parent = null;
    }

    public MDicionarioLoader(MDicionarioLoader parent) {
        this.parent = parent;
    }

    public MDicionarioLoader getParent() {
        return parent;
    }

    /**
     * Cria o dicionário necessário para o tipo informado. Se os loader
     * estiverem configurado, pode adicionar pacotes extras.
     */
    public final MDicionario loadDicionaryFor(String typeName) {
        MDicionario novo;
        if (parent == null) {
            novo = MDicionario.create();
        } else {
            novo = parent.loadDicionaryFor(typeName);
        }
        configDicionary(novo, typeName);
        return novo;

    }

    /**
     * Configura o dicionário criado de acordo com o comportamento do loader em
     * questão.
     */
    protected abstract void configDicionary(MDicionario newDicionary, String taregetTypeName);

    public final MTipo<?> loadType(String typeName) {
        MDicionario dicionary = loadDicionaryFor(typeName);
        return dicionary.getTipo(typeName);
    }

}
