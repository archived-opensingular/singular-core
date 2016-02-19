package br.net.mirante.singular.showcase.component;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.showcase.view.page.ItemCasePanel;

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

    private transient SType<?> caseType;
    
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
    private Class<? extends SPackage> getPackage() {
        String target = getClass().getName() + "Package";
        try {
            Class<?> c = getClass().getClassLoader().loadClass(target);
            if (!SPackage.class.isAssignableFrom(c)) {
                throw new RuntimeException(target + " não extende " + SPackage.class.getName());
            }
            return (Class<? extends SPackage>) c;
        } catch (ClassNotFoundException e) {
            throw new SingularFormException("É esperado uma classe com o nome " + target + " como complemento de " + getClass().getName(),
                    e);
        }
    }

    public String getTypeName() {
        return getPackage().getName() + ".testForm"; 
    }
    
    public SType<?> getCaseType() {
        if(caseType == null){
            SDictionary dicionario = SDictionary.create();
            SPackage p = dicionario.loadPackage(getPackage());
            
            caseType = p.getLocalTypeOptional("testForm")
                .orElseThrow(() -> new SingularFormException("O pacote " + p.getName() + " não define o tipo para exibição 'testForm'"));
        }
        return caseType;
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

    public boolean annotationEnabled() { return false;}
}
