/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.SType;

/**
 * Represents an MInstancia that is of a kind of MSelectionableType.
 * It allows specific types to establish their own strategy for defining their key (id)
 * and value (visible selectLabel) of the instance.
 */
public interface SSelectionableInstance extends SSelectionable {

    SType<?> getType();

    SOptionsConfig getOptionsConfig();

}
