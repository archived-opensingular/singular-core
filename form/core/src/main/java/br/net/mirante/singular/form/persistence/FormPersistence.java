package br.net.mirante.singular.form.persistence;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.*;

/**
 * @author Daniel C. Bordin
 */
public interface FormPersistence<INSTANCE extends SInstance> {

    public FormKey keyFromString(String persistenceString);

    public FormKey insert(INSTANCE instance);

    public INSTANCE load(FormKey key);

    public Optional<INSTANCE> loadOpt(FormKey key);

    public void delete(FormKey key);

    public void update(INSTANCE instance);

    public FormKey insertOrUpdate(INSTANCE instance);

    public Iterable<INSTANCE> loadAllAsIterable();

    public Collection<INSTANCE> loadAllAsCollection();

    public List<INSTANCE> loadAllAsList();
}
