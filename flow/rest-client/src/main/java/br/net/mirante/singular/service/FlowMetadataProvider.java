/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import br.net.mirante.singular.flow.core.dto.GroupDTO;
import br.net.mirante.singular.flow.core.service.IFlowMetadataProvider;
import br.net.mirante.singular.flow.core.service.IFlowMetadataService;

public class FlowMetadataProvider implements IFlowMetadataProvider {

    @Override
    public IFlowMetadataService getMetadataService(GroupDTO groupDTO) {
        try {
            return metadataService.get(groupDTO);
        } catch (ExecutionException e) {
            throw Throwables.propagate(e);
        }
    }

    private static LoadingCache<GroupDTO, IFlowMetadataService> metadataService = 
        CacheBuilder.newBuilder().maximumSize(5).expireAfterWrite(1, TimeUnit.HOURS).build(new CacheLoader<GroupDTO, IFlowMetadataService>() {
        @Override
        public IFlowMetadataService load(GroupDTO groupDTO) throws Exception {
            return new FlowMetadataSpringREST(groupDTO.getCod(), groupDTO.getConnectionURL());
        }
    });
}
