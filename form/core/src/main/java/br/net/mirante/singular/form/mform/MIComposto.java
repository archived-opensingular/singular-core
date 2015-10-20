package br.net.mirante.singular.form.mform;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class MIComposto extends MInstancia implements IPathEnabledInstance {

    private Map<String, MInstancia> campos;

    @Override
    public MTipoComposto<?> getMTipo() {
        return (MTipoComposto<?>) super.getMTipo();
    }

    @Override
    public Object getValor() {
        return getCampos();
    }

    @Override
    public boolean isEmptyOfData() {
        return campos == null || campos.values().stream().allMatch(i -> i.isEmptyOfData());
    }

    public Collection<MInstancia> getCampos() {
        if (campos == null) {
            return Collections.emptyList();
        }
        return campos.values();
    }

    @Override
    public MInstancia getCampo(String path) {
        return getCampo(new LeitorPath(path));
    }

    @Override
    final MInstancia getCampoLocal(LeitorPath leitor) {
        MInstancia instancia = null;
        if (campos != null) {
            instancia = campos.get(leitor.getTrecho());
        }
        if (instancia == null) {
            MTipo<?> tipoCampo = getMTipo().getCampo(leitor.getTrecho());
            if (tipoCampo == null) {
                throw new RuntimeException(leitor.getTextoErro(this, "Não é um campo definido"));
            }
            instancia = tipoCampo.novaInstancia();
            instancia.setPai(this);
            if (campos == null) {
                campos = new LinkedHashMap<>();
            }
            campos.put(leitor.getTrecho(), instancia);
        }
        return instancia;
    }

    @Override
    final MInstancia getCampoLocalSemCriar(LeitorPath leitor) {
        if (leitor.isIndice()) {
            throw new RuntimeException(leitor.getTextoErro(this, "Não é uma lista"));
        }
        return (campos == null) ? null : campos.get(leitor.getTrecho());
    }

    public <T extends MInstancia> T getFilho(MTipo<T> tipoPai) {
        throw new RuntimeException("Método não implementado");
    }

    @Override
    public final void setValor(String pathCampo, Object valor) {
        setValor(new LeitorPath(pathCampo), valor);
    }

    @Override
    void setValor(LeitorPath leitorPath, Object valor) {
        MInstancia instancia = null;
        if (campos != null) {
            instancia = campos.get(leitorPath.getTrecho());
        }
        if (instancia == null) {
            MTipo<?> tipoCampo = getMTipo().getCampo(leitorPath.getTrecho());
            if (tipoCampo == null) {
                throw new RuntimeException(leitorPath.getTextoErro(this, "Não é um campo definido"));
            }
            if (valor == null) {
                return;
            }
            instancia = tipoCampo.novaInstancia();
            instancia.setPai(this);
            if (campos == null) {
                campos = new LinkedHashMap<>();
            }
            campos.put(leitorPath.getTrecho(), instancia);
        }
        if (leitorPath.isUltimo()) {
            if (valor == null) {
                campos.remove(leitorPath.getTrecho());
            } else {
                instancia.setValor(valor);
            }
        } else {
            instancia.setValor(leitorPath.proximo(), valor);
        }
    }

    @Override
    public final <T extends Object> T getValor(String pathCampo, Class<T> classeDestino) {
        return getValor(new LeitorPath(pathCampo), classeDestino);
    }

    public Optional<Object> getValorOpt(String pathCampo) {
        return getValorOpt(pathCampo, null);
    }

    public final <T extends Object> Optional<T> getValorOpt(String pathCampo, Class<T> classeDestino) {
        return Optional.ofNullable(getValor(pathCampo, classeDestino));
    }

    @Override
    final <T extends Object> T getValorWithDefaultIfNull(LeitorPath leitor, Class<T> classeDestino) {
        if (campos != null) {
            MInstancia instancia = campos.get(leitor.getTrecho());
            if (instancia != null) {
                return instancia.getValorWithDefaultIfNull(leitor.proximo(), classeDestino);
            }
        }
        MTipo<?> tipo = MFormUtil.resolverTipoCampo(getMTipo(), leitor);
        return tipo.getValorAtributoOrDefaultValueIfNull(classeDestino);
    }

    public final <T extends Object> T getValorInterno(String nomeSimples, Class<T> classeDestino) {
        MInstancia instancia = campos.get(nomeSimples);
        return instancia.getValor(classeDestino);
    }
}
