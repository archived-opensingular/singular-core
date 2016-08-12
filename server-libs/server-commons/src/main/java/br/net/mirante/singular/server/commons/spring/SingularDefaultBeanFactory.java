package br.net.mirante.singular.server.commons.spring;

import br.net.mirante.singular.form.persistence.dao.FormAnnotationDAO;
import br.net.mirante.singular.form.persistence.dao.FormAnnotationVersionDAO;
import br.net.mirante.singular.form.persistence.dao.FormTypeDAO;
import br.net.mirante.singular.form.persistence.dao.FormVersionDAO;
import br.net.mirante.singular.server.commons.persistence.dao.form.DraftDAO;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
import org.springframework.context.annotation.Bean;

import br.net.mirante.singular.flow.core.service.IUserService;
import br.net.mirante.singular.form.document.SDocument;
import br.net.mirante.singular.form.persistence.dao.FileDao;
import br.net.mirante.singular.form.persistence.dao.FormDAO;
import br.net.mirante.singular.form.persistence.service.AttachmentPersistenceService;
import br.net.mirante.singular.form.service.FormService;
import br.net.mirante.singular.form.service.IFormService;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentPersistenceHandler;
import br.net.mirante.singular.server.commons.persistence.dao.flow.ActorDAO;
import br.net.mirante.singular.server.commons.persistence.dao.flow.GrupoProcessoDAO;
import br.net.mirante.singular.server.commons.persistence.dao.flow.TaskInstanceDAO;
import br.net.mirante.singular.server.commons.persistence.dao.form.PetitionDAO;
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.server.commons.spring.security.DefaultUserDetailService;
import br.net.mirante.singular.server.commons.spring.security.SingularUserDetailsService;

public class SingularDefaultBeanFactory {

    @Bean(name = "peticionamentoUserDetailService")
    public SingularUserDetailsService worklistUserDetailServiceFactory() {
        return new DefaultUserDetailService();
    }

    @Bean
    public <T extends PetitionEntity> PetitionDAO<T> peticaoDAO() {
        return new PetitionDAO<T>();
    }

    @Bean
    public DraftDAO draftDAO() {
        return new DraftDAO();
    }

    @Bean
    public <T extends PetitionEntity> PetitionService<T> worklistPetitionServiceFactory() {
        return new PetitionService<T>();
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
    public IAttachmentPersistenceHandler attachmentPersistenceService() {
        return new AttachmentPersistenceService();
    }

    @Bean
    public FileDao fileDao() {
        return new FileDao();
    }

    @Bean
    public IFormService formService(FormDAO formDAO, FormVersionDAO formVersionDAO, FormAnnotationDAO formAnnotationDAO, FormAnnotationVersionDAO formAnnotationVersionDAO, FormTypeDAO formTypeDAO) {
        return new FormService(formDAO, formVersionDAO, formAnnotationDAO, formAnnotationVersionDAO, formTypeDAO);
    }

    @Bean
    public FormDAO formDAO() {
        return new FormDAO();
    }

    @Bean
    public FormVersionDAO formVersionDAO() {
        return new FormVersionDAO();
    }

    @Bean
    public FormAnnotationDAO formAnnotationDAO() {
        return new FormAnnotationDAO();
    }

    @Bean
    public FormAnnotationVersionDAO formAnnotationVersionDAO() {
        return new FormAnnotationVersionDAO();
    }

    @Bean
    public FormTypeDAO formTypeDAO() {
        return new FormTypeDAO();
    }

    @Bean
    public IUserService userService() {
        return new SingularDefaultUserService();
    }


}
