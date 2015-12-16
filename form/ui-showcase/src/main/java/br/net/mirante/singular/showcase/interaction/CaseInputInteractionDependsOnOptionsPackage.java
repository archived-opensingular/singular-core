package br.net.mirante.singular.showcase.interaction;

import static java.util.stream.Collectors.*;

import java.lang.reflect.Modifier;
import java.util.stream.Stream;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;

public class CaseInputInteractionDependsOnOptionsPackage extends MPacote {
    private MTipoString prefix;
    private MTipoString suffix;

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        super.carregarDefinicoes(pb);
        if (Modifier.isStatic(pb.getClass().getModifiers()));

        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");
        prefix = tipoMyForm.addCampoString("prefixo");
        suffix = tipoMyForm.addCampoString("sufixo");

        prefix.as(MPacoteBasic.aspect())
            .label("Prefixo");
        prefix.selectionOf("A", "B", "C", "D", "E", "F");

        suffix.as(MPacoteBasic.aspect())
            .label("Sufixo")
            .dependsOn(prefix)
            .enabled(ins -> ins.findNearest(prefix).get().getValor() != null);
        suffix.withSelectionFromProvider(new ProviderImpl());
    }

    private static final class ProviderImpl implements MOptionsProvider {
        @Override
        public MILista<? extends MInstancia> getOpcoes(MInstancia ins) {
            CaseInputInteractionDependsOnOptionsPackage pac = (CaseInputInteractionDependsOnOptionsPackage) ins.getMTipo().getPacote();
            MIString pref = ins.findNearest(pac.prefix).get();
            return (pref.getValor() == null)
                ? pref.getMTipo().novaLista()
                : pref.getMTipo().novaLista()
                    .addValores(Stream.of("a", "b", "c", "d", "e", "f")
                        .map(s -> pref.getValor() + s)
                        .collect(toList()));
        }
    }
}
/**

package br.net.mirante.singular.showcase.interaction;

import static java.util.stream.Collectors.*;

import java.io.Serializable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstances;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;

public class CaseInputInteractionDependsOnOptionsPackage extends MPacote {

    private MFieldAccessor<CaseInputInteractionDependsOnOptionsPackage, MTipoString, MIString> prefix;
    private MFieldAccessor<CaseInputInteractionDependsOnOptionsPackage, MTipoString, MIString> suffix;

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        super.carregarDefinicoes(pb);

        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");
        prefix = new MFieldAccessor<>(tipoMyForm.addCampoString("Prefixo"));
        suffix = new MFieldAccessor<>(tipoMyForm.addCampoString("Sufixo"));

        prefix.withType(this, (p, t) -> {
            t.as(MPacoteBasic.aspect()).label("Prefixo");
            t.selectionOf("A", "B", "C", "D", "E", "F");
        });

        suffix.withType(this, (p, t) -> {
            t.as(MPacoteBasic.aspect())
                .label("Sufixo")
                .dependsOn(prefix.getFieldType(p))
                .enabled(ins -> prefix.near(ins).getValor() != null);
            t.withSelectionFromProvider(ins -> {
                MIString pref = prefix.near(ins);
                return (pref.getValor() == null)
                    ? pref.getMTipo().novaLista()
                    : pref.getMTipo().novaLista()
                        .addValores(Stream.of("a", "b", "c", "d", "e", "f")
                            .map(s -> pref.getValor() + s)
                            .collect(toList()));
            });
        });
    }

    private static final class ProviderImpl implements MOptionsProvider {
        @Override
        public MILista<? extends MInstancia> getOpcoes(MInstancia ins) {
            CaseInputInteractionDependsOnOptionsPackage pac = (CaseInputInteractionDependsOnOptionsPackage) ins.getMTipo().getPacote();
            MIString pref = pac.prefix.near(ins);
            return (pref.getValor() == null)
                ? pref.getMTipo().novaLista()
                : pref.getMTipo().novaLista()
                    .addValores(Stream.of("a", "b", "c", "d", "e", "f")
                        .map(s -> pref.getValor() + s)
                        .collect(toList()));
        }
    }

    public static class MFieldAccessor<P extends MPacote, T extends MTipo<I>, I extends MInstancia> implements Serializable {
        private final String name;
        public MFieldAccessor(T tipo) {
            name = tipo.getNome();
        }
        public List<I> listChildren(MInstancia rootInstance) {
            return MInstances.listDescendants(rootInstance, getFieldType(rootInstance.getDicionario()));
        }
        public I near(MInstancia someFieldInstance) {
            return someFieldInstance.findNearest(getFieldType(someFieldInstance.getDicionario())).get();
        }
        @SuppressWarnings("unchecked")
        public T getFieldType(MDicionario dicionario) {
            return (T) dicionario.getTipo(name);
        }
        public T getFieldType(MPacote pacote) {
            return getFieldType(pacote.getDicionario());
        }
        public void withType(P pacote, BiConsumer<P, T> consumer) {
            consumer.accept(pacote, getFieldType(pacote.getDicionario()));
        }
    }
}

*/