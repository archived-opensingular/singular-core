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

package org.opensingular.form.view;

import org.opensingular.form.enums.ModalSize;

public interface ConfigurableModal<S extends SView> {

    default S largeSize(){
        setModalSize(ModalSize.LARGE);
        return (S) this;
    }

    default S autoSize(){
        setModalSize(ModalSize.FIT);
        return (S) this;
    }

    default S smallSize(){
        setModalSize(ModalSize.SMALL);
        return (S) this;
    }

    default S mediumSize(){
        setModalSize(ModalSize.NORMAL);
        return (S) this;
    }

    default S fullSize(){
        setModalSize(ModalSize.FULL);
        return (S) this;
    }

    ModalSize getModalSize();

    void setModalSize(ModalSize size);

}
