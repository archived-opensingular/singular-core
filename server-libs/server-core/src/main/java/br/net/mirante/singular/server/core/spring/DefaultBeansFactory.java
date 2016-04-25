package br.net.mirante.singular.server.core.spring;

import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.server.commons.persistence.dao.TaskInstanceDAO;
import br.net.mirante.singular.server.commons.persistence.dto.TaskInstanceDTO;
import br.net.mirante.singular.server.commons.service.AnalisePeticaoService;
import br.net.mirante.singular.server.commons.ws.ServiceFactoryUtil;
import br.net.mirante.singular.server.core.persistence.dao.flow.ActorDAO;
import br.net.mirante.singular.server.core.persistence.dao.flow.GrupoProcessoDAO;
import br.net.mirante.singular.server.core.persistence.dao.form.FileDao;
import br.net.mirante.singular.server.core.persistence.dao.form.PeticaoDAO;
import br.net.mirante.singular.server.core.service.PetitionService;
import br.net.mirante.singular.server.core.spring.security.WUserDetailService;
import org.springframework.context.annotation.Bean;

public class DefaultBeansFactory {

    @Bean(name = "peticionamentoUserDetailService")
    public WUserDetailService worklistUserDetailServiceFactory() {
        return new WUserDetailService();
    }

    @Bean
    public PetitionService worklistPetitionServiceFactory() {
        return new PetitionService();
    }

    @Bean
    public AnalisePeticaoService<TaskInstanceDTO> analisePeticaoService() {
        return new AnalisePeticaoService<>();
    }

    @Bean
    public TaskInstanceDAO<TaskInstanceDTO> taskInstanceDAO() {
        return new TaskInstanceDAO<>();
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

    @Bean
    public PeticaoDAO peticaoDAO() {
        return new PeticaoDAO();
    }
}
