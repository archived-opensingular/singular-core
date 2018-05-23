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

public class SViewByRichText extends SView {

    //Be careful, this will broke the print A4 layout.
    private boolean disablePageLayout = false;

    public boolean isDisablePageLayout() {
        return disablePageLayout;
    }

    public void setDisablePageLayout(boolean disablePageLayout) {
        this.disablePageLayout = disablePageLayout;
    }
}
