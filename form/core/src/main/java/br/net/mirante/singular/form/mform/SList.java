package br.net.mirante.singular.form.mform;

import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SList<E extends SInstance> extends SInstance implements Iterable<E>, ICompositeInstance {

    private List<E> valores;

    private SType<E> tipoElementos;

    public SList() {}

    static <I extends SInstance> SList<I> of(SType<I> tipoElementos) {
        //        MILista<I> lista = new MILista<>();
        //TODO: FABS: Evaluate this case, sin it impacts in the serialization process.
        SList<I> lista = (SList<I>) tipoElementos.getDictionary().getType(STypeLista.class).novaInstancia();
        lista.setType(tipoElementos.getDictionary().getType(STypeLista.class));
        lista.tipoElementos = tipoElementos;
        return lista;
    }

    @Override
    public STypeLista<?, ?> getMTipo() {
        return (STypeLista<?, ?>) super.getMTipo();
    }

    @SuppressWarnings("unchecked")
    public SType<E> getTipoElementos() {
        if (tipoElementos == null) {
            tipoElementos = (SType<E>) getMTipo().getTipoElementos();
        }
        return tipoElementos;
    }

    @Override
    public List<Object> getValue() {
        if (valores == null) {
            return Collections.emptyList();
        }
        return valores.stream().map(SInstance::getValue).collect(Collectors.toList());
    }

    @Override
    public void clearInstance() {
        if (valores != null) {
            valores.forEach(SInstance::clearInstance);
        }
    }

    @Override
    public final <T> T getValor(String pathCampo, Class<T> classeDestino) {
        return getValor(new PathReader(pathCampo), classeDestino);
    }

    @Override
    protected void resetValue() {
        clear();
    }

    @Override
    public boolean isEmptyOfData() {
        return isEmpty() || valores.stream().allMatch(SInstance::isEmptyOfData);
    }

    public E addNovo() {
        return addInterno(getTipoElementos().newInstance(getDocument()));
    }

    public E addNovo(Consumer<E> consumer) {
        E novo = getTipoElementos().newInstance(getDocument());
        consumer.accept(novo);
        return addInterno(novo);
    }

    @SuppressWarnings("unchecked")
    public E addElement(Object e) {
        E element = (E) e;
        element.setDocument(getDocument());
        return addInterno(element);
    }

    public E addElementAt(int index, E e) {
        E element = e;
        element.setDocument(getDocument());
        addAtInterno(index, element);
        return element;
    }

    public E addNovoAt(int index) {
        E instancia = getTipoElementos().newInstance(getDocument());
        addAtInterno(index, instancia);
        return instancia;
    }

    public E addValor(Object valor) {
        E instancia = getTipoElementos().newInstance(getDocument());
        instancia.setValue(valor);
        return addInterno(instancia);
    }

    public SList<E> addValores(Collection<?> valores) {
        for (Object valor : valores)
            addValor(valor);
        return this;
    }

    private E addInterno(E instancia) {
        if (valores == null) {
            valores = new ArrayList<>();
        }
        valores.add(instancia);
        instancia.setParent(this);
        return instancia;
    }

    private void addAtInterno(int index, E instancia) {
        if (valores == null) {
            valores = new ArrayList<>();
        }
        valores.add(index, instancia);
        instancia.setParent(this);
    }

    public void clear() {
        if (valores != null) {
            valores.clear();
        }
    }

    public SInstance get(int index) {
        if (valores == null) {
            throw new IndexOutOfBoundsException(errorMsg("A lista " + getNome() + " está vazia (index=" + index + ")"));
        }
        return valores.get(index);
    }

    @Override
    public SInstance getCampo(String path) {
        return getCampo(new PathReader(path));
    }

    @Override
    final SInstance getCampoLocal(PathReader leitor) {
        if (!leitor.isIndice()) {
            throw new RuntimeException(leitor.getTextoErro(this, "Era esperado um indice do elemento (exemplo [1])"));
        }
        SInstance instancia = isEmpty() ? null : valores.get(leitor.getIndice());
        if (instancia == null) {
            MFormUtil.resolverTipoCampo(getMTipo(), leitor);
        }
        return instancia;
    }

    @Override
    final SInstance getCampoLocalSemCriar(PathReader leitor) {
        if (!leitor.isIndice()) {
            throw new RuntimeException(leitor.getTextoErro(this, "Era esperado um indice do elemento (exemplo [1])"));
        }
        return isEmpty() ? null : valores.get(leitor.getIndice());
    }

    @Override
    public void setValue(Object obj) {
        if(obj instanceof SList){
            clearInstance();
            valores = newArrayList(((SList)obj).valores);
            tipoElementos = ((SList)obj).tipoElementos;
            ((SList) obj).getValue().clear();
        }else{
            throw new RuntimeException("SList só suporta valores de mesmo tipo");
        }
    }
    @Override
    public final void setValor(String pathCampo, Object valor) {
        setValor(new PathReader(pathCampo), valor);
    }

    @Override
    void setValor(PathReader leitorPath, Object valor) {
        if (!leitorPath.isIndice()) {
            throw new RuntimeException(leitorPath.getTextoErro(this, "Era esperado um indice do elemento (exemplo [1])"));
        }
        SInstance instancia = get(leitorPath.getIndice());
        if (leitorPath.isUltimo()) {
            instancia.setValue(valor);
        } else {
            instancia.setValor(leitorPath.proximo(), valor);
        }
    }

    public SInstance remove(int index) {
        if (valores == null) {
            throw new IndexOutOfBoundsException(errorMsg("A lista " + getNome() + " está vazia (index=" + index + ")"));
        }
        E child = valores.get(index);
        child.internalOnRemove();
        return valores.remove(index);
    }

    public Object getValorAt(int index) {
        return get(index).getValue();
    }

    /**
     * Retornar o índice da instancia dentro da lista. Utiliza identidade (==)
     * em vez de equals().
     *
     * @param supposedChild
     * @return -1 senão encontrou
     */
    public int indexOf(SInstance supposedChild) {
        for (int i = size() - 1; i != -1; i--) {
            if (valores.get(i) == supposedChild) {
                return i;
            }
        }
        return -1;
    }

    public int size() {
        return (valores == null) ? 0 : valores.size();
    }

    public boolean isEmpty() {
        return (valores == null) || valores.isEmpty();
    }

    public List<E> getValores() {
        return (valores == null) ? Collections.emptyList() : valores;
    }

    @Override
    public Collection<E> getChildren() {
        return getValores();
    }

    @Override
    public Iterator<E> iterator() {
        return (valores == null) ? Collections.emptyIterator() : valores.iterator();
    }

    @Override
    public Stream<E> stream() {
        return getValores().stream();
    }

    public String toDebug() {
        return stream().map(SInstance::getDisplayString).collect(Collectors.joining("; "));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tipoElementos == null) ? 0 : tipoElementos.hashCode());
        for (E e : this)
            result = prime * result + (e == null ? 0 : e.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SList<?> other = (SList<?>) obj;
        if (size() != other.size()) {
            return false;
        } else if (!getMTipo().equals(other.getMTipo())) {
            return false;
        } else if (!Objects.equals(getTipoElementos(), other.getTipoElementos()))
            return false;
        for (int i = size() - 1; i != -1; i--) {
            if (!Objects.equals(get(i), other.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("MILista(%s)", getAllChildren());
    }
}
