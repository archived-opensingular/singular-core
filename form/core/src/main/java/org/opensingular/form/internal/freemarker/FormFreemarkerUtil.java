/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.internal.freemarker;

import freemarker.template.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.*;
import org.opensingular.form.calculation.SimpleValueCalculation;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.type.core.*;
import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.lib.commons.context.SingularContext;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
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

    private Configuration cfgIgnoreError;
    private Configuration cfgRethrowError;

    private Consumer<Configuration> configurationConsumer = null;

    private FormFreemarkerUtil() {
    }

    public static FormFreemarkerUtil get() {
        return ((SingularSingletonStrategy) SingularContext.get()).singletonize(FormFreemarkerUtil.class, FormFreemarkerUtil::new);
    }

    public static FormFreemarkerUtil get(Consumer<Configuration> configurationConsumer) {
        FormFreemarkerUtil formFreemarkerUtil = new FormFreemarkerUtil();
        formFreemarkerUtil.configurationConsumer = configurationConsumer;
        return formFreemarkerUtil;
    }

    public SimpleValueCalculation<String> createInstanceCalculation(String stringTemplate) {
        return context -> merge(context.instance(), stringTemplate);
    }

    public SimpleValueCalculation<String> createInstanceCalculation(String stringTemplate, boolean escapeContentHtml, boolean ignoreError) {
        return context -> merge(context.instance(), stringTemplate, escapeContentHtml, ignoreError);
    }

    /**
     * Gera uma string resultante do merge do template com os dados contídos no
     * documento informado. É o mesmo que merge(document.getRoot(),
     * templateString).
     */
    public String merge(SDocument document, String templateString) {
        return merge(document.getRoot(), templateString);
    }

    /**
     * Gera uma string resultante do merge do template com os dados contídos na
     * instancia informada.
     */
    public String merge(SInstance dados, String templateString) {
        return internalMerge(dados, templateString, false, false);
    }

    /**
     * Gera uma string resultante do merge do template com os dados contídos na
     * instancia informada podendo ignorar os erros.
     */
    public String merge(SInstance dados, String templateString, boolean escapeContentHtml, boolean ignoreError) {
        return internalMerge(dados, templateString, escapeContentHtml, ignoreError);
    }

    private String internalMerge(SInstance dados, String templateString, boolean escapeContentHtml, boolean ignoreError) {
        return merge(dados, escapeContentHtml, parseTemplate(templateString, ignoreError));
    }

    public String merge(SInstance dados, boolean escapeContentHtml, Template template) {
        StringWriter out = new StringWriter();
        try {
            template.process(dados, out, new FormObjectWrapper(escapeContentHtml));
        } catch (TemplateException | IOException e) {
            throw new SingularFormException("Erro mesclando dados da instancia com o template: " + template, e);
        }
        return out.toString();
    }

    private Template parseTemplate(String template, boolean ignoreError) {
        try {
            TemplateExceptionHandler result;
            String ignoreProperty = SingularProperties.get().getProperty(SingularProperties.FREEMARKER_IGNORE_ERROR);
            if (ignoreProperty != null) {
                if ("true".equalsIgnoreCase(ignoreProperty)) {
                    result = TemplateExceptionHandler.IGNORE_HANDLER;
                } else {
                    result = TemplateExceptionHandler.RETHROW_HANDLER;
                }
            } else {
                if (ignoreError) {
                    result = TemplateExceptionHandler.IGNORE_HANDLER;
                } else {
                    result = TemplateExceptionHandler.RETHROW_HANDLER;
                }

            }
            return new Template("templateStringParameter", template, getConfiguration(result));
        } catch (IOException e) {
            throw new SingularFormException("Erro fazendo parse do template: " + template, e);
        }
    }

    private Configuration getConfiguration(TemplateExceptionHandler exceptionHandler) {

        Configuration c = null;
        if (exceptionHandler != null) {
            if (exceptionHandler == TemplateExceptionHandler.IGNORE_HANDLER) {
                if (cfgIgnoreError == null) {
                    cfgIgnoreError = newConfiguration(exceptionHandler);
                }
                c = cfgIgnoreError;
            } else {
                if (cfgRethrowError == null) {
                    cfgRethrowError = newConfiguration(exceptionHandler);
                }
                c = cfgRethrowError;
            }
        }
        return c;
    }

    private Configuration newConfiguration(TemplateExceptionHandler exceptionHandler) {
        Configuration novo = new Configuration(Configuration.VERSION_2_3_22);
        novo.setDefaultEncoding(StandardCharsets.UTF_8.name());
        novo.setLocale(new Locale("pt", "BR"));
        novo.setTemplateExceptionHandler(exceptionHandler);
        return novo;
    }

    private TemplateModel toTemplateModel(Object obj, boolean escapeContentHtml) {
        if (obj == null) {
            return null;
        } else if (obj instanceof SISimple) {
            return toTemplateModelSimple((SISimple) obj, escapeContentHtml);
        } else if (obj instanceof SIComposite) {
            return new SICompositeTemplateModel((SIComposite) obj, escapeContentHtml);
        } else if (obj instanceof SIList) {
            return new SListTemplateModel((SIList<?>) obj, escapeContentHtml);
        }
        String msg = "A classe " + obj.getClass().getName() + " não é suportada para mapeamento no template";
        if (obj instanceof SInstance) {
            throw new SingularFormException(msg, (SInstance) obj);
        }
        throw new SingularFormException(msg);
    }

    private TemplateModel toTemplateModelSimple(SISimple obj, boolean escapeContentHtml) {
        if (obj != null && obj.getValue() == null) { // && !(obj instanceof SIString)
            return null;//nullModel
        }

        if (obj instanceof SIString) {
            return new SSimpleTemplateModel(obj, escapeContentHtml);
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
        return new SSimpleTemplateModel(obj, escapeContentHtml);
    }

    private class FormObjectWrapper implements ObjectWrapper {

        private boolean escapeContentHtml;

        public FormObjectWrapper(boolean escapeContentHtml) {
            this.escapeContentHtml = escapeContentHtml;
        }

        @Override
        public TemplateModel wrap(Object obj) throws TemplateModelException {
            return toTemplateModel(obj, escapeContentHtml);
        }
    }

    private abstract class SInstanceMethodTemplate<INSTANCE extends SInstance> implements TemplateMethodModelEx {
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

    private class SInstanceZeroArgumentMethodTemplate<INSTANCE extends SInstance> extends SInstanceMethodTemplate<INSTANCE> {

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

    private abstract class SInstanceTemplateModel<INSTANCE extends SInstance> implements TemplateScalarModel, TemplateHashModel {
        private final INSTANCE instance;
        private boolean invertedPriority;
        protected boolean escapeContentHtml;

        public SInstanceTemplateModel(INSTANCE instance) {
            this.instance = instance;
        }

        public SInstanceTemplateModel(INSTANCE instance, boolean esccapeContentHtml) {
            this.instance = instance;
            escapeContentHtml = esccapeContentHtml;
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
                return new SInstanceZeroArgumentMethodTemplate<>(getInstance(), key, SInstance::toStringDisplayDefault);
            } else if ("value".equals(key) || "getValue".equals(key)) {
                return new SInstanceZeroArgumentMethodTemplate<>(getInstance(), key, i -> getValue());
            } else if ("_inst".equals(key)) {
                Optional<Constructor<?>> constructor = Arrays.stream(getClass().getConstructors())
                        .filter(c -> c.getParameterCount() == 2 && c.getParameterTypes()[0].isAssignableFrom(getInstance().getClass()))
                        .findFirst();
                if (!constructor.isPresent()) {
                    throw new SingularFormException(
                            "Não foi encontrado o construtor " + getClass().getSimpleName() + "(SInstance)");
                }
                SInstanceTemplateModel<INSTANCE> newSelf;
                try {
                    newSelf = (SInstanceTemplateModel<INSTANCE>) constructor.get().newInstance(getInstance(), escapeContentHtml);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new SingularFormException("Erro instanciado _inst", e);
                }
                newSelf.invertedPriority = true;
                return newSelf;
            } else if ("toStringDisplay".equals(key)) {
                return new SInstanceZeroArgumentMethodTemplate<>(getInstance(), key, SInstance::toStringDisplay);
            }
            return null;
        }

        @Override
        public boolean isEmpty() throws TemplateModelException {
            return false;
        }
    }

    private class SSimpleTemplateModel<INSTANCE extends SISimple<?>> extends SInstanceTemplateModel<INSTANCE>
            implements TemplateScalarModel {


        public SSimpleTemplateModel(INSTANCE instance) {
            super(instance, false);
        }

        public SSimpleTemplateModel(INSTANCE instance, boolean escapeContentHtml) {
            super(instance, escapeContentHtml);
        }

        @Override
        public String getAsString() throws TemplateModelException {
            if (escapeContentHtml) {
                return StringUtils.defaultString(StringEscapeUtils.escapeHtml4(getInstance().toStringDisplayDefault()));
            } else {
                return StringUtils.defaultString(getInstance().toStringDisplayDefault());
            }
        }
    }

    private class SNumberTemplateModel<INSTANCE extends SINumber<?>> extends SSimpleTemplateModel<INSTANCE>
            implements TemplateNumberModel {

        public SNumberTemplateModel(INSTANCE instance) {
            super(instance, false);
        }

        public SNumberTemplateModel(INSTANCE instance, boolean escapeContentHtml) {
            super(instance, escapeContentHtml);
        }

        @Override
        public Number getAsNumber() throws TemplateModelException {
            return getInstance().getValue();
        }

    }

    private class SIBooleanTemplateModel extends SSimpleTemplateModel<SIBoolean> implements TemplateBooleanModel {

        public SIBooleanTemplateModel(SIBoolean instance) {
            super(instance, false);
        }

        public SIBooleanTemplateModel(SIBoolean instance, boolean escapeContentHtml) {
            super(instance, escapeContentHtml);
        }

        @Override
        public boolean getAsBoolean() throws TemplateModelException {
            Boolean v = getInstance().getValueWithDefault();
            return v == null ? false : v;
        }
    }

    private class SIDateTemplateModel extends SSimpleTemplateModel<SIDate> implements TemplateDateModel {

        public SIDateTemplateModel(SIDate instance) {
            super(instance, false);
        }

        public SIDateTemplateModel(SIDate instance, boolean escapeContentHtml) {
            super(instance, escapeContentHtml);
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

    private class SIDateTimeTemplateModel extends SSimpleTemplateModel<SIDateTime> implements TemplateDateModel {

        public SIDateTimeTemplateModel(SIDateTime instance) {
            super(instance, false);
        }

        public SIDateTimeTemplateModel(SIDateTime instance, boolean escapeContentHtml) {
            super(instance, escapeContentHtml);
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

    private class SITimeTemplateModel extends SSimpleTemplateModel<SITime> implements TemplateDateModel {

        public SITimeTemplateModel(SITime instance) {
            super(instance, false);
        }

        public SITimeTemplateModel(SITime instance, boolean escapeContentHtml) {
            super(instance, escapeContentHtml);
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

    private class SListTemplateModel extends SInstanceTemplateModel<SIList<?>> implements TemplateSequenceModel {


        public SListTemplateModel(SIList<?> list) {
            super(list, false);
        }

        public SListTemplateModel(SIList<?> list, boolean escapeContentHtml) {
            super(list, escapeContentHtml);
        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            return toTemplateModel(getInstance().get(index), escapeContentHtml);
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

    private class SICompositeTemplateModel extends SInstanceTemplateModel<SIComposite> {

        public SICompositeTemplateModel(SIComposite composite) {
            super(composite, false);
        }

        public SICompositeTemplateModel(SIComposite composite, boolean escapeContentHtml) {
            super(composite, escapeContentHtml);
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

        private TemplateModel getTemplateFromField(String key) {
            return getInstance().getFieldOpt(key).map(instance -> toTemplateModel(instance, escapeContentHtml)).orElse(null);
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
            return new SInstanceCollectionTemplateModel((Collection<SInstance>) getInstance().getValue(), escapeContentHtml);
        }
    }

    private class SInstanceCollectionTemplateModel implements TemplateCollectionModel {
        private final Collection<SInstance> collection;
        private boolean escapeContentHtml;

        public SInstanceCollectionTemplateModel(Collection<SInstance> collection, boolean escapeContentHtml) {
            this.collection = collection;
            this.escapeContentHtml = escapeContentHtml;
        }

        @Override
        public TemplateModelIterator iterator() throws TemplateModelException {
            Iterator<SInstance> it = collection.iterator();
            return new TemplateModelIterator() {

                @Override
                public TemplateModel next() {
                    return toTemplateModel(it.next(), escapeContentHtml);
                }

                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }
            };
        }
    }
}
