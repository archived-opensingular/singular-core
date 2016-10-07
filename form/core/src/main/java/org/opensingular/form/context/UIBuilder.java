/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.context;

import java.io.Serializable;

/**
 * Interface de marcação, apresenta os métodos necessários para montar uma tela
 * a partir de um pacote ou mtipo
 *
 * Diferentes tecnologias de interface proverão diferentes implementações
 * @param <MapperType> mapper para o tipo de builder implementado
 */
public interface UIBuilder<MapperType extends UIComponentMapper> extends Serializable {

}
