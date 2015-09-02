package br.net.mirante.singular.form.mform;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class MIComposto extends MInstancia {

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
    public boolean isNull() {
        return campos == null || campos.values().stream().allMatch(i -> i.isNull());
    }

    public Collection<MInstancia> getCampos() {
        if (campos == null) {
            return Collections.emptyList();
        }
        return campos.values();
    }

    public MInstancia getCampo(String path) {
        return getCampo(new LeitorPath(path));
    }

    public MInstancia getCampo(LeitorPath leitorPath) {
        if (!leitorPath.isNomeSimplesValido()) {
            throw new RuntimeException(leitorPath.getTextoErro(this, "Não é um nome de campo válido"));
        }
        MInstancia instancia = null;
        if (campos != null) {
            instancia = campos.get(leitorPath.getTrecho());
        }
        if (instancia == null) {
            MTipo<?> tipoCampo = getMTipo().getCampo(leitorPath.getTrecho());
            if (tipoCampo == null) {
                throw new RuntimeException(leitorPath.getTextoErro(this, "Não é um campo definido"));
            }
            instancia = tipoCampo.novaInstancia();
            instancia.setPai(this);
            if (campos == null) {
                campos = new LinkedHashMap<>();
            }
            campos.put(leitorPath.getTrecho(), instancia);
        }
        if (leitorPath.isUltimo()) {
            return instancia;
        } else {
            return ((MIComposto) instancia).getCampo(leitorPath.proximo());
        }
    }

    public <T extends MInstancia> T getFilho(MTipo<T> tipoPai) {
        throw new RuntimeException("Método não implementado");
    }

    @Override
    void setValor(LeitorPath leitorPath, Object valor) {
        if (!leitorPath.isNomeSimplesValido()) {
            throw new RuntimeException(leitorPath.getTextoErro(this, "Não é um nome de campo válido"));
        }
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

    public boolean isCampoNull(String pathCampo) {
        return getValor() == null;
    }

    public Optional<Object> getValorOpt(String pathCampo) {
        return getValorOpt(pathCampo, null);
    }

    public final <T extends Object> Optional<T> getValorOpt(String pathCampo, Class<T> classeDestino) {
        return Optional.ofNullable(getValor(pathCampo, classeDestino));
    }

    @Override
    final <T extends Object> T getValor(LeitorPath leitor, Class<T> classeDestino) {
        if (campos != null) {
            if (leitor.isEmpty()) {
                return getValor(classeDestino);
            }
            MInstancia instancia = campos.get(leitor.getTrecho());
            if (instancia != null) {
                return instancia.getValor(leitor.proximo(), classeDestino);
            }
        }
        MTipo<?> tipo = MFormUtil.resolverTipoCampo(getMTipo(), leitor);
        // if (!(tipo instanceof MTipoSimples)) {
        // throw new RuntimeException(leitor.getTextoErro(this,
        // "Não é um tipo simples definido"));
        // }
        return null;
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
