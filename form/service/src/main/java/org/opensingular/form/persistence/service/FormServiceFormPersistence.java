package org.opensingular.form.persistence.service;

import org.jetbrains.annotations.NotNull;
import org.opensingular.form.SFormUtil;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SType;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.persistence.AbstractFormPersistence;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.FormKeyLong;
import org.opensingular.form.persistence.dao.FormDAO;
import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.form.service.IFormService;

import javax.annotation.Nonnull;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
public class FormServiceFormPersistence<T extends SType<I>, I extends SIComposite> extends AbstractFormPersistence<I, FormKeyLong> {

    private IFormService formService;

    private SDocumentFactory documentFactory;

    private Class<T> typeClass;

    private FormDAO formDAO;

    public FormServiceFormPersistence() {
        super(FormKeyLong.class);
    }

    @Override
    public long countAll() {
        return loadAllInternal().size();
    }

    @Override
    protected I loadInternal(FormKeyLong key) {
        return (I) formService.loadSInstance(key, getRefType(), documentFactory);
    }

    @NotNull
    private RefType getRefType() {
        return RefType.of(typeClass);
    }

    @Nonnull
    @Override
    protected List<I> loadAllInternal() {
        return formDAO.listByFormAbbreviation(SFormUtil.getTypeName(typeClass))
                .stream()
                .map(FormEntity::getCod)
                .map(FormKeyLong::convertToKey)
                .map(this::load)
                .collect(Collectors.toList());
    }

    @Nonnull
    @Override
    protected List<I> loadAllInternal(long first, long max) {
        return formDAO.listByFormAbbreviation(SFormUtil.getTypeName(typeClass), first, max)
                .stream()
                .map(FormEntity::getCod)
                .map(FormKeyLong::convertToKey)
                .map(this::load)
                .collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public FormKey newVersion(@Nonnull I instance, Integer inclusionActor, boolean keepAnnotations) {
        return formService.newVersion(instance, inclusionActor, keepAnnotations);
    }

    @Override
    protected void updateInternal(@Nonnull FormKeyLong key, @Nonnull I instance, Integer inclusionActor) {
        instance.getDocument().persistFiles();
        formService.update(instance, inclusionActor);
    }

    @Override
    protected void deleteInternal(@Nonnull FormKeyLong key) {
        formDAO.delete(key.longValue());
    }

    @Nonnull
    @Override
    protected FormKeyLong insertInternal(@Nonnull I instance, Integer inclusionActor) {
        instance.getDocument().persistFiles();
        return (FormKeyLong) formService.insert(instance, inclusionActor);
    }

    @Override
    public I createInstance() {
        return (I) documentFactory.createInstance(getRefType());
    }

    @Override
    public void update(@Nonnull I instance, Integer inclusionActor) {
        super.update(instance, inclusionActor);
    }

    @Nonnull
    @Override
    public List<I> loadAll() {
        return super.loadAll();
    }

    @Nonnull
    @Override
    public List<I> loadAll(long first, long max) {
        return super.loadAll(first, max);
    }

    @Nonnull
    @Override
    public I load(@Nonnull FormKey key) {
        return super.load(key);
    }

    @Nonnull
    @Override
    public FormKey insert(@Nonnull I instance, Integer inclusionActor) {
        return super.insert(instance, inclusionActor);
    }

    @Nonnull
    @Override
    public FormKey insertOrUpdate(@Nonnull I instance, Integer inclusionActor) {
        return super.insertOrUpdate(instance, inclusionActor);
    }

    @Nonnull
    @Override
    public Optional<I> loadOpt(@Nonnull FormKey key) {
        return super.loadOpt(key);
    }

    @Override
    public void delete(@Nonnull FormKey key) {
        super.delete(key);
    }

    public IFormService getFormService() {
        return formService;
    }

    public void setFormService(IFormService formService) {
        this.formService = formService;
    }

    public SDocumentFactory getDocumentFactory() {
        return documentFactory;
    }

    public void setDocumentFactory(SDocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }

    public Class<T> getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(Class<T> typeClass) {
        this.typeClass = typeClass;
    }

    public FormDAO getFormDAO() {
        return formDAO;
    }

    public void setFormDAO(FormDAO formDAO) {
        this.formDAO = formDAO;
    }
}