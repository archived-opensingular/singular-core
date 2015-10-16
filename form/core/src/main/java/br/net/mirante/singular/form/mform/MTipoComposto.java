package br.net.mirante.singular.form.mform;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import br.net.mirante.singular.form.mform.core.MPacoteCore;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;

@MInfoTipo(nome = "MTipoComposto", pacote = MPacoteCore.class)
public class MTipoComposto<TIPO_INSTANCIA extends MIComposto> extends MTipo<TIPO_INSTANCIA> {

    private final Map<String, MTipo<?>> campos = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public MTipoComposto() {
        super((Class<? extends TIPO_INSTANCIA>) MIComposto.class);
    }

    protected MTipoComposto(Class<TIPO_INSTANCIA> classeInstancia) {
        super(classeInstancia);
    }

    public <I extends MInstancia, T extends MTipo<I>> T addCampo(Class<T> classeTipo) {
        T tipo = resolverTipo(classeTipo);
        return extenderTipo(tipo.getNomeSimples(), tipo);
    }

    public <I extends MInstancia, T extends MTipo<I>> T addCampo(String nomeCampo, Class<T> tipo, boolean obrigatorio) {
        T novo = addCampo(nomeCampo, tipo);
        novo.withObrigatorio(obrigatorio);
        return novo;
    }

    public <I extends MInstancia, T extends MTipo<?>> T addCampo(String nomeCampo, Class<T> classeTipo) {
        T novo = extenderTipo(nomeCampo, classeTipo);
        campos.put(nomeCampo, novo);
        return novo;
    }

    public <T extends MTipo<?>> MTipoLista<T> addCampoListaOf(String nomeCampo, T tipoElementos) {
        MTipoLista<T> novo = createTipoListaOf(nomeCampo, tipoElementos);
        campos.put(nomeCampo, novo);
        return novo;
    }

    public MTipoLista<MTipoComposto<?>> addCampoListaOfComposto(String nomeCampo, String nomeNovoTipoComposto) {
        MTipoLista<MTipoComposto<?>> novo = createTipoListaOfNovoTipoComposto(nomeCampo, nomeNovoTipoComposto);
        campos.put(nomeCampo, novo);
        return novo;
    }

    public MTipo<?> getCampo(String nomeCampo) {
        return campos.get(nomeCampo);
    }
    
    public Set<String> getCampos() {
        return campos.keySet();
    }

    // --------------------------------------------------------------------------
    // Atalhos de conveniência
    // --------------------------------------------------------------------------

    public MTipoComposto<?> addCampoComposto(String nomeCampo) {
        return addCampo(nomeCampo, MTipoComposto.class);
    }

    public MTipoString addCampoString(String nomeCampo) {
        return addCampo(nomeCampo, MTipoString.class);
    }

    public MTipoString addCampoString(String nomeCampo, boolean obrigatorio) {
        return addCampo(nomeCampo, MTipoString.class, obrigatorio);
    }

    public MTipoData addCampoData(String nomeCampo) {
        return addCampo(nomeCampo, MTipoData.class);
    }

    public MTipoData addCampoData(String nomeCampo, boolean obrigatorio) {
        return addCampo(nomeCampo, MTipoData.class, obrigatorio);
    }

    public MTipoBoolean addCampoBoolean(String nomeCampo) {
        return addCampo(nomeCampo, MTipoBoolean.class);
    }

    public MTipoBoolean addCampoBoolean(String nomeCampo, boolean obrigatorio) {
        return addCampo(nomeCampo, MTipoBoolean.class, obrigatorio);
    }

    public MTipoInteger addCampoInteger(String nomeCampo) {
        return addCampo(nomeCampo, MTipoInteger.class);
    }

    public MTipoInteger addCampoInteger(String nomeCampo, boolean obrigatorio) {
        return addCampo(nomeCampo, MTipoInteger.class, obrigatorio);
    }
}
