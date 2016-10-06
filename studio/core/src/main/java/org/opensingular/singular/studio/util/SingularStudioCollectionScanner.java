package org.opensingular.singular.studio.util;

import org.opensingular.form.SType;
import org.opensingular.singular.studio.core.CollectionDefinition;
import org.opensingular.singular.studio.core.SingularStudioException;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SingularStudioCollectionScanner {

    private static final Logger logger = LoggerFactory.getLogger(SingularStudioCollectionScanner.class);

    public static List<CollectionDefinition<SType<?>>> scan(String... packages) {
        try {
            List<CollectionDefinition<SType<?>>> collectionDefinitions = new ArrayList<>();
            Reflections reflections = new Reflections(packages);
            Set<Class<? extends CollectionDefinition>> definitions = reflections.getSubTypesOf(CollectionDefinition.class);
            for (Class<?> clazz : definitions) {
                if (Modifier.isPublic(clazz.getModifiers())
                        && !Modifier.isAbstract(clazz.getModifiers())
                        && !Modifier.isInterface(clazz.getModifiers())
                        ) {
                    collectionDefinitions.add((CollectionDefinition<SType<?>>) clazz.newInstance());
                }
            }
            return collectionDefinitions;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SingularStudioException(e);
        }
    }
}
