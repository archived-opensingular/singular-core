/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.bootstrap.datepicker;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MetaDataKey;

/**
 * Classe temporária. Existe porque ninguém se deu ao trabalho de criar um componente específico para o datepicker.
 * @author ronaldtm
 */
public class BSDatepickerConstants {
    
    public static final MetaDataKey<MarkupContainer> KEY_CONTAINER = new MetaDataKey<MarkupContainer>() {};
    public static final String JS_CHANGE_EVENT = "singularChangeDate";
    
}
