package br.net.mirante.singular.server.commons.spring;

import org.springframework.context.annotation.Bean;

import br.net.mirante.singular.flow.core.service.IUserService;
import br.net.mirante.singular.form.document.SDocument;
import br.net.mirante.singular.form.persistence.dao.FormDAO;
import br.net.mirante.singular.form.service.FormService;
import br.net.mirante.singular.form.service.IFormService;
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

    @Bean
    public IFormService formService() {
        return new FormService();
    }

    @Bean
    public FormDAO formDAO() {
        return new FormDAO();
    }

    @Bean
    public IUserService userService() {
        return new SingularDefaultUserService();
    }


}
