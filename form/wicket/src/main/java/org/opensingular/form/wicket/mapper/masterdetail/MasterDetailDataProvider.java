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

package org.opensingular.form.wicket.mapper.masterdetail;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.core.SIComparable;
import org.opensingular.form.view.list.SViewListByMasterDetail;
import org.opensingular.form.wicket.model.SInstanceListItemModel;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.wicket.util.datatable.BaseDataProvider;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * This is the provider of the master detail table.
 */
public class MasterDetailDataProvider extends BaseDataProvider<SInstance, String> {

    private final IModel<SIList<SInstance>> list;
    private final ISupplier<SViewListByMasterDetail> viewSupplier;

    MasterDetailDataProvider(IModel<SIList<SInstance>> list, ISupplier<SViewListByMasterDetail> viewSupplier) {
        this.list = list;
        this.viewSupplier = viewSupplier;
    }

    @Override
    public Iterator<SInstance> iterator(int first, int count, String sortProperty, boolean ascending) {
        final SIList<SInstance> siList = list.getObject();
        final List<SInstance> listOfPage = new ArrayList<>();
        final List<SInstance> sortableList = populateSortList(siList, sortProperty, ascending);

        for (int i = 0; (i < count) && (i + first < sortableList.size()); i++) {
            listOfPage.add(sortableList.get(i + first));
        }
        return listOfPage.iterator();
    }

    /**
     * This method is responsible for populate and sort the list.
     * <p>
     * Note: The sort will be the column chosen, and if it's null, will try to get the sortableColumn defined in the View.
     *
     * @param siList       The list of the master detail.
     * @param sortProperty The sort property.
     *                     Null if don't have a sort property defined.
     * @param ascending    The ascending chosen by user.
     *                     If user don't chosen one, it will try to get the view ascMode.
     * @return sortable List.
     */
    private List<SInstance> populateSortList(SIList<SInstance> siList, @Nullable String sortProperty, boolean ascending) {
        List<SInstance> sortableList = new ArrayList<>(siList.getValues());
        if (CollectionUtils.isNotEmpty(sortableList)) {
            if (StringUtils.isEmpty(sortProperty)) {
                sortListByConfigView(sortableList);
            } else {
                sortableList.sort(new ProviderMasterDetailCompator(sortProperty, ascending));
            }
        }
        return sortableList;
    }

    private void sortListByConfigView(List<SInstance> sortableList) {
        if (viewSupplier != null) {
            boolean ascMode;
            SViewListByMasterDetail view = viewSupplier.get();
            ascMode = view.isAscendingMode();
            if (view.getSortableColumn() != null) {
                sortableList.sort(new ProviderMasterDetailCompator(view.getSortableColumn().getNameSimple(), ascMode));
            }
        }
    }

    @Override
    public long size() {
        return list.getObject().size();
    }

    @Override
    public IModel<SInstance> model(SInstance object) {
        return new SInstanceListItemModel<>(list, list.getObject().indexOf(object));
    }

    /**
     * A comparator for sort the master detail list.
     * Note: This compator use the <code>SIComparable</code> for compare the Instance's of the list.
     */
    public static class ProviderMasterDetailCompator implements Comparator<SInstance>, Loggable, Serializable {

        private String sortableProperty;
        private boolean ascMode;

        ProviderMasterDetailCompator(String sortableProperty, boolean ascMode) {
            this.sortableProperty = sortableProperty;
            this.ascMode = ascMode;
        }

        @Override
        public int compare(SInstance instanceList1, SInstance instanceList2) {
            Optional<SInstance> obj1 = getObjectBySortProperty(instanceList1);
            Optional<SInstance> obj2 = getObjectBySortProperty(instanceList2);
            return compareTheObject(obj1, obj2);
        }

        /**
         * This will sort the two object passed.
         * <p>
         * Note: The sort will happen just if the two optional object exists, and the value is a instanceOf SIComparable.
         * Note: If some object in comparable is null, the logic will be the NULLSFIRST.
         *
         * @param obj1 The first object to be comparable.
         * @param obj2 The second object to be comparable.
         * @return return the result of the <code>SIComparable#compareTo</code>.
         */
        private int compareTheObject(Optional<SInstance> obj1, Optional<SInstance> obj2) {
            if (hasValue(obj1, obj2) && isInstanceOfSIComparable(obj1.get(), obj2.get())) {
                Integer compareToNullResult = nullsFirstLogic(obj1.get(), obj2.get());
                if (compareToNullResult != null) {
                    return compareToNullResult;
                }
                if (ascMode) {
                    return ((SIComparable) obj1.get()).compareTo((SIComparable) obj2.get());
                }
                return ((SIComparable) obj2.get()).compareTo((SIComparable) obj1.get());
            }
            getLogger().info("The comparator will be the natural compare.");
            return ascMode ? -1 : 1;
        }

        private boolean hasValue(Optional<SInstance> obj1, Optional<SInstance> obj2) {
            return obj1.isPresent() && obj2.isPresent()
                    && (obj1.get().getValue() != null || obj2.get().getValue() != null);
        }


        private boolean isInstanceOfSIComparable(SInstance obj1, SInstance obj2) {
            return obj1 instanceof SIComparable && obj2 instanceof SIComparable;
        }

        /**
         * This method will use the logic NullsFirst. The null elements will be shown in the begin of the list.
         *
         * @param obj1 first object to be compare.
         * @param obj2 second object to be compare.
         * @return The sortOrder to put the null element in the begin, or null if the two elements have value.
         */
        @Nullable
        private Integer nullsFirstLogic(SInstance obj1, SInstance obj2) {
            if (obj1.getValue() == null) {
                return -1;
            }
            if (obj2.getValue() == null) {
                return 1;
            }
            return null; //The two objects have value.
        }

        /**
         * This method will try to find the object to be sortable.
         *
         * @param instance The instance containing a list of objects, the columns of the master detail.
         * @return Optional with the sortable object.
         */
        private Optional<SInstance> getObjectBySortProperty(SInstance instance) {
            if (instance != null && instance.getValue() instanceof ArrayList) {
                return (Optional<SInstance>) ((ArrayList) instance.getValue())
                        .parallelStream()
                        .filter(i -> ((SInstance) i).getType().getNameSimple().equals(sortableProperty))
                        .findFirst();
            }
            return Optional.empty();
        }

    }

}