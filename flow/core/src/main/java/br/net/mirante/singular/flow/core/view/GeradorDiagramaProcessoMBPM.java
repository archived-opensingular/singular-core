package br.net.mirante.singular.flow.core.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import br.net.mirante.singular.flow.core.ProcessDefinition;

public class GeradorDiagramaProcessoMBPM {

    private static final Cache<Class<?>, byte[]> cache =
            CacheBuilder.newBuilder().expireAfterWrite(4, TimeUnit.HOURS).build();

    public static InputStream gerarDiagrama(ProcessDefinition<?> definicao) {
        byte[] imagem = cache.getIfPresent(definicao.getClass());
        if (imagem == null) {
            imagem = GeradorDiagramaProcessoMBPMEd.getInstance().gerarBPMNImage(definicao);
            cache.put(definicao.getClass(), imagem);
        }
        return new ByteArrayInputStream(imagem);
    }
}
