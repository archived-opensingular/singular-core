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

package org.opensingular.form.view.date;


public class SViewDateTime extends SViewDate implements ISViewTime {

    private SViewTime sViewTime = new SViewTime();

    public SViewDateTime setMode24hs(boolean value) {
        sViewTime.setMode24hs(value);
        return this;
    }

    public SViewDateTime setMinuteStep(Integer value) {
        sViewTime.setMinuteStep(value);
        return this;
    }

    public boolean isMode24hs() {
        return sViewTime.isMode24hs();
    }

    public Integer getMinuteStep() {
        return sViewTime.getMinuteStep();
    }

}
