package org.opensingular.form.report;

import org.opensingular.form.SInstance;
import org.opensingular.form.io.SFormXMLUtil;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.report.ReportFilter;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class FormReportFilter implements ReportFilter {
    private final ISupplier<? extends SInstance> instanceSupplier;
    private final Map<String, Serializable> parameters;

    public FormReportFilter(ISupplier<? extends SInstance> instanceSupplier) {
        this.instanceSupplier = instanceSupplier;
        this.parameters = new LinkedHashMap<>();
    }

    @Override
    public void load(String XML) {
        SFormXMLUtil.fromXML(instanceSupplier.get(), XML);
    }

    @Override
    public String dumpXML() {
        return SFormXMLUtil.toStringXMLOrEmptyXML(instanceSupplier.get());
    }

    @Override
    public void setParam(String key, Serializable val) {
        parameters.put(key, val);
    }

    @Override
    public Object getParam(String key) {
        return parameters.get(key);
    }

    public SInstance getInstance() {
        return instanceSupplier.get();
    }
}