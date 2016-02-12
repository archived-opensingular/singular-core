package br.net.mirante.singular.pet.module.wicket;

import br.net.mirante.singular.util.wicket.page.error.Error403Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.request.component.IRequestableComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utiliza a lista de pacotes informados como exclusivos de peticionamento ou de análise
 * para validar se a página acessada pertence ao contexto de análise ou de peticionamento.
 */
public class SingularAuthorizationStrategy extends IAuthorizationStrategy.AllowAllAuthorizationStrategy {


    private List<String> packagesDisallowed = new ArrayList<>();

    /**
     * @param
     * Packages cujos componentes não podem ser instanciados por essa aplicação
     */
    public SingularAuthorizationStrategy(String[] packagesDisallowed) {
        this.packagesDisallowed.addAll(Arrays.asList(packagesDisallowed));
    }

    @Override
    public <T extends IRequestableComponent> boolean isInstantiationAuthorized(Class<T> componentClass) {
        if(!matchAnyPackage(componentClass.getPackage().getName(), packagesDisallowed)){
           return true;
        }
        throw new RestartResponseAtInterceptPageException(new Error403Page());
    }

    private boolean matchAnyPackage(String pckg, List<String> packages) {
        for (String p : packages) {
            if (pckg.startsWith(p)) {
                return true;
            }
        }
        return false;
    }
}
