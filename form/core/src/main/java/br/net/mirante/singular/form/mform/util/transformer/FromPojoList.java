package br.net.mirante.singular.form.mform.util.transformer;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;

import java.util.List;
import java.util.Map;

/**
 * Classe utilitária para converter uma lista de pojos
 * em uma MILista de MInstancias de um determinado MTipoComposto
 * @param <T>
 *     tipo paramétrico da lista - tipo do pojo
 */
public class FromPojoList<T> extends FromPojo<T> {

    private MTipo listType;
    private List<T> pojoList;

    /**
     *
     * @param target
     *  Tipo composto cujas instancias comporão a MILista criada
     * @param pojoList
     *  Lista com os pojos a serem convertidos.
     */
    public FromPojoList(MTipoComposto<? extends MIComposto> target, List<T> pojoList) {
        super(target);
        this.listType = target;
    }

    @Override
    public <K extends MTipo<?>> FromPojoList<T> map(K type, FromPojoFiedlBuilder<T> mapper) {
        super.map(type, mapper);
        return this;
    }

    @Override
    public <K extends MTipo<?>> FromPojoList<T> map(K type, Object value) {
        super.map(type, value);
        return this;
    }

    @Override
    public MILista<?> build() {
        MILista<?> lista = target.novaLista();
        for (T pojo : pojoList) {
            MIComposto instancia = target.novaInstancia();
            for (Map.Entry<MTipo, FromPojoFiedlBuilder> e : mappings.entrySet()) {
                instancia.setValor(e.getKey().getNome(), e.getValue().value(pojo));
            }
            lista.addElement(instancia);
        }
        return lista;
    }
}




