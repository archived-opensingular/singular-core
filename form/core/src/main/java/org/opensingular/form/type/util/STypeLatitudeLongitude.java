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

import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeString;

import java.math.BigDecimal;

/**
 * Created by danilo.mesquita on 04/01/2016.
 */
@SInfoType(name = "LatitudeLongitude", spackage = SPackageUtil.class)
public class STypeLatitudeLongitude extends STypeComposite<SILatitudeLongitude> {

    public static final String FIELD_LATITUDE  = "latitude";
    public static final String FIELD_LONGITUDE = "longitude";

    public STypeString latitude;
    public STypeString longitude;
    
    public STypeLatitudeLongitude() {
        super(SILatitudeLongitude.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        latitude = addFieldString(FIELD_LATITUDE);
        longitude = addFieldString(FIELD_LONGITUDE);

        latitude
                .asAtr().label("Latitude")//.fractionalMaxLength(16)
                .asAtrBootstrap().colPreference(2);

        latitude.addInstanceValidator(validatable ->{
           // TODO maneira de estar decimal
            if(validatable.getInstance().getValue() != null){
                BigDecimal decimal = new BigDecimal(validatable.getInstance().getValue());
                if (decimal.compareTo(new BigDecimal(85)) == 1){
                    validatable.error("O valor máximo para latitude é 85º");
                };
                if (decimal.compareTo(new BigDecimal(-85)) == -1){
                    validatable.error("O valor mínimo para latitude é -85º");
                };
            }
        });

        longitude
                .asAtr().label("Longitude")//.fractionalMaxLength(16)
                .asAtrBootstrap().colPreference(2);

        longitude.addInstanceValidator(validatable ->{
            // TODO verificar maneira de estar decimal
            if(validatable.getInstance().getValue() != null){
                BigDecimal decimal = new BigDecimal(validatable.getInstance().getValue());
                if (decimal.compareTo(new BigDecimal(180)) == 1){
                    validatable.error("O valor máximo para longitude é 180º");
                };
                if (decimal.compareTo(new BigDecimal(-180)) == -1){
                    validatable.error("O valor mínimo para longitude é -180º");
                };
            }
        });
    }
}
