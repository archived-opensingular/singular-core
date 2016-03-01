package br.net.mirante.singular.pet.module.flow;

import br.net.mirante.singular.flow.core.ITaskPageStrategy;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.TaskInstance;
import org.apache.wicket.markup.html.WebPage;

public class PetServerTaskPageStrategy implements ITaskPageStrategy {

    private SingularWebRef webRef;

    public PetServerTaskPageStrategy(Class<? extends WebPage> page) {
        this.webRef = new SingularWebRef(page);
    }

    public PetServerTaskPageStrategy() {

    }

    public static final PetServerTaskPageStrategy of(Class<? extends WebPage> page) {
        return new PetServerTaskPageStrategy(page);
    }

    @Override
    public SingularWebRef getPageFor(TaskInstance taskInstance, MUser user) {
        return webRef;
    }

}
