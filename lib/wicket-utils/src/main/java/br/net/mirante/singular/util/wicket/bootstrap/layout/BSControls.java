package br.net.mirante.singular.util.wicket.bootstrap.layout;

import static org.apache.commons.lang3.StringUtils.defaultString;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebApplication;

import br.net.mirante.singular.util.wicket.feedback.BSFeedbackPanel;
import br.net.mirante.singular.util.wicket.jquery.JQuery;
import br.net.mirante.singular.util.wicket.resource.Icone;

public class BSControls extends BSContainer<BSControls> implements IBSGridCol<BSControls> {

    public BSControls(String id) {
        this(id, true);
    }

    public BSControls(String id, boolean addGridBehavior) {
        super(id);
        if (addGridBehavior) {
            add(newBSGridColBehavior());
        }
    }

    public BSControls appendCheckbox(Component checkbox) {
        return this.appendCheckbox(checkbox, Model.of(""));
    }
    public BSControls appendCheckbox(Component checkbox, IModel<?> labelModel) {
        return this.appendCheckbox(checkbox, new Label("_", labelModel));
    }
    public BSControls appendCheckbox(Component checkbox, Component label) {
        this
            .appendTag("div", true, "class='checkbox'", new BSContainer<>("_" + checkbox.getId())
                .appendTag("label", new BSContainer<>("_")
                    .appendTag("input", false, "type='checkbox'", checkbox)
                    .appendTag("span", label)
                )
            );
        return this;
    }

    public BSControls appendCheckboxChoice(Component checkbox) {
        return super.appendTag("div", true, "class='checkbox-list'", checkbox);
    }

    public BSControls appendLabel(Component label) {
        return this.appendTag("label", label);
    }

    public BSControls appendInputEmail(Component input) {
        return super.appendTag("input", false, "type='email' class='form-control'", input);
    }

    public BSControls appendInputPassword(Component input) {
        return super.appendTag("input", false, "type='password' class='form-control'", input);
    }

    public BSControls appendInputText(Component input) {
        return super.appendTag("input", false, "type='text' class='form-control'", input);
    }
    
    public BSControls appendInputFile(Component input) {
	BSContainer buttonContainer = new BSContainer<>("_bt_" + input.getId())		
		.appendTag("span", new Label("_", Model.of("Selecionar ...")))
		.appendTag("input", true, "type='file' id='" + input.getMarkupId() + "'", input)
		;

	final String path = buttonContainer.getRequest().getContextPath() + "/fileUpload"; //TODO : need to be a service
	TemplatePanel scriptContainer = (TemplatePanel) buttonContainer.newComponent(id -> new TemplatePanel(id,
		() -> "<script > " 
			+ "$(function () {" 
			+ "  $('#" + input.getMarkupId() 
			+ "').fileupload({  "
			+ "    url: '"+path+"',  " 
			+ "    paramName: 'FILE-UPLOAD',  " 
			+ "    singleFileUploads: true,  " 
//			+ "    dataType: 'json',  "
			+ "    start: function (e, data) {  "
			+ "        $('#files_"+ input.getMarkupId()+"').html('');"
			+ "        $('#progress_"+ input.getMarkupId()+" .progress-bar').css('width','0%')"
			+ "    },"
			+ "    done: function (e, data) {  "
			+ "        console.log(e,data);    "
			+ "        $.each(data.result.files, function (index, file) {  "
			+ "            $('<p/>').text(file.name).appendTo('#files_"+ input.getMarkupId()+"'); "
			+ "        });  " 
			+ "    },  " 
			+ "    progressall: function (e, data) {  "
			+ "        var progress = parseInt(data.loaded / data.total * 100, 10); "
			+ "        $('#progress_"+ input.getMarkupId()+" .progress-bar').css( 'width', "
			+ "                        progress + '%' ); "
			+ "    }  " 
			+ "  }).prop('disabled', !$.support.fileInput)  "
			+ "    .parent().addClass($.support.fileInput ? undefined : 'disabled');  " 
			+ "});"
			+ " </script>\n"));
	scriptContainer
		// .add(component)
		.setRenderBodyOnly(true);

//	BSControls button = this.appendTag("span", true, "class='btn btn-success fileinput-button'", buttonContainer);
	
	BSContainer progressContainer = new BSContainer<>("_progress_" + input.getId());
	progressContainer.appendTag("div", true, "class='progress-bar progress-bar-success'",new Label("_", Model.of("")));
	
	BSContainer inputContainer = new BSContainer<>("_" + input.getId());
	inputContainer.appendTag("span", true, "class='btn btn-success fileinput-button'", buttonContainer);
	inputContainer.appendTag("div", true, "class='progress' id='progress_" + input.getMarkupId() + "'",progressContainer);
	inputContainer.appendTag("div", true, "class='files' id='files_" + input.getMarkupId() + "'",new Label("_", Model.of("")));
	
	return this.appendTag("div", inputContainer);
    }

    
    public BSControls appendRadioChoice(Component input) {
        return super.appendTag("div", true, "class='radio-list'", input);
    }

    public BSControls appendDatepicker(Component datepicker) {
        return this.appendDatepicker(datepicker, new HashMap<String, String>() {{
            put("data-date-format", "dd/mm/yyyy");
            put("data-date-start-view", "days");
            put("data-date-min-view-mode", "days");
        }});
    }

    public BSControls appendDatepicker(Component datepicker, Map<String, String> extraAttributes) {
        this.appendInputGroup(componentId -> newInputGroup()
                .appendExtraClasses("input-medium date date-picker")
                .appendExtraAttributes(extraAttributes)
                .appendInputText(datepicker)
                .appendButtonAddon(Icone.CALENDAR));
        return this;
    }

    public BSControls appendSelect(Component select) {
        return appendSelect(select, false, true);
    }

    public BSControls appendSelect(Component select, boolean multiple) {
        return appendSelect(select, multiple, true);
    }

    public BSControls appendSelect(Component select, boolean multiple, boolean bootstrap) {
        return super.appendTag("select", true, (bootstrap
                ? "class='bs-select form-control' data-width='80%' title='Selecione...'" : "class='form-control'")
                + (multiple ? "multiple" : ""), select);
    }

    public BSControls appendPicklist(Component select) {
        return super.appendTag("select", true, "class='multi-select' multiple", select);
    }

    public BSControls appendStaticText(Component text) {
        return super.appendTag("p", true, "class='form-control-static'", text);
    }

    public BSControls appendTextarea(Component textarea) {
        return super.appendTag("textarea", true, "class='form-control' rows='3'", textarea);
    }

    public BSControls appendTypeahead(Component typeahead) {
        return super.appendTag("div", typeahead);
    }

    public BSControls appendHeading(Component text, int level) {
        return super.appendTag("h" + level, true, "class='form-section'", text);
    }

    public BSControls appendInputButton(Component button) {
        return appendInputButton(null, button);
    }

    public BSControls appendInputButton(String extraClasses, Component button) {
        return super.appendTag("input",
            false,
            "type='button' class='btn btn-default " + defaultString(extraClasses) + "'", button);
    }

    public BSControls appendLinkButton(IModel<?> linkText, AbstractLink link) {
        return appendLinkButton(null, linkText, link);
    }

    public BSControls appendLinkButton(String extraClasses, IModel<?> linkText, AbstractLink link) {
        if (linkText != null)
            link.setBody(linkText);
        return super.appendTag("a", true, "class='btn btn-default " + defaultString(extraClasses) + "'", link);
    }

    public BSControls appendLink(IModel<?> linkText, AbstractLink link) {
        if (linkText != null)
            link.setBody(linkText);
        return super.appendTag("a", true, "class='btn btn-link'", link);
    }

    public BSControls appendInputGroup(IBSComponentFactory<BSInputGroup> factory) {
        return appendComponent(factory);
    }
    public BSInputGroup newInputGroup() {
        return newComponent(BSInputGroup::new);
    }

    public BSControls appendFeedback() {
        return appendFeedback(this, null);
    }

    public BSControls appendFeedback(Component fence, IFeedbackMessageFilter filter) {
        return super.appendTag("span", true,
            "class='help-block'", newFeedbackPanel("controlErrors", fence, filter)
                .add(new Behavior() {
                    @Override
                    public void renderHead(Component component, IHeaderResponse response) {
                        super.renderHead(component, response);
                        FeedbackPanel fp = (FeedbackPanel) component;
                        if (fp.anyErrorMessage()) {
                            response.render(OnDomReadyHeaderItem.forScript(""
                                + JQuery.$(fp) + ".closest('.can-have-error').addClass('has-error');"
                                + ""));
                        } else {
                            response.render(OnDomReadyHeaderItem.forScript(""
                                + JQuery.$(fp) + ".closest('.can-have-error').removeClass('has-error');"
                                + ""));
                        }
                    }
                })
            );
    }

    public Label newHelpBlock(IModel<String> textModel) {
        return super.newTag("span", true, "class='help-block'", (Label) newComponent(id -> new Label(id, textModel)));
    }
    public BSControls appendHelpBlock(IModel<String> textModel) {
        newHelpBlock(textModel);
        return this;
    }

    protected FeedbackPanel newFeedbackPanel(String id, Component fence, IFeedbackMessageFilter filter) {
        return new BSFeedbackPanel(id, fence, filter);
    }
}
