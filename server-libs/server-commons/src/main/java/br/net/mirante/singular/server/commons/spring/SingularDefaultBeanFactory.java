package br.net.mirante.singular.server.commons.spring;

import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.server.commons.persistence.dao.flow.ActorDAO;
import br.net.mirante.singular.server.commons.persistence.dao.flow.GrupoProcessoDAO;
import br.net.mirante.singular.server.commons.persistence.dao.flow.TaskInstanceDAO;
import br.net.mirante.singular.server.commons.persistence.dao.form.FileDao;
import br.net.mirante.singular.server.commons.persistence.dao.form.PetitionDAO;
import br.net.mirante.singular.server.commons.persistence.dto.TaskInstanceDTO;
import br.net.mirante.singular.server.commons.persistence.entity.form.AbstractPetitionEntity;
import br.net.mirante.singular.server.commons.service.AnalisePeticaoService;
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.server.commons.spring.security.DefaultUserDetailService;
import br.net.mirante.singular.server.commons.spring.security.SingularUserDetailsService;
import br.net.mirante.singular.server.commons.ws.ServiceFactoryUtil;
import org.springframework.context.annotation.Bean;

public class SingularDefaultBeanFactory {

    @Bean(name = "peticionamentoUserDetailService")
    public SingularUserDetailsService worklistUserDetailServiceFactory() {
        return new DefaultUserDetailService();
    }

    @Bean
    public <T extends AbstractPetitionEntity> PetitionDAO<T> peticaoDAO() {
        return new PetitionDAO<T>();
    }


    @Bean
    public <T extends AbstractPetitionEntity> PetitionService<T> worklistPetitionServiceFactory() {
        return new PetitionService<T>();
    }

    @Bean
    public AnalisePeticaoService<TaskInstanceDTO> analisePeticaoService() {
        return new AnalisePeticaoService<>();
    }

    @Bean
    public TaskInstanceDAO taskInstanceDAO() {
        return new TaskInstanceDAO();
    }

    @Bean
    public ServiceFactoryUtil serviceFactoryUtil() {
        return new ServiceFactoryUtil();
    }

    @Bean
    public ActorDAO actorDAO() {
        return new ActorDAO();
    }

    @Bean
    public GrupoProcessoDAO grupoProcessoDAO() {
        return new GrupoProcessoDAO();
    }

    @Bean(name = SDocument.FILE_PERSISTENCE_SERVICE)
    public FileDao fileDao() {
        return new FileDao();
    }


}