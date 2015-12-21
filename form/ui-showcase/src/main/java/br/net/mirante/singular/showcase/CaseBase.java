package br.net.mirante.singular.showcase;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.view.page.showcase.ItemCasePanel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Representa um exemplo de um componente ou solução junto com os respectivo
 * códigos e explicações.
 */
public class CaseBase implements Serializable {

    private final String componentName;
    private final String subCaseName;
    private String descriptionHtml;
    private final List<ItemCasePanel.ItemCaseButton> botoes = new ArrayList<>();
    private final List<ResourceRef> aditionalSources = new ArrayList<>();

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
        return getDescriptionResourceName().map(ResourceRef::getContent);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends MPacote> getPackage() {
        String target = getClass().getName() + "Package";
        try {
            Class<?> c = getClass().getClassLoader().loadClass(target);
            if (!MPacote.class.isAssignableFrom(c)) {
                throw new RuntimeException(target + " não extende " + MPacote.class.getName());
            }
            return (Class<? extends MPacote>) c;
        } catch (ClassNotFoundException e) {
            throw new SingularFormException("É esperado uma classe com o nome " + target + " como complemento de " + getClass().getName(),
                    e);
        }
    }

    public MTipo<?> getCaseType() {
        MDicionario dicionario = MDicionario.create();
        MPacote p = dicionario.carregarPacote(getPackage());

        return p.getTipoLocalOpcional("testForm")
                .orElseThrow(() -> new SingularFormException("O pacote " + p.getNome() + " não define o tipo para exibição 'testForm'"));
    }

    public Optional<ResourceRef> getDescriptionResourceName() {
        return ResourceRef.forClassWithExtension(getClass(), "html");
    }

    public Optional<ResourceRef> getMainSourceResourceName() {
        return ResourceRef.forSource(getPackage());
    }

    public List<ResourceRef> getAditionalSources() {
        return aditionalSources;
    }

    public List<ItemCasePanel.ItemCaseButton> getBotoes() {
        return botoes;
    }

    public boolean showValidateButton(){
        return getCaseType().hasAnyValidation();
    }
}
