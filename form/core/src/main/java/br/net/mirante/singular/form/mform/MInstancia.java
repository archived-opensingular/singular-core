package br.net.mirante.singular.form.mform;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.mform.util.MInstanciaUtils;
import br.net.mirante.singular.form.util.xml.MElement;

public abstract class MInstancia implements MAtributoEnabled {

    private MInstancia pai;

    private MTipo<?> mTipo;

    private Map<String, MInstancia> atributos;

    private SDocument document;

    public MTipo<?> getMTipo() {
        return mTipo;
    }

    public SDocument getDocument() {
        // if (document == null) {
        // throw new RuntimeException(errorMsg("Documento não foi configurado na
        // instância"));
        // }
        return document;
    }

    final void setDocument(SDocument document) {
        this.document = document;
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
        throw new RuntimeException(erroMsgMetodoNaoSuportado());
    }

    public abstract Object getValor();

    /**
     * <p>
     * Retorna true se a instancia não conter nenhuma informação diferente de
     * null. A pesquisa é feita em profundidade, ou seja, em todos os subitens
     * (se houverem) da intância atual serão verificados.
     * </p>
     * <p>
     * Para o tipo simples retorna true se o valor for null.
     * </p>
     * <p>
     * Para o tipo lista retorna true se a lista for vazia ou se todos os seus
     * elementos retornarem isEmptyOfData() como true.
     * </p>
     * <p>
     * Para o tipo registro (composto) retorna true se todos so seus campos
     * retornarem isEmptyOfData().
     * </p>
     *
     * @return
     */
    public abstract boolean isEmptyOfData();

    public Object getValorWithDefault() {
        throw new RuntimeException(erroMsgMetodoNaoSuportado());
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

    final <T extends Object> T getValor(LeitorPath leitor, Class<T> classeDestino) {
        MInstancia instancia = this;
        while (true) {
            if (leitor.isEmpty()) {
                return instancia.getValor(classeDestino);
            }
            MInstancia instanciaFilha = instancia.getCampoLocalSemCriar(leitor);
            if (instanciaFilha == null) {
                MFormUtil.resolverTipoCampo(instancia.getMTipo(), leitor);
                return null;
            }
            instancia = instanciaFilha;
            leitor = leitor.proximo();
        }
    }

    <T extends Object> MInstancia getCampoLocalSemCriar(LeitorPath leitor) {
        throw new RuntimeException(erroMsgMetodoNaoSuportado());
    }

    <T extends Object> T getValorWithDefaultIfNull(LeitorPath leitor, Class<T> classeDestino) {
        throw new RuntimeException(erroMsgMetodoNaoSuportado());
    }

    void setValor(LeitorPath leitorPath, Object valor) {
        throw new RuntimeException(erroMsgMetodoNaoSuportado());
    }

    final MInstancia getCampo(LeitorPath leitor) {
        MInstancia instancia = this;
        while (true) {
            instancia = instancia.getCampoLocal(leitor);
            if (leitor.isUltimo()) {
                return instancia;
            } else if (!(instancia instanceof ICompositeInstance)) {
                throw new RuntimeException(leitor.getTextoErro(instancia, "Não suporta leitura de subCampos"));
            }
            leitor = leitor.proximo();
        }
    }

    MInstancia getCampoLocal(LeitorPath leitor) {
        throw new RuntimeException(erroMsgMetodoNaoSuportado());
    }

    public String getDisplayString() {
        throw new RuntimeException(erroMsgMetodoNaoSuportado());
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
            instanciaAtr = tipoAtributo.newInstance(getDocument());
            atributos.put(atr.getNomeCompleto(), instanciaAtr);
        }
        if (subPath != null) {
            instanciaAtr.setValor(new LeitorPath(subPath), valor);
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

    public <A extends MInstancia & ICompositeInstance> A getAncestor(MTipo<A> ancestorType) {
        return findAncestor(ancestorType).get();
    }
    public <A extends MInstancia & ICompositeInstance> Optional<A> findAncestor(MTipo<A> ancestorType) {
        return MInstanciaUtils.findAncestor(this, ancestorType);
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> T as(Class<T> classeAlvo) {
        if (MTranslatorParaAtributo.class.isAssignableFrom(classeAlvo)) {
            return (T) MTranslatorParaAtributo.of(this, (Class<MTranslatorParaAtributo>) classeAlvo);
        }
        throw new RuntimeException(
            "Classe '" + classeAlvo + "' não funciona como aspecto. Deve extender " + MTranslatorParaAtributo.class.getName());
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

    final String erroMsgMetodoNaoSuportado() {
        return errorMsg("Método não suportado por " + getClass().getName());
    }

    /**
     * Cria uma mensagem de erro com o path da instância atual acrescido da
     * mensagem fornecida.
     */
    protected final String errorMsg(String msgToBeAppended) {
        return "'" + getCaminhoCompleto() + "' do tipo " + getMTipo().getNome() + "(" + getMTipo().getClass().getSimpleName() + ") : "
            + msgToBeAppended;
    }
}
