package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.core.SPackageCore;

public enum SInstanceViewState {
    EDITABLE, READONLY, HIDDEN;
    public boolean isVisible() {
        return this != HIDDEN;
    }
    public boolean isEnabled() {
        return this == EDITABLE;
    }

    /*
    
    +-----------+-----------------------+
    |           |        Enabled        |
    |           +-------+-------+-------+
    |           | TRUE  | FALSE | NULL  |
    +---+-------+-------+-------+-------+--------+
    | V |  NULL |   E   |   R   |   E   | Exists |
    | i |  TRUE |   E   |   R   |   E   | TRUE   |
    | s | FALSE |   H   |   H   |   H   |        |
    | i +-------+-------+-------+-------+--------+
    | b |  NULL |   H   |   H   |   H   | Exists |
    | l |  TRUE |   R   |   R   |   R   | FALSE  |
    | e | FALSE |   H   |   H   |   H   |        |
    +---+-------+-------+-------+-------+--------+
    E = editable
    R = readonly
    H = hidden
    
    */
    public static SInstanceViewState get(SInstance instance) {
        if (instance == null)
            return SInstanceViewState.HIDDEN;

        final boolean exists = instance.exists();
        final Boolean visible = SInstances.attributeValue(instance, SPackageBasic.ATR_VISIVEL, null);
        final Boolean enabled = SInstances.attributeValue(instance, SPackageBasic.ATR_ENABLED, null);

        if (exists) {
            if (visible != null && !visible) {
                return SInstanceViewState.HIDDEN;
            } else if (enabled != null && !enabled) {
                return SInstanceViewState.READONLY;
            } else {
                return SInstanceViewState.EDITABLE;
            }
        } else {
            if (visible != null && visible) {
                return SInstanceViewState.READONLY;
            } else {
                return SInstanceViewState.HIDDEN;
            }
        }
    }

    public static boolean isInstanceRequired(SInstance instance) {
        return (instance != null)
            && SInstances.attributeValue(instance, SPackageCore.ATR_REQUIRED, false)
            && get(instance).isEnabled();
    }

}
