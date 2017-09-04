package org.opensingular.form.internal.freemarker;

import freemarker.template.TemplateMethodModelEx;
import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;

import java.util.List;

public abstract class SInstanceMethodTemplate<INSTANCE extends SInstance> implements TemplateMethodModelEx {
    private final INSTANCE instance;
    private final String methodName;

    public SInstanceMethodTemplate(INSTANCE instance, String methodName) {
        this.instance = instance;
        this.methodName = methodName;
    }

    protected INSTANCE getInstance() {
        return instance;
    }

    protected void checkNumberOfArguments(List<?> arguments, int expected) {
        if (expected != arguments.size()) {
            throw new SingularFormException("A chamada do método '" + methodName + "'() em " + getInstance().getPathFull()
                    + "deveria ter " + expected + " argumentos, mas foi feito com " + arguments + " argumentos.");
        }
    }
}