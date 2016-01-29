package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.core.SPackageCore;

public enum MInstanceViewState {
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
    public static MInstanceViewState get(SInstance2 instance) {
        if (instance == null)
            return MInstanceViewState.HIDDEN;

        final boolean exists = instance.exists();
        final Boolean visible = MInstances.attributeValue(instance, SPackageBasic.ATR_VISIVEL, null);
        final Boolean enabled = MInstances.attributeValue(instance, SPackageBasic.ATR_ENABLED, null);

        if (exists) {
            if (visible != null && !visible) {
                return MInstanceViewState.HIDDEN;
            } else if (enabled != null && !enabled) {
                return MInstanceViewState.READONLY;
            } else {
                return MInstanceViewState.EDITABLE;
            }
        } else {
            if (visible != null && visible) {
                return MInstanceViewState.READONLY;
            } else {
                return MInstanceViewState.HIDDEN;
            }
        }
    }

    public static boolean isInstanceRequired(SInstance2 instance) {
        return (instance != null)
            && MInstances.attributeValue(instance, SPackageCore.ATR_OBRIGATORIO, false)
            && get(instance).isEnabled();
    }

}
