package br.net.mirante.singular.showcase;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Throwables;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.PacoteBuilder;

/**
 * Representa um exemplo de um componente ou solução junto com os respectivo
 * códigos e explicações.
 */
public class CaseBase {

    private final String componentName;
    private final String subCaseName;
    private String descriptionHtml;

    public CaseBase(String componentName) {
        this(componentName, null);
    }

    public CaseBase(String componentName, String subCaseName) {
        this.componentName = componentName;
        this.subCaseName = subCaseName;
    }

    public String getComponentName() {
        return componentName;
    }

    public String getSubCaseName() {
        return subCaseName;
    }

    public void setDescriptionHtml(String descriptionHtml) {
        this.descriptionHtml = descriptionHtml;
    }

    public Optional<String> getDescriptionHtml() {
        if (descriptionHtml != null) {
            return Optional.of(descriptionHtml);
        }
        return getDescriptionResourceName().map(ref -> ref.getContent());
    }

    @SuppressWarnings("unchecked")
    private Class<? extends CaseCode> getMainClass() {
        String target = getClass().getName() + "Code";
        try {
            Class<?> c = getClass().getClassLoader().loadClass(target);
            if (!CaseCode.class.isAssignableFrom(c)) {
                throw new RuntimeException(target + " não extende " + CaseCode.class.getName());
            }
            return (Class<? extends CaseCode>) c;
        } catch (ClassNotFoundException e) {
            throw Throwables.propagate(e);
        }
    }

    public MTipo<?> getCaseType() {
        MDicionario dicionario = MDicionario.create();

        PacoteBuilder pb = dicionario.criarNovoPacote(getClass().getName());

        try {
            return getMainClass().newInstance().createForm(pb);
        } catch (InstantiationException | IllegalAccessException e) {
            throw Throwables.propagate(e);
        }
    }

    public Optional<ResourceRef> getDescriptionResourceName() {
        return ResourceRef.forClassWithExtension(getClass(), "html");
    }

    public Optional<ResourceRef> getMainSourceResourceName() {
        return ResourceRef.forSource(getMainClass());
    }

    public List<ResourceRef> getAditionalSources() {
        return Collections.emptyList();
    }
}
