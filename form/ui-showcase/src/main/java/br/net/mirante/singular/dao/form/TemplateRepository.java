package br.net.mirante.singular.dao.form;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.reflections.Reflections;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.exemplo.curriculo.MPacoteCurriculo;
import br.net.mirante.singular.view.page.form.examples.ExamplePackage;

@SuppressWarnings({ "rawtypes", "serial" })
public class TemplateRepository {
	
	private static final MDicionario dicionario = MDicionario.create();
	private static final LinkedList<MTipoComposto> formTemplates;
	
	static{
		dicionario.carregarPacote(MPacoteCurriculo.class);
		dicionario.carregarPacote(ExamplePackage.class);
//		loadAllPackages(dicionario);
		formTemplates = new LinkedList<MTipoComposto>(){{
			add((MTipoComposto) dicionario.getTipo(MPacoteCurriculo.TIPO_CURRICULO));
			add((MTipoComposto) dicionario.getTipo(ExamplePackage.Types.ORDER.name));
		}};
	}

	@SuppressWarnings("unused")
	private static void loadAllPackages(MDicionario dicionario) {
		Reflections reflections = new Reflections("br");
		Set<Class<? extends MPacote>> subTypes = reflections.getSubTypesOf(MPacote.class);
		subTypes.stream()
			.filter( new Predicate<Class<? extends MPacote>>() {
				public boolean test(Class<? extends MPacote> mClass) {
					int modifiers = mClass.getModifiers();
					return !Modifier.isAbstract(modifiers) && 
							!Modifier.isAbstract(modifiers); 
				}
			})
			.forEach( mClass -> {
					try {
						dicionario.carregarPacote(mClass);
					} catch (Exception e) {
//						throw new RuntimeException(e);
					}
				}
			);
	}
	
	@SuppressWarnings("unchecked")
	public static List<MTipoComposto<?>> formTemplates(){
		return (List<MTipoComposto<?>>) formTemplates.clone();
	}
	
	public static MDicionario dicionario(){
		return dicionario;
	}

}
