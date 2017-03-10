package org.opensingular.server.commons.metadata;

public class DefaultSingularServerMetadata implements SingularServerMetadata{

    @Override
    public String getServerBaseUrl() {
        return "/singular";
    }

}
