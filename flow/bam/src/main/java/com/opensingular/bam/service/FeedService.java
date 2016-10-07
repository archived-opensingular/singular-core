/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.service;

import java.util.List;
import java.util.Set;

import com.opensingular.bam.dto.FeedDTO;

public interface FeedService {

    List<FeedDTO> retrieveFeed(String processCode, Set<String> processCodeWithAccess);
}
