package br.net.mirante.singular.service;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import br.net.mirante.singular.flow.core.dto.GroupDTO;
import br.net.mirante.singular.flow.core.service.IFlowAuthorizationProvider;
import br.net.mirante.singular.flow.core.service.IFlowAuthorizationService;

@Service
public class FlowAuthorizationProvider implements IFlowAuthorizationProvider {

    @Override
    public IFlowAuthorizationService getAuthorizationService(GroupDTO groupDTO) {
        try {
            return authorizationService.get(groupDTO);
        } catch (ExecutionException e) {
            throw Throwables.propagate(e);
        }
    }

    private static LoadingCache<GroupDTO, IFlowAuthorizationService> authorizationService = CacheBuilder.newBuilder().maximumSize(5).build(new CacheLoader<GroupDTO, IFlowAuthorizationService>() {
        @Override
        public IFlowAuthorizationService load(GroupDTO groupDTO) throws Exception {
            return new FlowAuthorizationSpringREST(groupDTO.getCod(), groupDTO.getConnectionURL());
        }
    });
}
