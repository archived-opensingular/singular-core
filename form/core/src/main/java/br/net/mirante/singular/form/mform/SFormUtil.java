package br.net.mirante.singular.form.mform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import javax.lang.model.SourceVersion;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;

public final class SFormUtil {

    public static boolean isValidSimpleName(String name) {
        Pattern idPattern = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*");
        return idPattern.matcher(name).matches();
    }

    public static void validateSimpleName(String name) {
        if (!isValidSimpleName(name)) {
            throw new RuntimeException("'" + name + "' não é um nome válido para tipo ou atributo");
        }
    }

    public static void validatePackageName(String name) {
        if (!SourceVersion.isName(name)) {
            throw new RuntimeException("'" + name + "' não é um nome válido para um pacote");
        }
    }

    public static SType<?> resolveFieldType(SType<?> type, PathReader pathReader) {
        while (!pathReader.isEmpty()) {
            type = resolveFieldTypeInternal(type, pathReader);
            pathReader = pathReader.next();
        }
        return type;
    }

    private static SType<?> resolveFieldTypeInternal(SType<?> type, PathReader pathReader) {
        if (type instanceof STypeComposite) {
            if (pathReader.isIndex()) {
                throw new RuntimeException(pathReader.getTextoErro(type, "Índice de lista não se aplica a um tipo composto"));
            }
            SType<?> campo = ((STypeComposite<?>) type).getField(pathReader.getTrecho());
            if (campo == null) {
                throw new RuntimeException(pathReader.getTextoErro(type, "Não existe o campo '" + pathReader.getTrecho() + "'"));
            }
            return campo;
        } else if (type instanceof STypeList) {
            if (pathReader.isIndex()) {
                return ((STypeList<?, ?>) type).getElementsType();
            }
            throw new RuntimeException(pathReader.getTextoErro(type, "Não se aplica a um tipo lista"));
        } else if (type instanceof STypeSimple) {
            throw new RuntimeException(pathReader.getTextoErro(type, "Não se aplica um path a um tipo simples"));
        } else {
            throw new RuntimeException(pathReader.getTextoErro(type, "Não implementado para " + type.getClass()));
        }
    }

    /**
     * Retorna o nome do filho atual indo em direção ao raiz mas parando segundo
     * a condicão de parada informada.
     */
    public final static String generatePath(SInstance current, Predicate<SInstance> stopCondition) {
        List<SInstance> sequencia = null;
        while (!stopCondition.test(current)) {
            if (sequencia == null) {
                sequencia = new ArrayList<>();
            }
            sequencia.add(current);
            current = current.getParent();
        }
        if (sequencia != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = sequencia.size() - 1; i != -1; i--) {
                current = sequencia.get(i);
                if (current.getParent() instanceof SIList) {
                    int pos = ((SIList<?>) current.getParent()).indexOf(current);
                    if (pos == -1) {
                        throw new SingularFormException("Filho não encontrado");
                    }
                    sb.append('[').append(pos).append(']');
                } else {
                    if (current.getParent() != null && sb.length() != 0) {
                        sb.append('.');
                    }
                    sb.append(current.getName());
                }
            }
            return sb.toString();
        }
        return null;
    }

    public static String generateUserFriendlyPath(SInstance instance) {
        return generateUserFriendlyPath(instance, null);
    }
    public static String generateUserFriendlyPath(SInstance instance, SInstance parentContext) {
        LinkedList<String> labels = new LinkedList<>();
        SInstance child = null;
        for (SInstance node = instance; node != null && !node.equals(parentContext); child = node, node = node.getParent()) {

            final String labelNode = node.as(SPackageBasic.aspect()).getLabel();

            if (node instanceof SIList<?>) {
                SIList<?> lista = (SIList<?>) node;
                String labelLista = lista.as(SPackageBasic.aspect()).getLabel();
                int index = lista.indexOf(child) + 1;
                labels.add(labelLista + ((index > 0) ? " [" + (index) + "]" : ""));
            } else {
                if (StringUtils.isNotBlank(labelNode))
                    labels.add(labelNode);
            }
        }
        Collections.reverse(labels);

        if (!labels.isEmpty())
            return StringUtils.join(labels, " > ");
        else
            return null;
    }
}
