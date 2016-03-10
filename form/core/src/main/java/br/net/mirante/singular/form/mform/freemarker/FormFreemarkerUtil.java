package br.net.mirante.singular.form.mform.freemarker;

import java.io.IOException;
import java.io.StringWriter;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SISimple;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.document.SDocument;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;

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
            return new Template("interno", template, getConfiguration());
        } catch (IOException e) {
            throw new SingularFormException("Erro fazendo parse do template: " + template, e);
        }
    }

    private static Configuration getConfiguration() {
        if (cfg == null) {
            Configuration novo = new Configuration(Configuration.VERSION_2_3_22);
            novo.setDefaultEncoding("UTF-8");
            novo.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            cfg = novo;
        }
        return cfg;
    }

    private static TemplateModel toTemplateModel(Object obj) throws TemplateModelException {
        if (obj == null) {
            return null;
        } else if (obj instanceof SISimple) {
            if (((SISimple<?>) obj).isNull()) {
                return null;
            }
            return new SimpleScalar(((SISimple<?>) obj).getDisplayString());
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

    private static class SListTemplateModel implements TemplateSequenceModel {
        private final SIList<?> list;

        public SListTemplateModel(SIList<?> list) {
            this.list = list;

        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            return toTemplateModel(list.get(index));
        }

        @Override
        public int size() throws TemplateModelException {
            return list.size();
        }
    }

    private static class SICompositeTemplateModel implements TemplateHashModel {
        private final SIComposite composite;

        public SICompositeTemplateModel(SIComposite composite) {
            this.composite = composite;
        }

        @Override
        public TemplateModel get(String key) throws TemplateModelException {
            SInstance campo = composite.getCampo(key);
            if (campo == null) {
                return null;
            }
            return toTemplateModel(composite.getCampo(key));
        }

        @Override
        public boolean isEmpty() throws TemplateModelException {
            return composite.isEmptyOfData();
        }
    }
}
