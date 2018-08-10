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

package org.opensingular.form.studio;

/**
 * Não considero esta uma solução interessante, devemos pensar melhor...
 */
@Deprecated
public interface StudioCRUDPermissionStrategy {
    StudioCRUDPermissionStrategy ALL = new StudioCRUDPermissionStrategy() {
        @Override
        public boolean canCreate() {
            return true;
        }

        @Override
        public boolean canEdit() {
            return true;
        }

        @Override
        public boolean canRemove() {
            return true;
        }

        @Override
        public boolean canView() {
            return true;
        }
    };
    StudioCRUDPermissionStrategy VIEW_ONLY = new StudioCRUDPermissionStrategy() {
        @Override
        public boolean canCreate() {
            return false;
        }

        @Override
        public boolean canEdit() {
            return false;
        }

        @Override
        public boolean canRemove() {
            return false;
        }

        @Override
        public boolean canView() {
            return true;
        }
    };

    boolean canCreate();

    boolean canEdit();

    boolean canRemove();

    boolean canView();
}