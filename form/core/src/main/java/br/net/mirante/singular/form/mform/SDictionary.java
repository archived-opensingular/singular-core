package br.net.mirante.singular.form.mform;

import java.util.Collection;

import br.net.mirante.singular.form.mform.basic.view.ViewResolver;
import br.net.mirante.singular.form.mform.core.SPackageCore;
import br.net.mirante.singular.form.mform.document.SDocument;

public class SDictionary implements ITypeContext {

    private MapByName<SPackage> packages = new MapByName<>(p -> p.getName());

    private MapByName<SType<?>> types = new MapByName<>(t -> t.getName());

    private final SDocument internalDocument = new SDocument();

    private ViewResolver viewResolver;

    private SDictionary() {
    }

    /**
     * Apenas para uso interno do dicionario de modo que os atributos dos tipos
     * tenha um documento de referencia.
     */
    final SDocument getInternalDicionaryDocument() {
        return internalDocument;
    }

    public Collection<SPackage> getPackages() {
        return packages.getValues();
    }

    /**
     * Retorna o registro e resolvedor (calculador) de views para as instâncias.
     * Permite registra view e decidir qual a view mais pertinente para a
     * instância alvo.
     */
    public ViewResolver getViewResolver() {
        if (viewResolver == null) {
            viewResolver = new ViewResolver();
        }
        return viewResolver;
    }

    public static SDictionary create() {
        SDictionary dicionario = new SDictionary();
        dicionario.loadPackage(SPackageCore.class);
        return dicionario;
    }

    public <T extends SPackage> T loadPackage(Class<T> packageClass) {
        if (packageClass == null){
            throw new SingularFormException("Classe pacote não pode ser nula");
        }
        T novo = packages.get(packageClass);
        if (novo == null) {
            packages.verifyMustNotBePresent(packageClass);
            novo = MapByName.newInstance(packageClass);
            packages.verifyMustNotBePresent(novo);
            carregarInterno(novo);
        }
        return novo;
    }

    public PackageBuilder createNewPackage(String nome) {
        packages.verifyMustNotBePresent(nome);
        SPackage novo = new SPackage(nome);
        novo.setDictionary(this);
        packages.add(novo);
        return new PackageBuilder(novo);
    }

    final static SInfoType getInfoType(Class<?> classeAlvo) {
        SInfoType mFormTipo = classeAlvo.getAnnotation(SInfoType.class);
        if (mFormTipo == null) {
            throw new SingularFormException("O tipo '" + classeAlvo.getName() + " não possui a anotação @" + SInfoType.class.getSimpleName()
                    + " em sua definição.");
        }
        return mFormTipo;
    }

    private static Class<? extends SPackage> getTypePackage(Class<?> classeAlvo) {
        Class<? extends SPackage> sPackage = getInfoType(classeAlvo).spackage();
        if (sPackage == null) {
            throw new SingularFormException(
                    "O tipo '" + classeAlvo.getName() + "' não define o atributo 'pacote' na anotação @"
                    + SInfoType.class.getSimpleName());
        }
        return sPackage;
    }

    @Override
    public <T extends SType<?>> T getTypeOptional(Class<T> typeClass) {
        T tipoRef = types.get(typeClass);
        if (tipoRef == null) {
            Class<? extends SPackage> classPacote = getTypePackage(typeClass);
            loadPackage(classPacote);

            tipoRef = types.get(typeClass);
        }
        return tipoRef;
    }

    public <I extends SInstance, T extends SType<I>> I newInstance(Class<T> classeTipo) {
        return getType(classeTipo).newInstance();
    }

    final MapByName<SType<?>> getTypesInternal() {
        return types;
    }

    @SuppressWarnings("unchecked")
    final <T extends SType<?>> T registeType(SScope scope, T newType, Class<T> classForRegister) {
        if (classForRegister != null) {
            Class<? extends SPackage> classePacoteAnotado = getTypePackage(classForRegister);
            SPackage pacoteAnotado = packages.getOrNewInstance(classePacoteAnotado);
            SPackage pacoteDestino = findPackage(scope);
            if (!pacoteDestino.getName().equals(pacoteAnotado.getName())) {
                throw new SingularFormException("Tentativa de carregar o tipo '" + newType.getNameSimple() + "' anotado para o pacote '"
                    + pacoteAnotado.getName() + "' como sendo do pacote '" + pacoteDestino.getName() + "'");
            }
        }
        newType.setScope(scope);
        newType.resolvSuperType(this);
        types.verifyMustNotBePresent(newType);
        ((SScopeBase) scope).register(newType);
        types.add(newType, (Class<SType<?>>) classForRegister);
        return newType;
    }

    private static SPackage findPackage(SScope scope) {
        while (scope != null && !(scope instanceof SPackage)) {
            scope = scope.getParentScope();
        }
        return (SPackage) scope;
    }

    @Override
    public SType<?> getTypeOptional(String pathFullName) {
        return types.get(pathFullName);
    }

    private void carregarInterno(SPackage newPackage) {
        PackageBuilder pb = new PackageBuilder(newPackage);
        newPackage.setDictionary(this);
        packages.add(newPackage);
        newPackage.carregarDefinicoes(pb);
    }

    public void debug() {
        System.out.println("=======================================================");
        packages.forEach(p -> p.debug());
        System.out.println("=======================================================");
    }
}
