package br.net.mirante.singular.form.mform.util.transformer;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;

import java.util.List;
import java.util.Map;

/**
 * Classe utilitária para converter uma lista de pojos
 * em uma MILista de MInstancias de um determinado MTipoComposto
 * @param <T>
 *     tipo paramétrico da lista - tipo do pojo
 */
public class FromPojoList<T> extends FromPojo<T> {

    private SType listType;
    private List<T> pojoList;

    /**
     *
     * @param target
     *  Tipo composto cujas instancias comporão a MILista criada
     * @param pojoList
     *  Lista com os pojos a serem convertidos.
     */
    public FromPojoList(STypeComposite<? extends SIComposite> target, List<T> pojoList) {
        super(target);
        this.pojoList = pojoList;
        this.listType = target;
    }

    @Override
    public <K extends SType<?>> FromPojoList<T> map(K type, FromPojoFiedlBuilder<T> mapper) {
        super.map(type, mapper);
        return this;
    }

    @Override
    public <K extends SType<?>> FromPojoList<T> map(K type, Object value) {
        super.map(type, value);
        return this;
    }

    @Override
    public SList<?> build() {
        SList<?> lista = target.novaLista();
        for (T pojo : pojoList) {
            SIComposite instancia = target.novaInstancia();
            for (Map.Entry<SType, FromPojoFiedlBuilder> e : mappings.entrySet()) {
                instancia.setValor(e.getKey().getName(), e.getValue().value(pojo));
            }
            lista.addElement(instancia);
        }
        return lista;
    }
}




