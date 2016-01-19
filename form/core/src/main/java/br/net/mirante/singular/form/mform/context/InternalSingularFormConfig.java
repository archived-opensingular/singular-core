package br.net.mirante.singular.form.mform.context;

import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.document.ServiceRegistry;

import java.util.Map;

/**
 * Interface de uso interno para acessar os valores configurados no SingularFormConfig
 * @param <T>
 * @param <K>
 */
public interface InternalSingularFormConfig<T  extends UIBuilder<K>, K extends UIComponentMapper>  {


    public ServiceRegistry getServiceRegistry();

    public  Map<Class<? extends MTipo>, Class<K>> getCustomMappers();

}
