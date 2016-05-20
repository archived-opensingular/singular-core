package br.net.mirante.singular.studio.core;

import br.net.mirante.singular.form.SType;

/**
 * @author Daniel C. Bordin
 */
public interface CollectionEditorConfig<TYPE extends SType<?>> {

    public void configEditor(EditorConfigBuilder cfg);

    public void complementTypeConfig(TYPE type);
}
