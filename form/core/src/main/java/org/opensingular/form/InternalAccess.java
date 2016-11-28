package org.opensingular.form;

import org.opensingular.form.internal.xml.MElement;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * PARA USO INTERNO DA API APENAS. Dá acesso a estrutura internas do form. Os métodos aqui disponibilizados não deve ser
 * utilizados fora do core do form, pois poderão ser removidos ou ter seu comportamento no futuro.
 *
 * @author Daniel C. Bordin
 */
public final class InternalAccess {

    private InternalAccess() {}

    public static InternalSInstance internal(SInstance instance) {
        return new InternalSInstance(instance);
    }

    public static final class InternalSInstance {

        private final SInstance instance;

        InternalSInstance(SInstance instance) {
            this.instance = Objects.requireNonNull(instance);
        }

        /**
         * @see {@link SInstance#addUnreadInfo(MElement)}
         */
        public void addUnreadInfo(MElement xmlInfo) {
            instance.addUnreadInfo(xmlInfo);
        }

        /**
         * @see {@link SInstance#getUnreadInfo()}
         */
        public List<MElement> getUnreadInfo() {
            return instance.getUnreadInfo();
        }

    }
}
