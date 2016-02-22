package br.net.mirante.singular.form.spring;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.NamedBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.RefTypeByKey;
import br.net.mirante.singular.form.mform.document.TypeLoader;

/**
 * Loader de dicionário baseado no Spring. Espera que o mesmo será um bean do
 * Spring. Com isso cria referências ({@link #createDictionaryRef(Serializable)}
 * ) serializáveis mediante uso do nome do bean no Spring como forma de
 * recuperar o loader a partir da referência ao ser deserialziada.
 *
 * @author Daniel C. Bordin
 */
public abstract class SpringTypeLoader<KEY extends Serializable> extends TypeLoader<KEY>
        implements ApplicationContextAware, BeanNameAware, NamedBean {

    private String springBeanName;

    @Override
    protected final Optional<RefType> loadRefTypeImpl(KEY typeId) {
        Optional<SType<?>> type = loadType(typeId);
        if (type == null) {
            throw new SingularFormException(getClass().getName() + ".loadType(KEY) retornou null em vez de um Optional");
        }
        return type.map(t -> new SpringRefType(SpringFormUtil.checkBeanName(this), typeId, t));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringFormUtil.setApplicationContext(applicationContext);
    }

    @Override
    public void setBeanName(String springBeanName) {
        this.springBeanName = springBeanName;
    }

    @Override
    public String getBeanName() {
        return springBeanName;
    }

    final static class SpringRefType<KEY extends Serializable> extends RefTypeByKey<KEY> {

        private final String springBeanName;

        private SpringRefType(String springBeanName, KEY typeId, SType<?> type) {
            super(typeId, type);
            this.springBeanName = springBeanName;
        }

        @Override
        public SType<?> retrieveByKey(KEY typeId) {
            SpringTypeLoader<KEY> loader = SpringFormUtil.getApplicationContext().getBean(springBeanName, SpringTypeLoader.class);
            if (loader == null) {
                throw new SingularFormException(
                        "Não foi encontrado o bean de nome '" + springBeanName + "' do tipo " + SpringTypeLoader.class.getName());
            }
            return loader.loadType(typeId).orElseThrow(() -> new SingularFormException(
                    SpringFormUtil.erroMsg(loader, " não encontrou o " + SType.class.getSimpleName() + " para o id=" + typeId)));
        }

    }
}
