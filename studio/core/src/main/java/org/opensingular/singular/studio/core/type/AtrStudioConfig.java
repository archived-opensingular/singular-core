package org.opensingular.singular.studio.core.type;

import org.opensingular.singular.form.SAttributeEnabled;
import org.opensingular.singular.form.STranslatorForAttribute;

/**
 * Created by Daniel on 19/05/2016.
 */
public class AtrStudioConfig extends STranslatorForAttribute {


        public AtrStudioConfig() {
        }

        public AtrStudioConfig(SAttributeEnabled alvo) {
            super(alvo);
        }

        public AtrStudioConfig defaultSearchCriteria(Boolean value) {
            setAttributeValue(SPackageCollectionEditorConfig.ATR_DEFAULT_SEARCH_CRITERIA, value);
            return this;
        }
}
