package br.net.mirante.singular.form.mform;

import java.util.Optional;

/**
 * Carrega as definições resolvendo dependências do dicionário bem como
 * carregando definições extras configuranda pela aplicação. Pode ser utilizada
 * para ler do classpath ou mesmo de um banco de dados.
 *
 * @author Daniel C. Bordin
 */
public abstract class MDicionarioLoader extends MDicionarioResolver {

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
    @Override
    public final Optional<MDicionario> loadDicionaryForType(String typeName) {
        MDicionario novo;
        if (parent == null) {
            novo = MDicionario.create();
        } else {
            novo = parent.loadDicionaryForType(typeName).get();
        }
        configDicionary(novo, typeName);
        return Optional.of(novo);

    }

    /**
     * Configura o dicionário criado de acordo com o comportamento do loader em
     * questão.
     */
    protected abstract void configDicionary(MDicionario newDicionary, String taregetTypeName);

}
