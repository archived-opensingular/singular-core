package org.opensingular.form;

import org.opensingular.form.document.SDocument;
import org.opensingular.internal.lib.commons.xml.MElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * PARA USO INTERNO DA API APENAS. Dá acesso a estrutura internas do form. Os métodos aqui disponibilizados não deve ser
 * utilizados fora do core do form, pois poderão ser removidos ou ter seu comportamento alterado no futuro.
 *
 * @author Daniel C. Bordin
 */
public final class InternalAccess {

    public static final InternalAccess INTERNAL = new InternalAccess();

    private InternalAccess() {}

    /**
     * @see {@link SInstance#addUnreadInfo(MElement)}
     */
    public void addUnreadInfo(SInstance instance, MElement xmlInfo) {
        instance.addUnreadInfo(xmlInfo);
    }

    /**
     * @see {@link SInstance#getUnreadInfo()}
     */
    public List<MElement> getUnreadInfo(SInstance instance) {
        return instance.getUnreadInfo();
    }

    /**
     * @see {@link SType#setAttributeValueSavingForLatter(String, String)}
     */
    public void setAttributeValueSavingForLatter(@Nonnull SType<?> target, @Nonnull String attributeName,
            @Nullable String value) {
        target.setAttributeValueSavingForLatter(attributeName, value);
    }

    /**
     * @see {@link SInstance#setAttributeValueSavingForLatter(String, String)}
     */
    public void setAttributeValueSavingForLatter(@Nonnull SInstance target, @Nonnull String attributeName,
            @Nullable String value) {
        target.setAttributeValueSavingForLatter(attributeName, value);
    }

    /** @see {@link SType#newInstance(boolean, SDocument)}  */
    public SInstance newInstance(@Nonnull SType target, boolean executeInstanceInitListeners,@Nonnull SDocument owner) {
        return target.newInstance(executeInstanceInitListeners, owner);
    }

    /** @see {@link SType#getAspectDirect(int)} */
    @Nullable
    public final Object getAspectDirect(@Nonnull SType target, int index) {
        return target.getAspectDirect(index);
    }
}
