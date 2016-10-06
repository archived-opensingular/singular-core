package org.opensingular.singular.server.commons.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import org.opensingular.flow.core.renderer.IFlowRenderer;
import org.opensingular.flow.core.service.IUserService;
import org.opensingular.flow.schedule.IScheduleService;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.persistence.dao.AttachmentContentDao;
import org.opensingular.form.persistence.dao.AttachmentDao;
import org.opensingular.form.persistence.dao.FormAnnotationDAO;
import org.opensingular.form.persistence.dao.FormAnnotationVersionDAO;
import org.opensingular.form.persistence.dao.FormDAO;
import org.opensingular.form.persistence.dao.FormTypeDAO;
import org.opensingular.form.persistence.dao.FormVersionDAO;
import org.opensingular.form.persistence.service.AttachmentPersistenceService;
import org.opensingular.form.service.FormService;
import org.opensingular.form.service.IFormService;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.singular.server.commons.flow.renderer.remote.YFilesFlowRemoteRenderer;
import org.opensingular.singular.server.commons.persistence.dao.EmailAddresseeDao;
import org.opensingular.singular.server.commons.persistence.dao.EmailDao;
import org.opensingular.singular.server.commons.persistence.dao.ParameterDAO;
import org.opensingular.singular.server.commons.persistence.dao.flow.ActorDAO;
import org.opensingular.singular.server.commons.persistence.dao.flow.GrupoProcessoDAO;
import org.opensingular.singular.server.commons.persistence.dao.flow.TaskInstanceDAO;
import org.opensingular.singular.server.commons.persistence.dao.form.DraftDAO;
import org.opensingular.singular.server.commons.persistence.dao.form.FormPetitionDAO;
import org.opensingular.singular.server.commons.persistence.dao.form.PetitionContentHistoryDAO;
import org.opensingular.singular.server.commons.persistence.dao.form.PetitionDAO;
import org.opensingular.singular.server.commons.persistence.dao.form.PetitionerDAO;
import org.opensingular.singular.server.commons.persistence.entity.form.PetitionEntity;
import org.opensingular.singular.server.commons.schedule.TransactionalQuartzScheduledService;
import org.opensingular.singular.server.commons.service.EmailPersistenceService;
import org.opensingular.singular.server.commons.service.IEmailService;
import org.opensingular.singular.server.commons.service.ParameterService;
import org.opensingular.singular.server.commons.service.PetitionService;
import org.opensingular.singular.server.commons.spring.security.AuthorizationService;
import org.opensingular.singular.server.commons.spring.security.DefaultUserDetailService;
import org.opensingular.singular.server.commons.spring.security.PermissionResolverService;
import org.opensingular.singular.server.commons.spring.security.SingularUserDetailsService;

@SuppressWarnings("rawtypes")
public class SingularDefaultBeanFactory {

    @Bean(name = "peticionamentoUserDetailService")
    public SingularUserDetailsService worklistUserDetailServiceFactory() {
        return new DefaultUserDetailService();
    }

    @Bean
    public <T extends PetitionEntity> PetitionDAO<T> peticaoDAO() {
        return new PetitionDAO<>();
    }

    @Bean
    public PetitionContentHistoryDAO petitionContentHistoryDAO() {
        return new PetitionContentHistoryDAO();
    }

    @Bean
    public FormPetitionDAO formPetitionDAO() {
        return new FormPetitionDAO();
    }

    @Bean
    public DraftDAO draftDAO() {
        return new DraftDAO();
    }

    @Bean
    public <T extends PetitionEntity> PetitionService<T> worklistPetitionServiceFactory() {
        return new PetitionService<>();
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
    public AttachmentDao attachmentDao() {
        return new AttachmentDao();
    }

    @Bean
    public AttachmentContentDao attachmentContentDao() {
        return new AttachmentContentDao();
    }

    @Bean
    public IFormService formService() {
        return new FormService();
    }

    @Bean
    public PetitionerDAO petitionerDAO() {
        return new PetitionerDAO();
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

    @Bean
    public PermissionResolverService getPermissionResolverService() {
        return new PermissionResolverService();
    }

    @Bean
    public AuthorizationService getAuthorizationService() {
        return new AuthorizationService();
    }

    @Bean
    public EmailDao<?> emailDao(){
        return new EmailDao<>();
    }
    
    @Bean
    public EmailAddresseeDao<?> emailAddresseeDao(){
        return new EmailAddresseeDao<>();
    }

    @Bean
    @DependsOn(SDocument.FILE_PERSISTENCE_SERVICE)
    public IEmailService<?> emailService(){
        return new EmailPersistenceService();
    }

    @Bean
    public IScheduleService scheduleService(){
        return new TransactionalQuartzScheduledService();
    }
    
    @Bean
    public IFlowRenderer flowRenderer(){
        return new YFilesFlowRemoteRenderer(null);
    }

    @Bean
    public ParameterDAO parameterDAO() {
        return new ParameterDAO();
    }

    @Bean
    public ParameterService parameterService() {
        return new ParameterService();
    }
}
