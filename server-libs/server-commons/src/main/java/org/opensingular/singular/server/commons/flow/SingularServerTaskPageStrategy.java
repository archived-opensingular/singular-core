package org.opensingular.singular.server.commons.flow;

import org.opensingular.singular.flow.core.ITaskPageStrategy;
import org.opensingular.singular.flow.core.MUser;
import org.opensingular.singular.flow.core.TaskInstance;
import org.apache.wicket.markup.html.WebPage;

public class SingularServerTaskPageStrategy implements ITaskPageStrategy {

    private SingularWebRef webRef;

    public SingularServerTaskPageStrategy(Class<? extends WebPage> page) {
        this.webRef = new SingularWebRef(page);
    }

    public SingularServerTaskPageStrategy() {

    }

    public static final SingularServerTaskPageStrategy of(Class<? extends WebPage> page) {
        return new SingularServerTaskPageStrategy(page);
    }

    @Override
    public SingularWebRef getPageFor(TaskInstance taskInstance, MUser user) {
        return webRef;
    }

}
