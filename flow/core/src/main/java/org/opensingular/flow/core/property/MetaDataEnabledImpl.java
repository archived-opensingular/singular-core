package org.opensingular.flow.core.property;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Represent a concrete implementation of {@link MetaDataEnabled}.
 *
 * @see MetaDataEnabled
 * @author Daniel C. Bordin
 * @since 2018-08-23
 */
public class MetaDataEnabledImpl implements MetaDataEnabled {

    private MetaDataMap metaDataMap;

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public final MetaDataMap getMetaData() {
        if (metaDataMap == null) {
            metaDataMap = new MetaDataMap();
        }
        return metaDataMap;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public final Optional<MetaDataMap> getMetaDataOpt() {
        return Optional.ofNullable(metaDataMap);
    }
}
