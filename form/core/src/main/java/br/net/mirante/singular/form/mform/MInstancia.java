package br.net.mirante.singular.form.mform;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.util.xml.MElement;

public abstract class MInstancia implements MAtributoEnabled {

    private MInstancia pai;

    private MTipo<?> mTipo;

    private Map<String, MInstancia> atributos;

    public MTipo<?> getMTipo() {
        return mTipo;
    }
    public MView getView() {
        return getMTipo().getView();
    }

    @Override
    public MDicionario getDicionario() {
        return getMTipo().getDicionario();
    }

    final void setPai(MInstancia pai) {
        this.pai = pai;
    }

    final void setTipo(MTipo<?> tipo) {
        this.mTipo = tipo;
    }

    public void setValor(Object valor) {
        throw new RuntimeException("Método não suportado");
    }

    public abstract Object getValor();

    public abstract boolean isNull();

    public Object getValorWithDefault() {
        throw new RuntimeException("Método não suportado");
    }

    public final <T extends Object> T getValorWithDefault(Class<T> classeDestino) {
        if (classeDestino == null) {
            return (T) getValor();
        }
        return getMTipo().converter(getValorWithDefault(), classeDestino);
    }

    public final <T extends Object> T getValor(Class<T> classeDestino) {
        if (classeDestino == null) {
            return (T) getValor();
        }
        return getMTipo().converter(getValor(), classeDestino);
    }

    public final Object getValor(String pathCampo) {
        return getValor(new LeitorPath(pathCampo), null);
    }

    public final <T extends Object> T getValor(String pathCampo, Class<T> classeDestino) {
        return getValor(new LeitorPath(pathCampo), classeDestino);
    }

    <T extends Object> T getValor(LeitorPath leitor, Class<T> classeDestino) {
        throw new RuntimeException("Método não suportado");
    }

    <T extends Object> T getValorWithDefaultIfNull(LeitorPath leitor, Class<T> classeDestino) {
        throw new RuntimeException("Método não suportado");
    }

    public final String getValorString(String pathCampo) {
        return getValor(pathCampo, String.class);
    }

    public final <T extends Enum<T>> T getValorEnum(String pathCampo, Class<T> enumType) {
        String valor = getValorString(pathCampo);
        if (valor != null) {
            return Enum.valueOf(enumType, valor);
        }
        return null;
    }

    public final void setValor(String pathCampo, Object valor) {
        setValor(new LeitorPath(pathCampo), valor);
    }

    void setValor(LeitorPath leitorPath, Object valor) {
        throw new RuntimeException("Método não suportado");
    }

    public String getDisplayString() {
        throw new RuntimeException("Método não suportado");
    }

    @Override
    public <V extends Object> void setValorAtributo(AtrRef<?, ?, V> atr, String subPath, V valor) {
        MInstancia instanciaAtr = null;
        if (atributos == null) {
            atributos = new HashMap<>();
        } else {
            instanciaAtr = atributos.get(atr.getNomeCompleto());
        }
        if (instanciaAtr == null) {
            MAtributo tipoAtributo = getMTipo().getAtributoDefinidoHierarquia(atr.getNomeCompleto());
            instanciaAtr = tipoAtributo.novaInstancia();
            atributos.put(atr.getNomeCompleto(), instanciaAtr);
        }
        if (subPath != null) {
            instanciaAtr.setValor(subPath, valor);
        } else {
            instanciaAtr.setValor(valor);
        }
    }

    @Override
    public <V extends Object> V getValorAtributo(String nomeCompleto, Class<V> classeDestino) {
        if (atributos != null) {
            MInstancia inst = atributos.get(nomeCompleto);
            if (inst != null) {
                return inst.getValor(classeDestino);
            }
        }
        return getMTipo().getValorAtributo(nomeCompleto, classeDestino);
    }

    public MInstancia getPai() {
        return this.pai;
    }

    public <K extends MInstancia> K getIrmao(MTipo<K> tipoPai) {
        throw new RuntimeException("implementar");
    }

    public <T extends Object> T as(Class<T> classeAlvo) {
        if (MTranslatorParaAtributo.class.isAssignableFrom(classeAlvo)) {
            return (T) MTranslatorParaAtributo.of(this, (Class<MTranslatorParaAtributo>) classeAlvo);
        }
        throw new RuntimeException("Classe '" + classeAlvo + "' não funciona como aspecto");
    }
    public <T> T as(Function<? super MInstancia, T> aspectFactory) {
        return aspectFactory.apply(this);
    }

    public String getNome() {
        return getMTipo().getNomeSimples();
    }

    public final String getCaminhoCompleto() {
        if (pai == null) {
            return getNome();
        }
        return getCaminhoCompleto(new StringBuilder(), null).toString();
    }

    protected StringBuilder getCaminhoCompleto(StringBuilder sb, MInstancia filhoReferencia) {
        if (pai != null) {
            pai.getCaminhoCompleto(sb, this);
            sb.append('.');
        }
        sb.append(getNome());
        return sb;
    }

    public void debug() {
        MElement xml = MformPersistenciaXML.toXML(this);
        if (xml == null) {
            System.out.println("null");
        } else {
            xml.printTabulado();
        }
    }
}
