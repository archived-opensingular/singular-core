package org.opensingular.form.wicket.mapper.decorator;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.decorator.action.ISInstanceActionCapable;
import org.opensingular.form.decorator.action.ISInstanceActionsProvider;
import org.opensingular.form.decorator.action.SInstanceAction;

import com.google.common.collect.Lists;

public class SInstanceActionsProviders implements Serializable, ISInstanceActionCapable {

    private List<Entry>             entries;
    private ISInstanceActionCapable owner;

    public SInstanceActionsProviders(ISInstanceActionCapable owner) {
        this.owner = owner;
    }

    public Iterator<SInstanceAction> actionIterator(final IModel<? extends SInstance> model) {
        return actionList(model).iterator();
    }

    public List<SInstanceAction> actionList(final IModel<? extends SInstance> model) {
        if (this.entries == null)
            return Collections.emptyList();
        else
            return this.entries.stream()
                .map(it -> it.provider)
                .flatMap(it -> Lists.newArrayList(it.getActions(owner, model.getObject())).stream())
                .collect(toList());
    }

    @Override
    public void addSInstanceActionsProvider(int sortPosition, ISInstanceActionsProvider provider) {
        if (this.entries == null)
            this.entries = new ArrayList<>();
        this.entries.add(new Entry(sortPosition, provider));
        this.entries.sort(comparingInt(it -> it.position));
    }

    private static final class Entry implements Serializable {
        public final int                       position;
        public final ISInstanceActionsProvider provider;
        public Entry(int position, ISInstanceActionsProvider provider) {
            this.position = position;
            this.provider = provider;
        }
    }
}
