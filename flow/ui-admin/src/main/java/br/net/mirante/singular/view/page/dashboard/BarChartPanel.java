package br.net.mirante.singular.view.page.dashboard;

import br.net.mirante.singular.service.PesquisaService;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.AbstractChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.IOnChangeListener;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.settings.DebugSettings;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.value.AttributeMap;
import org.apache.wicket.util.value.IValueMap;

import javax.inject.Inject;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

public class BarChartPanel extends Panel {

    @Inject
    private PesquisaService pesquisaService;

    private List<Map<String, String>> dadosGrafico;
    private String title;
    private String subtitle;
    private String selected;

    public BarChartPanel(String id, String title, String subtitle) {
        super(id);
        this.title = title;
        this.subtitle = subtitle;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        dadosGrafico = pesquisaService.retrieveMeanTimeByProcess(Period.ofWeeks(-1));
        setOutputMarkupId(true);
        add(new Label("title", new ResourceModel(title)));
        add(new Label("subtitle", new ResourceModel(subtitle)));
        add(createChartFilter());
        add(createBarChart());

    }

    private WebMarkupContainer createBarChart() {
        WebMarkupContainer barChartDiv = new WebMarkupContainer("chart-div");
        barChartDiv.setOutputMarkupId(true);
        barChartDiv.add($b.onReadyScript(this::montarScript));
        return barChartDiv;
    }

    private Component createChartFilter() {
        Form<Object> form = new Form<>("chart-form");
        form
            .queue(new WebMarkupContainer("semanal").add(new AjaxEventBehavior("click") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    selected = "semanal";
                    dadosGrafico = pesquisaService.retrieveMeanTimeByProcess(Period.ofWeeks(-1));
                    target.add(form.getParent());
                }
            }))
            .queue(new WebMarkupContainer("mensal").add(new AjaxEventBehavior("click") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    selected = "mensal";
                    dadosGrafico = pesquisaService.retrieveMeanTimeByProcess(Period.ofMonths(-1));
                    target.add(form.getParent());
                }
            }))
            .queue(new WebMarkupContainer("anual").add(new AjaxEventBehavior("click") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    selected = "anual";
                    dadosGrafico = pesquisaService.retrieveMeanTimeByProcess(Period.ofYears(-1));
                    target.add(form.getParent());
                }
            }));


        List<String> options = Arrays.asList("Semanal", "Mensal", "Anual");

        return form;
    }

    private String parseToJson(List<Map<String, String>> dados) {
        return new JSONArray(dados).toString();
    }

    private CharSequence montarScript(Component comp) {
        String id = comp.getMarkupId();
        return "            AmCharts.makeChart( \"" + id + "\", { " +
                "                \"type\": \"serial\", " +
                "                \"theme\": \"light\", " +
                "                \"dataProvider\":  " +
                parseToJson(dadosGrafico) +
                "                , " +
                "                \"valueAxes\": [ { " +
                "                    \"gridColor\": \"#FFFFFF\", " +
                "                    \"gridAlpha\": 0.2, " +
                "                    \"dashLength\": 0 " +
                "                } ], " +
                "                \"gridAboveGraphs\": true, " +
                "                \"startDuration\": 1, " +
                "                \"graphs\": [ { " +
                "                    \"balloonText\": \"[[category]]: <b>[[value]]</b>\", " +
                "                    \"fillAlphas\": 0.8, " +
                "                    \"lineAlpha\": 0.2, " +
                "                    \"type\": \"column\", " +
                "                    \"valueField\": \"MEAN\" " +
                "                } ], " +
                "                \"chartCursor\": { " +
                "                    \"categoryBalloonEnabled\": false, " +
                "                    \"cursorAlpha\": 0, " +
                "                    \"zoomable\": false " +
                "                }, " +
                "                \"categoryField\": \"NOME\", " +
                "                \"categoryAxis\": { " +
                "                    \"gridPosition\": \"start\", " +
                "                    \"gridAlpha\": 0, " +
                "                    \"tickPosition\": \"start\", " +
                "                    \"tickLength\": 20, " +
                "                    \"autoWrap\": true " +
                "                }, " +
                "                \"export\": { " +
                "                    \"enabled\": true " +
                "                } " +
                "            } ); ";
    }

    static class PillChoice<T> extends RadioChoice<T> {

        public PillChoice(String id) {
            super(id);
        }

        public PillChoice(String id, List choices) {
            super(id, choices);
        }

        public PillChoice(String id, List choices, IChoiceRenderer renderer) {
            super(id, choices, renderer);
        }

        public PillChoice(String id, IModel model, List choices) {
            super(id, model, choices);
        }

        public PillChoice(String id, IModel model, List choices, IChoiceRenderer renderer) {
            super(id, model, choices, renderer);
        }

        public PillChoice(String id, IModel choices) {
            super(id, choices);
        }

        public PillChoice(String id, IModel model, IModel choices) {
            super(id, model, choices);
        }

        public PillChoice(String id, IModel choices, IChoiceRenderer renderer) {
            super(id, choices, renderer);
        }

        public PillChoice(String id, IModel model, IModel choices, IChoiceRenderer renderer) {
            super(id, model, choices, renderer);
        }

        @Override
        protected IValueMap getAdditionalAttributesForLabel(int index, Object choice) {
            IValueMap map = new AttributeMap();
            map.put("class", "btn btn-transparent grey-salsa btn-circle btn-sm");
            return map;
        }

        protected IValueMap getAdditionalAttributesForInput(int index, Object choice) {
            IValueMap map = new AttributeMap();
            map.put("class", "toggle");
            return map;
        }

        @Override
        protected void appendOptionHtml(final AppendingStringBuffer buffer, final T choice, int index,
                                        final String selected)
        {
            Object displayValue = getChoiceRenderer().getDisplayValue(choice);
            Class<?> objectClass = (displayValue == null ? null : displayValue.getClass());

            // Get label for choice
            String label = "";

            if (objectClass != null && objectClass != String.class)
            {
                @SuppressWarnings("rawtypes")
                final IConverter converter = getConverter(objectClass);
                label = converter.convertToString(displayValue, getLocale());
            }
            else if (displayValue != null)
            {
                label = displayValue.toString();
            }

            // If there is a display value for the choice, then we know that the
            // choice is automatic in some way. If label is /null/ then we know
            // that the choice is a manually created radio tag at some random
            // location in the page markup!
            if (label != null)
            {
                // Append option suffix
                buffer.append(getPrefix(index, choice));

                String id = getChoiceRenderer().getIdValue(choice, index);
                final String idAttr = getMarkupId() + "-" + id;

                boolean enabled = isEnabledInHierarchy() && !isDisabled(choice, index, selected);

                // Add label for radio button
                String display = label;
                if (localizeDisplayValues())
                {
                    display = getLocalizer().getString(label, this, label);
                }

                CharSequence escaped = display;
                if (getEscapeModelStrings())
                {
                    escaped = Strings.escapeMarkup(display);
                }

                // Allows user to add attributes to the <label..> tag
                IValueMap labelAttrs = getAdditionalAttributesForLabel(index, choice);
                StringBuilder extraLabelAttributes = new StringBuilder();

                if (isSelected(choice, index, selected)) {
                    String classes = (String) labelAttrs.get("class");
                    if (classes == null) {
                        classes = "";
                    }

                    classes += " active";
                    labelAttrs.put("class", classes);
                }

                if (labelAttrs != null)
                {
                    for (Map.Entry<String, Object> attr : labelAttrs.entrySet())
                    {
                        extraLabelAttributes.append(' ')
                                .append(attr.getKey())
                                .append("=\"")
                                .append(attr.getValue())
                                .append('"');
                    }
                }

                buffer.append("<label")
                        .append(extraLabelAttributes)
                        .append('>')
                        .append(escaped)
                        .append(' ');

                // Allows user to add attributes to the <input..> tag
                IValueMap inputAttrs = getAdditionalAttributesForInput(index, choice);
                StringBuilder extraInputAttributes = new StringBuilder();
                if (inputAttrs != null)
                {
                    for (Map.Entry<String, Object> attr : inputAttrs.entrySet())
                    {
                        extraInputAttributes.append(' ')
                                .append(attr.getKey())
                                .append("=\"")
                                .append(attr.getValue())
                                .append('"');
                    }
                }

                // Add radio tag
                buffer.append("<input name=\"")
                        .append(getInputName())
                        .append('"')
                        .append(" type=\"radio\"")
                        .append((isSelected(choice, index, selected) ? " checked=\"checked\"" : ""))
                        .append((enabled ? "" : " disabled=\"disabled\""))
                        .append(" value=\"")
                        .append(id)
                        .append("\" id=\"")
                        .append(idAttr)
                        .append('"')
                        .append(extraInputAttributes);

                // Should a roundtrip be made (have onSelectionChanged called)
                // when the option is clicked?
                if (wantOnSelectionChangedNotifications())
                {
                    CharSequence url = urlFor(IOnChangeListener.INTERFACE, new PageParameters());

                    Form<?> form = findParent(Form.class);
                    if (form != null)
                    {
                        buffer.append(" onclick=\"")
                                .append(form.getJsForInterfaceUrl(url))
                                .append(";\"");
                    }
                    else
                    {
                        // NOTE: do not encode the url as that would give
                        // invalid JavaScript
                        buffer.append(" onclick=\"window.location.href='")
                                .append(url)
                                .append((url.toString().indexOf('?') > -1 ? '&' : '?') + getInputName())
                                .append('=')
                                .append(id)
                                .append("';\"");
                    }
                }

                // Allows user to add attributes to the <input..> tag
                {
                    IValueMap attrs = getAdditionalAttributes(index, choice);
                    if (attrs != null)
                    {
                        for (Map.Entry<String, Object> attr : attrs.entrySet())
                        {
                            buffer.append(' ')
                                    .append(attr.getKey())
                                    .append("=\"")
                                    .append(attr.getValue())
                                    .append('"');
                        }
                    }
                }

                DebugSettings debugSettings = getApplication().getDebugSettings();
                String componentPathAttributeName = debugSettings.getComponentPathAttributeName();
                if (Strings.isEmpty(componentPathAttributeName) && debugSettings.isOutputComponentPath())
                {
                    // fallback to the old 'wicketpath'
                    componentPathAttributeName = "wicketpath";
                }
                if (Strings.isEmpty(componentPathAttributeName) == false)
                {
                    CharSequence path = getPageRelativePath();
                    path = Strings.replaceAll(path, "_", "__");
                    path = Strings.replaceAll(path, ":", "_");
                    buffer.append(' ').append(componentPathAttributeName).append("=\"")
                            .append(path)
                            .append("_input_")
                            .append(index)
                            .append('"');
                }

                buffer.append("/>");

                buffer.append("</label>");

                // Append option suffix
                buffer.append(getSuffix(index, choice));
            }
        }

        @Override
        protected boolean wantOnSelectionChangedNotifications() {
            return true;
        }
    }
}
