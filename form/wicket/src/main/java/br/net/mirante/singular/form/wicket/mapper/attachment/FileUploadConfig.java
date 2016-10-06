package br.net.mirante.singular.form.wicket.mapper.attachment;

import static br.net.mirante.singular.commons.util.ConversionUtils.*;

import java.io.Serializable;

import br.net.mirante.singular.commons.base.SingularProperties;
import static br.net.mirante.singular.commons.base.SingularProperties.*;

public class FileUploadConfig implements Serializable {

    public long globalMaxFileAge;
    public long globalMaxFileCount;
    public long globalMaxFileSize;
    public long globalMaxRequestSize;
    public long defaultMaxFileSize;
    public long defaultMaxRequestSize;

    public FileUploadConfig(SingularProperties sp) {
        //@formatter:off
        this.globalMaxFileAge      = toLongHumane(sp.getProperty(FILEUPLOAD_GLOBAL_MAX_FILE_AGE      ), Long.MAX_VALUE);
        this.globalMaxFileCount    = toLongHumane(sp.getProperty(FILEUPLOAD_GLOBAL_MAX_FILE_COUNT    ), Long.MAX_VALUE);
        this.globalMaxFileSize     = toLongHumane(sp.getProperty(FILEUPLOAD_GLOBAL_MAX_FILE_SIZE     ), Long.MAX_VALUE);
        this.globalMaxRequestSize  = toLongHumane(sp.getProperty(FILEUPLOAD_GLOBAL_MAX_REQUEST_SIZE  ), Long.MAX_VALUE);

        this.defaultMaxFileSize    = toLongHumane(sp.getProperty(FILEUPLOAD_DEFAULT_MAX_FILE_SIZE    ), Long.MAX_VALUE);
        this.defaultMaxRequestSize = toLongHumane(sp.getProperty(FILEUPLOAD_DEFAULT_MAX_REQUEST_SIZE ), Long.MAX_VALUE);
        //@formatter:on
    }
}
