package br.net.mirante.singular.form.util.xml;

import org.w3c.dom.Element;

/**
 * Indica que uma classe possui um Element embutido internamente. Permite
 * assim obter o elemento original.
 *
 * @author Daniel C. Bordin
 */
interface EWrapper {

    /**
     * Obtem o Element contido internamente pelo envoltorio.
     *
     * @return Geralmente not null, mas depente do wrapper
     */
    public Element getOriginal();

}
