package org.opensingular.form.internal.freemarker;

import freemarker.template.TemplateModelException;
import org.opensingular.form.SInstance;

import java.util.List;
import java.util.function.Function;

public class SInstanceZeroArgumentMethodTemplate<INSTANCE extends SInstance> extends SInstanceMethodTemplate<INSTANCE> {

    private final Function<INSTANCE, Object> function;

    public SInstanceZeroArgumentMethodTemplate(INSTANCE instance, String methodName, Function<INSTANCE, Object> function) {
        super(instance, methodName);
        this.function = function;
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        checkNumberOfArguments(arguments, 0);
        return function.apply(getInstance());
    }
}