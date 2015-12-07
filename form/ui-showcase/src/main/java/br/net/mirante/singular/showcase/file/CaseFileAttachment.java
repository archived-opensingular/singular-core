package br.net.mirante.singular.showcase.file;

import br.net.mirante.singular.showcase.CaseBase;

import java.io.Serializable;

public class CaseFileAttachment extends CaseBase implements Serializable {

    public CaseFileAttachment() {
        super("Attachment");
        setDescriptionHtml("Campo para anexar arquivos");
        //TODO: How to handle personalization of attachment
        /**
         * Steps to not forget:
         *  On the instance you should set 
         *  <code>
         *      document = instance.getDocument();
         *      document.setAttachmentPersistenceHandler(temporaryRef);
         *      document.bindLocalService(SDocument.FILE_PERSISTENCE_SERVICE, 
         *          persistanceRef);
         *  </code>
         *  before persising (or generating the XML) you should call
         *      <code>instance.getDocument().persistFiles(); </code>
         *  
         */
        
    }
}
