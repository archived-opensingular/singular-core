package br.net.mirante.singular.form.mform;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.core.SPackageCore;
import br.net.mirante.singular.form.mform.document.SDocument;

/**
 * Representa um tipo lista, o qual deve ter um tipo definido para todos os seus
 * elementos. Basicamente representa um array.
 *
 * @author Daniel C. Bordin
 */
@MInfoTipo(nome = "MTipoLista", pacote = SPackageCore.class)
public class STypeLista<E extends SType<I>, I extends SInstance> extends SType<SList<I>> implements ICompositeType {

    private E tipoElementos;
    private Integer minimumSize;
    private Integer maximumSize;

    @SuppressWarnings("unchecked")
    public STypeLista() {
        // O cast na linha abaixo parece redundante, mas é necessário para
        // contornar um erro de compilação do JDK 8.0.60. Talvez no futuro
        // possa ser retirada
        super((Class<? extends SList<I>>) (Class<? extends SInstance>) SList.class);
    }

    @Override
    public Collection<SType<?>> getContainedTypes() {
        return Collections.singleton(getTipoElementos());
    }

    /**
     * Cria a nova instância de lista com o cast para o generic tipo de
     * instancia informado. Se o tipo do conteudo não for compatível dispara
     * exception.
     * <p>
     * <pre>
     * MTipoLista&lt;MTipoString> tipoLista = ...
     *
     * // metodo simples e não dispara exception se tipo errado
     * MILista&lt;MIString> lista1 = (MILista&lt;MIString>) tipoLista.novaInstancia();
     *
     * // já devolvendo lista no tipo certo e verificando se correto
     * MILista&lt;MIString> lista2 = tipoLista.novaInstancia(MIString.class);
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public <T extends SInstance> SList<T> novaInstancia(Class<T> classOfElements) {
        SList<?> nova = novaInstancia();
        if (!classOfElements.isAssignableFrom(getTipoElementos().getClasseInstancia())) {
            throw new RuntimeException("As instancias da lista são do tipo " + getTipoElementos().getClasseInstancia().getName()
                    + ", que não é compatível com o solicitado " + classOfElements.getName());
        }
        return (SList<T>) nova;
    }

    @Override
    SList<I> newInstance(SDocument owner) {
        if (tipoElementos == null) {
            throw new RuntimeException("Não é possível instanciar o tipo '" + getName()
                    + "' pois o tipo da lista (o tipo de seus elementos) não foram definidos");
        }
        SList<I> lista = new SList<>();
        lista.setType(this);
        lista.setDocument(owner);
        return lista;
    }

    protected void setTipoElementos(E tipoElementos) {
        if (this.tipoElementos != null) {
            throw new RuntimeException("O tipo da lista já está definido");
        }
        this.tipoElementos = tipoElementos;
    }

    /**
     * Define que o tipo da lista sera um novo tipo record (tipo composto) com o
     * nome infomado. O novo tipo é criado sem campos, devendo ser estruturado
     * na sequencia.
     */
    @SuppressWarnings("unchecked")
    void setTipoElementosNovoTipoComposto(String nomeSimplesNovoTipoComposto) {
        STypeComposite<?> tipo = extenderType(nomeSimplesNovoTipoComposto, STypeComposite.class);
        setTipoElementos((E) tipo);
    }

    /**
     * Retorna o tipo do elementos contido na lista.
     */
    public E getTipoElementos() {
        return tipoElementos;
    }

    public STypeLista<E, I> withMiniumSizeOf(Integer size) {
        this.minimumSize = size;
        return this;
    }

    public STypeLista<E, I> withMaximumSizeOf(Integer size) {
        this.maximumSize = size;
        return this;
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        addInstanceValidator(validatable -> {
            final Integer minimumSize = validatable.getInstance().getType().minimumSize;
            if (minimumSize != null && validatable.getInstance().getValue().size() < minimumSize) {
                validatable.error("A Quantidade mínima de " + getLabel(validatable.getInstance()) + " é " + minimumSize);
            }
        });
        addInstanceValidator(validatable -> {
            final Integer maximumSize = validatable.getInstance().getType().maximumSize;
            if (maximumSize != null && validatable.getInstance().getValue().size() > maximumSize) {
                validatable.error("A Quantidade máxima " + getLabel(validatable.getInstance()) + " é " + maximumSize);
            }
        });
    }

    private String getLabel(SInstance ins) {
        return Optional.ofNullable(ins.getValorAtributo(SPackageBasic.ATR_LABEL)).orElse("valores");
    }

    public Integer getMinimumSize() {
        return minimumSize;
    }

    public Integer getMaximumSize() {
        return maximumSize;
    }
}
