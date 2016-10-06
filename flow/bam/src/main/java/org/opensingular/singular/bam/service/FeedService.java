/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.bam.service;

import java.util.List;
import java.util.Set;

import org.opensingular.singular.bam.dto.FeedDTO;

public interface FeedService {

    List<FeedDTO> retrieveFeed(String processCode, Set<String> processCodeWithAccess);
}
