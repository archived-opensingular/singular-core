/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.flow.rest.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.opensingular.flow.core.dto.GroupDTO;
import org.opensingular.flow.core.service.IFlowMetadataProvider;
import org.opensingular.flow.core.service.IFlowMetadataService;

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
