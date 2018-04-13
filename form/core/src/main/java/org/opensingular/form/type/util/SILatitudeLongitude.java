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

package org.opensingular.form.type.util;

import java.math.BigDecimal;

import org.opensingular.form.SIComposite;

/**
 * Created by danilo.mesquita on 04/01/2016.
 */
public class SILatitudeLongitude extends SIComposite {

    @Override
    public STypeLatitudeLongitude getType() {
        return (STypeLatitudeLongitude) super.getType();
    }

    public void setLongitude(BigDecimal longitude) {
        this.setValue(STypeLatitudeLongitude.FIELD_LONGITUDE, longitude);
    }

    public void setLatitude(BigDecimal latitude) {
        this.setValue(STypeLatitudeLongitude.FIELD_LATITUDE, latitude);
    }

    public BigDecimal getLongitude() {
        return this.getField(getType().longitude).getValue();
    }

    public BigDecimal getLatitude() {
        return this.getField(getType().latitude).getValue();
    }

}
