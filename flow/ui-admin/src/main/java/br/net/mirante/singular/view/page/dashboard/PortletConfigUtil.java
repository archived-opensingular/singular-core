package br.net.mirante.singular.view.page.dashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.wicket.Application;
import org.apache.wicket.Localizer;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;

import br.net.mirante.singular.bamclient.builder.amchart.AmChartValueField;
import br.net.mirante.singular.bamclient.chart.AreaChart;
import br.net.mirante.singular.bamclient.chart.ColumnSerialChart;
import br.net.mirante.singular.bamclient.chart.DonutPieChart;
import br.net.mirante.singular.bamclient.chart.LineSerialChart;
import br.net.mirante.singular.bamclient.chart.PieChart;
import br.net.mirante.singular.bamclient.chart.SingularChart;
import br.net.mirante.singular.bamclient.portlet.AmChartPortletConfig;
import br.net.mirante.singular.bamclient.portlet.DataEndpoint;
import br.net.mirante.singular.bamclient.portlet.MorrisChartPortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletQuickFilter;
import br.net.mirante.singular.bamclient.portlet.PortletSize;

public class PortletConfigUtil {

    private static final Map<String, Supplier<PortletConfig<?>>> mapa = new HashMap<>();

    public static final String STATS_BY_ACTIVE_TASK = "STATS_BY_ACTIVE_TASK";
    public static final String MEAN_TIME_BY_TASK = "MEAN_TIME_BY_TASK";
    public static final String MEAN_TIME_BY_PROCESS = "MEAN_TIME_BY_PROCESS";
    public static final String MEAN_TIME_ACTIVE_INSTANCES = "MEAN_TIME_ACTIVE_INSTANCES";
    public static final String NEW_INSTANCES_QUANTITY_LAST_YEAR = "NEW_INSTANCES_QUANTITY_LAST_YEAR";
    public static final String COUNTER_ACTIVE_INSTANCES = "COUNTER_ACTIVE_INSTANCES";
    public static final String MEAN_TIME_FINISHED_INSTANCES = "MEAN_TIME_FINISHED_INSTANCES";
    public static final String END_STATUS_QUANTITY_BY_PERIOD = "END_STATUS_QUANTITY_BY_PERIOD";
    public static final String STATS_TIME_BY_ACTIVE_TASK = "STATS_TIME_BY_ACTIVE_TASK";
    public static final String AVERAGE_TIMES_ACTIVE_INSTANCES = "AVERAGE_TIMES_ACTIVE_INSTANCES";

    static {
        // Dashboards Gerais
        mapa.put(MEAN_TIME_BY_PROCESS, PortletConfigUtil::buildPortletConfigMeanTimeByProcess);
        mapa.put(NEW_INSTANCES_QUANTITY_LAST_YEAR, PortletConfigUtil::buildPortletConfigNewInstancesQuantityLastYear);
        mapa.put(COUNTER_ACTIVE_INSTANCES, PortletConfigUtil::buildPortletConfigCounterActiveInstances);

        // Dashboards por Processo
        mapa.put(STATS_BY_ACTIVE_TASK, PortletConfigUtil::buildPortletConfigStatsByActiveTask);
        mapa.put(MEAN_TIME_BY_TASK, PortletConfigUtil::buildPortletConfigMeanTimeByTask);
        mapa.put(MEAN_TIME_ACTIVE_INSTANCES, PortletConfigUtil::buildPortletConfigMeanTimeActiveInstances);
        mapa.put(MEAN_TIME_FINISHED_INSTANCES, PortletConfigUtil::buildPortletConfigMeanTimeFinishedInstances);
        mapa.put(END_STATUS_QUANTITY_BY_PERIOD, PortletConfigUtil::buildPortletConfigEndStatusQuantityByPeriod);
        mapa.put(STATS_TIME_BY_ACTIVE_TASK, PortletConfigUtil::buildPortletConfigStatsTimeByActiveTask);
        mapa.put(AVERAGE_TIMES_ACTIVE_INSTANCES, PortletConfigUtil::buildPortletConfigAverageTimesActiveInstances);
    }

    public static PortletConfig<?> getById(String id) {
        Supplier<PortletConfig<?>> supplier = mapa.get(id);
        if (supplier != null) {
            return supplier.get();
        }

        throw new IllegalArgumentException(String.format("Não foi encontrado nenhum dashboard para o id %s.", id));
    }

    private static String getString(String key) {
        return getLocalizer().getString(key, null);
    }

    public static Localizer getLocalizer() {
        return Application.get().getResourceSettings().getLocalizer();
    }

    private static String appendRelativeURL(String path) {
        RequestCycle requestCycle = RequestCycle.get();
        Request request = requestCycle.getRequest();
        final String fullUrl = requestCycle.getUrlRenderer().renderFullUrl(request.getUrl());
        final String currentPath = request.getUrl().toString();
        final int beginPath = fullUrl.lastIndexOf(currentPath);
        final Optional<String> contextPath = Optional.ofNullable(requestCycle.getRequest().getContextPath());
        return fullUrl.substring(0, beginPath - 1) + contextPath.orElse("") + path;
    }

    private static void addPeriodQuickFilter(List<PortletQuickFilter> list) {
        list.add(new PortletQuickFilter("1 Semana", String.valueOf(PeriodType.WEEKLY)));
        list.add(new PortletQuickFilter("1 Mês", String.valueOf(PeriodType.MONTHLY)));
        list.add(new PortletQuickFilter("1 Ano", String.valueOf(PeriodType.YEARLY)));
    }

    public static PortletConfig<?> buildPortletConfigStatsByActiveTask() {

        final SingularChart chart = new PieChart("NOME", "QUANTIDADE");

        return new AmChartPortletConfig(DataEndpoint.local(appendRelativeURL("/rest/statsByActiveTask")), chart)
                .setPortletSize(PortletSize.MEDIUM)
                .setTitle(getString("label.chart.count.task.title"))
                .setSubtitle(getString("label.chart.count.task.subtitle"));
    }

    public static PortletConfig<?> buildPortletConfigMeanTimeByTask() {

        final SingularChart chart = new PieChart("NOME", "MEAN");
        final AmChartPortletConfig config = new AmChartPortletConfig(DataEndpoint.local(appendRelativeURL("/rest/meanTimeByTask")), chart);

        addPeriodQuickFilter(config.getQuickFilter());

        return config.setPortletSize(PortletSize.LARGE)
                .setTitle(getString("label.chart.mean.time.task.title"))
                .setSubtitle(getString("label.chart.mean.time.task.subtitle"));
    }


    public static PortletConfig<?> buildPortletConfigMeanTimeByProcess() {

        final SingularChart chart = new ColumnSerialChart("NOME", new AmChartValueField("MEAN", "", "dia(s)"));
        final AmChartPortletConfig config = new AmChartPortletConfig(DataEndpoint.local(appendRelativeURL("/rest/meanTimeByProcess")), chart);

        addPeriodQuickFilter(config.getQuickFilter());

        config.setPortletSize(PortletSize.LARGE);
        config.setTitle(getString("label.chart.mean.time.process.title"));
        config.setSubtitle(getString("label.chart.mean.time.process.subtitle"));

        return config;
    }

    public static PortletConfig<?> buildPortletConfigMeanTimeActiveInstances() {

        final SingularChart chart = new LineSerialChart("MES", new AmChartValueField("TEMPO", ""));

        return new AmChartPortletConfig(DataEndpoint.local(appendRelativeURL("/rest/meanTimeActiveInstances")), chart)
                .setPortletSize(PortletSize.MEDIUM)
                .setTitle(getString("label.chart.active.instances.mean.time.title"))
                .setSubtitle(getString("label.chart.active.instances.mean.time.subtitle"));
    }

    public static PortletConfig<?> buildPortletConfigNewInstancesQuantityLastYear() {

        final List<AmChartValueField> valueFields = new ArrayList<>();
        valueFields.add(new AmChartValueField("QTD_NEW", getString("label.chart.new.instance.quantity.new")));
        valueFields.add(new AmChartValueField("QTD_CLS", getString("label.chart.new.instance.quantity.finished")));

        final SingularChart chart = new LineSerialChart("MES", valueFields);

        return new AmChartPortletConfig(DataEndpoint.local(appendRelativeURL("/rest/newInstancesQuantityLastYear")), chart)
                .setPortletSize(PortletSize.MEDIUM)
                .setTitle(getString("label.chart.new.instance.quantity.title"))
                .setSubtitle(getString("label.chart.new.instance.quantity.title"));
    }

    public static PortletConfig<?> buildPortletConfigCounterActiveInstances() {

        final SingularChart chart = new LineSerialChart("MES", new AmChartValueField("QUANTIDADE", ""));

        return new AmChartPortletConfig(DataEndpoint.local(appendRelativeURL("/rest/counterActiveInstances")), chart)
                .setPortletSize(PortletSize.MEDIUM)
                .setTitle(getString("label.chart.active.instance.quantity.title"))
                .setSubtitle(getString("label.chart.active.instance.quantity.subtitle"));
    }

    public static PortletConfig<?> buildPortletConfigMeanTimeFinishedInstances() {

        final SingularChart chart = new LineSerialChart("MES", new AmChartValueField("TEMPO", ""));

        return new AmChartPortletConfig(DataEndpoint.local(appendRelativeURL("/rest/meanTimeFinishedInstances")), chart)
                .setPortletSize(PortletSize.MEDIUM)
                .setTitle(getString("label.chart.finished.instances.mean.time.title"))
                .setSubtitle(getString("label.chart.finished.instances.mean.time.subtitle"));
    }

    public static PortletConfig<?> buildPortletConfigEndStatusQuantityByPeriod() {
        final SingularChart chart = new DonutPieChart("SITUACAO", "QUANTIDADE");
        final AmChartPortletConfig config = new AmChartPortletConfig(DataEndpoint.local(appendRelativeURL("/rest/endStatusQuantityByPeriod")), chart);

        addPeriodQuickFilter(config.getQuickFilter());

        return config.setPortletSize(PortletSize.MEDIUM)
                .setTitle(getString("label.chart.status.hour.quantity.title"))
                .setSubtitle(getString("label.chart.status.hour.quantity.subtitle"));
    }

    public static PortletConfig<?> buildPortletConfigStatsTimeByActiveTask() {

        final SingularChart chart = new PieChart("NOME", "TEMPO");

        return new AmChartPortletConfig(DataEndpoint.local(appendRelativeURL("/rest/statsByActiveTask")), chart)
                .setPortletSize(PortletSize.MEDIUM)
                .setTitle(getString("label.chart.active.task.mean.time.title"))
                .setSubtitle(getString("label.chart.active.task.mean.time.subtitle"));
    }

    public static PortletConfig<?> buildPortletConfigAverageTimesActiveInstances() {

        final SingularChart chart = new AreaChart("DATA", "TEMPO", "TEMPO2").
                labels(getString("label.chart.active.instances.average.time.3"), getString("label.chart.active.instances.average.time.6"));

        return new MorrisChartPortletConfig(DataEndpoint.local(appendRelativeURL("/rest/averageTimesActiveInstances")), chart)
                .setPortletSize(PortletSize.MEDIUM)
                .setTitle(getString("label.chart.active.instances.average.time.title"))
                .setSubtitle(getString("label.chart.active.instances.average.time.subtitle"));
    }

}
