package br.net.mirante.singular.util.wicket.datatable;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.navigation.paging.IPageableItems;

public class BSItemsPerPageDropDown extends DropDownChoice<Long> {

    private final IPageableItems pageableComponent;

    public <P extends Component & IPageableItems> BSItemsPerPageDropDown(String id, P pageableComponent) {
        super(id);
        this.pageableComponent = pageableComponent;
        setModel($m.getSet(() -> this.getPageable().getItemsPerPage(), arg -> this.getPageable().setItemsPerPage(arg)));
        setChoices($m.get(this::getItemsPerPageOptions));
    }

    @SuppressWarnings("unchecked")
    public <P extends Component & IPageableItems> P getPageable() {
        return (P) pageableComponent;
    }

    protected List<Long> getItemsPerPageOptions() {
        Set<Long> options = new TreeSet<>();
        options.add(getPageable().getItemsPerPage());
        options.add(10L);
        options.add(30L);
        options.add(50L);
        options.add(100L);
        return new ArrayList<>(options);
    }
}
