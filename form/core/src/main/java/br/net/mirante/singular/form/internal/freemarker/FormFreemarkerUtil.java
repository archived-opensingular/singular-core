/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.internal.freemarker;

import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.calculation.CalculationContext;
import br.net.mirante.singular.form.calculation.SimpleValueCalculation;
import br.net.mirante.singular.form.document.SDocument;
import br.net.mirante.singular.form.type.core.*;
import freemarker.template.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;

/**
 * Integra o Singular Form com o Freemarker
 * <a href="http://freemarker.incubator.apache.org">http://freemarker.incubator.
 * apache.org</a> permitindo fazer o merge de template do freemaker com os dados
 * de uma instância do formulário.
 *
 * @author Daniel C. Bordin
 */
public final class FormFreemarkerUtil {

    private static Configuration cfg;
    private static FormObjectWrapper wrapper;

    public static SimpleValueCalculation<String> createInstanceCalculation(String stringTemplate) {
        return new SimpleValueCalculation<String>() {
            @Override
            public String calculate(CalculationContext context) {
                return merge(context.instance(), stringTemplate);
            }
        };
    }

    /**
     * Gera uma string resultante do merge do template com os dados contídos no
     * documento informado. É o mesmo que merge(document.getRoot(),
     * templateString).
     */
    public static String merge(SDocument document, String templateString) {
        return merge(document.getRoot(), templateString);
    }

    /**
     * Gera uma string resultante do merge do template com os dados contídos na
     * instancia informada.
     */
    public static String merge(SInstance dados, String templateString) {
        Template template = parseTemplate(templateString);
        StringWriter out = new StringWriter();
        try {
            template.process(dados, out, new FormObjectWrapper());
        } catch (TemplateException | IOException e) {
            throw new SingularFormException("Erro mesclando dados da instancia com o template: " + template, e);
        }
        return out.toString();
    }

    private static Template parseTemplate(String template) {
        try {
            return new Template("templateStringParameter", template, getConfiguration());
        } catch (IOException e) {
            throw new SingularFormException("Erro fazendo parse do template: " + template, e);
        }
    }

    private static Configuration getConfiguration() {
        if (cfg == null) {
            Configuration novo = new Configuration(Configuration.VERSION_2_3_22);
            novo.setDefaultEncoding("UTF-8");
            novo.setLocale(new Locale("pt", "BR"));
            novo.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            cfg = novo;
        }
        return cfg;
    }

    private static TemplateModel toTemplateModel(Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof SISimple) {
            if (obj instanceof SIString) {
                return new SSimpleTemplateModel((SISimple<?>) obj);
            } else if (obj instanceof SINumber) {
                return new SNumberTemplateModel<>((SINumber<?>) obj);
            } else if (obj instanceof SIBoolean) {
                return new SIBooleanTemplateModel((SIBoolean) obj);
            } else if (obj instanceof SIDate) {
                return new SIDateTemplateModel((SIDate) obj);
            } else if (obj instanceof SIDateTime) {
                return new SIDateTimeTemplateModel((SIDateTime) obj);
            } else if (obj instanceof SITime) {
                return new SITimeTemplateModel((SITime) obj);
            }
            return new SSimpleTemplateModel((SISimple<?>) obj);
        } else if (obj instanceof SIComposite) {
            return new SICompositeTemplateModel((SIComposite) obj);
        } else if (obj instanceof SIList) {
            return new SListTemplateModel((SIList<?>) obj);
        }
        String msg = "A classe " + obj.getClass().getName() + " não é suportada para mapeamento no template";
        if (obj instanceof SInstance) {
            throw new SingularFormException(msg, (SInstance) obj);
        }
        throw new SingularFormException(msg);
    }

    private static class FormObjectWrapper implements ObjectWrapper {

        @Override
        public TemplateModel wrap(Object obj) throws TemplateModelException {
            return toTemplateModel(obj);
        }
    }

    private static abstract class SInstanceMethodTemplate<INSTANCE extends SInstance> implements TemplateMethodModelEx {
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

    private static class SInstanceZeroArgumentMethodTemplate<INSTANCE extends SInstance> extends SInstanceMethodTemplate<INSTANCE> {

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

    private static abstract class SInstanceTemplateModel<INSTANCE extends SInstance> implements TemplateScalarModel, TemplateHashModel {
        private final INSTANCE instance;
        private boolean invertedPriority;

        public SInstanceTemplateModel(INSTANCE instance) {
            this.instance = instance;
        }

        protected INSTANCE getInstance() {
            return instance;
        }

        protected boolean isInvertedPriority() {
            return invertedPriority;
        }

        protected Object getValue() {
            return instance.getValue();
        }

        @Override
        public TemplateModel get(String key) throws TemplateModelException {
            if ("toStringDisplayDefault".equals(key)) {
                return new SInstanceZeroArgumentMethodTemplate<INSTANCE>(getInstance(), key, i -> i.toStringDisplayDefault());
            } else if ("value".equals(key) || "getValue".equals(key)) {
                return new SInstanceZeroArgumentMethodTemplate<INSTANCE>(getInstance(), key, i -> getValue());
            } else if ("_inst".equals(key)) {
                Optional<Constructor<?>> constructor = Arrays.stream(getClass().getConstructors())
                        .filter(c -> c.getParameterCount() == 1 && c.getParameterTypes()[0].isAssignableFrom(getInstance().getClass()))
                        .findFirst();
                SInstanceTemplateModel<INSTANCE> newSelf;
                try {
                    newSelf = (SInstanceTemplateModel<INSTANCE>) constructor.get().newInstance(getInstance());
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new SingularFormException("Erro instanciado _inst", e);
                }
                newSelf.invertedPriority = true;
                return newSelf;
            } else if ("toStringDisplay".equals(key)) {
                return new SInstanceZeroArgumentMethodTemplate<INSTANCE>(getInstance(), key, i -> i.toStringDisplay());
            }
            return null;
        }

        @Override
        public boolean isEmpty() throws TemplateModelException {
            return false;
        }
    }

    private static class SSimpleTemplateModel<INSTANCE extends SISimple<?>> extends SInstanceTemplateModel<INSTANCE>
            implements TemplateScalarModel {

        public SSimpleTemplateModel(INSTANCE instance) {
            super(instance);
        }

        @Override
        public String getAsString() throws TemplateModelException {
            return StringUtils.defaultString(getInstance().toStringDisplayDefault());
        }
    }

    private static class SNumberTemplateModel<INSTANCE extends SINumber<?>> extends SSimpleTemplateModel<INSTANCE>
            implements TemplateNumberModel {

        public SNumberTemplateModel(INSTANCE instance) {
            super(instance);
        }

        @Override
        public Number getAsNumber() throws TemplateModelException {
            return (Number) getInstance().getValue();
        }

    }

    private static class SIBooleanTemplateModel extends SSimpleTemplateModel<SIBoolean> implements TemplateBooleanModel {

        public SIBooleanTemplateModel(SIBoolean instance) {
            super(instance);
        }

        @Override
        public boolean getAsBoolean() throws TemplateModelException {
            Boolean v = getInstance().getValueWithDefault();
            return v == null ? false : v;
        }
    }

    private static class SIDateTemplateModel extends SSimpleTemplateModel<SIDate> implements TemplateDateModel {

        public SIDateTemplateModel(SIDate instance) {
            super(instance);
        }

        @Override
        public Date getAsDate() throws TemplateModelException {
            return getInstance().getValue();
        }

        @Override
        public int getDateType() {
            return DATE;
        }
    }

    private static class SIDateTimeTemplateModel extends SSimpleTemplateModel<SIDateTime> implements TemplateDateModel {

        public SIDateTimeTemplateModel(SIDateTime instance) {
            super(instance);
        }

        @Override
        public Date getAsDate() throws TemplateModelException {
            return getInstance().getValue();
        }

        @Override
        public int getDateType() {
            return DATETIME;
        }
    }

    private static class SITimeTemplateModel extends SSimpleTemplateModel<SITime> implements TemplateDateModel {

        public SITimeTemplateModel(SITime instance) {
            super(instance);
        }

        @Override
        public Date getAsDate() throws TemplateModelException {
            return getInstance().getValue();
        }

        @Override
        public int getDateType() {
            return TIME;
        }
    }

    private static class SListTemplateModel extends SInstanceTemplateModel<SIList<?>> implements TemplateSequenceModel {

        public SListTemplateModel(SIList<?> list) {
            super(list);

        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            return toTemplateModel(getInstance().get(index));
        }

        @Override
        public int size() throws TemplateModelException {
            return getInstance().size();
        }

        @Override
        public String getAsString() throws TemplateModelException {
            return StringUtils.defaultString(getInstance().toStringDisplay());
        }
    }

    private static class SICompositeTemplateModel extends SInstanceTemplateModel<SIComposite> {

        public SICompositeTemplateModel(SIComposite composite) {
            super(composite);
        }

        @Override
        public TemplateModel get(String key) throws TemplateModelException {
            TemplateModel model;
            if (isInvertedPriority()) {
                model = super.get(key);
                if (model == null) {
                    model = getTemplateFromField(key);
                }
            } else {
                model = getTemplateFromField(key);
                if (model == null) {
                    model = super.get(key);
                }
            }
            return model;
        }

        private TemplateModel getTemplateFromField(String key) throws TemplateModelException {
            return getInstance().getFieldOpt(key).map(instance -> toTemplateModel(instance)).orElse(null);
        }

        @Override
        public boolean isEmpty() throws TemplateModelException {
            return getInstance().isEmptyOfData();
        }

        @Override
        public String getAsString() throws TemplateModelException {
            return StringUtils.defaultString(getInstance().toStringDisplay());
        }

        @Override
        protected Object getValue() {
            return new SInstanceCollectionTemplateModel((Collection<SInstance>) getInstance().getValue());
        }
    }

    private static class SInstanceCollectionTemplateModel implements TemplateCollectionModel {
        private final Collection<SInstance> collection;

        public SInstanceCollectionTemplateModel(Collection<SInstance> collection) {
            this.collection = collection;
        }

        @Override
        public TemplateModelIterator iterator() throws TemplateModelException {
            Iterator<SInstance> it = collection.iterator();
            return new TemplateModelIterator() {

                @Override
                public TemplateModel next() throws TemplateModelException {
                    return toTemplateModel(it.next());
                }

                @Override
                public boolean hasNext() throws TemplateModelException {
                    return it.hasNext();
                }
            };
        }
    }
}
