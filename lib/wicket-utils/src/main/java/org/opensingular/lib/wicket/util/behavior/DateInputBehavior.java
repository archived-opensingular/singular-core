package org.opensingular.lib.wicket.util.behavior;

import org.apache.commons.collections.map.SingletonMap;
import org.apache.wicket.util.template.PackageTextTemplate;

public class DateInputBehavior {

    private final PackageTextTemplate initScript = new PackageTextTemplate(DateInputBehavior.class, "DateInputBehavior.js");
    private String componentDate;

    public DateInputBehavior(String componentDate) {
        this.componentDate = componentDate;
    }

    public String generateScript() {
        return initScript.asString(new SingletonMap("inputDate", componentDate));
    }
}
