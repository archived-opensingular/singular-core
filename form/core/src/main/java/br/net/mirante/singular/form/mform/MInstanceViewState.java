package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.core.MPacoteCore;

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
    public static MInstanceViewState get(MInstancia instance) {
        if (instance == null)
            return MInstanceViewState.HIDDEN;

        final boolean exists = instance.exists();
        final Boolean visible = MInstances.attributeValue(instance, MPacoteBasic.ATR_VISIVEL, null);
        final Boolean enabled = MInstances.attributeValue(instance, MPacoteBasic.ATR_ENABLED, null);

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

    public static boolean isInstanceRequired(MInstancia instance) {
        return (instance != null)
            && MInstances.attributeValue(instance, MPacoteCore.ATR_OBRIGATORIO, false)
            && get(instance).isEnabled();
    }

}
