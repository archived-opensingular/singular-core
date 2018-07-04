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

package org.opensingular.form.wicket.model;

import org.apache.wicket.model.IModel;
import org.opensingular.form.SAttributeEnabled;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.form.provider.AtrProvider;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A Wicket Model to handle SIList on multi select components. This model will:
 * Always return new models for each item of the SIList when the getObject is called;
 * Never replace or overwrite the SIList itens on the setObject;
 *
 * @author Danilo Mesquita
 * @see IModel
 * @see SIList
 */
@SuppressWarnings("unchecked")
public class MultipleSelectSInstanceAwareModel extends AbstractSInstanceAwareModel<List<Serializable>> {

    /**
     * SerialVersionUID, last changed on 11/06/2018 (incompatibility issues because previous
     * version contained two serializable field and this contains only one)
     */
    private static final long serialVersionUID = 7424751526796663865L;

    /**
     * The SIList Model
     */
    private final IModel<? extends SIList> listModel;

    /**
     * Constructor that expects that the model parameter contain a SIList
     * @param listModel the SIList model
     */
    public MultipleSelectSInstanceAwareModel(IModel<? extends SInstance> listModel) {
        if (!(listModel.getObject() instanceof SIList)) {
            throw new SingularFormException("This model is only allowed to SIList", listModel.getObject());
        }
        this.listModel = (IModel<? extends SIList>) listModel;
    }

    /**
     * Get the serializable values from the SIlist. Will always create the item models to avoid desynchronization.
     * @return the serializable values
     */
    @Override
    public List<Serializable> getObject() {
        ArrayList<SelectSInstanceAwareModel> selects = new ArrayList<>();
        for (int i = 0; i < listModel.getObject().size(); i += 1) {
            selects.add(new SelectSInstanceAwareModel(new SInstanceListItemModel<>(listModel, i), new RetrieveFromParentSelectConverterResolver()));
        }
        return selects.stream().map(IModel::getObject).collect(Collectors.toList());
    }

    /**
     * Set the values checking if any value were removed from the list.
     * @param objects the serializable value to set on the list
     */
    @Override
    public void setObject(List<Serializable> objects) {
        //initial state with all values
        Map<Serializable, SInstance> valueAndInstanceMap = makeValueInstanceMap();
        for (Serializable object : objects) {
            if (valueAndInstanceMap.containsKey(object)) {
                //removes the value that remain in submitted list
                valueAndInstanceMap.remove(object);
            } else {
                //add new value that aren't in the old state
                getSIListConverter().fillInstance(getSIList().addNew(), object);
            }
        }
        //removes from SIList the removed values from the model
        valueAndInstanceMap.forEach((key, val) -> getSIList().remove(val));
    }

    /**
     * Get the model instance without cast
     * @return the instance
     */
    @Override
    public SInstance getSInstance() {
        return listModel.getObject();
    }

    /**
     * Makes a map of serializable values retrieved from instance converter and the instance itself. This method uses
     * the instance converter to transform the instance to serializable value.
     * @see SInstanceConverter
     * @return the map
     */
    private Map<Serializable, SInstance> makeValueInstanceMap() {
        return getSIList().stream()
                .collect(LinkedHashMap::new
                        , (map, inst) -> map.put(getSIListConverter().toObject((SInstance) inst), (SInstance) inst)
                        , LinkedHashMap::putAll);
    }

    /**
     * Get the converter attribute from model instance
     * @return the converter
     */
    private SInstanceConverter getSIListConverter() {
        return getSIList().asAtrProvider().getConverter();
    }

    /**
     * Get the SIlist from the model instance
     * @return the SIList
     */
    private SIList<SInstance> getSIList() {
        return listModel.getObject();
    }

    /**
     * Resolver that retrieve the converter attribute from parent instance
     * @see AtrProvider#getConverter()
     * @see SelectSInstanceAwareModel.SelectConverterResolver
     * @see SelectSInstanceAwareModel#getObject()
     * @see SelectSInstanceAwareModel#setObject(Serializable) ()
     */
    private static class RetrieveFromParentSelectConverterResolver implements SelectSInstanceAwareModel.SelectConverterResolver {
        @Override
        public Optional<SInstanceConverter> apply(SInstance inst) {
            return Optional.ofNullable(inst.getParent()).map(SAttributeEnabled::asAtrProvider).map(AtrProvider::getConverter);
        }
    }

}